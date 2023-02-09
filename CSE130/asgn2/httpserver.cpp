#include <unistd.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <string>
#include <vector>
#include <netdb.h>
#include <regex>
#include <iostream>
#include <err.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <pthread.h>
#include <semaphore.h>

sem_t empty;
sem_t full;

pthread_mutex_t request_mutex        = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t globalOffset_mutex   = PTHREAD_MUTEX_INITIALIZER;

int fd_container   = -2;
int logFileFD      = -1;
int thread_counter = 0;
int globalOffset   = 0;
std::vector<int> fd_buffer;
std::string log_terminator = "========\n";

//using namespace std;

sockaddr_in setHostByName(char* arg, int port) {
    struct hostent *hent = gethostbyname(arg);
    struct sockaddr_in address;
    memcpy(&address.sin_addr.s_addr, hent->h_addr, hent->h_length);
    address.sin_port = htons(port);
    address.sin_family = AF_INET;

    //    struct in_addr ip_addr;
    // 	ip_addr = *(struct in_addr *)(&address.sin_addr.s_addr);
    // 	printf("Hostname: %s, was resolved to: %s with port number: %i\n", hent->h_name,inet_ntoa(ip_addr), ntohs(address.sin_port));


    return address;
}

int setSocket(sockaddr_in address) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    int optvl = 1;
    ::setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &optvl, sizeof(optvl));
    ::bind(sock, (struct sockaddr *)&address, sizeof(address));
    return sock;
}

int getContentLength(std::vector<char*> string) {
    for (std::vector<char*>::iterator word=string.begin(); word!=string.end(); ++word) {
        if(strcmp(*word, "Content-Length:") == 0) {
            int length = atoi(*++word);
            return length;
        }
    }
    return -1;
}

int verifyName(char* string, int fd) {
    char errorBadRequest[] = "HTTP/1.1 400 Bad Request\r\n\r\n";
    //printf("%s\n", string);
    if(strlen(string) != 27 || !std::regex_match(string, std::regex("[a-zA-Z0-9-_]+"))) {
        write(fd, errorBadRequest, sizeof(errorBadRequest));
        return 0;
    }
    return 1;
}

void replyPUT(std::vector<char*> string, int fd) {
    char* trimmedFileName = strtok(string[1], "/");
    int validName = verifyName(trimmedFileName, fd);
    int writeFD = open(trimmedFileName, O_RDWR | O_TRUNC, 0666);
    int fileCreated = 0;
    int contentLength = getContentLength(string);
    char replyOK[] = "HTTP/1.1 200 OK\r\n\r\n";
    char replyCreateOK[] = "HTTP/1.1 201 OK\r\n\r\n";
    char errorForbidden[] = "HTTP/1.1 403 Forbidden\r\n\r\n";

    std::string logCommand = std::string(string[0]) + " " + std::string(trimmedFileName);
    int logSize = strlen(log_terminator.c_str());

    if(writeFD == -1) {
        writeFD = open(trimmedFileName, O_RDWR | O_TRUNC | O_CREAT, 0666);
        fileCreated = 1;
    }

    if(validName == 0) {
        if(logFileFD != -1) {
            std::string logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 400\\n\n" + log_terminator;
            logSize = strlen(logHeader.c_str());
            char logGet[logSize];
            sprintf(logGet, "%s", logHeader.c_str());

            pthread_mutex_lock(&globalOffset_mutex);
            pwrite(logFileFD, logGet, logSize, globalOffset);
            globalOffset += logSize;
            pthread_mutex_unlock(&globalOffset_mutex);
        }
        close(writeFD);
        return;
    }

    if(errno == 13) {
        if(logFileFD != -1) {
            std::string logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 403\\n\n" + log_terminator;
            logSize = strlen(logHeader.c_str());
            char logGet[logSize];
            sprintf(logGet, "%s", logHeader.c_str());

            pthread_mutex_lock(&globalOffset_mutex);
            pwrite(logFileFD, logGet, logSize, globalOffset);
            globalOffset += logSize;
            pthread_mutex_unlock(&globalOffset_mutex);
        }
        write(fd, errorForbidden, strlen(errorForbidden));
        close(writeFD);
        return;
    }

    int full_lines = contentLength/20;
    int remaining_bytes = contentLength % 20;
    int data_space = (full_lines * (8 + (20 * 3) + 1)) + (8 + (remaining_bytes * 3) + 1);
    std::string logHeader = logCommand + " length " + std::to_string(contentLength) + "\n";
    logSize += strlen(logHeader.c_str());
    int logSpace = logSize + data_space;
    char logPut[logSpace];
    char *logPutPointer = &logPut[0];

    switch(contentLength) {
        char buffer[16384];
        int bytes_read;
        case -1:
            do {

                bytes_read = read(fd, buffer, sizeof(buffer));
                write(writeFD, buffer, bytes_read);
                memset(buffer, 0, sizeof(buffer));
                if(!fileCreated) { write(fd, replyOK, strlen(replyOK)); }
                else { write(fd, replyCreateOK, strlen(replyCreateOK)); }

            } while (bytes_read > 0);
            break;
        default:
            int buff_pos = 0;
            logPutPointer += sprintf(logPutPointer, "%s%08d", logHeader.c_str(), buff_pos);
            do {

                bytes_read = read(fd, buffer, contentLength);
                if(logFileFD != -1) {
                    for(; buff_pos < bytes_read; buff_pos++) {
                        logPutPointer += sprintf(logPutPointer, " %02x", buffer[buff_pos] & 0xff);
                        if((buff_pos % 20) == 19) {
                            logPutPointer += sprintf(logPutPointer, "\n%08d", buff_pos+1);
                        }
                    }
                }
                write(writeFD, buffer, bytes_read);
                memset(buffer, 0, sizeof(buffer));
                contentLength -= bytes_read;
                if(contentLength == 0) { 
                    if(!fileCreated) { write(fd, replyOK, strlen(replyOK)); }
                    else { write(fd, replyCreateOK, strlen(replyCreateOK)); }
                }
            } while (contentLength > 0);
            sprintf(logPutPointer, "\n%s\n", log_terminator.c_str());

            pthread_mutex_lock(&globalOffset_mutex);
            pwrite(logFileFD, logPut, logSpace, globalOffset);
            globalOffset += logSpace;
            pthread_mutex_unlock(&globalOffset_mutex);
    }
    close(writeFD);
}

void replyGET(std::vector<char*> string, int fd) {
    char* trimmedFileName = strtok(string[1], "/");
    int validName = verifyName(trimmedFileName, fd);
    char errorNotFound[] = "HTTP/1.1 404 Not Found\r\n";
    char responseOK[] = "HTTP/1.1 200 OK\r\n";
    char errorForbidden[] = "HTTP/1.1 403 Forbidden\r\n";

    std::string logCommand = std::string(string[0]) + " " + std::string(trimmedFileName);
    int logSize = strlen(log_terminator.c_str());
    int readFD = open(trimmedFileName, O_RDWR);

    if(validName == 0) {
        if(logFileFD != -1) {
            std::string logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 400\\n\n" + log_terminator;
            logSize = strlen(logHeader.c_str());
            char logGet[logSize];
            sprintf(logGet, "%s", logHeader.c_str());

            pthread_mutex_lock(&globalOffset_mutex);
            pwrite(logFileFD, logGet, logSize, globalOffset);
            globalOffset += logSize;
            pthread_mutex_unlock(&globalOffset_mutex);
        }
        close(readFD);
        return;
    }

    switch(errno) {
        case 2:
            if(logFileFD != -1) {
                std::string logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 404\\n\n" + log_terminator;
                logSize = strlen(logHeader.c_str());
                char logGet[logSize];
                sprintf(logGet, "%s", logHeader.c_str());

                pthread_mutex_lock(&globalOffset_mutex);
                pwrite(logFileFD, logGet, logSize, globalOffset);
                globalOffset += logSize;
                pthread_mutex_unlock(&globalOffset_mutex);
            }
            write(fd, errorNotFound, sizeof(errorNotFound));
            close(readFD);
            return;
        case 13:
            if(logFileFD != -1) {
                std::string logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 403\\n\n" + log_terminator;
                logSize = strlen(logHeader.c_str());
                char logGet[logSize];
                sprintf(logGet, "%s", logHeader.c_str());

                pthread_mutex_lock(&globalOffset_mutex);
                pwrite(logFileFD, logGet, logSize, globalOffset);
                globalOffset += logSize;
                pthread_mutex_unlock(&globalOffset_mutex);
            }
            write(fd, errorForbidden, strlen(errorForbidden));
            close(readFD);
            return;
    }

    //Method to get file size from file descriptor from here: https://stackoverflow.com/questions/6537436/how-do-you-get-file-size-by-fd
    int fileSize = lseek(readFD, 0, SEEK_END);
    lseek(readFD, 0, SEEK_SET);

    std::string Clength = "Content-Length: " + std::to_string(fileSize) + "\r\n\n";
    std::string logHeader = logCommand + " length 0" + "\n";

    if(logFileFD != -1) {
        logSize += strlen(logHeader.c_str());
        char logGet[logSize];
        sprintf(logGet, "%s", (logHeader + log_terminator).c_str());

        pthread_mutex_lock(&globalOffset_mutex);
        pwrite(logFileFD, logGet, logSize, globalOffset);
        globalOffset += logSize;
        pthread_mutex_unlock(&globalOffset_mutex);
    }

    write(fd, Clength.c_str(), strlen(Clength.c_str()));
    char buffer[16384];
    for(;;) {
        int bytes_read = read(readFD, buffer, sizeof(buffer));
        if(bytes_read == 0) {
            write(fd, responseOK, sizeof(responseOK));
            break;
        }
        else {
            write(fd, buffer, bytes_read);
            memset(buffer, 0, sizeof(buffer));
        }
    }
    close(readFD);
}

void executeCommands(std::vector<char*> string, int fd) {
    char* trimmedFileName = strtok(string[1], "/");
    std::string logCommand = std::string(string[0]) + " " + std::string(trimmedFileName);
    char errorInternalError[] = "HTTP/1.1 500 Internal Server Error\r\n";
    if(strcmp(string[0], "PUT") == 0) {
        replyPUT(string, fd);
    }
    else if(strcmp(string[0], "GET") == 0) {
        replyGET(string, fd);
    }
    else {
        if(logFileFD != -1) {
            std::string logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 500\\n\n" + log_terminator;
            int logSize = strlen(logHeader.c_str());
            char logGet[logSize];
            sprintf(logGet, "%s", logHeader.c_str());

            pthread_mutex_lock(&globalOffset_mutex);
            pwrite(logFileFD, logGet, logSize, globalOffset);
            globalOffset += logSize;
            pthread_mutex_unlock(&globalOffset_mutex);
        }
        write(fd, errorInternalError, strlen(errorInternalError));
        return;
    }
}

void parseHeader(int fd) {
    char buffer[16384];
    read(fd, buffer, sizeof(buffer));

    char* delimited;
    std::vector<char*> parsedString;

    delimited = strtok(buffer, " \r\n");
    while(delimited != NULL) {
        parsedString.push_back(delimited);
        delimited = strtok(NULL, " \r\n");
    }
    executeCommands(parsedString, fd);
    memset(buffer, 0, sizeof(buffer));
}

void *threadGrabRequest(void* arg) {
    for(;;) {
        sem_wait(&full);
        pthread_mutex_lock(&request_mutex);
        int handle_fd = fd_buffer.back();
        fd_buffer.pop_back();
        pthread_mutex_unlock(&request_mutex);

        parseHeader(handle_fd);
        close(handle_fd);
        sem_post(&empty);
    }
}

int main(int argc, char** argv) {
    int PORT_NUMBER = 80;
    int nThreads = 4;
    pthread_t pool[nThreads];
    sem_init(&empty, 0, nThreads);
    sem_init(&full, 0, 0);

    int opt;
    while((opt = getopt(argc, argv, "N:l:")) != -1) {
        switch (opt) {
            case 'N':
                nThreads = atoi(optarg);
                break;
            case 'l':
                logFileFD = open(optarg, O_RDWR | O_TRUNC | O_CREAT, 0666);
                break;
        }
    }

    for(int thread_pos = 0; thread_pos < nThreads; thread_pos++) {
        //thread_args[thread_pos] = thread_pos;
        pthread_create(&pool[thread_pos], NULL, threadGrabRequest, (void *) (intptr_t) thread_pos);
    }

    std::vector<char*> new_argv;
    for(; optind < argc; optind++) {
        new_argv.push_back(argv[optind]);
        printf("%s\n", argv[optind]);
    }

    if(new_argv.size() == 2) {
        PORT_NUMBER = atoi(new_argv[1]);
    }

    sockaddr_in address = setHostByName(new_argv[0], PORT_NUMBER);
    int socket = setSocket(address);
    int addresslen = sizeof(address);
    ::listen(socket, SOMAXCONN);
    for(;;) {
        int fd = ::accept(socket, (struct sockaddr *)&address, (socklen_t*)&addresslen);
        sem_wait(&empty);
        pthread_mutex_lock(&request_mutex);
        fd_buffer.push_back(fd);
        pthread_mutex_unlock(&request_mutex);
        sem_post(&full);
    }
    return 0;
}
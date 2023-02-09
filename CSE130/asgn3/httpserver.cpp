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
#include <unordered_map>

//using namespace std;

int globalOffset           = 0;
int logFileFD              = -1;
std::string log_terminator = "========\n";
int cacheFlag              = 0;

struct page {
    std::string name;
    char* buffer;
    int dirty;
};

std::vector<page> cache;

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
    char errorBadRequest[] = "HTTP/1.1 400 Bad Request\r\n";
    if(strlen(string) != 27 || !std::regex_match(string, std::regex("[a-zA-Z0-9-_]+"))) {
        write(fd, errorBadRequest, sizeof(errorBadRequest));
        return 0;
    }
    return 1;
}

void logWriteError(std::string logHeader) {
    int logSize = strlen(logHeader.c_str());
    char logGet[logSize];
    sprintf(logGet, "%s", logHeader.c_str());
    pwrite(logFileFD, logGet, logSize, globalOffset);
    globalOffset += logSize;

}

void responseHeaderForCache(int fd, int file_exists, char* replyCreateOK, char* replyOK) {
    if(file_exists == -1) {
        write(fd, replyCreateOK, strlen(replyCreateOK));
    } else {
        write(fd, replyOK, strlen(replyOK));
    }
}

void replyPUT(std::vector<char*> string, int fd) {
    char* trimmedFileName = strtok(string[1], "/");
    int validName = verifyName(trimmedFileName, fd);
    int writeFD;
    int fileCreated = 0;
    std::string logCommand = std::string(string[0]) + " " + std::string(trimmedFileName);
    int logSize = strlen(log_terminator.c_str());
    int contentLength = getContentLength(string);
    char replyOK[] = "HTTP/1.1 200 OK\r\n";
    char replyCreateOK[] = "HTTP/1.1 201 OK\r\n";
    char errorForbidden[] = "HTTP/1.1 403 Forbidden\r\n";

    if(cacheFlag == 0 || contentLength == -1) {
        writeFD = open(trimmedFileName, O_RDWR | O_TRUNC, 0666);
        if(writeFD == -1) {
            writeFD = open(trimmedFileName, O_RDWR | O_TRUNC | O_CREAT, 0666);
            fileCreated = 1;
        }
    }

    if(validName == 0) {
        if(logFileFD != -1) {
            std::string logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 400\\n\n" + log_terminator;
            logWriteError(logHeader);
        }
        if(cacheFlag == 0 || contentLength == -1) { close(writeFD); }
        return;
    }

    if(errno == 13) {
        if(logFileFD != -1) {
            std::string logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 403\\n\n" + log_terminator;
            logWriteError(logHeader);
        }
        write(fd, errorForbidden, strlen(errorForbidden));
        if(cacheFlag == 0 || contentLength == -1) { close(writeFD); }
        return;
    }

    char *bufferPUT;
    bufferPUT = (char *)malloc(contentLength * sizeof(char));
    bufferPUT[contentLength] = '\0';

    switch(contentLength) {
        int bytes_read;
        case -1:
            do {

                bytes_read = read(fd, bufferPUT, sizeof(bufferPUT));
                write(writeFD, bufferPUT, bytes_read);
                memset(bufferPUT, 0, sizeof(*bufferPUT)*contentLength);
                if(!fileCreated) { write(fd, replyOK, strlen(replyOK)); }
                else { write(fd, replyCreateOK, strlen(replyCreateOK)); }

            } while (bytes_read > 0);
            break;
        default:
            int full_lines = contentLength/20;
            int remaining_bytes = contentLength % 20;
            int data_space = (full_lines * (8 + (20 * 3) + 1)) + (8 + (remaining_bytes * 3) + 1);
            int buff_pos = 0;

            int inCache = 0;
            std::string logHeader;
            auto it = cache.begin();
            if(cacheFlag == 1) {
                logHeader = logCommand + " length " + std::to_string(contentLength) + " [was not in cache]\n";
                for(; it != cache.end(); ++it) {
                    if(trimmedFileName == it->name) {
                        inCache = 1;
                        logHeader = logCommand + " length " + std::to_string(contentLength) + " [was in cache]\n";
                        break;
                    }
                }
            } else {
                logHeader = logCommand + " length " + std::to_string(contentLength) + "\n";
            }

            logSize += strlen(logHeader.c_str());
            int logSpace = logSize + data_space;
            char logPut[logSpace];
            char *logPutPointer = &logPut[0];
            logPutPointer += sprintf(logPutPointer, "%s%08d", logHeader.c_str(), buff_pos);
            do {

                bytes_read = read(fd, bufferPUT, contentLength);

                if(logFileFD != -1) {
                    for(; buff_pos < bytes_read; buff_pos++) {
                        logPutPointer += sprintf(logPutPointer, " %02x", bufferPUT[buff_pos] & 0xff);
                        if((buff_pos % 20) == 19) {
                            logPutPointer += sprintf(logPutPointer, "\n%08d", buff_pos+1);
                        }
                    }
                }

                if(cacheFlag == 1) {
                    page myPage;
                    myPage.name = trimmedFileName;
                    myPage.buffer = bufferPUT;

                    if(inCache == 0) {
                        if(cache.size() == 4) {
                            if(cache.begin()->dirty == 1) {
                                writeFD = open(cache.begin()->name.c_str(), O_RDWR | O_TRUNC| O_CREAT, 0666);
                                write(writeFD, cache.begin()->buffer, strlen(cache.begin()->buffer));
                                close(writeFD);
                            }
                            free(cache.begin()->buffer);
                            cache.erase(cache.begin());
                        }
                        int file_exists = open(trimmedFileName, O_RDWR);
                        if(file_exists == -1) {
                            close(file_exists);
                            errno = 0;
                            myPage.dirty = 1;
                            cache.push_back(myPage);
                        }
                        else {
                            int diskFile = open(trimmedFileName, O_RDWR);
                            int diskFileSize = lseek(diskFile, 0, SEEK_END);
                            char diskFileBuffer[diskFileSize];
                            lseek(diskFile, 0, SEEK_SET);
                            read(diskFile, diskFileBuffer, diskFileSize);
                            if(std::strcmp(myPage.buffer, diskFileBuffer) == 0) {
                                myPage.dirty = 0;
                            }
                            else {
                                myPage.dirty = 1;
                            }
                            close(diskFile);
                            cache.push_back(myPage);
                        }
                        responseHeaderForCache(fd, file_exists, replyCreateOK, replyOK);
                    }
                    else {
                        if(std::strcmp(myPage.buffer, it->buffer) != 0) {
                            free(it->buffer);
                            cache.erase(it);
                            myPage.dirty = 1;
                            cache.push_back(myPage);
                        }
                        else {
                            myPage.dirty = it->dirty;
                            free(it->buffer);
                            cache.erase(it);
                            cache.push_back(myPage);
                        }
                        write(fd, replyOK, strlen(replyOK));
                    }
                    contentLength -= bytes_read;
                }

                //If not caching
                else if(cacheFlag == 0) {
                    write(writeFD, bufferPUT, bytes_read);
                    memset(bufferPUT, 0, sizeof(*bufferPUT)*contentLength);
                    contentLength -= bytes_read;
                    if(contentLength == 0) { 
                        if(!fileCreated) { write(fd, replyOK, strlen(replyOK)); }
                        else { write(fd, replyCreateOK, strlen(replyCreateOK)); }
                    }
                }
            } while (contentLength > 0);

            if(logFileFD != -1) {
                sprintf(logPutPointer, "\n%s\n", log_terminator.c_str());
                pwrite(logFileFD, logPut, logSpace, globalOffset);
                globalOffset += logSpace;
            }
    }
    if(cacheFlag == 0 || contentLength == -1) {
        free(bufferPUT);
        close(writeFD);
    }
}

void replyGET(std::vector<char*> string, int fd) {
    char* trimmedFileName = strtok(string[1], "/");
    int validName = verifyName(trimmedFileName, fd);
    char errorNotFound[] = "HTTP/1.1 404 Not Found\r\n";
    char responseOK[] = "HTTP/1.1 200 OK\r\n";
    char errorForbidden[] = "HTTP/1.1 403 Forbidden\r\n";
    std::string logCommand = std::string(string[0]) + " " + std::string(trimmedFileName);
    int readFD;
    int fileSize;
    char *buffer;

    if(validName == 0) {
        if(logFileFD != -1) {
            std::string logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 400\\n\n" + log_terminator;
            logWriteError(logHeader);
        }
        return;
    }

    if(cacheFlag == 0) {
        errno = 0;
        readFD = open(trimmedFileName, O_RDWR);
        switch(errno) {
            case 2:
                if(logFileFD != -1) {
                    std::string logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 404\\n\n" + log_terminator;
                    logWriteError(logHeader);
                }
                write(fd, errorNotFound, sizeof(errorNotFound));
                close(readFD);
                return;
            case 13:
                if(logFileFD != -1) {
                    std::string logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 403\\n\n" + log_terminator;
                    logWriteError(logHeader);
                }
                write(fd, errorForbidden, strlen(errorForbidden));
                close(readFD);
                return;
        }
        fileSize = lseek(readFD, 0, SEEK_END);
        lseek(readFD, 0, SEEK_SET);

        std::string Clength = "Content-Length: " + std::to_string(fileSize) + "\r\n\n";
        write(fd, Clength.c_str(), strlen(Clength.c_str()));

        buffer = (char *)malloc(fileSize * sizeof(char));
        buffer[fileSize] = '\0';
    }

    int inCache = 0;
    std::string logHeader;
    if(cacheFlag == 1) {
        for(auto it = cache.begin(); it != cache.end(); ++it) {
            if(trimmedFileName == it->name) {
                inCache = 1;
                break;
            }
        }
    } else {
        logHeader = logCommand + " length 0\n" + log_terminator;
    }

    if(cacheFlag == 1) {
        auto it = cache.begin();
        for(; it != cache.end(); ++it) {
            if(it->name == trimmedFileName) {
                logHeader = logCommand + " length 0 [was in cache]\n" + log_terminator;
                write(fd, it->buffer, strlen(it->buffer));
                write(fd, responseOK, sizeof(responseOK));
                break;
            }
        }
        if(it == cache.end()) {
            errno = 0;
            readFD = open(trimmedFileName, O_RDWR);

            switch(errno) {
                case 2:
                    if(logFileFD != -1) {
                        logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 404\\n\n" + log_terminator;
                        logWriteError(logHeader);
                    }
                    write(fd, errorNotFound, sizeof(errorNotFound));
                    close(readFD);
                    return;
                case 13:
                    if(logFileFD != -1) {
                        logHeader = "FAIL: " + logCommand + " HTTP/1.1 -- response 403\\n\n" + log_terminator;
                        logWriteError(logHeader);
                    }
                    write(fd, errorForbidden, strlen(errorForbidden));
                    close(readFD);
                    return;
            }

            //If cache is full, delete and do a writeback if needed
            if(cache.size() == 4) {
                if(cache.begin()->dirty == 1) {
                    int writeFD = open(cache.begin()->name.c_str(), O_RDWR | O_TRUNC| O_CREAT, 0666);
                    write(writeFD, cache.begin()->buffer, strlen(cache.begin()->buffer));
                    close(writeFD);
                }
                free(cache.begin()->buffer);
                cache.erase(cache.begin());
            }

            fileSize = lseek(readFD, 0, SEEK_END);
            lseek(readFD, 0, SEEK_SET);

            std::string Clength = "Content-Length: " + std::to_string(fileSize) + "\r\n\n";
            write(fd, Clength.c_str(), strlen(Clength.c_str()));

            buffer = (char *)malloc(fileSize * sizeof(char));
            buffer[fileSize] = '\0';

            int bytes_read = read(readFD, buffer, fileSize);
            page myPage;
            myPage.name = trimmedFileName;
            myPage.buffer = buffer;
            myPage.dirty = 0;

            cache.push_back(myPage);
            logHeader = logCommand + " length 0 [was not in cache]\n" + log_terminator;
            write(fd, buffer, bytes_read);
            write(fd, responseOK, sizeof(responseOK));
            close(readFD);
        }
    }

    else {
        for(;;) {
            int bytes_read = read(readFD, buffer, sizeof(buffer));
            if(bytes_read == 0) {
                write(fd, responseOK, sizeof(responseOK));
                break;
            }
            else {
                write(fd, buffer, bytes_read);
                memset(buffer, 0, sizeof(*buffer)*fileSize);
            }
        }
        free(buffer);
        close(readFD);
    }

    if(logFileFD != -1) {
        int logSize = strlen(logHeader.c_str());
        char logGet[logSize];
        sprintf(logGet, "%s", logHeader.c_str());
        pwrite(logFileFD, logGet, logSize, globalOffset);
        globalOffset += logSize;
    }
}

void executeCommands(std::vector<char*> string, int fd) {
    char errorInternalError[] = "HTTP/1.1 500 Internal Server Error\r\n";
    if(strcmp(string[0], "PUT") == 0) {
        replyPUT(string, fd);
    }
    else if(strcmp(string[0], "GET") == 0) {
        replyGET(string, fd);
    }
    else {
        write(fd, errorInternalError, strlen(errorInternalError));
        return;
    }
}

void parseHeader(int fd) {
    char buffer[32768];
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

int main(int argc, char** argv) {
    int PORT_NUMBER = 80;

    int opt;
    while((opt = getopt(argc, argv, "cl:")) != -1) {
        switch (opt) {
            case 'c':
                cacheFlag = 1;
                break;
            case 'l':
                logFileFD = open(optarg, O_RDWR | O_TRUNC | O_CREAT, 0666);
                break;
        }
    }

    std::vector<char*> new_argv;
    for(; optind < argc; optind++) {
        new_argv.push_back(argv[optind]);
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
        parseHeader(fd);
        close(fd);
    }
    return 0;
}
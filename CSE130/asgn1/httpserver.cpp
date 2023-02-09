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
    char errorBadRequest[] = "HTTP/1.1 400 Bad Request\r\n";
    if(strlen(string) != 27 || !std::regex_match(string, std::regex("[a-zA-Z0-9-_]+"))) {
        write(fd, errorBadRequest, sizeof(errorBadRequest));
        return 0;
    }
    return 1;
}

void replyPUT(std::vector<char*> string, int fd) {
    char* trimmedFileName = strtok(string[1], "/");
    int validName = verifyName(trimmedFileName, fd);
    int writeFD = open(trimmedFileName, O_RDWR | O_TRUNC);
    int fileCreated = 0;
    int contentLength = getContentLength(string);
    char replyOK[] = "HTTP/1.1 200 OK\r\n";
    char replyCreateOK[] = "HTTP/1.1 201 OK\r\n";
    char errorForbidden[] = "HTTP/1.1 403 Forbidden\r\n";

    if(writeFD == -1) {
        writeFD = open(trimmedFileName, O_RDWR | O_TRUNC | O_CREAT);
        fileCreated = 1;
    }

    if(validName == 0) {
        close(writeFD);
        return;
    }

    if(errno == 13) {
        write(fd, errorForbidden, strlen(errorForbidden));
        close(writeFD);
        return;
    }

    switch(contentLength) {
        char buffer[32768];
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
            do {

                bytes_read = read(fd, buffer, contentLength);
                write(writeFD, buffer, bytes_read);
                memset(buffer, 0, sizeof(buffer));
                contentLength -= bytes_read;
                if(contentLength == 0) { 
                    if(!fileCreated) { write(fd, replyOK, strlen(replyOK)); }
                    else { write(fd, replyCreateOK, strlen(replyCreateOK)); }
                }

            } while (contentLength > 0);
    }
    close(writeFD);
}

void replyGET(std::vector<char*> string, int fd) {
    char* trimmedFileName = strtok(string[1], "/");
    int validName = verifyName(trimmedFileName, fd);
    char errorNotFound[] = "HTTP/1.1 404 Not Found\r\n";
    char responseOK[] = "HTTP/1.1 200 OK\r\n";
    char errorForbidden[] = "HTTP/1.1 403 Forbidden\r\n";

    int readFD = open(trimmedFileName, O_RDWR);

    if(validName == 0) {
        close(readFD);
        return;
    }

    switch(errno) {
        case 2:
            write(fd, errorNotFound, sizeof(errorNotFound));
            close(readFD);
            return;
        case 13:
            write(fd, errorForbidden, strlen(errorForbidden));
            close(readFD);
            return;
    }

    //Method to get file size from file descriptor from here: https://stackoverflow.com/questions/6537436/how-do-you-get-file-size-by-fd
    int fileSize = lseek(readFD, 0, SEEK_END);
    lseek(readFD, 0, SEEK_SET);

    std::string Clength = "Content-Length: " + std::to_string(fileSize) + "\r\n\n";
    write(fd, Clength.c_str(), strlen(Clength.c_str()));

    char buffer[32768];
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
    if(argc == 3) {
        PORT_NUMBER = atoi(argv[2]);
    }

    sockaddr_in address = setHostByName(argv[1], PORT_NUMBER);
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
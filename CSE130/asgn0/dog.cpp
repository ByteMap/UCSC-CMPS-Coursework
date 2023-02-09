#include <sys/types.h>
#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <err.h>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>

using namespace std;

int main(int argc, char** argv) {
	//First check for Case C in the DESIGN.pdf where no argument(s) is given after executing the program
	//If no arguments is given, statically allocate 32KiB into a buffer, and loop the reading of the standard input
	//At the same time, continue to write out the inputs read from the buffer to standard output
	//Then flush the buffer for the next read() to happen
	//
	//Watch out if EOF is inputted (CTRL+D or bytes_read = 0):
	//If EOF inputted, return 0 and exit the program.
	if(argc == 1) {
		char* buffer[1];
		for (ssize_t bytes_read = read(0, buffer, 100); bytes_read > 0; bytes_read = read(0, buffer, 100)) {
			if(bytes_read == 0) {
				return 0;
			}
			write(1, buffer, bytes_read);
			memset(buffer, 0, sizeof(buffer));
		}
		return 0;
	}
	//Case A and Case B:
	//Iterate over the arguments. Set arg to be the first argument after the executable name (dog)
	//While the arg is not pointing to the null pointer (argv[argc]), increment arg (read the next argument)
	//
	//If the argument(s) contain '-', read into standard input and look out for the EOF character. If 0 bits are read,
	//aka EOF character, break out of the loop
	//
	//Continue opening the argument files and If there is an error in with opening a file (-1 is returned), then warn and skip file
	//Else continue reading the file into the buffer, write out the buffer into standard output, flush the buffer, and repeat until EOF
	for(char** arg = argv+1; *arg != argv[argc]; ++arg) {
		char* buffer[32768];
		if(strcmp(*arg, "-") == 0) {
			for (ssize_t bytes_read = read(0, buffer, sizeof(buffer)); bytes_read > 0; bytes_read = read(0, buffer, sizeof(buffer))) {
				if(bytes_read == 0) {
					break;
				}
			write(1, buffer, bytes_read);
			memset(buffer, 0, sizeof(buffer));
			}
		}

		else {
			int fd = open(*arg, O_RDWR);
			if(fd == -1) {
				warn("%s", *arg);
				//break;
			}
			else {
				for (ssize_t bytes_read = read(fd, buffer, sizeof(buffer)); bytes_read > 0; bytes_read = read(fd, buffer, sizeof(buffer))) {
					write(1, buffer, bytes_read);
					memset(buffer, 0, sizeof(buffer));
				}
			}
			close(fd);
		}
	}
	return 0;
}
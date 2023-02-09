//
//  main.c
//  queueClient
//
//  Created by Andy Chen on 11/28/17.
//  Copyright © 2017 Andy Chen. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "queue.h"

int main(int argc, const char * argv[]) {
    FILE* infile = fopen(argv[1], "r");
    FILE* outfile = fopen(argv[2], "w");
    char string[100];
    
    if(argc != 3) {
        fprintf(outfile, "Usage: %s <input file> <output file>\n", argv[0]);
        exit(EXIT_FAILURE);
    }
    if(infile == NULL) {
        fprintf(outfile, "Unable to read from file %s\n", argv[1]);
        exit(EXIT_FAILURE);
    }
    if(outfile == NULL) {
        fprintf(outfile, "Unable to write to file %s\n", argv[2]);
        exit(EXIT_FAILURE);
    }
    
    while(fgets(string, 100, infile) != NULL) {
        char *command = strtok(string, " ");
        if(*command == 'E') {
            int number = atoi(strtok(NULL, " "));
            enqueue(number);
            fprintf(outfile, "Enqueue %d\n", number);
        }
        else if(*command == 'P') {
            print(outfile);
        }
        else if(*command == 'D') {
            dequeue(outfile);
        }
    }
    freeAll();
    fclose(infile);
    fclose(outfile);
    return 0;
}

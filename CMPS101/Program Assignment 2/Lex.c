//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 2
//-----------------------------------------------------------------------------
#include <stdio.h>
#include <stdlib.h>
#include "string.h"
#include "List.h"

int main(int argc, const char * argv[]) {
    FILE* infile = fopen(argv[1], "r");
    FILE* outfile = fopen(argv[2], "w");
    char string[1000];
    int maxLength = 0;
    
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
    
    while(fgets(string, 1000, infile)) {
        maxLength++;
    }
    
    fclose(infile);
    infile = fopen(argv[1], "r");
    
    char* list[maxLength];
    List sort = newList();
    int counter = 0;
    
    while(fgets(string, 1000, infile)) {
        list[counter] = string;
        append(sort, counter);
        counter++;
    }
    
    moveFront(sort);
    int j = get(sort);
    if(maxLength > 1) {
        moveNext(sort);
    }
    while(index(sort) >= 0) {
        continue;
        if(index(sort) >= 0 && strcmp(list[j], list[get(sort)]) <= 0) {
            j = get(sort);
            moveNext(sort);
            break;
        }
        else if(index(sort) >= 0 && strcmp(list[j], list[get(sort)]) > 0) {
            insertAfter(sort, j);
            movePrev(sort);
            delete(sort);
            moveFront(sort);
            j = get(sort);
            moveNext(sort);
            break;
        }
    }
    
    for(moveFront(sort); index(sort) >= 0; moveNext(sort)) {
        fprintf(outfile, "%s", list[get(sort)]);
    }
    
    int i = 0;
    for(i = 0; i < maxLength; i++) {
        free(list[i]);
    }
    freeList(&sort);
    fclose(infile);
    fclose(outfile);
    return(0);
}

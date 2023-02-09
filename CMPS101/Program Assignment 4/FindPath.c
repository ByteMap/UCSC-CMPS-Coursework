//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 5
//-----------------------------------------------------------------------------

#include "Graph.h"
#include "List.h"
#include "string.h"
#include <stdio.h>
#include <stdlib.h>

int main(int argc, const char * argv[]) {
    FILE* infile = fopen(argv[1], "r");
    FILE* outfile = fopen(argv[2], "w");
    char string[1000];
    List L = newList();
    
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
    
    int size;
    fscanf(infile, "%d", &size);
    Graph G = newGraph(size);
    
    int vertex1;
    int vertex2;
    while(fgets(string, 1000, infile) != NULL) {
        fscanf(infile, "%d %d", &vertex1, &vertex2);
        if(vertex1 == 0 && vertex2 == 0) {
            break;
        }
        addEdge(G, vertex1, vertex2);
    }
    
    printGraph(outfile, G);
    
    while(fgets(string, 1000, infile) != NULL) {
        fscanf(infile, "%d, %d", &vertex1, &vertex2);
        if(vertex1 == 0 && vertex2 == 0) {
            break;
        }
        BFS(G, vertex1);
        fprintf(outfile, "The distance from %d to %d is ", vertex1, vertex2);
        if(getDist(G, vertex2) == INF) {
            fprintf(outfile, "infinity\n");
        }
        else {
            //int distance = getDist(G, vertex2);
            fprintf(outfile, "%d\n", getDist(G, vertex2));
        }
        
        getPath(L, G, vertex2);
        if(front(L) != NIL) {
            fprintf(outfile, "A shortest %d-%d path is: ", vertex1, vertex2);
            printList(outfile, L);
            fprintf(outfile, "\n");
        }
        else if(front(L) == NIL) {
            fprintf(outfile, "No %d-%d path exist\n", vertex1, vertex2);
        }
    }
    freeGraph(&G);
    freeList(&L);
    fclose(infile);
    fclose(outfile);
    return(0);
}



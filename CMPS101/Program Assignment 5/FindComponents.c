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
        addArc(G, vertex1, vertex2);
    }
    
    fprintf(outfile, "Adjacency list representation of G:\n");
    printGraph(outfile, G);
    fprintf(outfile, "\n");
    
    for(int i = 1; i <= size; i++) {
        append(L, i);
    }
    
    int strongComponents = 0;
    DFS(G, L);
    Graph transposeGraph = transpose(G);
    DFS(transposeGraph, L);
    moveFront(L);
    while(index(L) >= 0) {
        if(getParent(transposeGraph, get(L)) == NIL) {
            strongComponents++;
        }
        moveNext(L);
    }
    fprintf(outfile, "G contains %d strongly connected components: \n", strongComponents);
    List strongComponentsList = newList();
    for(int j = 1; j <= strongComponents; j++) {
        fprintf(outfile, "Component %d: ", j);
        moveBack(L);
        while(index(L) >= 0) {
            prepend(strongComponentsList, get(L));
            if(getParent(transposeGraph, get(L)) == NIL) {
                printList(outfile, strongComponentsList);
                clear(strongComponentsList);
                movePrev(L);
                deleteBack(L);
                break;
            }
            movePrev(L);
            deleteBack(L);
        }
        fprintf(outfile, "\n");
    }
    
    freeGraph(&G);
    freeGraph(&transposeGraph);
    freeList(&L);
    fclose(infile);
    fclose(outfile);
    return(0);
}



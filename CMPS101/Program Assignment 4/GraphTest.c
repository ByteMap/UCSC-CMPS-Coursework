//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 4
//-----------------------------------------------------------------------------
#include<stdio.h>
#include<stdlib.h>
#include"Graph.h"
#include"List.h"

int main(int argc, char* argv[]){
    int i, s, max, min, d, n=10;
    List  A = newList();
    List  B = newList();
    Graph G = newGraph(n);
    List L = newList();
    
    addEdge(G, 1, 2);
    addEdge(G, 1, 3);
    addEdge(G, 2, 3);
    addEdge(G, 3, 4);
    addEdge(G, 4, 1);
    BFS(G, 4);
    printf(stdout, "The distance between 4 and 1 is: %d\n", getDist(G, 1);
    printf(stdout, "The path is: \n");
    printList(stdout, L);
    printf(stdout, "\n");
           
    makeNull(G);
    printGraph(stdout, G);
    printf(stdout, "\n");
    
    
    addEdge(G, 1, 2);
    addEdge(G, 1, 3);
    addEdge(G, 2, 3);
    addEdge(G, 3, 4);
    addEdge(G, 4, 1);
    printf(stdout, "The new graph is: \n");
    printGraph(stdout, G);
    printf(stdout, "\n");
    
    addArc(G, 4, 2);
    printf(stdout, "The new graph is: \n");
    printGraph(stdout, G);
    printf(stdout, "\n");
    
    freeGraph(&G);
    freeList(&L);
    
    return(0);
}

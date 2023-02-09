//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 5
//-----------------------------------------------------------------------------

#include "Graph.h"
#include "List.h"
#include <stdio.h>
#include <stdlib.h>

// Exported Type --------------------------------------------------------------

//typedef struct GraphObj* Graph;

typedef struct GraphObj {
    List* neighbors;
    int* color;
    int* parent;
    int* discover;
    int* finish;
    int order; //vertices
    int size; //edges
    int time; //recent source for BFS
}GraphObj;

void Visit(Graph G, List L, int i);

// Constructors-Destructors ---------------------------------------------------

Graph newGraph(int n) {
    Graph graph = malloc(sizeof(GraphObj));
    graph->neighbors = calloc(n+1, sizeof(List));
    graph->color = calloc(n+1, sizeof(int));
    graph->parent = calloc(n+1, sizeof(int));
    graph->discover = calloc(n+1, sizeof(int));
    graph->finish = calloc(n+1, sizeof(int));
    graph->order = n;
    graph->size = 0;
    for(int i = 1; i < n+1; i++) {
        graph->neighbors[i] = newList();
        graph->color[i] = White;
        graph->parent[i] = NIL;
        graph->discover[i] = UNDEF;
        graph->finish[i] = UNDEF;
    }
    return graph;
}

void freeGraph(Graph* pG) {
    if(pG != NULL && *pG != NULL) {
        for(int i = 1; i <= getOrder(*pG); i++) {
            freeList(&(*pG)->neighbors[i]);
        }
        free((*pG)->neighbors);
        free((*pG)->color);
        free((*pG)->discover);
        free((*pG)->parent);
        free((*pG)->finish);
        free(*pG);
        *pG = NULL;
    }
}

// Access functions -----------------------------------------------------------

int getOrder(Graph G) {
    if(G == NULL) {
        printf("Error on getOrder(): Graph G is not defined");
        exit(1);
    }
    return G->order;
}

int getSize(Graph G) {
    if(G == NULL) {
        printf("Error on getSize(): Graph G is not defined");
        exit(1);
    }
    return G->size;
}

int getParent(Graph G, int u) {
    if(G == NULL) {
        printf("Error on getParent(): Graph G is not defined");
        exit(1);
    }
    return G->parent[u];
}

int getDiscover(Graph G, int u) {
    if(G == NULL) {
        printf("Error on getDiscover(): Graph G is not defined");
        exit(1);
    }
    return G->discover[u];
}

int getFinish(Graph G, int u) {
    if(G == NULL) {
        printf("Error on getDiscover(): Graph G is not defined");
        exit(1);
    }
    return G->finish[u];
}

// Manipulation procedures ----------------------------------------------------

void visit(Graph G, List L, int u) {
    G->color[u] = Gray;
    ++G->time;
    G->discover[u] = G->time;
    moveFront(G->neighbors[u]);
    while(index(G->neighbors[u]) >= 0) {
        int y = get(G->neighbors[u]);
        if(G->color[y] == White) {
            G->parent[y] = u;
            visit(G, L, y);
        }
        moveNext(G->neighbors[u]);
    }
    G->color[u] = Black;
    G->time++;
    G->finish[u] = G->time;
    prepend(L, u);
}

void addEdge(Graph G, int u, int v) {
    if(G == NULL) {
        printf("Error on addEdge(): Graph G is not defined");
        exit(1);
    }
    if(u < 1 || u > getOrder(G) || v < 1 || v > getOrder(G)) {
        printf("Error on addEdge: Precondition of 1 < u < getOrder(G) and 1 < v < getOrder(G) not met");
        exit(1);
    }
    List U = G->neighbors[u];
    List V = G->neighbors[v];
    if(length(U) != 0) {
        moveFront(U);
        while(index(U) >= 0 && get(U) != v) {
            if(v < get(U)) {
                insertBefore(U, v);
                break;
            }
            moveNext(U);
        }
        if(index(U) == -1) {
            append(U, v);
        }
    }
    
    else if(length(U) == 0) {
        append(U, v);
    }
    
    if(length(V) != 0) {
        moveFront(V);
        while(index(V) >= 0 && get(V) != u) {
            if(u < get(V)) {
                insertBefore(V, u);
                //G->size++;
                break;
            }
            moveNext(V);
        }
        if(index(V) == -1) {
            append(V, u);
        }
    }
    else if(length(V) == 0) {
        append(V, u);
    }
    G->size++;
}

void addArc(Graph G, int u, int v) {
    if(G == NULL) {
        printf("Error on addArc(): Graph G is not defined");
        exit(1);
    }
    if(u < 1 || u > getOrder(G) || v < 1 || v > getOrder(G)) {
        printf("Error on addEdge: Precondition of 1 < u < getOrder(G) and 1 < v < getOrder(G) not met");
        exit(1);
    }
    List U = G->neighbors[u];
    if(length(U) != 0) {
        moveFront(U);
        while(index(U) >= 0 && get(U) != v) {
            if(v < get(U)) {
                insertBefore(U, v);
                break;
            }
            moveNext(U);
        }
        if(index(U) == -1) {
            append(U, v);
        }
    }
    else if(length(U) == 0) {
        append(U, v);
    }
    G->size++;
}

void DFS(Graph G, List S) {
    if(G == NULL) {
        printf("Error on DFS(): Graph G is not defined");
        exit(1);
    }
    for(int i = 1; i <= getOrder(G); i++) {
        G->color[i] = White;
        G->parent[i] = NIL;
    }
    G->time = 0;
    moveFront(S);
    while(index(S) >= 0) {
        if(G->color[get(S)] == White) {
            visit(G, S, get(S));
        }
        //deleteBack(S);
        moveNext(S);
    }
    for(int i = 1; i <= getOrder(G); i++) {
        deleteBack(S);
    }
}

// Other operations -----------------------------------------------------------

Graph transpose(Graph G) {
    if(G == NULL) {
        printf("Error on addArc(): Graph G is not defined");
        exit(1);
    }
    Graph tGraph = newGraph(getOrder(G));
    for(int i = 1; i <= getOrder(G); i++) {
        List L = G->neighbors[i];
        if(length(L) > 0) {
            moveFront(L);
            while(index(L) >= 0) {
                addArc(tGraph, get(L), i);
                moveNext(L);
            }
        }
    }
    return tGraph;
}

Graph copyGraph(Graph G) {
    if(G == NULL) {
        printf("Error on addArc(): Graph G is not defined");
        exit(1);
    }
    Graph cGraph = newGraph(getOrder(G));
    for(int i = 1; i <= getOrder(G); i++) {
        List L = G->neighbors[i];
        if(length(L) > 0) {
            moveFront(L);
            while(index(L) >= 0) {
                addArc(cGraph, i, get(L));
                moveNext(L);
            }
        }
    }
    return cGraph;
}

void printGraph(FILE* out, Graph G) {
    if(G == NULL || out == NULL) {
        exit(1);
    }
    for(int i = 1; i <= getOrder(G); i++) {
        fprintf(out, "%d: ", i);
        printList(out, G->neighbors[i]);
        fprintf(out, "\n");
    }
}


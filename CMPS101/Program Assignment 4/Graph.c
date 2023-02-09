//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 4
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
    int* distance;
    int order; //vertices
    int size; //edges
    int source; //recent source for BFS
}GraphObj;

// Constructors-Destructors ---------------------------------------------------

Graph newGraph(int n) {
    Graph graph = malloc(sizeof(GraphObj));
    graph->neighbors = calloc(n+1, sizeof(List));
    graph->color = calloc(n+1, sizeof(int));
    graph->parent = calloc(n+1, sizeof(int));
    graph->distance = calloc(n+1, sizeof(int));
    graph->order = n;
    graph->size = 0;
    graph->source = NIL;
    for(int i = 1; i < n+1; i++) {
        graph->neighbors[i] = newList();
        graph->color[i] = White;
        graph->parent[i] = NIL;
        graph->distance[i] = INF;
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
        free((*pG)->distance);
        free((*pG)->parent);
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

int getSource(Graph G) {
    if(G == NULL) {
        printf("Error on getSource(): Graph G is not defined");
        exit(1);
    }
    else {
        return G->source;
    }
}

int getParent(Graph G, int u) {
    if(G == NULL) {
        printf("Error on getParent(): Graph G is not defined");
        exit(1);
    }
    if(getSource(G) == NIL) {
        return NIL;
    }
    else {
        return G->parent[u];
    }
}

int getDist(Graph G, int u) {
    if(G == NULL) {
        printf("Error on getDist(): Graph G is not defined");
        exit(1);
    }
    if(getSource(G) == NIL) {
        return INF;
    }
    else {
        return G->distance[u];
    }
}

void getPath(List L, Graph G, int u) {
    if(G == NULL) {
        printf("Error on getPath(): Graph G is not defined");
        exit(1);
    }
    if(getSource(G) != NIL) {
        if(u >= 1 && u <= getOrder(G)) {
            if(G->source == u) {
                append(L, u);
            }
            else if(G->parent[u] == NIL) {
                append(L, NIL);
            }
            else {
                getPath(L, G, G->parent[u]);
                append(L, u);
            }
        }
        else {
            printf("Condition 1 <= u <= getOrder(G) has not been met for getPath()");
            exit(1);
        }
    }
    else {
        printf("BFS() must be called before getPath()");
        exit(1);
    }
}

// Manipulation procedures ----------------------------------------------------

void makeNull(Graph G) {
    if(G == NULL) {
        printf("Error on makeNull(): Graph G is not defined");
        exit(1);
    }
    for(int i = 1; i <= getOrder(G); i++) {
        clear(G->neighbors[i]);
    }
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

void BFS(Graph G, int s) {
    if(G == NULL) {
        printf("Error on addArc(): Graph G is not defined");
        exit(1);
    }
    int x;
    for(x = 1; x <= getOrder(G); x++) {
        G->color[x] = White;
        G->distance[x] = INF;
        G->parent[x] = NIL;
    }
    G->color[s] = Gray;
    G->distance[s] = 0;
    G->source = s;
    List Q = newList();
    append(Q, s);
    while(length(Q) != 0) {
        x = front(Q);
        deleteFront(Q);
        if(length(G->neighbors[x]) > 0) {
            moveFront(G->neighbors[x]);
            while(index(G->neighbors[x]) >= 0) {
                int y = get(G->neighbors[x]);
                if(G->color[y] == White) {
                    G->color[y] = Gray;
                    G->distance[y] = G->distance[x]+1;
                    G->parent[y] = x;
                    append(Q, y);
                }
                moveNext(G->neighbors[x]);
            }
            G->color[x] = Black;
        }
    }
    freeList(&Q);
}

// Other operations -----------------------------------------------------------

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


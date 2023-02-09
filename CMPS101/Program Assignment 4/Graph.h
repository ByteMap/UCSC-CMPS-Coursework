//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 4
//-----------------------------------------------------------------------------

#ifndef graph_h
#define graph_h
#define INF -1
#define NIL 0
#define White 1
#define Gray 2
#define Black 3

#include <stdio.h>
#include "List.h"

// Exported Type --------------------------------------------------------------

typedef struct GraphObj* Graph;

// Constructors-Destructors ---------------------------------------------------

Graph newGraph(int n);

void freeGraph(Graph* pG);

// Access functions -----------------------------------------------------------

int getOrder(Graph G);

int getSize(Graph G);

int getSource(Graph G);

int getParent(Graph G, int u);

int getDist(Graph G, int u);

void getPath(List L, Graph G, int u);

// Manipulation procedures ----------------------------------------------------

void makeNull(Graph G);

void addEdge(Graph G, int u, int v);

void addArc(Graph G, int u, int v);

void BFS(Graph G, int s);

// Other operations -----------------------------------------------------------

void printGraph(FILE* out, Graph G);

#endif /* Graph_h */

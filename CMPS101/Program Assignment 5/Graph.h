//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 5
//-----------------------------------------------------------------------------

#ifndef graph_h
#define graph_h
#define UNDEF -1
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

int getParent(Graph G, int u);

int getDiscover(Graph G, int u);

int getFinish(Graph G, int u);

// Manipulation procedures ----------------------------------------------------

void addEdge(Graph G, int u, int v);

void addArc(Graph G, int u, int v);

void DFS(Graph G, List S);

// Other operations -----------------------------------------------------------

Graph transpose(Graph G);

Graph copyGraph(Graph G);

void printGraph(FILE* out, Graph G);

#endif /* Graph_h */

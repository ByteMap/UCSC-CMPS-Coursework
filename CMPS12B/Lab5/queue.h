//
//  queue.h
//  
//
//  Created by Andy Chen on 11/26/17.
//
//

#ifndef queue_h
#define queue_h

#include <stdio.h>

struct Node {
    int item;
    struct Node *next;
}*front, *back;

void enqueue(int item);

int dequeue(FILE* out);

int peek(FILE* out);

int isEmpty();

void print(FILE* out);

void freeAll();

#endif /* queue_h */

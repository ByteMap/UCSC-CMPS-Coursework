//
//  queue.c
//  
//
//  Created by Andy Chen on 11/26/17.
//
//

#include "queue.h"
#include <stdio.h>
#include <stdlib.h>

int isEmpty() {
    if(front == NULL) {
        return 1;
    }
    else {
        return 0;
    }
}

void enqueue(int item) {
    struct Node *newItem = (struct Node*)malloc(sizeof(struct Node));
    newItem->item = item;
    if(isEmpty() == 1) {
        front = newItem;
    }
    else {
        newItem->next = NULL;
        back->next = newItem;
    }
    back = newItem;
}

int dequeue(FILE* out) {
    if(isEmpty() == 0) {
        struct Node *temp = front;
        int tempData = temp->item;
        if(front == back) {
            back = NULL;
            front = front->next;
            free(temp);
        }
        else {
            front = front->next;
            free(temp);
        }
        fprintf(out, "Dequeue %d\n", tempData);
        return tempData;
    }
    else {
        fprintf(out, "Dequeue fails\n");
        return 0;
    }
}

int peek(FILE* out) {
    if(isEmpty() == 1) {
        fprintf(out, "Queue is Empty\n");
        return 0;
    }
    else {
        return front->item;
    }
}

void print(FILE* out) {
    fprintf(out, "Queue is ");
    if(isEmpty() == 1){
        fprintf(out, "Empty\n");
        return;
    }
    else {
        struct Node *temp = front;
        while(temp != NULL) {
            int tempData = temp->item;
            fprintf(out, "%d ", tempData);
            temp = temp->next;
        }
        free(temp);
        fprintf(out, "\n");
    }
}

void freeAll() {
    while(front != NULL) {
        struct Node *temp = front;
        printf("%d", temp->item);
        front = front->next;
        free(temp);
    }
}

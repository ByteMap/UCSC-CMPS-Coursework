//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 4
//-----------------------------------------------------------------------------

#include "List.h"
#include <stdio.h>
#include <stdlib.h>

// private NodeObj type
typedef struct NodeObj {
    int data;
    struct NodeObj *next;
    struct NodeObj *prev;
}NodeObj;

// private Node type
typedef NodeObj* Node;

// private ListObj type
typedef struct ListObj {
    Node head;
    Node tail;
    Node curr;
    int length;
    int returnIndex;
} ListObj;

// Constructors-Destructors ---------------------------------------------------

Node newNode(int nodeData) {
    Node N = malloc(sizeof(NodeObj));
    N->next = NULL;
    N->prev = NULL;
    N->data = nodeData;
    return(N);
}

void freeNode(Node * pN) {
    if(pN != NULL && *pN != NULL) {
        free(*pN);
        *pN = NULL;
    }
}

List newList(void){
    List L;
    L = malloc(sizeof(ListObj));
    L->head = NULL;
    L->tail = NULL;
    L->curr = NULL;
    L->length = 0;
    L->returnIndex = -1;
    return(L);
}

void freeList(List* pL) {
    if(pL != NULL && *pL != NULL) {
        while(isEmpty(*pL) == 0) {
            deleteFront(*pL);
        }
        free(*pL);
        *pL = NULL;
    }
}

// Access functions -----------------------------------------------------------

int isEmpty(List L) {
    if(L == NULL) {
        return 1;
    }
    else {
        if(length(L) > 0) {
            return 0;
        }
        else {
            return 1;
        }
    }
}

int length(List L) {
    Node N = NULL;
    L->length = 0;
    for(N = L->head; N != NULL; N = N->next) {
        L->length++;
    }
    return L->length;
}

int index(List L) {
    if(L->curr != NULL) {
        return L->returnIndex;
    }
    else {
        L->returnIndex = -1;
        return L->returnIndex;
    }
}

int front(List L) {
    if(length(L) > 0) {
        return L->head->data;
    }
    else {
        return -1;
    }
}

int back(List L) {
    if(length(L) > 0) {
        return L->tail->data;
    }
    else {
        return -1;
    }
}

int get(List L) {
    if(length(L) > 0 && index(L) >= 0) {
        return L->curr->data;
    }
    else {
        return -1;
    }
}

int equals(List A, List B) {
    if(length(A) != length(B)) {
        return 0;
    }
    else if(length(A) == length(B)) {
        Node N = A->head;
        Node M = B->head;
        while(N != NULL && M != NULL) {
            if(N->data != M->data) {
                return 0;
            }
            N = N->next;
            M = M->next;
        }
    }
    return 1;
}

// Manipulation procedures ----------------------------------------------------

void clear(List L) {
    L->curr = NULL;
    L->head = NULL;
    L->tail = NULL;
    L->returnIndex = -1;
    L->length = 0;
}

void moveFront(List L) {
    if(length(L) > 0) {
        L->curr = L->head;
        L->returnIndex = 0;
    }
}

void moveBack(List L) {
    if(length(L) > 0) {
        L->curr = L->tail;
        L->returnIndex = length(L) - 1;
    }
}

void movePrev(List L) {
    if(L->curr == L->head) {
        L->curr = NULL;
        L->returnIndex = -1;
    }
    else if(L->curr != NULL && L->curr != L->head) {
        L->curr = L->curr->prev;
        L->returnIndex--;
    }
}

void moveNext(List L) {
    if(L->curr == L->tail) {
        L->curr = NULL;
        L->returnIndex = -1;
    }
    else if(L->curr != NULL && L->curr != L->tail) {
        L->curr = L->curr->next;
        L->returnIndex++;
    }
}

void prepend(List L, int data) {
    Node N = newNode(data);
    if(length(L) > 0) {
        L->head->prev = N;
        N->next = L->head;
        L->head = N;
        if(L->curr != NULL) {
            L->returnIndex++;
        }
    }
    else if(length(L) == 0) {
        L->head = N;
        L->tail = L->head;
    }
    L->length++;
}

void append(List L, int data) {
    Node N = newNode(data);
    if(length(L) > 0) {
        L->tail->next = N;
        N->prev = L->tail;
        L->tail = N;
    }
    else if(length(L) == 0) {
        L->head = N;
        L->tail = L->head;
    }
    L->length++;
}

void insertBefore(List L, int data) {
    Node N = newNode(data);
    if(length(L) <= 0) {
        L->head = N;
        L->tail = L->head;
        L->length++;
    }
    if(length(L) > 0 && index(L) >= 0) {
        if(index(L) == 0 || L->curr->prev == NULL) {
            L->head->prev = N;
            N->next = L->head;
            L->head = L->head->prev;
            L->head->prev = NULL;
            L->length++;
            L->returnIndex++;
        }
        else if(L->curr->prev != NULL) {
            Node M = L->curr;
            M->prev->next = N;
            N->prev = M->prev;
            L->curr->prev = N;
            N->next = L->curr;
            L->length++;
            L->returnIndex++;
        }
    }
}

void insertAfter(List L, int data) {
    Node N = newNode(data);
    if(length(L) <= 0) {
        L->head = N;
        L->tail = L->head;
        L->length++;
    }
    if(length(L) > 0 && index(L) >= 0) {
        if(index(L) == 0 || L->curr->next == NULL) {
            L->tail->next = N;
            N->prev = L->tail;
            L->tail = L->tail->next;
            L->tail->next = NULL;
            L->length++;
        }
        else if(L->curr->next != NULL) {
            Node M = L->curr;
            M->next->prev = N;
            N->next = M->next;
            L->curr->next = N;
            N->prev = L->curr;
            L->length++;
        }
    }
}

void deleteFront(List L) {
    Node N = NULL;
    if(length(L) > 0) {
        if(length(L) == 1) {
            N = L->head;
            L->head = NULL;
            L->tail = L->head;
            L->curr = L->tail;
            L->returnIndex = -1;
            L->length--;
            freeNode(&N);
        }
        else if(L->curr == L->head && length(L) > 1) {
            L->curr = NULL;
            N = L->head;
            L->head = L->head->next;
            L->head->prev = NULL;
            freeNode(&N);
            L->returnIndex = -1;
            L->length--;
        }
        else {
            N = L->head;
            L->head = L->head->next;
            L->head->prev = NULL;
            freeNode(&N);
            L->returnIndex--;
            L->length--;
        }
    }
}

void deleteBack(List L) {
    Node N = NULL;
    if(length(L) > 0) {
        if(length(L) == 1) {
            N = L->head;
            L->head = NULL;
            L->tail = L->head;
            L->curr = L->tail;
            L->returnIndex = -1;
            L->length--;
            freeNode(&N);
        }
        else if(L->curr == L->tail && length(L) > 1) {
            L->curr = NULL;
            N = L->tail;
            L->tail = L->tail->prev;
            L->tail->next = NULL;
            freeNode(&N);
            L->returnIndex = -1;
            L->length--;
        }
        else {
            N = L->tail;
            L->tail = L->tail->prev;
            L->tail->next = NULL;
            freeNode(&N);
            L->length--;
        }
    }
}

void delete(List L) {
    if(length(L) > 0 && index(L) >= 0) {
        if(L->curr != L->head && L->curr != L->tail) {
            Node N = NULL;
            N = L->curr;
            L->curr->prev->next = L->curr->next;
            L->curr->next->prev = L->curr->prev;
            L->curr = NULL;
            freeNode(&N);
            L->length--;
            L->returnIndex--;
        }
        else if(L->curr == L->head) {
            deleteFront(L);
        }
        else if(L->curr == L->tail) {
            deleteBack(L);
        }
    }
}

// Other operations -----------------------------------------------------------

void printList(FILE* out, List L) {
    Node N = NULL;
    if(length(L) > 0) {
        for(N = L->head; N != NULL; N = N->next) {
            fprintf(out, "%d ", N->data);
        }
    }
    else {
        fprintf(out, "List is empty");
    }
}

List copyList(List L) {
    List copy = newList();
    Node N = NULL;
    for(N = L->head; N != NULL; N = N->next) {
        append(copy, N->data);
    }
    return copy;
}


//
//  balanced.c
//  balanced
//
//  Created by Andy Chen on 11/7/17.
//
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct stack {
    int items[100];
    int top;
};

struct stack stack;

int isFull() {
    if(stack.top == 99) {
        return 1;
    }
    else {
        return 0;
    }
}

int isEmpty() {
    if(stack.top == -1) {
        return 1;
    }
    else {
        return 0;
    }
    
}

int nextIsEmpty() {
    if((stack.top - 1) == -1)
        return 1;
    else {
        return 0;
    }
}

void push(char item) {
    if(isFull() == 1) {
        printf("Stack is full");
        return;
    }
    else {
        //stack.top = stack.top + 1;
        stack.items[++stack.top] = item;
    }
    return;
}

char pop() {
    char item = '\0';
    if(isEmpty() == 0) {
        item = stack.items[stack.top];
        stack.top = stack.top - 1;
        return item;
    }
    else {
        printf("Stack is Empty");
        return item;
    }
}

char peek() {
    char item = '\0';
    if(isEmpty() == 0) {
        item = stack.items[stack.top];
        return item;
    }
    else {
        //printf("Stack is Empty");
        return item;
    }
}

char peekNext() {
    char item = '\0';
    if(nextIsEmpty() == 0 && isEmpty() == 0) {
        item = stack.items[(stack.top)-1];
        return item;
    }
    else {
        printf("Stack is Empty or Contains Only One Item");
        return item;
    }
}

int notNull() {
    if((stack.top)-1 != -1) {
        return 1;
    }
    else {
        return 0;
    }
}

int main(int argc, const char * argv[]) {
    FILE* in = fopen(argv[1], "r");
    FILE* out = fopen(argv[2], "w");
    char string[100];
    
    if(argc != 3) {
        fprintf(out, "Usage: %s <input file> <output file>\n", argv[0]);
        exit(EXIT_FAILURE);
    }
    if(in == NULL) {
        fprintf(out, "Unable to read from file %s\n", argv[1]);
        exit(EXIT_FAILURE);
    }
    if(out == NULL) {
        fprintf(out, "Unable to write to file %s\n", argv[2]);
        exit(EXIT_FAILURE);
    }
    
    while(fgets(string, 100, in) != NULL) {
        //fprintf(out, "%s\n", string);
        //struct stack stack;
        //fgets(string, 100, in);
        //fprintf(out, "%s", string);
        stack.top = -1;
        for(int i = 0; i < strlen(string); i++) {
            if(string[i] == '(' || string[i] == ')'|| string[i] == '{' || string[i] == '}' || string[i] == '[' || string[i] == ']' || string[i] == '<' || string[i] == '>') {
                push(string[i]);
                //stack.top++;
                //printf(out, "%c", string[i]);
            }
            if((stack.top)-1 != -1 && peek() == ')' && peekNext() == '(') {
                pop();
                //fprintf(out, "%c", pop());
                pop();
                //fprintf(out, "%c", pop());
            }
            if((stack.top)-1 != -1 && peek() == '}' && peekNext() == '{') {
                pop();
                //fprintf(out, "%c", pop());
                pop();
                //fprintf(out, "%c", pop());
            }
            if((stack.top)-1 != -1 && peek() == ']' && peekNext() == '[') {
                pop();
                //fprintf(out, "%c", pop());
                pop();
                //fprintf(out, "%c", pop());
            }
            if((stack.top)-1 != -1 && peek() == '>' && peekNext() == '<') {
                pop();
                //fprintf(out, "%c", pop());
                pop();
                //fprintf(out, "%c", pop());
            }
        }
        if(stack.top == -1) {
            fprintf(out, "Y\n");
        }
        else if(stack.top != -1) {
            fprintf(out, "N\n");
        }
    }
    fclose(in);
    fclose(out);
    return 0;
}

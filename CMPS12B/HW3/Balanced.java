import java.util.*;
import java.io.*;
public class Balanced {
    
    public static void main(String[] args) throws StackException, IOException {
        Scanner input = new Scanner(new File(args[0]));
        PrintWriter out = new PrintWriter(new FileWriter(args[1]));
        while(input.hasNextLine()) {
            Stack stack = new Stack();
            String str = input.nextLine();
            char[] tokens = str.toCharArray();
            for(int i = 0; i < tokens.length; i++) {
                if(tokens[i] == '(' || tokens[i] == ')' || tokens[i] == '[' || tokens[i] == ']' || tokens[i] == '{' || tokens[i] == '}' || tokens[i] == '<' || tokens[i] == '>') {
                    stack.push(tokens[i]);
                    if(stack.notNull() && stack.peek() == ')' && stack.peekNext() == '(') {
                        stack.pop();
                        stack.pop();
                    }
                    else if(stack.notNull() && stack.peek() == '}' && stack.peekNext() == '{') {
                        stack.pop();
                        stack.pop();
                    }
                    else if(stack.notNull() && stack.peek() == ']' && stack.peekNext() == '[') {
                        stack.pop();
                        stack.pop();
                    }
                    else if(stack.notNull() && stack.peek() == '>' && stack.peekNext() == '<') {
                        stack.pop();
                        stack.pop();
                    }
                }
            }		
            if(stack.isEmpty()) {
                out.println("Y");
            }
            else if(!stack.isEmpty()) {
                out.println("N");
            }
        }
        input.close();
        out.close();
    }
}

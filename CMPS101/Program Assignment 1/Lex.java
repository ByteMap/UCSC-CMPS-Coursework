//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 1
//-----------------------------------------------------------------------------

import java.util.*;
import java.io.*;

public class Lex {
    public static void main(String[] args) throws IOException{
        
        Scanner input = new Scanner(new File(args[0]));
        PrintWriter out = new PrintWriter(new FileWriter(args[1]));
        List sort = new List();
        int maxLength = 0;
        String increment;
        String temp;
        
        while(input.hasNextLine()) {
            maxLength++;
            increment = input.nextLine();
        }
        
        input = new Scanner(new File(args[0]));
        String[] list = new String[maxLength];
        int counter = 0;
        
        while(input.hasNextLine()) {
            list[counter] = input.nextLine();
            sort.append(counter);
            counter++;
        }

        sort.moveFront();
        int j = sort.get();
        //int k;
        if(maxLength > 1) {
            sort.moveNext();
        }
            while((sort.index >= 0)) {
                if(list[j].compareTo(list[sort.get()]) <= 0) {
                    j = sort.get();
                    sort.moveNext();
                }
                else if((sort.index >= 0) && list[j].compareTo(list[sort.get()]) > 0) {
                    sort.insertAfter(j);
                    sort.movePrev();
                    sort.delete();
                    sort.moveFront();
                    j = sort.get();
                    sort.moveNext();
            }
        }
        
        
        for(sort.moveFront(); sort.index() >= 0; sort.moveNext()) {
            out.println(list[sort.get()]);
            //System.out.println(list[sort.get()]);
        }
        input.close();
        out.close();
    }
}


import java.io.*;
import java.util.*;

public class DeadBeef {
    
    public static void main(String[] args) throws IOException{
        PrintWriter out = new PrintWriter(new FileWriter("Output.txt"));
        for(int i = 1; i <= 1000; i++) {
            if((i%4 == 0) && (i%9 == 0)) {
                out.println("DEADBEEF");
            }
            else if(i%4 == 0) {
                out.println("DEAD");
            }
            else if(i%9 == 0) {
                out.println("BEEF");
            }
            else {
                out.println(i);
            }
        }
        out.close();
    }

}

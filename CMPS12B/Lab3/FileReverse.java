import java.io.*;
import java.util.*;

public class FileReverse {
    public static void main(String[] args) throws IOException {
        int lineNumber = 0;
        if(args.length < 2) {
         System.out.println("Usage: java -jar FileReverse.jar <input file> <output file>");
         System.exit(1);
         }
        
        Scanner in = new Scanner(new File(args[0]));
        PrintWriter out = new PrintWriter(new FileWriter(args[1]));
        while(in.hasNextLine() ){
            lineNumber++;
            String line = stringReverse(in.nextLine().trim()) + " ";
            String[] token = line.split("\\s+");
            int n = token.length;
            for(int i = 0; i < n; i++) {
                out.println(token[i]);
            }
        }
        in.close();
        out.close();
    }
    public static String stringReverse(String givenString) {
        String[] split = givenString.split("\\s+");
        String revString = "";
        for(int i = 0; i < split.length; i++) {
            for(int j = split[i].length()-1; j >= 0; j--) {
                revString = revString + split[i].charAt(j);
                if(j == 0) {
                    revString = revString + " ";
                }
            }
        }
        
        return revString;
    }
}

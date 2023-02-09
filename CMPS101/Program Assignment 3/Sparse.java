//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 3
//-----------------------------------------------------------------------------
import java.io.*;
import java.util.Scanner;

public class Sparse {
    public static void main(String args[]) throws IOException {
        Scanner input = new Scanner(new File(args[0]));
        PrintWriter out = new PrintWriter(new FileWriter(args[1]));
        if(!(input.hasNextLine())) {
            throw new RuntimeException("Exception On Scanner: File Is Empty");
        }
        else {
            int size = input.nextInt();
            int aNNZ = input.nextInt();
            int bNNZ = input.nextInt();
            input.nextLine();
            Matrix A = new Matrix(size);
            Matrix B = new Matrix(size);
            for(int i = 0; i < aNNZ; i++) {
                int row = input.nextInt();
                int column = input.nextInt();
                double data = input.nextDouble();
                A.changeEntry(row, column, data);
            }
            input.nextLine();
        
            for(int j = 0; j < bNNZ; j++) {
                int row = input.nextInt();
                int column = input.nextInt();
                double data = input.nextDouble();
                B.changeEntry(row, column, data);
               /* if(input.hasNextLine()) {
                    input.nextLine();
                }*/
            }
            out.println("A has " + aNNZ + " non-zero entries:");
            out.println(A);
            out.println("B has " + bNNZ + " non-zero entries:");
            out.println(B);
            out.println("(1.5)*A =");
            out.println(A.scalarMult(1.5));
            out.println("A+B =");
            out.println(A.add(B));
            out.println("A+A =");
            out.println(A.add(A));
            out.println("B-A =");
            out.println(B.sub(A));
            out.println("A-A =");
            out.println(A.sub(A));
            out.println("Transpose(A) =");
            out.println(A.transpose());
            out.println("A*B =");
            out.println(A.mult(B));
            out.println("B*B =");
            out.println(B.mult(B));
            
            input.close();
            out.close();
        }
    }
}

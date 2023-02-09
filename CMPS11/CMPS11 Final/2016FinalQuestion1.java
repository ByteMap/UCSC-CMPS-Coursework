import java.util.Scanner;
import static java.lang.System.out;
class Problem1 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int sz = in.nextInt();
        for (int i = 1; i <= sz+1; i++) {
            for (int j = 1; j < i; j++)
                out.print(j);
            for (int j = 1; j <= sz; j++)
                out.print("X");
            for (int j = i; j <= sz; j++)
                out.print(j);
            out.println();
        }
    }
}
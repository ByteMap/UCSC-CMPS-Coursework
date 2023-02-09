import java.util.*;
class Ordered {
    static final Scanner in = new Scanner(System.in);
    public static void main(String[] args) {
        boolean x = increasing(in.nextInt(), in.nextInt(), in.nextInt());
        System.out.println(x);
    }
    public static boolean increasing(int a, int b, int c) {
        if(a < b && b < c) {
            return true;
        }
        else {
            return false;
        }
    }
}
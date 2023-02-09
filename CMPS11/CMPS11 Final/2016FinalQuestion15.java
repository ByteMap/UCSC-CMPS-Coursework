class ChangeTest {
    public static void main(String[] args) {
        Change b1 = new Change(3, 2, 1); // 3 nickels, 2 dimes, and 1 quarter
        Change b2 = new Change(1, 2, 2); // 1 nickel, 2 dimes, and 2 quarters
        System.out.println(b1);
        System.out.println(b2);
        Change b3 = b1.with(b2);
        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b3);
    }
}
class Change {
    int nickels;
    int dimes;
    int quarters;
    public Change(int n, int d, int q) {
        nickels = n;
        dimes = d;
        quarters = q;
    }
    Change with(Change second) {
        Change another = new Change(n + second.n, d + second.d, q + second.q);
        n = d = q = 0;
        second.n = second.d = second.q = 0;
        return another;
    }
    String toString() {
        if(n == 0 && d == 0 && q == 0) {
            System.out.println("Empty");
        }
        else {
            return n + " nickels, " + d + " dimes, " + q + " quarters, ";
        }
    }
}

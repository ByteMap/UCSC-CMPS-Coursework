import java.util.Scanner;
class PointFinal {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Point p1 = read(in);
        Point p2 = read(in);
        mystery(p1);
        System.out.println("p1 is " + p1);
        System.out.println("p2 is " + p2);
        p2 = p1;
        mystery(p2);
        System.out.println("p1 is " + p1);
        System.out.println("p2 is " + p2);
    }
    static Point read(Scanner in) {
        return new Point(in.nextInt(), in.nextInt());
    }
    static void mystery(Point point) { point.translate(100, 10); }
}
class Point {
    private int x, y;
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    void translate(int dx, int dy) {
        x = x + dx;
        y = y + dy;
    }
    public String toString() { return "(" + x + "," + y + ")"; }
}
public class Point {
    static int x, y;
    Point(int x, int y) {
        this.x = x;
        this.y = y;       
    }
    static void translate(int i, int k) {
            x = x + i;
            y = y + k;
    }
}
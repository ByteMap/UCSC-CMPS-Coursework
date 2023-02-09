class quiz4problem3 {
    public static void main(String args[]) {
        Point p1 = new Point(5, 6);
        Point p2 = new Point(5, 6);
        
        System.out.println(p1.equals(p2)); // prints true
    }
}

class Point {
    int x;
    int y;
    Point(int x, int y) {
    this.x = x;
    this.y = y;
    }
    void translate(int dx, int dy) {
        x = x + dx;
        y = y + dy;
    }
    public String toString() {
        return "(" + x +  ","  + y + ")";
    }
    public boolean equals(Point p) {
        if(p.x == x && p.y == y) {
            return true;
        } else {
            return false;
        }
    }
}
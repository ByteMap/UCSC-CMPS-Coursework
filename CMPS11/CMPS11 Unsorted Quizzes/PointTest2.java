class PointTest2 {
    public static void main(String[] args) {
        Point p1 = new Point(5, 6);
        Point p2 = new Point(7, 8);
        p1.translate(10, 20);
        p2.translate(5, 7);
        System.out.println(p1); // prints (15, 26)
        System.out.println(p2); // prints (12, 15)
    }
}

            
    
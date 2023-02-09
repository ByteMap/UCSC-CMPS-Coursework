class question11 {
    public static void main(String args[]) {
        int[] x = {4, 5, 6};
        int[] y = {1, 2, 3};
        diff(x, y);
    }
    public static int[] diff(int[] x, int[]y) {
        int subtracted = 0;
        int[] array = new int[x.length];
        for(int i = 0; i < x.length; i++) {
                subtracted = x[i] - y[i];
                array[i] = subtracted;
                System.out.println(array[i]);
            
        }
        return array;
    }
}
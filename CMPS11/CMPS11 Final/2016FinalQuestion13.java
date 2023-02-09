class sum {
    public static void main(String args[]) {
        int[] array = {1, 5, 2, 6, 7, 10};
        sums(array);
    }
    static int sums(int[] x) {
        int summation = 0;
        for(int i = 0; i < x.length; i++) {
            summation = summation + x[i];
        }
        System.out.print(summation);
        return summation;
    }
}
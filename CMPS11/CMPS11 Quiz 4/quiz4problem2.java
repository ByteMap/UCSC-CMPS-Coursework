class quiz4 {
    public static void main(String args[]) {
        sums(5);
    }
    public static int[] sums(int n) {
        int[] a = new int[n];
        a[0] = 0;
        for(int i = 1; i < n; i++) {
            a[i] = a[i-1] + i;
        }
        return a;
    }
}
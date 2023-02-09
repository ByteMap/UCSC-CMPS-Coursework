public int[] rangeBuilder(int m, int n) {
    int[] a = new int[n-m+1];  
    a[0] = m;
    for(int i = 1; i < a; i++) {
        a[i] = a[i-1] + 1;
    }
    return a;
}
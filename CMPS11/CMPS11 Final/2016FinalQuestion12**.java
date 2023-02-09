class question12 {
    public static void main(String args[]) {
        int[] array = {1, 5, 6, 0, 2};
        print(array);
    }
    static void print(int[] counts) {
        int counted = 0;
        for(int i = 0; i < counts.length; i++) {
            counted = counts[i];
            System.out.print(i+":");
            for(int j = counts[i]; j > 0; j--) {
                System.out.print("*");
                
            }
            System.out.println();
        }
    }
}
class prints {
    public static void main(String args[]) {
        for(int i = 1; i <= 500; i++) {
            if((i%3) == 0 && (i%5) == 0) {
                System.out.println("fizzbuzz");
            }
            else if((i%3) == 0) {
                System.out.println("fizz");
            }
            else if((i%5) == 0) {
                System.out.println("buzz");
            }
            
            
            System.out.println(i);
        }
    }
}
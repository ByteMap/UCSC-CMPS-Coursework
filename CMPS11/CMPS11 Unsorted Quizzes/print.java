class print {
    public static void main(String[] args) {
        printBoard();
    }
    static void printBoard() {
        System.out.print(" ");
        for(int i = 0; i < 10; i++) {
            System.out.print(i);
        }
        System.out.println("");
        for(int x = 0; x < 10; x++) {
            System.out.print(x);
            for(int k = 0; k < 10; k++) {
                System.out.print(".");
            }
            System.out.println();
            
        }
    }
}

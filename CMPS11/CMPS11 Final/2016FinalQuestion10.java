class question10 {
    public static void main(String args[]) {
        int[][] myArray = fill(4, 5);
    }
    
    public static int[][] fill(int row, int column) {
        int[][] filled = new int[row][column];
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < column; j++) {
                filled[i][j] = j;
                System.out.print(filled[i][j]);
            }
            System.out.println();
        }
        return filled;
    }
}
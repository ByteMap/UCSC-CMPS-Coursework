class Final4 {
    public static void main(String[] args) {
        char[][] data = {{'0','1','2'},{'3','4'},{'5','6','7','8'}};
        for (int i = data.length-1; i >= 0; i--) {
            for (int j = 0; j < data[i].length; j++)
                System.out.print(data[i][j]);
            System.out.println();
        }
    }
}
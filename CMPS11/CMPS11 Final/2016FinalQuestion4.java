class Final4 {
    public static void main(String[] args) {
        char[][] data = {{'0','1','2'},{'3','4'},{'5','6','7','8'}};
        for (int i = 0; i < data.length; i++) {
            for (int j = data[i].length; j >= 0; j--)
                System.out.print(data[i][j]);
            System.out.println();
        }
    }
}
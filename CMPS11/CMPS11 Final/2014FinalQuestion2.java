class Final2 {
    public static void main(String[] args) {
        int[] vector = new int[3];
        int[] answer;
        for (int i = 0; i < vector.length; i++ )
            vector[i] = i + 2;
        answer = mystery(vector);
        print("vector", vector);
        print("answer", answer);
    }
    static void print(String label, int[] array) {
        System.out.print(label+":");
        for (int i = 0; i < array.length; i++)
            System.out.print(array[i] + " ");
        System.out.println();
    }
    static int[] mystery(int[] array) {
        int[] temp = new int[array.length];
        int j = array.length - 1;
        for (int i = 0; i < array.length; i++) {
            temp[i] = array[j];
            array[j] = array[j]*10;
            j--;
        }
        print("temp", temp);
        print("array", array);
        return temp;
    }
}
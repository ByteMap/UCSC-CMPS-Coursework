class Final2 {
    public static void main(String[] args) {
        int[] data = new int[3];
        int[] result;
        for (int i = 0; i < data.length; i++ )
            data[i] = i*i;
        result = mystery(data);
        print("data", data);
        print("result", result);
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
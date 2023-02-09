class Main1 {
    public static void main(String[] args) {
        IntListElement e1 = new IntListElement(10,null);
        e1 = new IntListElement(123, e1);
        e1 = new IntListElement(456, e1);
        mystery(e1); // prints _______
    }
    static void mystery(IntListElement list) {
        if (list != null) {
            mystery(list.next);
            System.out.println(list.data);
        }
    }
}
class IntListElement {
    IntListElement(int value, IntListElement e) {
        data = value;
        next = e;
    }
    IntListElement next;
    int data;
}

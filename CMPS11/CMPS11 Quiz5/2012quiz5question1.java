class Main1 {
    public static void main(String[] args) {
        IntListElement e1 = new IntListElement(10,null);
        e1 = new IntListElement(123, e1);
        e1 = new IntListElement(456, e1);
        System.out.println(e1.data); // prints _______
        System.out.println(e1.next.next.data); // prints _______
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
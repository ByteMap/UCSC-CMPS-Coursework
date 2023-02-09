class IntListElementTest {
    public static void main(String[] args) {
        IntListElement list = null;
        for (int i = 5; i < 10; i++)
            list = new IntListElement(i, list);
        mystery(list);
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
class IntListElementTest {
    public static void main(String[] args) {
        IntListElement list = null;
        for (int i = 5; i < 10; i++)
            list = new IntListElement(i, list);
        times(list, 10);
    }
    /*static void mystery(IntListElement list) {
        if (list != null) {
            mystery(list.next);
            System.out.println(list.data);
        }
    }*/
    public static void times(IntListElement list, int n) {
        
        if(list != null) {
            list.data = list.data * n;
            System.out.println(list.data);
            times(list.next, n);
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
class IntListElementTest {
    public static void main(String args[]) {
        IntListElement list = new IntListElement(10, null);
        list = new IntListElement(2, list);
        list = new IntListElement(7, list);
        print(list, 0);
    }
    static void print(IntListElement list, int n) {
        if (list != null) {
            System.out.println(n);
            print(list.next, n+1);
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
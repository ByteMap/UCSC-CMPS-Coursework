class IntListElementTest{
    public static void main(String args[]) {
        IntListElement list = new IntListElement(4, null);
        list = new IntListElement(5, list);
        list = new IntListElement(10, list);
        sum(list);
    }
    public static int sum(IntListElement e) {
        int summation = 0;
        if(e != null) {
            summation = e.data + sum(e.next);
        }
        System.out.println(summation);
        return summation;
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

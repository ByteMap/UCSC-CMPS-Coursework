class Main1 {
    public static void main(String[] args) {
        IntListElement e1 = new IntListElement(1776,null);
        e1 = new IntListElement(1984, e1);
        e1 = new IntListElement(2001, e1);
        e1 = new IntListElement(2013, e1);
        //System.out.println(e1.data); // prints _______
        //System.out.println(e1.next.next.data); // prints _______
    }
}
class IntListElement {
    IntListElement(int value, IntListElement e) {
        data = value;
        next = e;
    }
    IntListElement next;
    int data;
    
    int get(int n) {
        IntListElement list = this;
        int x = 0;
        for(int i = 0; i < n; i++) {
            x = this.data;
            list = list.next;
        }    
        return x;
    }
}
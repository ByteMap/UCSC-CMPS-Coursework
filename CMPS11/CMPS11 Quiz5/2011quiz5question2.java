class IntListElementTest {
 public static void main(String[] args) {
 IntListElement list = null;
 for (int i = 10; i < 14; i++) {
 list = new IntListElement(i, list);
 }
 while (list != null) {
 System.out.print(list.data + " ");
 list = list.next;
 }
 }
}
class IntListElement {
 IntListElement(int value, IntListElement e) {
 data = value;
 next = e;
 }
 IntListElement next;
 int data;
 public void set(int n, int value) {
     if(n < 0 || n < this.value.length-1) { 
         return;
     }
}
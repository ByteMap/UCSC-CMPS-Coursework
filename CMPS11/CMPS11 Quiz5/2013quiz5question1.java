class IntListElement {
 IntListElement(int value, IntListElement e) {
 data = value;
 next = e;
 }
 IntListElement next;
 int data;
}

IntListElement listA = new IntListElement(2014, null);
IntListElement listA = new IntListElement(2015, null);

listA.next = new IntListElement(2015, null);


IntListElement listA = new IntListElement(2014, new IntListElement(2015, null));
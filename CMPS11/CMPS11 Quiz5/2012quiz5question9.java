class Main1{
    public static void main(String[] args) {
        IntListElement e1 = new IntListElement(1776, null);
        e1 = new IntListElement(1984, e1);
        e1 = new IntListElement(2001, e1);
        e1 = new IntListElement(2013, e1);
        addEmUp(e1);
    }
        public static int addEmUp(IntListElement list) {
        //IntListElement temp = list;
        int sum = 0;
        if(list != null){
            sum = list.data + addEmUp(list.next);
        }
        System.out.println(sum);
        return sum;
    }
}

class IntListElement {
    IntListElement next;
    int data;
    IntListElement(int value, IntListElement e) {
        data = value;
        next = e;
    }
    
}


    
    
    
    /**public static int addEmUp(IntListElement list) {
      int x;
      if(list != null) {
      x = x + list.data;
      addEmUp(list.next);
      }
      return x;
      }
      
    
    public static int addEmUp(IntListElement list) {
        IntListElement temp = list;
        int sum = 0;
        if(temp != null){
            sum = temp.data + addEmUp(temp.next);
        }
        return sum;
    }
    
**/
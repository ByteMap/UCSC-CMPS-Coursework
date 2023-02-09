//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 1
//-----------------------------------------------------------------------------
public class List {
	
	private class Node {
		private int data;
		private Node next;
		private Node prev;
		
		Node(int newData) {
			data = newData;
			next = null;
		}
	}
	
	public Node head;
	public Node curr;
	public Node tail;
	public Node temp;
	public Node temp2;
	public int length = 0;
	public int index = -1;
	
	public List() {
		head = null;
		tail = null;
	}
	
	public int length() {
        length = 0;                                             //By default, length would be set to zero whenever
		for(temp = head; temp != null; temp = temp.next) {      //this function is called. A loop to iterate through
			length++;                                           //the linked list would then be called and per loop,
		}                                                       //length would be incremented by 1 then the length
		return length;                                          //is returned once the loop ends
	}
	
	public int index() {
		if(curr != null) {         //If curr is defined, return the index of the pointer, else
			return index;          //return -1 if it is not undefined.
		}
		else {
			return -1;
		}
	}
	
	public int front() {          //If the list is not empty (size is greater than 0) return the data inside
		if(length() > 0) {        //the head node. If the list is empty, return -1.
			return head.data;
		}
		else {
			return -1;
		}
	}
	
	public int back() {         //If the list is not empty(size is greater than 0) return the data inside the
		if(length() > 0) {      //tail node. If the list is empty, return -1.
			return tail.data;
		}
		else {
			return -1;
		}
	}
	
	public int get() {
		if(length() > 0 && index() >= 0) {      //If the list is not empty and the cursor is defined (has an index)
			return curr.data;                   //return the data inside the pointer (cursor). If either the list is
		}                                       //empty or the pointer is not defined, return -1.
		else {
			return -1;
		}
	}
	
	public boolean equals(List L) {
        if(L.length() != length()) {                    //If the list's length in the parameter is compared with
            return false;                               //the actual length of the current list and their lengths
        }                                               //differ, return false. If their lengths are the same, go
		else if(L.length() == length()) {               //through the second screening where both of their temp nodes
			L.temp = L.head;                            //are set to their respective heads, then a while loop is
			temp = head;                                //activated to go through both of the linked lists from
			while(L.temp != null && temp != null) {     //their heads to tails, and, if the loop detects that if
				if(L.temp.data != temp.data) {          //any of their elements in the same indices differ, return
					return false;                       //false. After the screening, if the function has not returned
                }                                       //false, it means that both lists are equal to one another,
				L.temp = L.temp.next;                   //thus, the function will return true.
				temp = temp.next;
            }
        }
            return true;
    }
	
	public void clear() {
		curr = null;            //Sets the cursor, the head node, and the tail node to null, which, will make the list
        head = null;            //empty. As a result of this, the index will respectively be set to -1 as curr is null
        tail = null;            //and the length will be set to 0 as the list is empty.
        index = -1;
        length = 0;
	}
	
	public void moveFront() {
		if(length() > 0) {      //If the list is not empty, set the cursor to the head node, and as a result, the
			curr = head;        //index of the cursor will be set to 0 to indicate that it is pointing at the head
            index = 0;          //node. If the list is empty, do nothing.
		}
	}
	
	public void moveBack() {        //If the list is not empty, set the cursor to the tail node, and as a result, the
		if(length() > 0) {          //index of the cursor will be set to the max length subtracted by one as that will
            curr = tail;            //always be the index of the tail node. If the list is empty, do nothing.
            index = length() - 1;
		}
	}
	
	public void movePrev() {                        //If the cursor is pointing to the head node, undefine the cursor
        if(curr == head) {                          //and as a result, the index of the cursor will be set to -1 and
            curr = null;                            //the function will end. If the cursor is not pointing to the head
            index = -1;                             //node and the cursor is defined, set the cursor to the element
            return;                                 //behind it, and as a result, the index of the cursor will also be
        }                                           //set back by 1. If the cursor is undefined, do nothing.
		else if(curr != null && curr != head) {
			curr = curr.prev;
			index--;
		}
	}
	
	public void moveNext() {                        //If the cursor is pointing to the tail node, undefine the cursor
        if(curr == tail) {                          //and as a result, the index of the cursor will be set to -1 and
            curr = null;                            //the function will end. If the cursor is not pointing to the tail
            index = -1;                             //node and the cursor is defined, set the cursor to the element
            return;                                 //in front of what it is pointing to, and as a result, the index
        }                                           //of the cursor will also be set forwared by 1. If the cursor
		else if(curr != null && curr != tail) {     //is undefined, do nothing.
			curr = curr.next;
			index++;
		}
		
	}
	
	public void prepend(int data) {     //Create a new node N that holds the new data. If the list is not empty, set
		Node N = new Node(data);        //the element previous of head to N and have N become the new head whilst
		if(length() > 0) {              //next to N would be the old head. Also, the element prior to N or the new head
			head.prev = N;              //will be set to null. In addition, if the cursor is defined, increment the
			N.next = head;              //index of the cursor by 1 as a new element has been added to the list that
			head = head.prev;           //is able to shift everything down. If the list is empty, set the head node
			head.prev = null;           //and tail node to the new node N. At the end of the function, increment the
			if(curr != null) {          //length since a new element has been added.
				index++;
			}
		}
		else if(length() == 0){
            head = N;
            tail = head;
            curr = head;
		}
		length++;
	}
	
	public void append(int data) {      //Create a new node N that holds the new data. If the list is not empty, set
		Node N = new Node(data);        //the element next to the tail to N and have N become the new tail whilst
		if(length() > 0) {              //behind N would be the old tail. Also, the element after N or the new tail
			tail.next = N;              //will be set to null. The cursor index does not need to be incremented by 1
			N.prev = tail;              //as adding an element from the back does not shift things down one. If the
            tail = tail.next;           //list is empty, set the head node and tail node new node N. At the end of
            N.next = null;              //the function, increment the length since a new element has been added.
		}
		else if(length() == 0) {
            head = N;
            tail = head;
            curr = head;
        }
		length++;
    }
	
	public void insertBefore(int data) {
		Node N = new Node(data);
        if(length() <= 0) {
            head = N;
            tail = head;
            length++;
        }
		if(length() > 0 && index() >= 0) {
			if(index() == 0 || curr.prev == null) {
                head.prev = N;
                N.next = head;
                head = head.prev;
                head.prev = null;
                length++;
                index++;
			}
			else if(curr.prev != null) {
                Node tempCurr = curr;
                tempCurr.prev.next = N;
                N.prev = tempCurr.prev;
                curr.prev = N;
                N.next = curr;
                length++;
                index++;
            }
        }
	}
	
	public void insertAfter(int data) {
		Node N = new Node(data);
        if(length() <= 0) {
            head = N;
            tail = head;
            length++;
        }
		if(length() > 0 && index() >= 0) {
			if(index() == 0 || curr.next == null) {
                tail.next = N;
                N.prev = tail;
                tail = tail.next;
                tail.next = null;
                length++;
            }
			else if(curr.next != null){
                Node tempCurr = curr;
                tempCurr.next.prev = N;
                N.next = tempCurr.next;
                curr.next = N;
                N.prev = curr;
                length++;
			}
		}
	}
	
	public void deleteFront() {
		if(length() > 0) {
			if(length() == 1) {
				head = null;
				tail = null;
                curr = null;
				index = -1;
				length--;
				return;
			}
			else if(curr == head && length > 1) {
				curr = null;
                head = head.next;
                head.prev = null;
                length--;
                index = -1;

			}
            else {
                head = head.next;
                head.prev = null;
                index--;
                length--;
            }
		}
	}
	
	public void deleteBack() {
		if(length() > 0) {
			if(length() == 1) {
				head = null; 
                tail = head;
                curr = head;
				index = -1;
				length--;
				return;
			}
			else if(curr == tail && length > 1) {
				curr = null;
                tail = tail.prev;
                tail.next = null;
                length--;
                index = -1;
			}
            else {
                tail = tail.prev;
                tail.next = null;
                length--;
            }
		}
	}
	
	public void delete() {
		if(length > 0 && index >= 0) {
            if(curr != head && curr != tail) {
            curr.prev.next = curr.next;
            curr.next.prev = curr.prev;
            curr = null;
            length--;
			index = -1;
            }
            else if(curr == head) {
                deleteFront();
            }
            else if(curr == tail) {
                deleteBack();
            }
		}
	}
    
    public String toString() {
        String string = new String();
        if(length() > 0) {
            for(temp = head; temp != null; temp = temp.next) {
                string = string + temp.data + " ";
            }
            return string;
        }
        else {
            string = "";
            return string;
        }
    }
    
    public List copy() {
        List copy = new List();
        for(temp = head; temp != null; temp = temp.next) {
            copy.append(temp.data);
        }
        return copy;
    }
}

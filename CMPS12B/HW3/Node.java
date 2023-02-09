class Node {
	private char item;
	private Node next;

	Node(char newItem) {
		item = newItem;
		next = null;
	}

	Node(char newItem, Node nextNode) {
		item = newItem;
		next = nextNode;
	} 
	
	public void setNext(Node nextNode) {
		next = nextNode;
	}
	
	public Node getNext() {
		return next;
	}
	
	public void setItem(char newItem) {
		item = newItem;
	}
	
	public char getItem() {
		return item;
	}
} 

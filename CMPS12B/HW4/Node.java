public class Node {
	private Customer item;
	private Node next;
	
	public Node(Customer newItem) {
		item = newItem;
		next = null;
	}
	
	public Node(Customer newItem, Node nextNode) {
		item = newItem;
		next = nextNode;
	}
	
	public void setItem(Customer newItem) {
		item = newItem;
	}
	
	public Customer getItem() {
		return item;
	}
	
	public void setNext(Node nextNode) {
		next = nextNode;
	}
	
	public Node getNext() {
		return next;
	}

}

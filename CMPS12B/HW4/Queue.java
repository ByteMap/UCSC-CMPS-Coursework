public class Queue implements QueueInterface {
	private Node lastNode;
	
	public Queue() {
		lastNode = null;
	}
	
	public boolean isEmpty() {
		if(lastNode == null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void dequeueAll() {
		lastNode = null;
	}
	
	public void enqueue(Customer newItem) {
		Node newNode = new Node(newItem);
		if(isEmpty()) {
			newNode.setNext(newNode);
		}
		else {
			newNode.setNext(lastNode.getNext());
			lastNode.setNext(newNode);
		}
		lastNode = newNode;
	}
	
	public Customer dequeue() throws QueueException {
		if(!isEmpty()) {
			Node firstNode = lastNode.getNext();
			if(firstNode == lastNode) {
				lastNode = null;
			}
			else {
				lastNode.setNext(firstNode.getNext());
			}
			return firstNode.getItem();
		}
		else {
			throw new QueueException("Queue is Empty");
		}
	}
	
	public Customer peek() throws QueueException {
		if(!isEmpty()) {
			Node firstNode = lastNode.getNext();
			return firstNode.getItem();
		}
		else {
			throw new QueueException("Queue is Empty");
		}
	}
}

public interface QueueInterface {
	public boolean isEmpty();
	
	public void enqueue(Customer newItem) throws QueueException;
	
	public Customer dequeue() throws QueueException;
	
	public void dequeueAll();
	
	public Customer peek() throws QueueException;
}

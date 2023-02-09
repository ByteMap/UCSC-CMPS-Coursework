public interface StackInterface {
	
	public boolean isEmpty();
	
	public void popAll();
	
	public void push(char newItem) throws StackException;
	
	public char pop() throws StackException;
	
	public char peek() throws StackException;
	
}

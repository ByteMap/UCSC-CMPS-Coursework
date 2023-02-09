public class Stack implements StackInterface {
	private Node top;
	
	public Stack() {
		top = null;
	}
	
	public void push(char newObject) {
		top = new Node(newObject, top);
		//System.out.println(top.getItem());
	}
	
	public char pop() throws StackException {
		if(!isEmpty()) {
			Node temp = top;
			top = top.getNext();
			//System.out.println(temp.getItem());
			return temp.getItem();
		}
		else {
			throw new StackException("Stack Exception on " + "pop: stack empty");
		}
	}
	
	public boolean isEmpty() {
		if(top == null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void popAll() {
		top = null;				
	}
	
	public char peek() throws StackException {
		if(!isEmpty()) {
			return top.getItem();
		}
		else {
			throw new StackException("StackException on " + "peek: stack empty");
		}
	}
	
	public char peekNext() throws StackException {
		if(!isEmpty()) {
			return top.getNext().getItem();
		}
		else {
			throw new StackException("StackException on " + "peek: stack empty");
		}
	}
    
    public boolean notNull() {
        if(top.getNext() != null) {
            return true;
        }
        else {
            return false;
        }
    }
}

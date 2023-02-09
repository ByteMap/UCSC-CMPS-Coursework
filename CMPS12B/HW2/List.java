public class List {
	public Node head;
	public Node curr;
	
	public List() {
		head = null;
	}
	
	public void add(Chesspiece c) {
		Node n = new Node(c);
		n.next = head;
		head = n;
	}
	
	public void check() {
		Node curr = head;
		while(curr != null) 
			curr = curr.next;
		}
	
	public boolean find(int x, int y) {
		Node curr = head;
		check();
		while(curr != null) {
			if(curr.piece.column == x && curr.piece.row == y) {
				//System.out.println(curr.piece.column+""+curr.piece.row);
				return true;
			}
			else {
				//System.out.println(curr.piece.column+""+curr.piece.row);
				curr = curr.next;
			}
		}
		return false;
	}
	
	
	public Chesspiece returnFind(int x, int y) {
		Node curr = head;
		while(curr != null) {		
			if(curr.piece.column == x && curr.piece.row == y) {
				return curr.piece;
			}
			else {
				curr = curr.next;
			}
		}
		return null;
	}
	
	public boolean duplicate(Chesspiece c) {
		Node curr = head;
		int counter = 0;
		while(curr != null) {
			if(c.column == curr.piece.column && c.row == curr.piece.row) {
				counter ++;
				curr = curr.next;
			}
			else {
				curr = curr.next;
			}
		}
		if(counter > 1) {
			return true;
		}
		else {
			return false;
		}
	}
}

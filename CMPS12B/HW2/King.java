public class King extends Chesspiece { 
	
	public King(char color, int xPos, int yPos) {
		super(color, xPos, yPos);	
	}
	
	public boolean isAttacking(Chesspiece c) {
		if((column == c.column && row == c.row-1) || (column == c.column && row == c.row+1) || (column == c.column-1 && row == c.row) || (column == c.column+1 && row == c.row) || (Math.abs(row - c.row) == 1 && Math.abs(column - c.column) == 1)) {
		return true;
	}
		else {
			return false;
		}
	}
}

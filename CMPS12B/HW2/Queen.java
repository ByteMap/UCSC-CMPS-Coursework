public class Queen extends Chesspiece {
	
	public Queen(char color, int xPos, int yPos) {
		super(color, xPos, yPos);
	}
	
	public boolean isAttacking(Chesspiece c) {
		if(column == c.column || row == c.row || Math.abs(c.column - column) == Math.abs(c.row - row)) {
			return true;
		}
		else {
			return false;
		}
	}
}
	


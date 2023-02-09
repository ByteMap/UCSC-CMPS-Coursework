public class Bishop extends Chesspiece {
	
	public Bishop(char color, int xPos, int yPos) {
		super(color, xPos, yPos);
	}
	
	public boolean isAttacking(Chesspiece c) {
		if(Math.abs(column-c.column) == Math.abs(row-c.row)) {
			return true;
		}
		else {
			return false;
		}
	}

}

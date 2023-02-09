public class Rook extends Chesspiece {
	
	public Rook(char color, int xPos, int yPos) {
		super(color, xPos, yPos);
	}
	
	public boolean isAttacking(Chesspiece c) {
		if(column == c.column || row == c.row) {
			return true;
		}
		else {
			return false;
		}
	}

}

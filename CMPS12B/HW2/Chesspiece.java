import java.util.*;
import java.io.*;

public class Chesspiece {
	public char pieceColor;
	public int row;
	public int column;
	
	public Chesspiece(char color, int xPos, int yPos) {
		pieceColor = color;
		column = xPos;
		row = yPos;
	}
	
	public boolean color(char color) {
		if(Character.isUpperCase(color)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isAttacking(Chesspiece c) {
		return false;
	}
}

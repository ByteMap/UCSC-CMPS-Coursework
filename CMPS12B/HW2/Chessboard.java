import java.util.*;
import java.io.*;

public class Chessboard {
	public static int boardSize = 8;
	Node curr;
	
	public static void main(String args[]) throws IOException {
        Scanner input = new Scanner(new File(args[0]));
        PrintWriter out = new PrintWriter(new FileWriter(args[1]));
		while(input.hasNextLine()) {
			List list = new List();
			int counter = 0;
			int counter2 = 0;
			int counter3 = 0;
			int whiteKing = 0;
			int blackKing = 0;
			String in = input.nextLine();
			in = in.replaceAll("\\s+", "");
			String[] split = in.split(":");
			for(int i = 0; i < split[1].length(); i = i + 3) {
				Chesspiece c;
				char color = split[1].charAt(i);
				int xPos = Character.digit(split[1].charAt(i+1), 10);
				int yPos = Character.digit(split[1].charAt(i+2), 10);
				if(color == 'k' || color == 'K') {
					c = new King(color, xPos, yPos);
					list.add(c);
					if(c.color(color) == false) {
						blackKing++;
					}
					else if(c.color(color) == true) {
						whiteKing++;
					}
				}
				else if(color == 'q' || color == 'Q') {
					c = new Queen(color, xPos, yPos);
					list.add(c);				
				}
				else if(color == 'b' || color == 'B') {
					c = new Bishop(color, xPos, yPos);
					list.add(c);
				}
				else if(color == 'r' || color == 'R') {
					c = new Rook(color, xPos, yPos);
					list.add(c);
			}
			//List.check();
			if(whiteKing != 0 && blackKing != 0 && !list.find(Character.digit(split[0].charAt(0), 10), Character.digit(split[0].charAt(1), 10))) {
				counter2++;
				out.println("-");
			}
			
			list.curr = list.head;
			while(list.curr != null) {
				if(counter2 == 0 && list.duplicate(list.curr.piece)) {
					out.println("Invalid");
					counter2++;
					counter3++;
					}	
				list.curr = list.curr.next;			
			}
				
			if(counter3 == 0 && whiteKing != 0 && blackKing != 0 && list.find(Character.digit(split[0].charAt(0), 10), Character.digit(split[0].charAt(1), 10))) {
				counter2++;
				list.curr = list.head;
				//System.out.print(list.returnFind(Character.digit(split[0].charAt(0), 10), Character.digit(split[0].charAt(1), 10)).pieceColor);
				while(list.curr != null) {
					//System.out.print(list.curr.piece.column + "" + list.curr.piece.row);
					//System.out.println();
					if(list.curr.next != null && list.curr.piece == list.returnFind(Character.digit(split[0].charAt(0), 10), Character.digit(split[0].charAt(1), 10))) {
						list.curr = list.curr.next;			
					}
					if(list.curr.piece.color(list.curr.piece.pieceColor) != list.returnFind(Character.digit(split[0].charAt(0), 10), Character.digit(split[0].charAt(1), 10)).color(list.returnFind(Character.digit(split[0].charAt(0), 10), Character.digit(split[0].charAt(1), 10)).pieceColor) && list.curr.piece.isAttacking(list.returnFind(Character.digit(split[0].charAt(0), 10), Character.digit(split[0].charAt(1), 10)))) {
						counter++;
						out.println(list.returnFind(Character.digit(split[0].charAt(0), 10), Character.digit(split[0].charAt(1), 10)).pieceColor + " y");
					}
					if(list.curr.next == null && counter == 0) {
						out.println(list.returnFind(Character.digit(split[0].charAt(0), 10), Character.digit(split[0].charAt(1), 10)).pieceColor + " n");
					}
					list.curr = list.curr.next;
					}
				}

			}
			if(counter2 == 0 && (blackKing == 0 || whiteKing == 0)) {
				counter2++;
				out.println("Invalid");
			}
		}
        input.close();
        out.close();
	}
    
}


import java.util.*;
import java.io.*;

public class NQueens {
	public static int[] storedXPos = new int[15];
	public static int[] storedYPos = new int[15];
	
	public static void main(String[] args) throws IOException {
        // Reads the input file and prints out the results
        // in the output file named out.txt
        Scanner input = new Scanner(new File(args[0]));
        PrintWriter out = new PrintWriter(new File(args[1]));
        
        while(input.hasNextLine()) {
            // Create a String called list that takes in the first line of the file and
            // split first line, which, would get rid of the spaces, then, use the parseInt() method
            // to extract what is the board size, the given x position, and the given y position
            // of the line of the file.
            String list = input.nextLine();
            String[] splitList = list.split("\\s+");
            int boardSize = Integer.parseInt(splitList[0]);
            int givenXPos = Integer.parseInt(splitList[1]);
            int givenYPos = Integer.parseInt(splitList[2]);
            storedXPos = new int[boardSize];
            storedYPos = new int[boardSize];

            if(PlaceQueens(boardSize, givenXPos, givenYPos, 1, 0)) {
                for(int i = 0; i < boardSize; i++) {
                    out.print(storedXPos[i] + " " + storedYPos[i] + " ");
                }
               out.println();
            }
            else {
                out.println("No solution");
            }
        }
        // closes the input and outputs
        input.close();
        out.close();
	}
	
	public static boolean PlaceQueens(int boardSize, int givenXPos, int givenYPos, int xPos, int arrayPlace) {	
		// Adds the given x and y values to the ith place of the array list where i is equal to the column number that
		// the x and y positions are expected to be at.
		storedXPos[givenXPos-1] = givenXPos;
		storedYPos[givenXPos-1] = givenYPos;
		boolean queenPlaced = false;
		// First Base Case: if xPosition or column position ever recurs past the board, it means that it has successfully found a solution.
		if(xPos > boardSize) {
			return true;	
		} 
		// Second Base Case: if xPosition ever backtracks itself past the beginning of the board, then there will be no solution found.
		 if (xPos < 0) {
			System.out.print("No Solution");
			return true;
		}
        // If the x position ever reaches to the given x position, add the given x position to the array and
        // skip to the next x position.
		else if(xPos == givenXPos) {
			AddQueen(boardSize, givenXPos, givenYPos, arrayPlace);
			queenPlaced = PlaceQueens(boardSize, givenXPos, givenYPos, xPos+1, arrayPlace+1);
			return queenPlaced;
		}
		else {
			// queenPlaced defines if there exists a queen on a certain spot on the chess board. 
			// starts at the first position of the y value
			int yPos = 1;
			while(!queenPlaced && (yPos <= boardSize)) {
				// if there is a point on the board is being attacked by another queen, move down the yPosition until there is a spot
				// that is not being attacked by any queens
				if(Attacked(boardSize, xPos, yPos)) {
					++yPos; //pre-increments the yPosition
					}
				// Once it has found a position for a queen to be on, add that queen to the specific spot.
				else {
					AddQueen(boardSize, xPos, yPos, arrayPlace);
					queenPlaced = PlaceQueens(boardSize, givenXPos, givenYPos, xPos+1, arrayPlace+1);
                    // If there is no spot in the next column, then backtrack
					if(!queenPlaced) {
						RemoveQueen(arrayPlace);
						++yPos;
						}
					}
				
				}
				return queenPlaced;
			} 
		
		}
		
	
	// Adds the queen's x and y positions to the array list
	public static void AddQueen(int boardSize, int xPos, int yPos, int arrayPlace) {
			storedXPos[arrayPlace] = xPos;
			storedYPos[arrayPlace] = yPos;
	}
	
    // Removes the queen's x and y positions from the array list, or technically speaking, set them to a number
    // that will not be used.
	public static void RemoveQueen(int arrayPlace) {
		storedXPos[arrayPlace] = 0;
		storedYPos[arrayPlace] = 0;	
	}
	
    // Checks if the current queen is being attacked by the previous queens, whether it be being attacked from
    // the column, row, or diagonal.
    // If it is being attacked, return true, else, false.
	public static boolean Attacked(int boardSize, int xPos, int yPos) {
		for(int i = 0; i < boardSize; i++) {
				if(xPos == storedXPos[i] || yPos == storedYPos[i]) {
					return true;
			}
				if(Math.abs(xPos - storedXPos[i]) == Math.abs(yPos - storedYPos[i])) {
					return true;
				}
		}
		return false;
	}
}

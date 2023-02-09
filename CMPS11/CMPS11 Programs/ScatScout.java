/**
 * A program to play Scat Scout...
 */
import static java.lang.System.*; // so you can write out.println() instead of System.out.println()
import java.util.*;

class ScatScout {
    static final int boardSize = 10;
    static final Random rand = new Random();
    static final boolean SCAT = true;
    static final boolean CLEAR = false;
    
    public static void main(String[] args) {
        Scanner input = new Scanner(in);
        boolean[][] board = new boolean[boardSize][boardSize]; // scat or no scat
        boolean[][] exposed = new boolean[boardSize][boardSize]; // showing or hidden
        int[][] counts = new int[boardSize][boardSize]; // number of neighbors with scat
        
        if (args.length > 0) {
            // expect the only argument if any to be a number used to seed the random number generator for testing
            rand.setSeed(Integer.parseInt(args[0]));
        } 
        //initializes the board
        initializeBoard(board);
        neighborCount(counts, board);   
        while(unexposedSpaces(board) != scat(board, exposed)) {
            //prints the board.
            printBoard(board , counts, exposed);
            expose(input.nextInt(), input.nextInt(), board, exposed, counts); 
        }
        
        // play the game until the user either steps in some or no non-scat cells have been exposed
        //    read the move coordinates
        //    see if the stepped in it
        //    if not expose 1 or more cells
        //    print the updated board
        // print an appropriate win/lose message
    }
    
    /**
     * Expose the specified location. In addition, if the location has a count of zero (no neighbors contain scat),
     * recursively expose all of the neighbors of the specified location.
     * @param r - the row number of the location to expose
     * @param c - the column number of the location to expose
     * @param board - the array indicating where the scat is located
     * @param exposed - the array indicating which locations have been exposed
     * @param counts - the array of counts of neighbors with scat including the location itself if it contains scat
     */
    static void expose(int r, int c, boolean[][] board, boolean[][] exposed, int[][] counts) {
        if (exposed[r][c]) return; // nothing to do
        // expose any neighbors that have zero counts
        exposed[r][c] = true;
        if (counts[r][c] > 0) return;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int x = r+i;
                int y = c+j;
                if (!(i==0 && j==0) && x >= 0 && x < board.length && y >= 0 && y < board[x].length) {
                    if (counts[x][y] == 0) {
                        expose(x, y, board, exposed, counts);
                    }
                    else {
                        exposed[x][y] = true; // just expose the boarder region - no recursive call
                    }
                }
            }
        }
    }
    // the code to print out the board
    static void printBoard(boolean[][] board, int[][] counts, boolean[][] exposed) {
        System.out.print(" ");
        for(int i = 0; i < 10; i++) {
            System.out.print(i);
        }
        System.out.println("");
        for(int x = 0; x < 10; x++) {
            System.out.print(x);
            for(int k = 0; k < 10; k++) {
                if(!exposed[x][k]) {
                    System.out.print(".");
                }
                else if(board[x][k]) {
                    System.out.print("*");
                }
                else {
                    System.out.print(counts[x][k]);
                }           
            }
            System.out.println();     
        }   
        result(board, counts, exposed);
    }
    
    static void initializeBoard(boolean[][] board){
        for(int i=0; i < boardSize; i++){ 
            for(int j=0; j < boardSize; j++){
                if(rand.nextBoolean()){
                    board[i][j] = SCAT;        
                }
                else{
                    board[i][j] = CLEAR; 
                }
            }
        }
    }
    
    // compute the number of scat filled neighbors and store that info in counts     
    static void neighborCount(int[][] counts, boolean[][] board) {  
        for(int i = 0; i < boardSize; i++) {
            for(int j = 0; j < boardSize; j++) { 
                for (int k = -1; k <= 1; k++) { 
                    for (int l = -1; l <= 1; l++) { 
                        int x = i + k;
                        int y = j + l;      
                        if (k != 0 && x != 0 && x >= 0 && x < board.length && y >= 0 && y < board[x].length) {
                            if(board[x][y]){
                                counts[i][j]++;
                            }
                        }
                    }
                }
            }
        }
    }
    
    static int unexposedSpaces(boolean[][] exposed) {
        int unexposedTiles = 0;
        for(int i = 0; i < boardSize; i++) {
            for(int j = 0; j < boardSize; j++) {
                if(!exposed[i][j]) {
                    unexposedTiles++;
                }
            }
        }
        return unexposedTiles;
    }
    
    static int scat(boolean[][] board, boolean[][] exposed) {
        int numScat = 0;
        for(int i = 0; i < boardSize; i++) {
            for(int j = 0; j < boardSize; j++) {
                if(board[i][j] && !exposed[i][j]) {
                    numScat++;
                }
            }
        }
        return numScat;
    }
    
    // the code determines the result (win or lose)
    static void result(boolean[][] board, int[][] counts, boolean[][] exposed) {
        int scat = 0;
        int tiles = 0;
        for(int i = 0; i < boardSize; i++) {
            for(int j = 0; j < boardSize; j++) {
                if(board[i][j] && !exposed[i][j]) {
                    scat++;
                }
                if(!exposed[i][j]) {
                    tiles++;
                }
            }
            if(scat == tiles) {
                System.out.print("You Win!");
            }
        }
        for(int i = 0; i < boardSize; i++) {
            for(int j = 0; j < boardSize; j++) {
                if(board[i][j] && exposed[i][j]) {
                    System.out.print("You Lose");
                }
            }
        }
    } 
}
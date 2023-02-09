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
  static int endGame = 0;
  public static void main(String[] args) {
    Scanner input = new Scanner(in);
    boolean[][] board = new boolean[boardSize][boardSize]; // scat or no scat
    boolean[][] exposed = new boolean[boardSize][boardSize]; // showing or hidden
    int[][] counts = new int[boardSize][boardSize]; // number of neighbors with scat
    if (args.length > 0) {
      // expect the only argument if any to be a number used to seed the random number generator for testing
      rand.setSeed(Integer.parseInt(args[0]));
    }

    initializeBoard(board); // initialize the board
    neighborCounts(counts, board); // compute the number of scat filled neighors and store that info in counts
    while(endGame==0){
      expose(input.nextInt(), input.nextInt(), board, exposed, counts); // play the game until the user either steps in some or no non-scat cells have been exposed
                                                                        //    read the move coordinates
      checkEnd(board, exposed, counts);                                 //    see if they stepped in it
                                                                        //    if not expose 1 or more cells
      printBoard(board, exposed, counts);                               //    print the updated board
    }
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
  
  static void checkEnd(boolean[][] board, boolean[][] exposed, int[][] counts){
    for(int x = 0; x < boardSize; x++){
      for(int y = 0; y < boardSize; y++){
        if(board[x][y] && exposed[x][y]){
          endGame=1;
        } 
      }
    }
    int numUncheckedScat = 0;
    int numUncheckedTiles = 0;
    for(int x = 0; x < boardSize; x++){
      for(int y = 0; y < boardSize; y++){
        if(board[x][y] && !exposed[x][y])
          numUncheckedScat++;
        if(!exposed[x][y])
          numUncheckedTiles++;
      }
    }
    if(numUncheckedScat==numUncheckedTiles)
      endGame=2;
  }
  
  static void printBoard(boolean[][] board, boolean[][] exposed, int[][] counts){
    if(endGame!=0){
      if(endGame==1)
        System.out.println("Eeewww! You stepped in it!");
      if(endGame==2)
        System.out.println("Well done!");
      System.out.println("  0123456789 ");
      for(int x = 0; x < boardSize; x++){
        System.out.print(x+" ");
        for(int y = 0; y < boardSize; y++){
          if(board[x][y])
            System.out.print("*");
          else 
            System.out.print(counts[x][y]);
        }
        System.out.println(" "+x);
      }
      System.out.println("  0123456789 ");
    }
    else { 
      System.out.println("  0123456789 ");
      for(int x = 0; x < boardSize; x++){
        System.out.print(x+" ");
        for(int y = 0; y < boardSize; y++){
          if(exposed[x][y])
            System.out.print(counts[x][y]);
          else System.out.print(".");
        }
        System.out.println(" "+x);
      }
      System.out.println("  0123456789 ");
    }
  }
  
  static void initializeBoard(boolean[][] board){
    for(int x = 0; x < boardSize; x++){
      for(int y = 0; y < boardSize; y++){
        board[x][y]=(rand.nextInt(10)>=9);
      }
    }
  }
  
  static void neighborCounts(int[][] counts, boolean[][] board){
    for(int r = 0; r < boardSize; r++){
      for(int c = 0; c < boardSize; c++){ //this double for loop iterates through the entire board
        for (int i = -1; i <= 1; i++) { 
          for (int j = -1; j <= 1; j++) { //this double for loop checks elements around the current r and c (except for AT r and c)
            int x = r+i;
            int y = c+j;
            
            if (!(i==0 && j==0) && x >= 0 && x < board.length && y >= 0 && y < board[x].length) {
              if(board[x][y]){
                counts[r][c]++;
              }
            }
          }
        }
      }
    }
  }
}
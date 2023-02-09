/**
 *  A program to play Scat Scout...  
 */
import static java.lang.System.*; // so you can write out.println() instead of System.out.println()
import java.util.*;

class ScatScout {
  static final int boardSize = 10;
  static final Random rand = new Random();
  static final boolean SCAT = true;
  static final boolean CLEAR = false;
  static boolean gameWin = false;
  static boolean gameLost = false;
  public static void main(String[] args) {
    Scanner input = new Scanner(in);
    boolean[][] board = new boolean[boardSize][boardSize]; // scat or no scat
    boolean[][] exposed = new boolean[boardSize][boardSize]; // showing or hidden
    int[][] counts = new int[boardSize][boardSize]; // number of neighbors with scat
    if (args.length > 0) {
      // expect the only argument if any to be a number used to seed the random number generator for testing
      rand.setSeed(Integer.parseInt(args[0]));
    }
  //initialize the board
    createScat(board); 
     // compute the number of scat filled neighors and store that info in counts
    computeScat(board, counts);
     //keep going until game win or loss
    if(!gameWin&&!gameLost){
    printBoard(board, counts, exposed);
    expose(input.nextInt(), input.nextInt(), board, exposed, counts);
    if (gameLost){
      for (int i = 1; i<= board.length; i++){
        for (int j = 1; j<= board.length; j++){
          if (!board[i][j]){
            exposed[i][j] = true;
          }
        }
      }    
      printBoard(board, counts, exposed);
     out.print("You Lose!");  
     
    }
    gameWin = true; 
    for(int i = 1; i< board.length; i++){
      for(int j = 1; j< board.length; j++){
        if(!exposed[i][j]&&!board[i][j]){
          gameWin=false; 
        }
        }
        }
      if (gameWin){
     out.print("You Win!"); 
      
    }
    }
    input.close();
  }
  public static void printBoard( boolean[][] board, int[][] counts, boolean[][] exposed ){ 
//initialize board
   System.out.println(" 0123456789");
   for(int i=0; i<=boardSize; i++){
     System.out.print(i + " "); 
     for(int j=0; j<=boardSize; j++){
       if (exposed[i][j] == true){
       out.print(counts[i][j]); 
       }
      else{
         out.print("*");
       }
     }
    out.print(" ");
    out.print(i); 
     out.println(" ");
   }
   out.println("0123456789"); 
  }

  public static void createScat(boolean[][] board){

    for(int i=1; i<= board.length-1; i++){
      int x = rand.nextInt(board.length-2);
      int y = rand.nextInt(board.length-2);
      if(x!=0&&y!=0&&board[x][y]==CLEAR){
      board[x][y]=SCAT; 
      }
    }
  }
// store in the counts
  public static void computeScat(boolean[][] board, int[][] counts){ 
    int numRows = counts.length;
    int numCols = counts[0].length;
    for(int i=1; i<=numRows-2; i++){
      for(int j=1; j<=numCols-2; j++){
       
            
              counts[i][j]=counts[i][j]+1; 
            }
          }
        
      
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
            exposed[x][y] = true; 
          }
        }
      }
    }
  }
}
    
     

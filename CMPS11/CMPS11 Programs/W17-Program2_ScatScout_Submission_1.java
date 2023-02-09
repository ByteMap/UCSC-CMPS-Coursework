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
  int scatCount = 0; 
  
  public static void main(String[] args) {
    Scanner input = new Scanner(in);
    boolean[][] board = new boolean[boardSize][boardSize]; // scat or no scat
    boolean[][] exposed = new boolean[boardSize][boardSize]; // showing or hidden
    boolean[][] newExposed = new boolean[boardSize][boardSize]; // showing or hidden
    boolean[][] playBoard = new boolean[boardSize][boardSize]; //new one i needed
    int[][] counts = new int[boardSize][boardSize]; // number of neighbors with scat
    int[][] newCounts = new int[boardSize][boardSize]; // new version of Count
    if (args.length > 0) {
      // expect the only argument if any to be a number used to seed the random number generator for testing
      rand.setSeed(Integer.parseInt(args[0]));
    }
    //   while(input.hasNext()) {
    //     int number=input.nextInt();
    // if(number<=9) {
    out.println("The object is to locate all of the animal droppings (scat) in the yard without stepping in any of it. You are able to step anywhere in the yard, and when you do, youcan smell how much scat is around you but you can't be sure where it is");
    out.println();
    out.println("You indicate where you want to step by entering a row and column number. If you didn't step in any, that cell will be filled with a number indicating how many neighboring cells have scat in them. Also, if no neighboring cells have any scat, then all adjoining cells that are empty, and their immediate neighbors will display the count of neighboring cells that have scat.");
    out.println();
    out.println("Try it!");
    playBoard= boardInitialize(board);
    newCounts=reCount(counts,playBoard);
    newExposed= printBoard(boardSize,exposed);
    playGame(input.nextInt(),input.nextInt(),playBoard, newExposed, newCounts, exposed); 
    
// initialize the board
    // compute the number of scat filled neighors and store that info in counts
    // play the game until the user either steps in some or no non-scat cells have been exposed
    //    read the move coordinates
    //    see if the stepped in it
    //    if not expose 1 or more cells
    //    print the updated board
    // print an appropriate win/lose message
  }
  static boolean[][] printBoard(int size,boolean[][] exposed ){
    out.print("  ");
    for(int i = 0; i< size; i++){
      out.print(i);
    }
    out.println();
    out.println();
    for(int i = 0; i< size; i++){
      out.print(i + " ");
      for(int j = 0; j< size; j++){
        out.print(".");
      }
      out.println(" " +i);
    }
    out.println();
    out.print("  ");
    for(int i = 0; i< size; i++){
      out.print( i);
    }
    out.println();
    for( int i = 0; i< exposed.length; i++){
      for (int j = 0; j < exposed.length; j++){
        exposed[i][j] =false;}}
    return exposed;
  }
  
  static boolean[][] boardInitialize(boolean[][] gameBoard) {
    int counter ;
    for(int i = 0; i<= gameBoard.length-1; i++){
      for (int j = 0; j<= gameBoard.length - 1; j++){
        counter = rand.nextInt(10);
        //out.println(counter);
        if(counter <=1){
          gameBoard[i][j] = SCAT;}
        else {
          gameBoard[i][j] = CLEAR;
        }
      }
    }
    return gameBoard;
  }
  
  static int[][] reCount(int[][] setter,boolean[][] theScat) {
    int count;
    for ( int w = 0; w <setter.length; w++){
      for (int z = 0; z < setter.length; z++){
        count = 0;
        if(w<9) {
          if(theScat[w+1][z] == SCAT){
            count++;
          }}
        if (w >0){
          if(theScat[w-1][z] == SCAT){
            count++;
          }}
        if(z<9){
          if(theScat[w][z+1] == SCAT){
            count++;
          }}
        if(z>0){
          if(theScat[w][z-1] == SCAT){
            count++;  }}
          setter[w][z] = count;
        }}
    return setter;}
  
 
  static void printBoardPlay(int size, int r, int c, boolean[][] board, boolean[][] exposed, int[][] counts){
    out.println(exposed[r][c]);
    //expose(r, c, board, exposed, counts);
    out.print("  ");
    for(int i = 0; i< size; i++){
      out.print(i);
    }
    out.println();
    out.println();
    for(int i = 0; i< size; i++){
      out.print(i + " ");
      expose(r, c, board, exposed, counts);
      for(int j = 0; j< size; j++){
        if (exposed[i][j]){
          out.print(counts[i][j]);
        }else{
          out.print(".");
        }
      }
      out.println(" " + i);
 
    }
    out.println();
    out.print("  ");
    for(int i = 0; i< size; i++){
      out.print(i);
    }
    out.println();
  }
  
  static void playGame(int w, int z, boolean[][] newBoard, boolean[][] see, int[][] near, boolean[][] exposed) {
    Scanner gameLoop = new Scanner(in); 
    //printBoard(boardSize);
    if(newBoard[w][z] ==SCAT){
      out.println(" GAME OVER");}
    else {
      out.println(see[w][z]);
      printBoardPlay(boardSize, w, z, newBoard, see, near);
      //WINNING CONDITION
      boolean win = true;
      for(int i = 0; i < exposed.length; i++){
        for(int j = 0; j < exposed.length; j++){
          if(exposed[i][j] == false && newBoard[i][j] != SCAT){
            win = false;
          }
        }
      }
      if (win == true ){
        out.println("Hey That's Pretty Good, You Win");
        return;
      }
      else{ 
      out.println("Well Done" );
      out.println("Enter two integers (row and column)");
      playGame(gameLoop.nextInt(),gameLoop.nextInt(),newBoard, see, near, exposed);
      //  neighbors(w,z,newBoard,see,near);
      }
    
  }
  }
  //pre define count. set it to 1 if it has scat in it and 0 if it doesnt.
  
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
}
/**
 * A program to play Scat Scout...
 */
import static java.lang.System.*; // so you can write out.println() instead of System.out.println()
import java.util.*;
import java.util.Scanner;

class ScatScout {
  static Scanner input = new Scanner(in);
  
  static boolean Finished = false;
  static boolean Won;
  static boolean Lost;
  
  static final int boardSize = 10;
  static final Random rand = new Random();
  static final boolean SCAT = true;
  static final boolean CLEAR = false;
  public static void main(String[] args) {
    

    boolean[][] board = new boolean[boardSize][boardSize]; // scat or no scat
    boolean[][] exposed = new boolean[boardSize][boardSize]; // showing or hidden
    int[][] counts = new int[boardSize][boardSize]; // number of neighbors with scat
    
    initializeBoard(board);
    drawBoard(exposed, counts);
    neighborcells(board,counts);
    //printNeighborcells(board,counts); //for us to test if the game was working or not
    playMove(board, exposed, counts);

   
    if (args.length > 0) {
      // expect the only argument if any to be a number used to seed the random number generator for testing
      rand.setSeed(Integer.parseInt(args[0]));
    }

    // initialize the board  check!
    // compute the number of scat filled neighors and store that info in counts check!
    // play the game until the user either steps in some or no non-scat cells have been exposed
    //    read the move coordinates
    //    see if the stepped in it
    //    if not expose 1 or more cells
    //    print the updated board
    // print an appropriate win/lose message
  }

  //PlAYING PART
  static void playMove(boolean board[][], boolean exposed[][], int[][] counts){
    out.println("Enter row"); 
    int x = input.nextInt();
    out.println("Enter column");
    int y = input.nextInt();

    expose(x,y,board,exposed,counts); //shows if there is scat around or not
    drawBoard(exposed, counts); //redraws board with the neighbor count of scat
    
    while(!Won && !Lost ){ //allows you to keep entering rows and columns until you win or lose
      
     
        out.println("Enter Row");
        x = input.nextInt();
        out.println("Enter Column");
        y = input.nextInt();
      
    expose(x,y,board,exposed,counts);
    drawBoard(exposed, counts);
    
     if(board[x][y]==SCAT){
       Lost=true;
       expose(x,y,board,exposed,counts);
       printNeighborcells(board,counts);
    }
    
    }
    if(Won){
      out.println("Congrats You Win");
    }
    if(Lost){
      out.println("You Lose");
    }
    
  while(!Finished){
    check(board, exposed, counts); //checks the board to see if there is still scat and if the player finished game
  }
    }
      
  
  

  
  //checks to see if the player has located all scat or not
  static void check(boolean board[][], boolean exposed[][], int[][] counts){
    Finished = true;
    Won = true;
    for(int i = 0; i < boardSize; i++){
      for(int j = 0; j < board[i].length; j++){
        if(board[i][j] == CLEAR){
          if(exposed[i][j] == false){
          Finished = false;      
        }
      }
      else{ 
        if(exposed[i][j]==true){
          Finished = true;
          Won = false;
          return;     
        }
      }
    }
  }
}   
      
       
                    
   // initialize the board
  static void drawBoard(boolean[][] exposed, int[][]counts){   
    //0-9 on top
    out.print(" ");
    for(int i = 0; i < boardSize; i++){
      out.print(i);
    }
    out.println();
    //0-9 left down
    for(int i = 0; i < boardSize; i++){
      out.print(i);
      for(int j = 0; j < boardSize; j++){
        if (exposed[i][j] == false){
        out.print("."); 
        }
        else{ out.print(counts[i][j]); }
      }
      out.println(i);
    }
    out.print(" ");
     for(int i = 0; i < boardSize; i++){
      out.print(i);}
     out.println();
  }
  
  //initialize board so it can contain scat
  static void initializeBoard(boolean board[][]){
       // initializing board
        for (int i = 1; i <= boardSize-1; i++)
          for (int j = 1; j<=boardSize-1; j++){     
               board[i][j] = (Math.random() < .2);
               if(board[i][j]) 
                 board[i][j] = SCAT; 
               else
                 board[i][j] = CLEAR;}
  }
  
  //draw board with scat
  static void printWscat(boolean board[][]){ //not printed
        // print game
        for (int i = 1; i <= boardSize-2; i++) {
            for (int j = 1; j <= boardSize-2; j++)
                if (board[i][j]) 
              System.out.print("* ");
                else             
                  System.out.print(". ");
            System.out.println();
        }
  }
  
  //neighbor counts, shows how many scat is around the point you specified
  static void neighborcells(boolean board[][], int[][] counts){
        for (int i = 1; i <= boardSize-2; i++)
            for (int j = 1; j <= boardSize-2; j++)
                for (int p = i - 1; p <= i + 1; p++)
                    for (int m = j - 1; m <= j + 1; m++)
                        if (board[p][m])
                          counts[i][j]++;

        //counts on the side Left and Right
        int j = 0;  
         for(int s = 1; s<9; s++){
            if(board[s][j+1]) 
              counts[s][j]++;
             if(board[s-1][j]) 
               counts[s][j]++;
              if(board[s-1][j+1]) 
                counts[s][j]++;
               if(board[s+1][j+1]) 
                 counts[s][j]++;
                if(board[s+1][j]) 
                  counts[s][j]++;
            
         }
        int R = 9;
         for(int s = 1; s<9; s++){
           if(board[s-1][R-1]) 
             counts[s][R]++;
            if(board[s-1][R]) 
              counts[s][R]++;
             if(board[s][R-1]) 
               counts[s][R]++;
              if(board[s+1][R-1]) 
                counts[s][R]++;
                if(board[s+1][R]) 
                  counts[s][R]++;        
  }
         //Counts on the side TOp and Bottom
         //TOP
         for(int s = 1; s < 9; s++){
            if(board[j+1][s]) 
              counts[j][s]++;
             if(board[j][s-1]) 
               counts[j][s]++;
              if(board[j+1][s-1]) 
                counts[j][s]++;
               if(board[j+1][s+1]) 
                 counts[j][s]++;
                if(board[j][s+1]) 
                  counts[j][s]++;
         }
         //BOTTOM       
         for( int s = 1; s < 9; s++){
           if(board[R-1][s-1]) 
             counts[R][s]++;
            if(board[R][s-1]) 
              counts[R][s]++;
             if(board[R-1][s]) 
               counts[R][s]++;
              if(board[R-1][s+1]) 
                counts[R][s]++;
                if(board[R][s+1]) 
                  counts[R][s]++;  
                }
         //Corners
         int C = 0;
         //top left
         for(int s = 0; s < 1; s++){
          if(board[C][s+1]) 
            counts[C][s]++;
           if(board[C+1][s+1]) 
             counts[C][s]++;
            if(board[C+1][s]) 
              counts[C][s]++;
         }
         //top right
         for(int s = 9; s < 10; s++){
          if(board[C][s-1]) 
            counts[C][s]++;
           if(board[C+1][s-1]) 
             counts[C][s]++;
            if(board[C+1][s]) 
              counts[C][s]++;
          }
         //bottom left
         for(int s = 9; s < 10; s++){
          if(board[s-1][C]) 
            counts[s][C]++;
           if(board[s-1][C+1]) 
             counts[s][C]++;
            if(board[s][C+1]) 
              counts[s][C]++;
         }
         //bottom right
         int ct = 9;
         for(int s = 9; s < 10; s++){
          if(board[s-1][ct]) 
            counts[s][ct]++;
           if(board[s-1][ct-1]) 
             counts[s][ct]++;
            if(board[s][ct-1]) 
              counts[s][ct]++;
         }
  }
        
 //for us to test if the game worked
  static void printNeighborcells(boolean board[][], int[][]counts){ //not printed
        System.out.println();
        for (int i = 0; i <= boardSize-1; i++) {
            for (int j = 0; j <= boardSize-1; j++) {
                if (board[i][j]) 
                  System.out.print("* ");
                else             
                  System.out.print(counts[i][j] + " ");
            }
            System.out.println();
        }
  }

  
  // play the game until the user either steps in some or no non-scat cells have been exposed
 
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
/**
 * Sample object class for stuff that could be in a room.
 */
public class Thing {
  private String name;
  private static boolean inPossession=false;

  public Thing(String name) { 
    this.name = name;
  }
  
  public String toString() {
    return name;
  }
  
  String name() {
    return name;
  }
  
  boolean canBeCarried() {
    return true;
  }
  
  //Function that will be used to check if the player has this thing in their inventory
  public static boolean hasThisThing(){
      return inPossession;
  }

  //Added this function, changes the inPossession boolean to the opposite value
  public boolean changeInPossession() {
      if(inPossession) {
          inPossession=false;
      } else {
          inPossession=true;
      }
      return inPossession;
  }
}
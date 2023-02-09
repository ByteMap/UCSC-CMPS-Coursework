/**
 * Sample object class for stuff that could be in a room.
 */
public class Thing {
  private String name;
  
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
  
}


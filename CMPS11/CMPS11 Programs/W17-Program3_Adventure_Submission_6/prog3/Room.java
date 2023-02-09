import java.util.ArrayList;

/**
 * A room in an adventure game. Rooms can contain things and each room can be connected to
 * up to four other rooms via passageways to the north, south, east, and west.
 */
public class Room {
  private Room north, south, east, west;
  private ArrayList<Thing> stuff = new ArrayList<Thing>();
  private String name;
  
  public Room(String name) { 
    this.name = name;
  }
  
  public String toString() {
    return name;
  }
  
  /**
   * @return String of all of the things in this room, one item per line.
   */
  String whatStuff() {
    StringBuilder list = new StringBuilder();
    for (Thing thing : stuff) {
      list.append(thing.toString() + "\n");
    }
    return list.toString();
  }
  /**
   * Attempt to remove (and return) an item from this room.
   * @param name of thing to try and remove
   * @return reference to the thing removed or null if the thing isn't in this room.
   */
  Thing remove(String name) {
    for (int i = 0; i < stuff.size(); i++ ) {
      Thing thing = stuff.get(i);
      if (thing.name().equals(name)) {
        stuff.remove(thing);
        return thing;
      }
    }
    return null; // might want to throw an exception instead
  }

  
  /**
   * Allow the player to get a reference to a thing in the room in order to possibly interact with the thing.
   * @return thing if this room contains the named item.or null otherwise. The thing is NOT removed from the room.
   */
  Thing get(String name) {
    for (Thing thing : stuff) {
      if (thing.name().equals(name)) {
        return thing;
      }
    }
    return null;
  }
  
  /**
   * See if something is in the room.
   * @return true if this room cantains the named item.
   */
  boolean contains(String name) {
    return get(name) != null;
  }
  
  /**
   * Add another thing to this room. There is no limit to how many things can be in a room.
   */
  void add(Thing thing) {
    stuff.add(thing);
  }
  
  // Accessor methods to find connected rooms.
  Room north() {
    return north;
  }
  
  Room south() {
    return south;
  }
  
  Room west() {
    return west;
  }
  
  Room east() {
    return east;
  }
  
  // mutator methods to make room connections
  void connectNorth(Room room) {
    north = room;
  }
  
  void connectSouth(Room room) {
    south = room;
  }
  
  void connectEast(Room room) {
    east = room;
  }
  
  void connectWest(Room room) {
    west = room;
  }
  
}

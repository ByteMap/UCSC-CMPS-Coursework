import java.util.ArrayList;
/**
 * Representation of a Player moving around in an adventure game.
 * The player "knows" where s/he is and can carry items in a pack.
 */
public class Player {
  private ArrayList<Thing> pack = new ArrayList<Thing>();
  private Room location;
  private static final int MAX_THINGS = 3;
  
  public String toString() {
    StringBuffer result = new StringBuffer();
    for (Thing thing : pack) {
      result.append(thing.toString() + "\n");
    }
    result.append("Available capacity = " + (MAX_THINGS - pack.size()));
    return result.toString();
  }
  
  /**
   * Move this player to the specified room as long as the room is not null.
   * @param room to move to
   * @return true if the move was successful, false otherwise
   */
  boolean moveTo(Room room) {
    if (room != null) {
      location = room;
      return true;
    }
    else {
      return false;
    }
  }
  /**
   * @return the room the player is currently in.
   */
  Room getLocation() {
    return location;
  }
  
  /**
   * @param thing the player would like to add to the pack
   * @return true if the player can carry thing, false otherwise
   */
  boolean canCarry(Thing thing) {
    return (pack.size() < MAX_THINGS);
  }
  
  /**
   * Try and drop an item.
   * @param name of thing to drop
   * @return true if the player successfully dropped the thing, false otherwise
   */
  boolean drop(String name) {
    for (int i = 0; i < pack.size(); i++) {
      if (pack.get(i).name().equals(name)) {
        Thing item = pack.remove(i);
        location.add(item);
        return true;
      }
    }
    return false;
  }
  
  /**
   * Try and pickup an item.
   * @param name of the thing to pickup.
   * @return the Thing if it was successfully picked up, otherwise null
   */
  Thing pickup(String name) {
    if (location.contains(name)) {
      Thing thing = location.remove(name);
      if (canCarry(thing)) {
          if(name.equals("fire")){
              Fire.changeBurn();
          }
          if(name.equals("dragon")) {
              Dragon.changeMood();
          }
        pack.add(thing); // got it
        return thing;
      }
      else {
        location.add(thing); // put it back. That was too much.
        return null;
      }
    }
    else {
      return null; // it wasn't there to be picked up
    }
  }
  
  Thing get(String name) {
    for (Thing thing : pack) {
      if (thing.name().equals(name)) {
        return thing;
      }
    }
    return null;
  }
}

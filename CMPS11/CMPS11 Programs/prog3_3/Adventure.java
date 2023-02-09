import java.util.*;
import java.io.*;
/**
 * A basic adventure game where a player moves from room to room interacting with things in the rooms.
 * The initial world is created from a text file named world.txt or an alternative input file can be specified
 * on the command line. See the specification for the format of the input text file.
 */
class Adventure {
  public static void main(String[] args) throws IOException {
    Player player = new Player();
    
    Room entryWay;
    Random rand;
    if (args.length == 2) {
      // use a seed if provided for testing
      rand = new Random(Integer.parseInt(args[1]));
    }
    else {
      rand = new Random(); // let it be really random
    }
    if (args.length >= 1) {
      entryWay = randomWorld(args[0], rand);
    }
    else {
      entryWay = randomWorld("world.txt", rand);
    }
    
    player.moveTo(entryWay);
    
    printInstructions();
    
    play(player);
    
  }
  
  /**
   * Read the input file for the list of rooms and their content, then connect them randomly.
   * @param fileName - the name of the world specification file.
   */
  static Room randomWorld(String fileName, Random rand) throws IOException {
    Scanner fileIn = new Scanner(new File(fileName));
    ArrayList<Room> rooms = new ArrayList<Room>();
    
    // first create the rooms and their content - first room is the entrance room
    Room entrance = new Room(fileIn.nextLine());
    addStuff(entrance, fileIn);
    rooms.add(entrance);
    
    // add more rooms
    while (fileIn.hasNextLine()) {
      String name = fileIn.nextLine();
      if (name.equals("*****")) break; // YUK!
      else {
        Room room = new Room(name);
        addStuff(room, fileIn);
        rooms.add(room);   
      }
    }
    
    // now connect the rooms randomly
    for (Room room : rooms) {
      room.connectNorth(rooms.get(rand.nextInt(rooms.size())));
      room.connectEast(rooms.get(rand.nextInt(rooms.size())));
      room.connectSouth(rooms.get(rand.nextInt(rooms.size())));
      room.connectWest(rooms.get(rand.nextInt(rooms.size())));
    }   
    return entrance;    
  }
  
  /**
   * Assumes there is always a blank line after the last line of stuff being added.
   * @param room - the room to fill.
   * @param in - a Scanner reading from the world specification file, ready to read the next room.
   */
  static void addStuff(Room room, Scanner in) {
    String name = in.nextLine();
    while (name.length() > 0) {
      if(name.startsWith("mushroom")) {
        room.add(new Mushroom(name));
      }
      else if(name.startsWith("Charlie!")) {
        room.add(new Charlie(name));
      }
      else if(name.startsWith("frog")) {
        room.add(new Frog(name));
      }
      else {
        room.add(new Thing(name));
      }
      name = in.nextLine();
    }
  }
  
  
  static void printInstructions() {
    System.out.println("Welcome to Charlie's Mushroom Adventure!\n" +
                       "In this game, you need to navigate your way around four randomly connected rooms,\n" + 
                       "collect all three mushrooms and return them to Charlie in time for him to make soup.\n" +
                       "Be careful, the rooms do NOT obey normal rules. \n" +
                       "You might go out the north door of room 1 to enter room 2, \n" +
                       "but going out the south door of room 2 will not necessarily take you back into room 1.\n" +
                       "Beware! There is also a terrifying frog guarding one of the mushrooms. \n" +
                       "Defeat the frog by typing 'slay', but make sure you have a sword first!\n" +
                       "You can move around by typing north, south, east, or west.\n" +
                       "You can see what is in the room you are in by typing look.\n" +
                       "You can pick things up by typing 'pickup thing' where thing is what you see in the room.\n"+
                       "You can drop things you are carrying by typing 'drop thing' where thing names something you have.\n"+
                       "You can smell mushrooms by typing 'smell'. \n" +
                       "You can tickle Charlie if you're in the same room as him by typing 'tickle'. \n" + 
                       "You can see your status by typing status and quit by typing quit.\n");
  }
  
  
  /**
   * The main game loop. When called, the world should have been setup and the Player placed into one of the rooms of the world.
   */
  static void play(Player player) {
    Scanner in = new Scanner(System.in);
    boolean check = false;
    
    while (in.hasNextLine()) {
      String cmd = in.nextLine();
      if (cmd.equals("quit")) {
        System.out.println("Return soon!");
        return;
      }
      else if (cmd.contains("north")) {
        enter(player, player.getLocation().north());      
      }
      else if (cmd.contains("south")) {
        enter(player, player.getLocation().south()); 
      }
      else if (cmd.contains("east")) {
        enter(player, player.getLocation().east()); 
      }
      else if (cmd.contains("west")) {
        enter(player, player.getLocation().west()); 
      }
      else if (cmd.contains("look")) {
        look(player);
      }
      else if (cmd.startsWith("pickup") ) {
        if(cmd.substring(7).equals("mushroom") && !(player.hasSlainFrog(check)) && player.getLocation().contains("frog")) {
          System.out.println("You must kill the frog before you can get the mushroom.");
        }
           else if(cmd.substring(7).equals("mushroom") && player.hasSlainFrog(check)) {
             pickup(player, cmd.substring(7)); //player can only pickup mushroom after slaying frog
           }
           else if(cmd.substring(7).equals("frog"))
             System.out.println("Something ribbit in your pocket. Maybe you should put the fella back.");
           else {
           pickup(player, cmd.substring(7)); // MAGIC NUMBER length of pickup plus a space
           }
      }
      else if (cmd.startsWith("drop")) {
        drop(player, cmd.substring(5)); // MAGIC NUMBER length of drop plus a space
        if(cmd.substring(5).equals("mushroom")) {
          if(player.getLocation().howMany(cmd.substring(5)) == 3 && player.getLocation().contains("Charlie!")){
            System.out.println(player.winningMessage()); //prints winning message
            return; //ends program
          }
        }
      }
      else if(cmd.startsWith("tickle")) {
        if(player.getLocation().contains("Charlie!")) 
         System.out.println(player.tickleCharlie());
        else
          System.out.println("Tickle who? Charlie isn't in here.");
      }
      else if (cmd.startsWith("slay")) {
        if(player.containsItem("sword")){
          System.out.println(player.slay()); //prints "You slain the frog."
          check = true; //becomes true after slaying frog
          player.hasSlainFrog(check);//hasSlainFrog becomes true
          player.getLocation().remove("frog");  //removes frog and replaces with a slain frog
          player.getLocation().add(new Thing("slain frog"));
        }
        else 
          System.out.println("You can't slay without a sword!");
      }
      else if (cmd.contains("status")) {
        System.out.println(player);
      } 
      else if (cmd.contains("smell")) {
        System.out.println(player.smellMushroom());
      }
      else {
        System.out.println("What?");
      }
      }
  }
  
  /**
   * Player attempts to move into the specificed room.
   * This could be teleporting or to a connected room. There is no check for passageway.
   * If the room is null, the move will fail.
   * @param Player - the player trying to move.
   * @param room - the room to move to - could be null
   */
  static void enter(Player player, Room room) {
    if (player.moveTo(room)) {
      System.out.println("You just entered " + player.getLocation());
    }
    else {
      System.out.println("That way appears to be blocked.");
    }
  }
  
  /**
   * Display the contents of what the player sees in the room s/he is currently in.
   * @param player - the player doing the looking
   */
  static void look(Player player) {
    String stuff = player.getLocation().whatStuff();
    if (!stuff.equals("")) {
      System.out.println("You see:\n" + stuff);
    }
    else {
      System.out.println("You see an empty room.");
    }
  }
  
  /**
   * Player attempts to pickup the specified object.
   * @param player - player doing the picking up
   * @param - what to pickup
   */
  static void pickup(Player player, String what) {
    if (player.getLocation().contains(what)) {
      Thing thing = player.pickup(what);
      if (thing != null) {
        System.out.println("You now have " + thing);
      }
      else {
        System.out.println("You can't carry that. You may need to drop something first.");
      }
    }
    else {
      System.out.println("I don't see a " + what);
    }
  }
  
  /**
   * Player attempts to drop the specified object.
   * @param player - player doing the dropping
   * @param - what to drop
   */
  static void drop(Player player, String what) {
    if (player.drop(what)) {
      System.out.println("You dropped " + what);
    }
    else {
      System.out.println("You aren't carrying " + what);
    }
  }
  
  }
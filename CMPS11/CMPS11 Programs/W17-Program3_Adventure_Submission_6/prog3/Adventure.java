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
	    if(name.equals("Mountain Dew")){//nice
	    	room.add(new MountainDew());
	    }
	    else if(name.equals("Mom")){//nice
	    	room.add(new Mom());
	    }
	    else{
	      room.add(new Thing(name));
	    }
	      name = in.nextLine();
    }   
  }
  
  static void printInstructions() {
    System.out.println("Welcome to Yash and Max's adventure of happy fun times.\n" +
    				   "Your goal is to find the drink enough Mountain Dew, to make your mom dissapointed in your life choices.\n"+
                       "You can move around by typing north, south, east, or west.\n" +
                       "You can see what is in the room your are in by typing look.\n" +
                       "You can pick things up by typing 'pickup thing' where thing is what you see in the room.\n"+
                       "You can drop things you are carrying by typing 'drop thing' where thing names someting you have.\n"+
                       "You can see your inventory by typing inventory and quit by typing quit.\n");
  }
  
  /**
   * The main game loop. When called, the world should have been setup and the Player placed into one of the rooms of the world.
   */
  static void play(Player player) {
    @SuppressWarnings("resource")
	Scanner in = new Scanner(System.in);
    
    while (in.hasNextLine()) {
      String cmd = in.nextLine();
      if (cmd.equals("quit")) {
        System.out.println("Good bye.");
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
      else if (cmd.contains("drink")) {
        if(player.contains("Mountain Dew")){
         	MountainDew dew = new MountainDew();
        	System.out.println(dew.Drink());
        	player.destroy("Mountain Dew");
        	Thing emptyBottle = new Thing("Empty Bottle");
        	player.create(emptyBottle);
        }
        else{
        	System.out.println("You thirsty ************  (nothing to drink)");
        	
        }
      }
      else if (cmd.contains("throw")) {
    	  if(player.getLocation().whatStuff().contains("Mom")){
	          if(player.contains("Empty Bottle")){
	          	EmptyBottle emptyBottle = new EmptyBottle();
	          	player.destroy("Empty Bottle");
	          	System.out.println(emptyBottle.Throw());
	          	if(Mom.getHP()==3){
	          		System.out.println("\"How dare you, that **** at me!\"");
	          		Mom.loseHP();
	          	}
	          	else if(Mom.getHP()==2){
	          		System.out.println("\"You better stahp!\"");
	          		Mom.loseHP();
	          	}
	          	else{
	          		System.out.println("\"Don't talk to me or myself ever again!\"\n"
	          						+"Congratulations, you've beaten that horrible monster that kep you from your raids\n"
	          						+ "I guess you can say RAID BOSS DOWN");
	          		return;
	          	}
	          	
	          	
	          }
	          else{
	          	System.out.println("Don't have anything, useful to throw at Mom");
	          	
	          }
    	  }
    	  else{
    		  System.out.println("Why would you even throw anything right now?");
    		  
    	  }
        }
      else if (cmd.startsWith("pickup") ) {
        pickup(player, cmd.substring(7)); // MAGIC NUMBER length of pickup plus a space
        
      }
      else if (cmd.startsWith("drop")) {
        drop(player, cmd.substring(5)); // MAGIC NUMBER lenght of drop plus a space    
      }
      else if (cmd.contains("status")||cmd.contains("inventory")) {
        System.out.println(player);
      }
      else if (cmd.contains("nice")||cmd.contains("Nice")) {
          System.out.println("Nice!");
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
      
      if (thing != null && thing.canBeCarried()) {
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
import java.util.*;
import java.io.*;
import java.awt.Container;
import javax.swing.*;
import static java.awt.BorderLayout.*;
import java.awt.*;
/**
 * A basic adventure game where a player moves from room to room interacting with things in the rooms.
 * The initial world is created from a text file named world.txt or an alternative input file can be specified
 * on the command line. See the specification for the format of the input text file.
 */


// -----------------------DISCLAIMER---------------------------
// FOLLOWING THE GUIDELINES OF "GIVE CREDIT WHEN CREDIT IS DUE"
// THIS PROGRAM INCORPORATES PROGRAM 3: ADVENTURE GAME
// BY ANDY CHEN (ACHEN81@UCSC.EDU) AND
// BY KYLE GLOVER (KEGLOVER@UCSC.EDU)
// NOTE THAT THIS INCORPORATES PROGRAM 3 WITH PERMISSION OF BOTH PARTIES 
class Adventure {
    // static variable to set the text every time a specific button is pressed
    static JTextArea results;
    public static void main(String[] args) throws IOException {
        Player player = new Player();
        buttons(player);
        Room entryWay;
        Random rand;
        if (args.length == 2) {
            // use a seed if provided for testing
            rand = new Random(Integer.parseInt(args[1]));
        }
        else {
            rand = new Random(); // let ist be really random
        }
        if (args.length >= 1) {
            entryWay = randomWorld(args[0], rand);
        }
        else {
            entryWay = randomWorld("world.txt", rand);
        }
        
        player.moveTo(entryWay);       
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
        String name = in.nextLine().trim();
        while (name.length() > 0) {
            if(name.equals("sword")) {
                room.add(new Sword());
            } else if(name.equals("fire")) {
                room.add(new Fire());
            } else {
                room.add(new Thing(name));
            }
            name = in.nextLine();
        }
    }
    
    /**
     * The main game loop. When called, the world should have been setup and the Player placed into one of the rooms of the world.
     */
    
    /**
     * Player attempts to move into the specificed room.
     * This could be teleporting or to a connected room. There is no check for passageway.
     * If the room is null, the move will fail.
     * @param Player - the player trying to move.
     * @param room - the room to move to - could be null
     */
    static void enter(Player player, Room room) {
        if (player.moveTo(room)) {
            results.setText("You just entered " + player.getLocation());
        }
        else {
            results.setText("That way appears to be blocked.");
        }
    }
    
    /**
     * Display the contents of what the player sees in the room s/he is currently in.
     * @param player - the player doing the looking
     */
    static void look(Player player) {
        String stuff = player.getLocation().whatStuff();
        if (!stuff.equals("")) {
            results.setText("You see:\n" + stuff);
        }
        else {
            results.setText("You see an empty room.");
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
                results.setText("You now have " + thing);
            }
            else {
                results.setText("You can't carry that. You may need to drop something first.");
            }
        }
        else {
            results.setText("I don't see a " + what);
        }
    }
    
    /**
     * Player attempts to drop the specified object.
     * @param player - player doing the dropping
     * @param - what to drop
     */
    static void drop(Player player, String what) {
        if (player.drop(what)) {
            results.setText("You dropped " + what);
        }
        else {
            results.setText("You aren't carrying " + what);
        }
    }
    // Method to make the buttons which takes in a player object
    static void buttons(Player player) {
        // create 2 button frames: Directions and Actions
        JFrame frame1 = new JFrame("Directions");
        JFrame frame2 = new JFrame("Actions");
        
        Container pane1 = frame1.getContentPane();
        Container pane2 = frame2.getContentPane();
        
        pane2.setLayout(new GridLayout(2, 3));
        
        // names for each button
        JButton north = new JButton("North");
        JButton south = new JButton("South"); 
        JButton east = new JButton("East");
        JButton west = new JButton("West");
        JButton look = new JButton("Look");
        results = new JTextArea("Welcome to the Super Amazingly Magical Journey.\n" +
                           "You can move around by clicking the buttons 'north', 'south', 'east', or 'west'.\n" +
                           "You can see what is in the room your are in by click the button 'look'.\n" +
                           "You can pick things up by typing clicking the 'pickup' button while typing in what you want to pickup in the text field \n"+
                           "You can drop things you are carrying by clicking the 'drop button while typing in what you want to drop in the text field \n" +
                           "Your goal is to survive and find the sword to slay the dragon. \n" +
                           "You can swing the sword by clicking the 'swing' button. \n" +
                           "Be careful of what you pickup and use in the maze. \n" +
                           "Some things you pickup will not have an affect on the game \n" +               
                           "Have fun!. \n");
        JTextField chooseItem = new JTextField("<Choose the item you wish to pickup/drop here>"); 
        JButton pickup = new JButton("Pickup");
        JButton drop = new JButton("Drop");
        JButton attack = new JButton("Attack");
        JButton quit = new JButton("Quit");
        Actions listener = new Actions(player, chooseItem, results);
        
        // add the buttons and actionListener to have an output when the button is pressed
        pane1.add(north, NORTH);
        north.addActionListener(listener); 
        
        pane1.add(south, SOUTH);
        south.addActionListener(listener);
        
        pane1.add(east, EAST);
        east.addActionListener(listener);
        
        pane1.add(west, WEST);
        west.addActionListener(listener);
        
        pane1.add(results, CENTER);
        
        pane2.add(pickup);
        pickup.addActionListener(listener);
                
        pane2.add(look);
        look.addActionListener(listener);
        
        pane2.add(drop);
        drop.addActionListener(listener);
                
        pane2.add(quit);
        quit.addActionListener(listener);
  
        pane2.add(chooseItem);
        chooseItem.addActionListener(listener);
                
        pane2.add(attack);
        attack.addActionListener(listener);

        
        // make the gui visible so the player can use it
        frame1.pack();
        frame1.setVisible(true);
        frame2.pack();
        frame2.setVisible(true);
    }
    
}
import javax.swing.*;
import java.awt.event.*;
class Actions implements ActionListener {
    // makes a new Adventure object so that it can be used to make the 
    // buttons do the actions that they are assigned with
    Adventure adventure = new Adventure();
    private Player player;
    private JTextField str;
    private JTextArea results;
    // constructor for the class which takes in a player object, JTextField object
    public Actions(Player player, JTextField str, JTextArea results) {
        this.player = player;
        this.str = str;
        this.results = results;
    }
    // method to make the button do the actions for the game
    public void actionPerformed(ActionEvent e) {
        // If the button "North" is pressed
        // You will enter the room north of your current position
        if(e.getActionCommand().equals("North")) {
            adventure.enter(player, player.getLocation().north());         
        }
        // If the button "South" is pressed
        // You will enter the room south of your current position
        else if(e.getActionCommand().equals("South")) {
            adventure.enter(player, player.getLocation().south());     
        }
        // If the button "East" is pressed
        // You will enter the room east of your current position
        else if(e.getActionCommand().equals("East")) {
            adventure.enter(player, player.getLocation().east()); 
        }
        // If the button "West" is pressed
        // You will enter the room west of your current position
        else if(e.getActionCommand().equals("West")) {
            adventure.enter(player, player.getLocation().west()); 
        }
        // If the button "Look" is pressed
        // You will look for any items that may be in the room so that you may pickup or ignore
        else if(e.getActionCommand().equals("Look")) {
            adventure.look(player);
        }
        // Picks up an item that is typed into the JTextField and when the button "pickup" is pressed
        else if(e.getActionCommand().equals("Pickup")) {
            adventure.pickup(player, str.getText()); 
        }
        // Drop an item from the item that is typed in from the JTextField and when the button "drop" is pressed
        else if(e.getActionCommand().equals("Drop")) {
            adventure.drop(player, str.getText()); 
        }
        
        // If the button "Attack" is pressed and you are in the room with the dragon, you will do one of two things:
        // If you have a sword you will kill the dragon wjen you press the button "Attack"
        // If you do not have the sword, the dragon will kill you when you press the button "Attack"
        else if(e.getActionCommand().equals("Attack") && player.getLocation().contains("dragon")) {
            Thing stabby = player.get("sword");
            if(stabby == null) {
                 results.setText("You swing nothing and angered the dragon \n" +
                 "The dragon has breathed fire and you are dead \n" +
                 "You Lose!");
                 // omitted System.exit() for losing situation b/c it would just quit wihtout telling you that you have lost
                 //System.exit(0);
                 } else {
                results.setText("You have slain the dragon \n" +
                                "You have won! \n" +
                                "Congratulations!");
                //omitted System.exit() for winning situation b/c it would just quit wihtout telling you that you have won
                //System.exit(0);
            }
        }
        // If the button "Attack" is pressed and you are in any other rooms but the room with the dragon, you will do one of two things:
        // If you have the sword, you will just swing the sword
        // If you do not have the sword, you will ask yourself 'what are you swinging'
        else if(e.getActionCommand().equals("Attack")) {
            Thing stabby = player.get("sword");
            if(stabby == null){
                results.setText("What were you trying to swing around? Nothing you possess equates to a sharp, pointy, or blunt object.");
            } 
            
            else {
                Sword sword = (Sword) stabby;
                sword.swing(player);
            }
        }
        
        // If the button "Quit" is pressed, the program will end
        else if(e.getActionCommand().equals("Quit")) {
            System.exit(0);
        }
    }      
}





import java.awt.Container;
import javax.swing.*;

class GUIForGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Controls");
        Container pane = frame.getContentPane();
        pane.add(new JButton("North"));
    }
}
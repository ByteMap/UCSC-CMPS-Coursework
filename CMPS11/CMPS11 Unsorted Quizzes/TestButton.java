import java.awt.*;
import javax.swing.*;

class TestButton {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Controls");
        Container pane = frame.getContentPane();
        //pane.setLayout(new GridLayout());
        pane.add(new JButton("North"));
        JButton addButton = new JButton("add");
        frame.pack();
        frame.setVisible(true);
    }
}
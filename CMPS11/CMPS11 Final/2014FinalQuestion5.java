ActionEvent
    ActionListener
    addActionListener
    actionPerformed
    frame
    getContentPane
    new Submit()
    
    import java.awt.event.*;
class Submit implements ActionListener{
    public void actionPerformed (ActionEvent e) {
        System.out.println("Submitted");
    }
}
import javax.swing.*;
import java.awt.*;
class SimpleButton {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Simple Button");
        Container display = frame.getContentPane();
        JButton submit = new JButton("Submit");
        display.add(submit);
        submit.addActionListener(new Submit());
        frame.pack();
        frame.setVisible(true);
    }
}
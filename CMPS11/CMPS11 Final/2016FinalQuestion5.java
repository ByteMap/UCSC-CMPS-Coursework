import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
class SimpleButton {
    public static void main(String[] args) {
        JFrame window = new JFrame("Simple Button");
        Container cp = window.getContentPane();
        JButton submit = new JButton("Submit");
        cp.add(submit);
        submit.addActionListener(MyListener);
        window.pack();
        window.setVisible(true);
    }
}
class MyListener implements ActionListener {
    public void actionPerformed ( ActionEvent e) {
        System.out.println("Submitted");
    }
}

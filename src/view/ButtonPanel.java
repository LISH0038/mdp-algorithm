package view;

import javax.swing.*;

/**
 * Created by koallen on 26/8/17.
 */
public class ButtonPanel extends JPanel {

    public ButtonPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        JButton button1 = new JButton("Exploration");
        JButton button2 = new JButton("Fastest path");
        JButton button3 = new JButton("Load map");
        JButton button4 = new JButton("Time-limited");
        JButton button5 = new JButton("Coverage-limited");
        add(button1);
        add(button2);
        add(button3);
        add(button4);
        add(button5);
    }
}

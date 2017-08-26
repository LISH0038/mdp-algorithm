package view;

import controller.*;

import javax.swing.*;

/**
 * Bottom panel
 */
public class BottomPanel extends JPanel {

    public BottomPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        JCheckBox checkbox1 = new JCheckBox("Real run");
        JButton button1 = new JButton("Exploration");
        JButton button2 = new JButton("Fastest path");
        JButton button3 = new JButton("Load map");
        JButton button4 = new JButton("Time-limited");
        JButton button5 = new JButton("Coverage-limited");
        add(checkbox1);
        add(button1);
        add(button2);
        add(button3);
        add(button4);
        add(button5);

        button1.addActionListener(new ExplorationButtonListener());
        button2.addActionListener(new FastestPathButtonListener());
        button3.addActionListener(new LoadMapButtonListener());
        button4.addActionListener(new TimeLimitedButtonListener());
        button5.addActionListener(new CoverageLimitedButtonListener());
    }
}

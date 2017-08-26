package view;

import model.entity.Grid;
import model.entity.Robot;

import javax.swing.*;
import java.awt.*;
import java.util.Observer;

/**
 * Simulator
 */

public class Simulator extends JFrame {

    private JPanel mMapPanel;
    private JPanel mBottomPanel;
    private Grid mSimulationGrid;
    private Robot mSimulationRobot;

    private Simulator() {
        mSimulationGrid = new Grid();
        mSimulationRobot = new Robot();
        initializeMap();
        initializeButtons();
        initializeFrame();
    }

    private void initializeFrame() {
        pack();
        setTitle("MDP Group 16 Simulator");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initializeMap() {
        mMapPanel = new MapPanel(mSimulationGrid, mSimulationRobot);
        mSimulationRobot.addObserver((Observer) mMapPanel);
        mSimulationGrid.addObserver((Observer) mMapPanel);

        /* wrap the panel into a FlowLayout to maintain its preferred size */
        JPanel wrapper = new JPanel(new FlowLayout());
        wrapper.add(mMapPanel);
        this.add(wrapper, BorderLayout.CENTER);
    }

    private void initializeButtons() {
        mBottomPanel = new BottomPanel();
        this.add(mBottomPanel, BorderLayout.PAGE_END);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Simulator simulator = new Simulator();
            simulator.setVisible(true);
        });
        System.out.println("MDP simulator started.");
    }
}

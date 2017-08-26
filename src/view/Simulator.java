package view;

import model.entity.Grid;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Simulator
 */

public class Simulator extends JFrame {

    private JPanel mMapPanel;
    private JPanel mButtonPanel;
    private Grid mSimulationGrid;

    private Simulator() {
        mSimulationGrid = new Grid();
        initializeFrame();
        initializeMap();
        initializeButtons();
    }

    private void initializeFrame() {
        setTitle("MDP Group 16 Simulator");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initializeMap() {
        mMapPanel = new MapPanel(mSimulationGrid);
        JPanel wrapper = new JPanel(new FlowLayout());
        wrapper.add(mMapPanel);
        this.add(wrapper, BorderLayout.CENTER);
    }

    private void initializeButtons() {
        mButtonPanel = new ButtonPanel();
        this.add(mButtonPanel, BorderLayout.PAGE_END);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Simulator simulator = new Simulator();
            simulator.pack();
            simulator.setVisible(true);
        });
        System.out.println("MDP simulator started.");
    }
}

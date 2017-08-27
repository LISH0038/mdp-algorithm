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

    // Swing components
    private JPanel mMapPanel;
    private JButton mExplorationButton;
    private JButton mFastestPathButton;
    private JButton mLoadMapButton;
    private JButton mTimeLimitedButton;
    private JButton mCoverageLimitedButton;
    private JCheckBox mRealRunCheckBox;

    // model
    private Grid mSimulationGrid;
    private Robot mSimulationRobot;

    public Simulator(Grid grid, Robot robot) {
        mSimulationGrid = grid;
        mSimulationRobot = robot;
        initializeUi();
    }

    private void initializeUi() {
        // create components
        mMapPanel = new MapPanel(mSimulationGrid, mSimulationRobot);
        mExplorationButton = new JButton("Exploration");
        mFastestPathButton = new JButton("Fastest path");
        mLoadMapButton = new JButton("Load map");
        mTimeLimitedButton = new JButton("Time limited");
        mCoverageLimitedButton = new JButton("Coverage limited");
        mRealRunCheckBox = new JCheckBox("Real run");

        // set up as observer
        mSimulationRobot.addObserver((Observer) mMapPanel);
        mSimulationGrid.addObserver((Observer) mMapPanel);

        // layout components
        JPanel wrapper = new JPanel(new FlowLayout());
        wrapper.add(mMapPanel);
        this.add(wrapper, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(mRealRunCheckBox);
        bottomPanel.add(mExplorationButton);
        bottomPanel.add(mFastestPathButton);
        bottomPanel.add(mLoadMapButton);
        bottomPanel.add(mTimeLimitedButton);
        bottomPanel.add(mCoverageLimitedButton);
        this.add(bottomPanel, BorderLayout.PAGE_END);

        // set up the frame
        pack();
        setTitle("MDP Group 16 Simulator");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}

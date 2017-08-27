import controller.*;
import model.entity.Grid;
import model.entity.Robot;
import view.Simulator;

import javax.swing.*;

/**
 * Entry of the application
 */
public class AppRunner {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // models
            Grid grid = new Grid();
            Robot robot = new Robot();

            // view
            Simulator simulator = new Simulator(grid, robot);

            // controller
            // TODO: add them to the view
            new CoverageLimitedButtonListener();
            new ExplorationButtonListener();
            new FastestPathButtonListener();
            new LoadMapButtonListener();
            new TimeLimitedButtonListener();

            simulator.setVisible(true);
            System.out.println("Simulator started.");
        });
    }
}

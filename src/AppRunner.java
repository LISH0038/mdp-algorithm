import controller.*;
import model.entity.Grid;
import model.entity.Robot;
import model.entity.Sensor;
import view.Simulator;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import static constant.RobotConstants.*;

/**
 * Entry of the application
 */
public class AppRunner {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // models
            Grid grid = new Grid();
            Sensor sensor1 = new Sensor(3, 0, 0, MIDDLE);
            Sensor sensor2 = new Sensor(3, 1, 0, MIDDLE);
            Sensor sensor3 = new Sensor(3, 2, 0, MIDDLE);
            Sensor sensor4 = new Sensor(6, 2, 0, RIGHT);
            List<Sensor> sensors = new ArrayList<>();
            sensors.add(sensor1);
            sensors.add(sensor2);
            sensors.add(sensor3);
            sensors.add(sensor4);
            Robot robot = new Robot(grid, sensors);

            // view
            Simulator simulator = new Simulator(grid, robot);

            // controller
            // TODO: add them to the view
            new CoverageLimitedButtonListener(simulator, grid, robot);
            new ExplorationButtonListener(simulator, grid, robot);
            new FastestPathButtonListener(simulator, grid, robot);
            new LoadMapButtonListener(simulator, grid, robot);
            new TimeLimitedButtonListener(simulator, grid, robot);

            simulator.setVisible(true);
            System.out.println("Simulator started.");
        });
    }
}

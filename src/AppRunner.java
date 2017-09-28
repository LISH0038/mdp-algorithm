import controller.*;
import model.entity.Grid;
import model.entity.Robot;
import model.entity.Sensor;
import model.util.SocketMgr;
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
//        SocketMgr.getInstance().openConnection();
        SwingUtilities.invokeLater(() -> {
            // models
            Grid grid = new Grid();
            setObstaclesMap(grid);
            Sensor sensor1 = new Sensor(3, 0, 0, MIDDLE);
            Sensor sensor2 = new Sensor(3, 1, 0, MIDDLE);
            Sensor sensor3 = new Sensor(3, 2, 0, MIDDLE);
            Sensor sensor4 = new Sensor(6, 2, 0, RIGHT);
            Sensor sensor5 = new Sensor(3, 0, 0, LEFT);
            List<Sensor> sensors = new ArrayList<>();
            sensors.add(sensor1);
            sensors.add(sensor2);
            sensors.add(sensor3);
            sensors.add(sensor4);
            sensors.add(sensor5);
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

    public static void setObstaclesMap(Grid grid) {
        grid.setIsObstacle(11, 19, true);
        grid.setIsObstacle(11, 18, true);
        grid.setIsObstacle(11, 17, true);

        grid.setIsObstacle(8, 16, true);
        grid.setIsObstacle(9, 16, true);
        grid.setIsObstacle(10, 16, true);

        grid.setIsObstacle(0, 13, true);
        grid.setIsObstacle(1, 13, true);
        grid.setIsObstacle(2, 13, true);
        grid.setIsObstacle(2, 12, true);

        grid.setIsObstacle(0, 7, true);
        grid.setIsObstacle(1, 7, true);
        grid.setIsObstacle(2, 7, true);
        grid.setIsObstacle(2, 6, true);

        grid.setIsObstacle(3, 0, true);
        grid.setIsObstacle(3, 1, true);
        grid.setIsObstacle(3, 2, true);

        grid.setIsObstacle(7, 7, true);
        grid.setIsObstacle(6, 7, true);
        grid.setIsObstacle(6, 8, true);
        grid.setIsObstacle(6, 9, true);
        grid.setIsObstacle(6, 10, true);

        grid.setIsObstacle(8, 3, true);
        grid.setIsObstacle(8, 4, true);
        grid.setIsObstacle(9, 4, true);

        grid.setIsObstacle(14, 4, true);

        grid.setIsObstacle(12, 9, true);
        grid.setIsObstacle(12, 8, true);
        grid.setIsObstacle(13, 8, true);
        grid.setIsObstacle(14, 8, true);
    }
}

package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;

import static constant.RobotConstants.RIGHT;

/**
 * Created by koallen on 27/8/17.
 */
public class ExplorationAlgorithmRunner implements AlgorithmRunner {

    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        System.out.println("Started exploration");
        for (int i = 0; i < 100; i++) {
            robot.sense();
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
            if (robot.isObstacleAhead()) {
                System.out.println("Obstacle ahead, stopping");
                robot.turn(RIGHT);
            }
            robot.move();
        }
        robot.sense();
    }

}

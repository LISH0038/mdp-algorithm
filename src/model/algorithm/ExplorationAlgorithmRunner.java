package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;

import static constant.RobotConstants.LEFT;
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
            while (robot.isObstacleAhead()) {
                System.out.println("Obstacle ahead, stopping");
                try {
                    Thread.sleep(200);
                } catch (Exception e) {

                }
                robot.turn(LEFT);
            }
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
            robot.move();
        }
        robot.sense();
    }

}

package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;

/**
 * Created by koallen on 27/8/17.
 */
public class SimulatedExplorationAlgorithmRunner implements AlgorithmRunner {

    @Override
    public void run(Grid grid, Robot robot) {
        System.out.println("Started exploration");
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }

            robot.move();
        }
    }

}

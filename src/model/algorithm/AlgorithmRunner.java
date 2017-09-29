package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;

/**
 * Interface for algorithms
 */
public interface AlgorithmRunner {
    void run(Grid grid, Robot robot, boolean realRun);
}

package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;

import java.util.ArrayList;
import java.util.List;

import static constant.MapConstants.MAP_COLS;
import static constant.RobotConstants.*;

/**
 * Created by koallen on 25/8/17.
 */
public class FastestPathAlgorithmRunner implements AlgorithmRunner {
    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        System.out.println("Fastest path algorithm started");
        // functions:
        // - estimate h(n)
        //
        int pathCost = 0, estimatedCost = 0, totalCost = 0;
        int totalCostMove, totalCostTurningLeft = 100000, totalCostTurningRight = 100000;
        int newHeading;
        int goalX = MAP_COLS - 2, goalY = 1;
        List<String> actions = new ArrayList<>();
        robot.reset();

        // main loop of the A* search
        while (!isRobotInGoalZone(robot.getCenterPosX(), robot.getCenterPosY())) {
            System.out.println("Robot's current position: (" + robot.getCenterPosX() + "," + robot.getCenterPosY() + ")");
            // if move forward
            if (robot.isObstacleAhead()) {
                totalCostMove = 100000;
            } else {
                int nextX = robot.getCenterPosX(), nextY = robot.getCenterPosY();
                if (robot.getHeading() == NORTH)
                    nextY--;
                else if (robot.getHeading() == SOUTH)
                    nextY++;
                else if (robot.getHeading() == WEST)
                    nextX--;
                else if (robot.getHeading() == EAST)
                    nextX++;
                int penaltyMove = isObstacleOnFrontAndSide(grid, robot.getHeading(), nextX, nextY) ? 10 : 0;
                totalCostMove = pathCost + 1 // 1 for moving forward
                        + estimateDistanceToGoal(nextX, nextY, goalX, goalY)
                        + penaltyMove;
            }


            // if turn left
            newHeading = (robot.getHeading() + 3) % 4;
            int penaltyLeft = isObstacleOnFrontAndSide(grid, newHeading, robot.getCenterPosX(), robot.getCenterPosY()) ? 10 : 0;
            if (newHeading == NORTH || newHeading == EAST)
                penaltyLeft += 5;
            else
                penaltyLeft += 10;
            totalCostTurningLeft = pathCost
                    + estimateDistanceToGoal(robot.getCenterPosX(), robot.getCenterPosY(), goalX, goalY)
                    + penaltyLeft;

            // if turn right
            newHeading = (robot.getHeading() + 1) % 4;
            int penaltyRight = isObstacleOnFrontAndSide(grid, newHeading, robot.getCenterPosX(), robot.getCenterPosY()) ? 10 : 0;
            if (newHeading == NORTH || newHeading == EAST)
                penaltyRight += 5;
            else
                penaltyRight += 10;
            totalCostTurningRight = pathCost
                    + estimateDistanceToGoal(robot.getCenterPosX(), robot.getCenterPosY(), goalX, goalY)
                    + penaltyRight;

            try {
                Thread.sleep(500);
            } catch (Exception e) {

            }
            if (totalCostMove <= totalCostTurningLeft && totalCostMove <= totalCostTurningRight) {
                robot.move();
                //actions.add("m");
                pathCost++;
            } else if (totalCostTurningRight < totalCostMove && totalCostTurningRight < totalCostTurningLeft) {
                robot.turn(RIGHT);
            }
            else if (totalCostTurningLeft < totalCostMove && totalCostTurningLeft < totalCostMove) {
            robot.turn(LEFT);
            //actions.add("l");
            }
        }
    }

    /**
     * Calculates the estimated distance from (curX, curY) to (goalX, goalY)
     * The estimation is based on the Manhattan distance
     */
    private int estimateDistanceToGoal(int curX, int curY, int goalX, int goalY) {
        return Math.abs(goalX - curX) + Math.abs(goalY - curY);
    }

    private boolean isRobotInGoalZone(int x, int y) {
        if (x == MAP_COLS - 2 && y == 1)
            return true;
        return false;
    }

    private boolean isObstacleOnFrontAndSide(Grid grid, int heading, int x, int y) {
        boolean obstacleInFront = false, obstacleOnTheLeft = false, obstacleOnTheRight = false;

        for (int i = -1; i <= 1; i++) {
            if (heading == NORTH) {
                if (grid.getIsObstacle(x + i, y - 2))
                    obstacleInFront = true;
                if (grid.getIsObstacle(x - 2, y + i))
                    obstacleOnTheLeft = true;
                if (grid.getIsObstacle(x + 2, y + i))
                    obstacleOnTheRight = true;
            } else if (heading == SOUTH) {
                if (grid.getIsObstacle(x + i, y + 2))
                    obstacleInFront = true;
                if (grid.getIsObstacle(x + 2, y + i))
                    obstacleOnTheLeft = true;
                if (grid.getIsObstacle(x - 2, y + i))
                    obstacleOnTheRight = true;
            } else if (heading == WEST) {
                if (grid.getIsObstacle(x - 2, y + i))
                    obstacleInFront = true;
                if (grid.getIsObstacle(x + i, y + 2))
                    obstacleOnTheLeft = true;
                if (grid.getIsObstacle(x + i, y - 2))
                    obstacleOnTheRight = true;
            } else if (heading == EAST) {
                if (grid.getIsObstacle(x + 2, y + i))
                    obstacleInFront = true;
                if (grid.getIsObstacle(x + i, y - 2))
                    obstacleOnTheLeft = true;
                if (grid.getIsObstacle(x + i, y + 2))
                    obstacleOnTheRight = true;
            }
        }

        if (obstacleInFront && obstacleOnTheLeft && obstacleOnTheRight)
            return true;
        return false;
    }
}

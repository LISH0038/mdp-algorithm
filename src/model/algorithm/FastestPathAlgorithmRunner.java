package model.algorithm;

import model.entity.Cell;
import model.entity.Grid;
import model.entity.Robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static constant.MapConstants.MAP_COLS;
import static constant.MapConstants.MAP_ROWS;
import static constant.RobotConstants.*;

/**
 * Created by koallen on 25/8/17.
 */
public class FastestPathAlgorithmRunner implements AlgorithmRunner {

    private int sleepDuration;
    private static final int INFINITY = 1000000;
    private static final int START_X = 0;
    private static final int START_Y = 17;
    private static final int GOAL_X = 12;
    private static final int GOAL_Y = 0;
    private boolean[][] closedSet;
    private List<Cell> openSet;
    private HashMap<Cell, Cell> cameFrom;
    private int[][] gScore;
    private int[][] fScore;
    private Cell[][] cells;

    public FastestPathAlgorithmRunner(int speed) {
        sleepDuration = 1000 / speed;

        closedSet = new boolean[MAP_COLS - 2][MAP_ROWS - 2];
        openSet = new ArrayList<>();
        cameFrom = new HashMap<>();
        gScore = new int[MAP_COLS - 2][MAP_ROWS - 2];
        fScore = new int[MAP_COLS - 2][MAP_ROWS - 2];
        cells = new Cell[MAP_COLS - 2][MAP_ROWS - 2];

        for (int x = 0; x < MAP_COLS - 2; x++)
            for (int y = 0; y < MAP_ROWS - 2; y++) {
                gScore[x][y] = INFINITY;
                fScore[x][y] = INFINITY;
                closedSet[x][y] = false;
                cells[x][y] = new Cell(x, y);
            }
        gScore[START_X][START_Y] = 0;
        fScore[START_X][START_Y] = estimateDistanceToGoal(START_X, START_Y, GOAL_X, GOAL_Y);
        cells[START_X][START_Y].setDistance(fScore[START_X][START_Y]);
        openSet.add(cells[START_X][START_Y]);
    }

    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        System.out.println("Fastest path algorithm started");

        robot.reset();
        System.out.println("Reset!");
        try {
            while (!openSet.isEmpty()) {
                Cell current = getCurrent();
                System.out.println("Current: " + current.getX() + ", " + current.getY());
                if (current.getX() == GOAL_X && current.getY() == GOAL_Y) {
                    System.out.println("Reached goal");
                    reconstructPath(grid, robot, current);
                    return;
                }

                openSet.remove(current);
                closedSet[current.getX()][current.getY()] = true;

                for (Cell neighbor : generateNeighbor(grid, current)) {
                    System.out.println("Begin updating");
                    if (closedSet[neighbor.getX()][neighbor.getY()])
                        continue;

                    if (!openSet.contains(neighbor))
                        openSet.add(neighbor);

                    int tentativeGScore = gScore[current.getX()][current.getY()] + 1; // TODO: should this always be 1?
                    if (tentativeGScore >= gScore[neighbor.getX()][neighbor.getY()])
                        continue;

                    cameFrom.put(neighbor, current);
                    gScore[neighbor.getX()][neighbor.getY()] = tentativeGScore;
                    fScore[neighbor.getX()][neighbor.getY()] = tentativeGScore + estimateDistanceToGoal(neighbor.getX(), neighbor.getY(), GOAL_X, GOAL_Y);
                    System.out.println("Finish updating");
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void printMap() {
        for (int y = 0; y < MAP_ROWS - 2; y++) {
            for (int x = 0; x < MAP_COLS - 2; x++) {
                System.out.print(fScore[x][y] + " ");
            }
            System.out.println();
        }
    }

    private Cell getCurrent() {
        Cell minCell = null;
        int minF = INFINITY;
        for (Cell cell : openSet) {
           if (fScore[cell.getX()][cell.getY()] < minF) {
               minF = fScore[cell.getX()][cell.getY()];
               minCell = cell;
           }
        }

        return minCell;
    }

    private void reconstructPath(Grid grid, Robot robot, Cell current) {
        // construct the path first
        List<Cell> path = new LinkedList<>();
        path.add(current);
        while (cameFrom.get(current) != null) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        path.remove(0); // remove the starting point

        // convert path to robot movement
        // TODO: encode the actions as a string for sending to actual robot
        //List<String> actions = new ArrayList<>();
        for (Cell cell : path) {
            // see if we need to turn
            int nextHeading = 0;
            if (robot.getCenterPosX() < cell.getX() + 1)
                nextHeading = EAST;
            else if (robot.getCenterPosX() > cell.getX() + 1)
                nextHeading = WEST;
            else if (robot.getCenterPosY() < cell.getY() + 1)
                nextHeading = SOUTH;
            else if (robot.getCenterPosY() > cell.getY() + 1)
                nextHeading = NORTH;

            if (nextHeading != robot.getHeading()) {
                try {
                    Thread.sleep(sleepDuration);
                } catch (Exception e) {}
                if ((robot.getHeading() + 1) % 4 == nextHeading)
                    robot.turn(RIGHT);
                else
                    robot.turn(LEFT);
            }
            try {
                Thread.sleep(sleepDuration);
            } catch (Exception e) {}
            robot.move();
        }
    }

    private List<Cell> generateNeighbor(Grid grid, Cell current) {
        boolean left = true, right = true, front = true, back = true;
        List<Cell> neighbors = new ArrayList<>();

        // check front
        for (int i = -1; i <= 1; ++i) {
            if (grid.isOutOfArena(current.getX() + i + 1, current.getY() - 1))
                front = false;
            if (grid.getIsObstacle(current.getX() + i + 1, current.getY() - 1))
                front = false;
        }
        if (current.getY() <= 0)
            front = false;
        if (front)
            neighbors.add(cells[current.getX()][current.getY() - 1]);

        // check back
        for (int i = -1; i <= 1; ++i) {
            if (grid.isOutOfArena(current.getX() + i + 1, current.getY() + 3))
                back = false;
            if (grid.getIsObstacle(current.getX() + i + 1, current.getY() + 3))
                back = false;
        }
        if (current.getY() >= MAP_ROWS - 3)
            back = false;
        if (back)
            neighbors.add(cells[current.getX()][current.getY() + 1]);

        // check left
        for (int i = -1; i <= 1; ++i) {
            if (grid.isOutOfArena(current.getX() - 1, current.getY() + i + 1))
                left = false;
            if (grid.getIsObstacle(current.getX() - 1, current.getY() + i + 1))
                left = false;
        }
        if (current.getX() <= 0)
            left = false;
        if (left)
            neighbors.add(cells[current.getX() - 1][current.getY()]);

        // check right
        for (int i = -1; i <= 1; ++i) {
            if (grid.isOutOfArena(current.getX() + 3, current.getY() + i + 1))
                right = false;
            if (grid.getIsObstacle(current.getX() + 3, current.getY() + i + 1))
                right = false;
        }
        if (current.getX() >= MAP_COLS - 3)
            right = false;
        if (right)
            neighbors.add(cells[current.getX() + 1][current.getY()]);

        if (neighbors.size() == 0)
            System.out.println("No neighbors");
        System.out.println("Generated neighbors");

        return neighbors;
    }

    /**
     * Calculates the estimated distance from (curX, curY) to (goalX, goalY)
     * The estimation is based on the Manhattan distance
     */
    private int estimateDistanceToGoal(int curX, int curY, int goalX, int goalY) {
        int distance = Math.abs(goalX - curX) + Math.abs(goalY - curY);
        if (curX != goalX && curY != goalY)
            distance++;

        return distance;
    }

    private boolean isRobotInGoalZone(int x, int y) {
        if (x == MAP_COLS - 2 && y == 1)
            return true;
        return false;
    }
}

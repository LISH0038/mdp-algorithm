package model.algorithm;

import model.entity.Cell;
import model.entity.Grid;
import model.entity.Robot;
import model.entity.Sensor;
import model.util.MessageGenerator;
import model.util.SocketMgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static constant.CommConstants.TARGET_ANDROID;
import static constant.CommConstants.TARGET_ARDUINO;
import static constant.MapConstants.MAP_COLS;
import static constant.MapConstants.MAP_ROWS;
import static constant.RobotConstants.*;

/**
 * Fastest path algorithm using A* search + customized score functions
 */
public class FastestPathAlgorithmRunner implements AlgorithmRunner {

    private int sleepDuration;
    private static final int INFINITY = 1000000;
    private static final int START_X = 0;
    private static final int START_Y = 17;
    private static final int GOAL_X = 12;
    private static final int GOAL_Y = 0;

    public FastestPathAlgorithmRunner(int speed) {
        sleepDuration = 1000 / speed;
    }

    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        robot.reset();

        // receive waypoint
        int wayPointX, wayPointY;
        if (realRun) {
            // receive from Android
            System.out.println("Waiting for waypoint");
            SocketMgr.getInstance().clearInputBuffer();
            String msg = SocketMgr.getInstance().receiveMessage();
            List<Integer> waypoints;
            while ((waypoints = parseMessage(msg)) == null) {
                msg = SocketMgr.getInstance().receiveMessage();
            }
            // the coordinates in fastest path search is different from real grid coordinate
            wayPointX = waypoints.get(0)-1;
            wayPointY = waypoints.get(1)-1;
        } else {
            // ignore waypoint for simulation
            wayPointX = START_X;
            wayPointY = START_Y;
        }

        // run from start to waypoint and from waypoint to goal
        System.out.println("Fastest path algorithm started with waypoint " + wayPointX + "," + wayPointY);
        Robot fakeRobot = new Robot(null, new ArrayList<>());
        List<String> path1 = runAstar(START_X, START_Y, wayPointX, wayPointY, grid, fakeRobot);
        List<String> path2 = runAstar(wayPointX, wayPointY, GOAL_X, GOAL_Y, grid, fakeRobot);

        if (path1 != null && path2 != null) {
            System.out.println("Algorithm finished, executing actions");
            path1.addAll(path2);
            System.out.println(path1.toString());
            if (realRun) {
                // TODO: send actions to Arduino instead of running in simulator
                String compressedPath = compressPath(path1);
                System.out.println(compressedPath);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, compressedPath);
                //for (String action : path1) {
                //    if (action.equals("M")) {
                //        robot.move();
                //    } else if (action.equals("L")) {
                //        robot.turn(LEFT);
                //    } else if (action.equals("R")) {
                //        robot.turn(RIGHT);
                //    }
                //    SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                //            MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                //                    robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                //    takeStep();
                //}
            } else {
                String compressedPath = compressPath(path1);
                System.out.println(compressedPath);
                for (String action : path1) {
                    if (action.equals("M")) {
                        robot.move();
                    } else if (action.equals("L")) {
                        robot.turn(LEFT);
                    } else if (action.equals("R")) {
                        robot.turn(RIGHT);
                    }
                    takeStep();
                }
            }
        } else {
            System.out.println("Fastest path not found!");
        }
    }

    private String compressPath(List<String> actions) {
        int moveCounter = 0;
        StringBuilder builder = new StringBuilder();

        for (String action : actions) {
            // TODO: consider U turn?
            if (action.equals("L") || action.equals("R")) {
                if (moveCounter != 0) {
                    builder.append("M");
                    builder.append(String.format("%02d", moveCounter));
                    moveCounter = 0;
                }
                builder.append(action);
            } else if (action.equals("M")) {
                moveCounter++;
            }
        }
        if (moveCounter != 0) {
            builder.append("M");
            builder.append(String.format("%02d", moveCounter));
        }

        return builder.toString();
    }

    private List<Integer> parseMessage(String msg) {
        String[] splitString = msg.split(",", 2);
        List<Integer> waypoint = new ArrayList<>();

        Integer wayPointX, wayPointY;
        try {
            wayPointX = Integer.parseInt(splitString[0]);
            wayPointY = MAP_ROWS - Integer.parseInt(splitString[1]) - 1;
            waypoint.add(wayPointX);
            waypoint.add(wayPointY);
            return waypoint;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Run the A* algorithm from point (startX, startY) to (endX, endY)
     * @param startX Start point x coordinate
     * @param startY Start point y coordinate
     * @param endX End point x coordinate
     * @param endY End point y coordinate
     * @param grid Map
     * @param robot Robot
     * @return A list of actions for the robot to take to reach the goal
     */
    private List<String> runAstar(int startX, int startY, int endX, int endY, Grid grid, Robot robot) {
        // initialization
        boolean[][] closedSet;
        List<Cell> openSet;
        HashMap<Cell, Cell> cameFrom;
        int[][] gScore;
        int[][] fScore;
        Cell[][] cells;
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
        gScore[startX][startY] = 0;
        fScore[startX][startY] = estimateDistanceToGoal(startX, startY, endX, endY);
        cells[startX][startY].setDistance(fScore[startX][startY]);
        openSet.add(cells[startX][startY]);

        // run algorithm
        while (!openSet.isEmpty()) {
            Cell current = getCurrent(openSet, fScore);
            System.out.println("Current: " + current.getX() + ", " + current.getY());
            if (current.getX() == endX && current.getY() == endY) {
                System.out.println("Reached goal");
                return reconstructPath(robot, current, cameFrom);
            }

            openSet.remove(current);
            closedSet[current.getX()][current.getY()] = true;

            for (Cell neighbor : generateNeighbor(grid, current, cells)) {
                if (closedSet[neighbor.getX()][neighbor.getY()])
                    continue;

                if (!openSet.contains(neighbor))
                    openSet.add(neighbor);

                int tentativeGScore = gScore[current.getX()][current.getY()] + 1;
                Cell previousCell = cameFrom.get(current);
                if (previousCell != null && previousCell.getX() != neighbor.getX() && previousCell.getY() != neighbor.getY())
                    tentativeGScore += 1; // penalize turns
                if (tentativeGScore >= gScore[neighbor.getX()][neighbor.getY()])
                    continue;

                cameFrom.put(neighbor, current);
                gScore[neighbor.getX()][neighbor.getY()] = tentativeGScore;
                fScore[neighbor.getX()][neighbor.getY()] = tentativeGScore + estimateDistanceToGoal(neighbor.getX(), neighbor.getY(), endX, endY);
            }
        }

        System.out.println("No fastest path found.");
        return null;
    }

    private Cell getCurrent(List<Cell> openSet, int[][] fScore) {
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

    private List<String> reconstructPath(Robot robot, Cell current, HashMap<Cell, Cell> cameFrom) {
        // construct the path first
        List<Cell> path = new LinkedList<>();
        path.add(current);
        while (cameFrom.get(current) != null) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        path.remove(0); // remove the starting point

        // convert path to robot movement
        List<String> actions = new ArrayList<>();
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
                if ((robot.getHeading() + 1) % 4 == nextHeading) {
                    actions.add("R");
                    robot.turn(RIGHT);
                } else if ((robot.getHeading() + 3) % 4 == nextHeading){
                    actions.add("L");
                    robot.turn(LEFT);
                } else {
                    actions.add("L");
                    robot.turn(LEFT);
                    actions.add("L");
                    robot.turn(LEFT);
                }
            }
            actions.add("M");
            robot.move();
        }

        return actions;
    }

    private List<Cell> generateNeighbor(Grid grid, Cell current, Cell[][] cells) {
        boolean left = true, right = true, front = true, back = true;
        List<Cell> neighbors = new ArrayList<>();

        int trueX = current.getX() + 1, trueY = current.getY() + 1;
        // check front
        for (int i = -1; i <= 1; ++i) {
            if (grid.isOutOfArena(trueX + i, trueY - 2) ||
                    grid.getIsObstacle(trueX + i, trueY - 2))
                front = false;
        }
        if (front)
            neighbors.add(cells[current.getX()][current.getY() - 1]);

        // check back
        for (int i = -1; i <= 1; ++i) {
            if (grid.isOutOfArena(trueX + i, trueY + 2) ||
                    grid.getIsObstacle(trueX + i, trueY + 2))
                back = false;
        }
        if (back)
            neighbors.add(cells[current.getX()][current.getY() + 1]);

        // check left
        for (int i = -1; i <= 1; ++i) {
            if (grid.isOutOfArena(trueX - 2, trueY + i) ||
                    grid.getIsObstacle(trueX - 2, trueY + i))
                left = false;
        }
        if (left)
            neighbors.add(cells[current.getX() - 1][current.getY()]);

        // check right
        for (int i = -1; i <= 1; ++i) {
            if (grid.isOutOfArena(trueX + 2, trueY + i) ||
                    grid.getIsObstacle(trueX + 2, trueY + i))
                right = false;
        }
        if (right)
            neighbors.add(cells[current.getX() + 1][current.getY()]);

        return neighbors;
    }

    /**
     * Calculates the estimated distance from (curX, curY) to (goalX, goalY)
     * The estimation is based on the Manhattan distance
     */
    private int estimateDistanceToGoal(int curX, int curY, int goalX, int goalY) {
        int distance = Math.abs(goalX - curX) + Math.abs(goalY - curY);
        if (curX != goalX && curY != goalY)
            distance += 1;

        return distance;
    }

    private void takeStep() {
        try {
            Thread.sleep(sleepDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

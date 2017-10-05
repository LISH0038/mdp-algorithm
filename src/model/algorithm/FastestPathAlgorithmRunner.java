package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;
import model.util.SocketMgr;

import java.util.ArrayList;
import java.util.List;

import static constant.CommConstants.TARGET_ARDUINO;
import static constant.MapConstants.MAP_ROWS;
import static constant.RobotConstants.*;

/**
 * Fastest path algorithm using A* search + customized score functions
 */
public class FastestPathAlgorithmRunner implements AlgorithmRunner {

    private int sleepDuration;

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
            //SocketMgr.getInstance().clearInputBuffer();
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
        List<String> path1 = AlgorithmRunner.runAstar(START_X, START_Y, wayPointX, wayPointY, grid, fakeRobot);
        List<String> path2 = AlgorithmRunner.runAstar(wayPointX, wayPointY, GOAL_X, GOAL_Y, grid, fakeRobot);

        if (path1 != null && path2 != null) {
            System.out.println("Algorithm finished, executing actions");
            path1.addAll(path2);
            System.out.println(path1.toString());
            if (realRun) {
                String compressedPath = compressPath(path1);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, compressedPath);
            } else {
                for (String action : path1) {
                    if (action.equals("M")) {
                        robot.move();
                    } else if (action.equals("L")) {
                        robot.turn(LEFT);
                    } else if (action.equals("R")) {
                        robot.turn(RIGHT);
                    } else if (action.equals("U")) {
                        robot.turn(LEFT);
                        robot.turn(LEFT);
                    }
                    takeStep();
                }
            }
        } else {
            System.out.println("Fastest path not found!");
        }
    }

    /**
     * Convert the list of actions into a single string for sending
     * to Arduino. Specifically, consecutive moves are compressed to the
     * format "M5" to represent moving 5 cells at once.
     * @param actions Actions to perform
     * @return A string representing the actions
     */
    private String compressPath(List<String> actions) {
        int moveCounter = 0;
        StringBuilder builder = new StringBuilder();

        for (String action : actions) {
            if (action.equals("L") || action.equals("R") || action.equals("U")) {
                if (moveCounter != 0) {
                    builder.append("M");
                    builder.append(moveCounter);
                    moveCounter = 0;
                }
                builder.append(action);
            } else if (action.equals("M")) {
                moveCounter++;
            }
        }
        if (moveCounter != 0) {
            builder.append("M");
            builder.append(moveCounter);
        }

        return builder.toString();
    }

    /**
     * Parse waypoint message from Android, the Y coordinate received
     * starts from the bottom, so it's reversed.
     * @param msg
     * @return
     */
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
     * Pause the simulation for sleepDuration
     */
    private void takeStep() {
        try {
            Thread.sleep(sleepDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

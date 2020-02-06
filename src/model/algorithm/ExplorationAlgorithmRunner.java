package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;
import model.util.MessageMgr;
import model.util.SocketMgr;

import java.util.ArrayList;
import java.util.List;

import static constant.CommConstants.TARGET_ANDROID;
import static constant.CommConstants.TARGET_ARDUINO;
import static constant.MapConstants.MAP_COLS;
import static constant.MapConstants.MAP_ROWS;
import static constant.RobotConstants.*;

/**
 * Algorithm for exploration phase (full exploration)
 */
public class ExplorationAlgorithmRunner implements AlgorithmRunner {

    private int sleepDuration;
    private static final int START_X = 0;
    private static final int START_Y = 17;
    private static final int CALIBRATION_LIMIT = 5;
    public ExplorationAlgorithmRunner(int speed){
        sleepDuration = 1000 / speed;
    }

    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        grid.reset();
        robot.reset();
        if (realRun) {
            grid.clearObstacles();
            String msg = SocketMgr.getInstance().receiveMessage(false);
            while (!msg.equals("exs")) {
                msg = SocketMgr.getInstance().receiveMessage(false);
            }
        }
        // SELECT EITHER ONE OF THE METHODS TO RUN ALGORITHMS.
        runExplorationAlgorithmThorough(grid, robot, realRun);
//        runExplorationLeftWall(grid, robot, realRun);

        // CALIBRATION AFTER EXPLORATION
        calibrateAndTurn(robot, realRun);

        // GENERATE MAP DESCRIPTOR, SEND TO ANDROID
        String part1 = grid.generateDescriptorPartOne();
        String part2 = grid.generateDescriptorPartTwo();
        SocketMgr.getInstance().sendMessage(TARGET_ANDROID, MessageMgr.generateFinalDescriptor(part1, part2));
    }

    private void calibrateAndTurn(Robot robot, boolean realRun) {
        if (realRun) {
            while (robot.getHeading() != SOUTH) {
                robot.turn(LEFT);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
            }
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
        }
    }

    private void runExplorationLeftWall(Grid grid, Robot robot, boolean realRun){
        boolean endZoneFlag = false;
        boolean startZoneFlag = false;

        // CALIBRATE & SENSE
        int calibrationCounter = 0;
//        if (realRun) {
//            calibrateAtStart();
//        }
        robot.sense(realRun);

        // INITIAL UPDATE OF MAP TO ANDROID
        if (realRun)
            SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                    MessageMgr.generateMapDescriptorMsg(grid.generateForAndroid(),
                            robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));

        // MAIN LOOP (LEFT-WALL-FOLLOWER)
        while (!endZoneFlag || !startZoneFlag) {
            // CHECK IF TURNING IS NECESSARY
            boolean turned = leftWallFollower(robot, grid, realRun);

            if (turned) {
                // CALIBRATION
                if (realRun) {
                    calibrationCounter++;
                    // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                    // OTHERWISE CALIBRATE LEFT
                    if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                        calibrationCounter = 0;
                    } else if (robot.canCalibrateFront()) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                        calibrationCounter = 0;
                    } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                        calibrationCounter = 0;
                    }
                }

                // SENSE AFTER CALIBRATION
                senseAndUpdateAndroid(robot, grid, realRun);
            }

            // MOVE FORWARD
            if (realRun)
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M1");
            robot.move();
            stepTaken();

            // CALIBRATION
            if (realRun) {
                calibrationCounter++;
                // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                // OTHERWISE CALIBRATE LEFT
                if (robot.canCalibrateFront()) {
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                    calibrationCounter = 0;
                } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                    calibrationCounter = 0;
                }
            }

            // SENSE AFTER CALIBRATION
            senseAndUpdateAndroid(robot, grid, realRun);

            if (Grid.isInEndZone(robot.getPosX(), robot.getPosY())) {
                endZoneFlag = true;
            }
            if (endZoneFlag && Grid.isInStartZone(robot.getPosX() + 2, robot.getPosY())) {
                startZoneFlag = true;
            }
        }
    }

    private void runExplorationAlgorithmThorough(Grid grid, Robot robot, boolean realRun) {
        boolean endZoneFlag = false;
        boolean startZoneFlag = false;

        // CALIBRATE & SENSE
        int calibrationCounter = 0;
//        if (realRun) {
//            calibrateAtStart();
//        }
        robot.sense(realRun);

        // INITIAL UPDATE OF MAP TO ANDROID
        if (realRun)
            SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                    MessageMgr.generateMapDescriptorMsg(grid.generateForAndroid(),
                            robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));

        // MAIN LOOP (LEFT-WALL-FOLLOWER)
        while (!endZoneFlag || !startZoneFlag) {
            // CHECK IF TURNING IS NECESSARY
            boolean turned = leftWallFollower(robot, grid, realRun);

            if (turned) {
                // CALIBRATION
                if (realRun) {
                    calibrationCounter++;
                    // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                    // OTHERWISE CALIBRATE LEFT
                    if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                        calibrationCounter = 0;
                    } else if (robot.canCalibrateFront()) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                        calibrationCounter = 0;
                    } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                        calibrationCounter = 0;
                    }
                }

                // SENSE AFTER CALIBRATION
                senseAndUpdateAndroid(robot, grid, realRun);
            }

            // MOVE FORWARD
            if (realRun)
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M1");
            robot.move();
            stepTaken();

            // CALIBRATION
            if (realRun) {
                calibrationCounter++;
                // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                // OTHERWISE CALIBRATE LEFT
                if (robot.canCalibrateFront()) {
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                    calibrationCounter = 0;
                } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                    calibrationCounter = 0;
                }
            }

            // SENSE AFTER CALIBRATION
            senseAndUpdateAndroid(robot, grid, realRun);

            if (Grid.isInEndZone(robot.getPosX(), robot.getPosY())) {
                endZoneFlag = true;
            }
            if (endZoneFlag && Grid.isInStartZone(robot.getPosX() + 2, robot.getPosY())) {
                startZoneFlag = true;
            }

            // IF EXPLORATION COMPLETED & HAVE NOT GO BACK TO START, FIND THE FASTEST PATH BACK TO START POINT
            if(grid.checkExploredPercentage() == 100 && !startZoneFlag){
                Robot fakeRobot = new Robot(grid, new ArrayList<>());
                fakeRobot.setPosX(robot.getPosX());
                fakeRobot.setPosY(robot.getPosY());
                fakeRobot.setHeading(robot.getHeading());
                List<String> returnPath = AlgorithmRunner.runAstar(robot.getPosX(), robot.getPosY(), START_X, START_Y, grid, fakeRobot);
                fakeRobot.setPosX(robot.getPosX());
                fakeRobot.setPosY(robot.getPosY());
                fakeRobot.setHeading(robot.getHeading());
                String compressed = AlgorithmRunner.compressPathForExploration(returnPath, fakeRobot);

                if (returnPath != null) {
                    System.out.println("Algorithm finished, executing actions");
                    System.out.println(returnPath.toString());
                    if (realRun) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, compressed);
                    }

                    for (String action : returnPath) {
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
//                        robot.sense(realRun);
                        if (realRun)
                            SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                                    MessageMgr.generateMapDescriptorMsg(grid.generateForAndroid(),
                                            robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                        stepTaken();
                    }
                } else {
                    System.out.println("Fastest path not found!");
                }

                if (endZoneFlag && Grid.isInStartZone(robot.getPosX() + 2, robot.getPosY())) {
                    startZoneFlag = true;
                }
                //AT THIS STAGE, ROBOT SHOULD HAVE RETURNED BACK TO START POINT.
            }
        }

        //
        // BELOW IS THE 2ND EXPLORATION !!!!!!!!!!!!!
        //
        // INITIALISE NEW GRID TO PREVENT CHECKING PREVIOUSLY EXPLORED CELLS.
        Grid exploreChecker = new Grid();
        for (int x = 0; x < MAP_COLS; x++) {
            for (int y = 0; y < MAP_ROWS; y++) {
                exploreChecker.setExplored(x, y, grid.getIsExplored(x, y));
                exploreChecker.setIsObstacle(x, y, grid.getIsObstacle(x, y));
            }
        }

        // SWEEPING THROUGH UNEXPLORED, BUT REACHABLE CELLS WITHIN ARENA.
        if(grid.checkExploredPercentage() < 100.0){ // CHECK FOR UNEXPLORED CELLS
            System.out.println("NOT FULLY EXPLORED, DOING A 2ND RUN!");
            for (int y = MAP_ROWS - 1; y >= 0; y--) {
                for (int x = MAP_COLS - 1; x >= 0; x--) {
                    // CHECK FOR UNEXPLORED CELLS && CHECK IF NEIGHBOURS ARE REACHABLE OR NOT
                    if (!grid.getIsExplored(x, y) &&
                            ((checkUnexplored(robot, grid, x + 1, y, realRun)
                                    || checkUnexplored(robot, grid, x - 1, y, realRun)
                                    || checkUnexplored(robot, grid, x, y + 1, realRun)
                                    || checkUnexplored(robot, grid, x, y - 1, realRun)))) {
                        boolean startPointFlag = true;
                        while (startPointFlag) { // SET STARTPOINTFLAG TO TRUE TO INITIATE LEFT-WALL-FOLLOWER
                            startPointFlag = false; // SETSTARTPOINT FLAG TO FALSE TO SAY THAT ROBOT IS NOT AT START POINT.
                            boolean turned = leftWallFollower(robot, grid, realRun);

                            if (turned) {
                                // CALIBRATION
                                if (realRun) {
                                    calibrationCounter++;
                                    // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                                    // OTHERWISE CALIBRATE LEFT
                                    if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                        calibrationCounter = 0;
                                    } else if (robot.canCalibrateFront()) {
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                        calibrationCounter = 0;
                                    } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                                        calibrationCounter = 0;
                                    }
                                }

                                // SENSE AFTER CALIBRATION
                                senseAndUpdateAndroid(robot, grid, realRun);
                            }

                            // MOVE FORWARD
                            if (realRun)
                                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M1");
                            robot.move();
                            stepTaken();

                            // CALIBRATION
                            if (realRun) {
                                calibrationCounter++;
                                // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                                // OTHERWISE CALIBRATE LEFT
                                if (robot.canCalibrateFront()) {
                                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                    calibrationCounter = 0;
                                } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                                    calibrationCounter = 0;
                                }
                            }

                            // SENSE AFTER CALIBRATION
                            senseAndUpdateAndroid(robot, grid, realRun);

                            if (grid.checkExploredPercentage() == 100) { // IF FULLEST EXPLORED, EXIT AND GO TO START
                                break;
                            }

                            while (exploreChecker.getIsExplored(robot.getPosX(), robot.getPosY()) != grid.getIsExplored(robot.getPosX(), robot.getPosY())) {
                                if (grid.checkExploredPercentage() == 100) { // IF FULLEST EXPLORED, EXIT AND GO TO START
                                    break;
                                }
                                turned = leftWallFollower(robot, grid, realRun);

                                if (turned) {
                                    // CALIBRATION
                                    if (realRun) {
                                        calibrationCounter++;
                                        // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                                        // OTHERWISE CALIBRATE LEFT
                                        if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                            calibrationCounter = 0;
                                        } else if (robot.canCalibrateFront()) {
                                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                            calibrationCounter = 0;
                                        } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                                            calibrationCounter = 0;
                                        }
                                    }

                                    // SENSE AFTER CALIBRATION
                                    senseAndUpdateAndroid(robot, grid, realRun);
                                }

                                // MOVE FORWARD
                                if (realRun)
                                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M1");
                                robot.move();
                                stepTaken();

                                // CALIBRATION
                                if (realRun) {
                                    calibrationCounter++;
                                    // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                                    // OTHERWISE CALIBRATE LEFT
                                    if (robot.canCalibrateFront()) {
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                        calibrationCounter = 0;
                                    } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                                        calibrationCounter = 0;
                                    }
                                }

                                // SENSE AFTER CALIBRATION
                                senseAndUpdateAndroid(robot, grid, realRun);
                            }
                        }
                    }
                }
            }
            /*
            FASTEST PATH BACK TO START ONCE THE EXPLORATION IS COMPLETED.
            */
            if(!Grid.isInStartZone(robot.getPosX()+2, robot.getPosY()+2)){
                Robot fakeRobot = new Robot(grid, new ArrayList<>());
                fakeRobot.setPosX(robot.getPosX());
                fakeRobot.setPosY(robot.getPosY());
                fakeRobot.setHeading(robot.getHeading());
                List<String> returnPath = AlgorithmRunner.runAstar(robot.getPosX(), robot.getPosY(), START_X, START_Y, grid, fakeRobot);

                if (returnPath != null) {
                    System.out.println("RUNNING A* SEARCH!");
                    System.out.println(returnPath.toString());

                    if (realRun) {
                        fakeRobot.setPosX(robot.getPosX());
                        fakeRobot.setPosY(robot.getPosY());
                        fakeRobot.setHeading(robot.getHeading());
                        String compressedPath = AlgorithmRunner.compressPathForExploration(returnPath, fakeRobot);
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, compressedPath);
                    } else {
                        for (String action : returnPath) {
//                            robot.sense(realRun);
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
                            if (realRun)
                                SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                                        MessageMgr.generateMapDescriptorMsg(grid.generateForAndroid(),
                                                robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                            stepTaken();
                        }
                    }
                }else {
                    System.out.println("FASTEST PATH NOT FOUND!!");
                }
            }
        }
        System.out.println("EXPLORATION COMPLETED!");
        System.out.println("PERCENTAGE OF AREA EXPLORED: " + grid.checkExploredPercentage() + "%!");
    }

    /**
     * Checks if a turn is necessary and which direction to turn
     * @param robot
     * @param grid
     * @param realRun
     * @return whether a turn is performed
     */
    private boolean leftWallFollower(Robot robot, Grid grid, boolean realRun){
        if (robot.isObstacleAhead()) {
            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                System.out.println("OBSTACLE DETECTED! (ALL 3 SIDES) U-TURNING");
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "U");
                robot.turn(RIGHT);
                robot.turn(RIGHT);
                //if (!realRun)
                stepTaken();
            } else if (robot.isObstacleLeft()) {
                System.out.println("OBSTACLE DETECTED! (FRONT + LEFT) TURNING RIGHT");
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                robot.turn(RIGHT);
                stepTaken();
            } else {
                System.out.println("OBSTACLE DETECTED! (FRONT) TURNING LEFT");
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                stepTaken();
            }
            System.out.println("-----------------------------------------------");

            return true; // TURNED
        } else if (!robot.isObstacleLeft()) {
            System.out.println("NO OBSTACLES ON THE LEFT! TURNING LEFT");
            if (realRun)
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
            robot.turn(LEFT);
            stepTaken();
            System.out.println("-----------------------------------------------");

            return true; // TURNED
        }
        return false; // DIDN'T TURN
    }

    private boolean checkUnexplored(Robot robot, Grid grid, int x, int y, boolean realRun){
        Robot fakeRobot = new Robot(grid, new ArrayList<>());
        fakeRobot.setPosX(robot.getPosX());
        fakeRobot.setPosY(robot.getPosY());
        fakeRobot.setHeading(robot.getHeading());
        List<String> returnPath = AlgorithmRunner.runAstar(robot.getPosX(), robot.getPosY(), x, y, grid, robot);
        if (returnPath != null) {
            System.out.println("Algorithm finished, executing actions");
            System.out.println(returnPath.toString());

            for (String action : returnPath) {
                robot.sense(realRun);
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                            MessageMgr.generateMapDescriptorMsg(grid.generateForAndroid(),
                                    robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                //if (!realRun)
                stepTaken();
                if (action.equals("M")) {
                    if (realRun)
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M1");
                    robot.move();
                } else if (action.equals("L")) {
                    if (realRun)
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                    robot.turn(LEFT);
                } else if (action.equals("R")) {
                    if (realRun)
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                    robot.turn(RIGHT);
                } else if (action.equals("U")) {
                    if (realRun)
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "U");
                    robot.turn(LEFT);
                    robot.turn(LEFT);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private void stepTaken(){
        /*
            MAKE IT MOVE SLOWLY SO CAN SEE STEP BY STEP MOVEMENT
             */
        try {
            Thread.sleep(sleepDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calibrateAtStart() {
        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
    }

    private void senseAndUpdateAndroid(Robot robot, Grid grid, boolean realRun) {
        robot.sense(realRun);
        if (realRun) {
            SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                    MessageMgr.generateMapDescriptorMsg(grid.generateForAndroid(),
                            robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
        }
    }
}
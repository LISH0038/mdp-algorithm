package model.algorithm;

import model.entity.Cell;
import model.entity.Grid;
import model.entity.Robot;
import model.util.MessageGenerator;
import model.util.SocketMgr;

import java.util.ArrayList;
import java.util.List;

import static constant.CommConstants.TARGET_ANDROID;
import static constant.CommConstants.TARGET_ARDUINO;
import static constant.MapConstants.MAP_COLS;
import static constant.MapConstants.MAP_ROWS;
import static constant.RobotConstants.LEFT;
import static constant.RobotConstants.RIGHT;
import static constant.RobotConstants.SOUTH;

/**
 * Algorithm for exploration phase (full exploration)
 */
public class ExplorationAlgorithmRunner implements AlgorithmRunner {

    private int sleepDuration;
    private static final int START_X = 0;
    private static final int START_Y = 17;
    public ExplorationAlgorithmRunner(int speed){
        sleepDuration = 1000 / speed;
    }

    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        grid.reset();
        robot.reset();
        if (realRun) {
            grid.clearObstacles();
            //SocketMgr.getInstance().clearInputBuffer();
            //System.out.println("Wait for startup message");
            String msg = SocketMgr.getInstance().receiveMessage();
            while (!msg.equals("exs")) {
                msg = SocketMgr.getInstance().receiveMessage();
            }
        }
        runExplorationAlgorithmThorough(grid, robot, realRun);
        grid.generateDescriptor();
    }

    private void runExplorationAlgorithmThorough(Grid grid, Robot robot, boolean realRun) {
        boolean endZoneFlag = false;
        boolean startZoneFlag = false;

        // FIRST SENSE
        if (realRun)
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "S");
        robot.sense(realRun);
        if (realRun)
            SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                    MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                            robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));

        // MAIN LOOP (LEFT-WALL-FOLLOWER)
        while (!endZoneFlag || !startZoneFlag) {
            leftWallFollower(robot, grid, realRun);
            if(Grid.isInEndZone(robot.getPosX(), robot.getPosY())){
                endZoneFlag = true;
            }
            if(endZoneFlag && Grid.isInStartZone(robot.getPosX()+2, robot.getPosY())){
                startZoneFlag = true;
            }

            // IF EXPLORATION COMPLETED & HAVE NOT GO BACK TO START, FIND THE FASTEST PATH BACK TO START POINT
            if(grid.checkExploredPercentage() == 100 && !startZoneFlag){
                Robot fakeRobot = new Robot(grid, new ArrayList<>());
                fakeRobot.setPosX(robot.getPosX());
                fakeRobot.setPosY(robot.getPosY());
                fakeRobot.setHeading(robot.getHeading());
                List<String> returnPath = AlgorithmRunner.runAstar(robot.getPosX(), robot.getPosY(), START_X, START_Y, grid, fakeRobot);

                if (returnPath != null) {
                    System.out.println("Algorithm finished, executing actions");
                    System.out.println(returnPath.toString());

                    for (String action : returnPath) {
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
                        robot.sense(realRun);
                        if (realRun)
                            SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                                    MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                                            robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                        if(!realRun)
                            stepTaken();
                    }
                }else {
                    System.out.println("Fastest path not found!");
                }
                if(endZoneFlag && Grid.isInStartZone(robot.getPosX()+2, robot.getPosY())){
                    startZoneFlag = true;
                }
                //AT THIS STAGE, ROBOT SHOULD HAVE RETURNED BACK TO START POINT.
            }
        }

        // INITIALISE NEW GRID TO PREVENT CHECKING PREVIOUSLY EXPLORED CELLS.
        Grid exploreChecker = new Grid();
        for (int x = 0; x < MAP_COLS; x++) {
            for (int y = 0; y < MAP_ROWS; y++) {
                exploreChecker.setExplored(x, y, grid.getIsExplored(x, y));
                exploreChecker.setIsObstacle(x, y, grid.getIsObstacle(x, y));
            }
        }

        // SWEEPING THROUGH UNEXPLORED, BUT REACHABLE CELLS WITHIN ARENA.
        if(grid.checkExploredPercentage() != 100){ // CHECK FOR UNEXPLORED CELLS
            for (int y = MAP_ROWS; y >= 0; y--) {
                for (int x = MAP_COLS-1; x >= 0; x--) {
                    if(!grid.getIsExplored(x, y)){ // CHECK FOR UNEXPLORED CELLS
                        if(checkUnexplored(robot, grid, x+1, y, realRun)){ // CHECK IF NEIGHBOURS ARE REACHABLE OR NOT
                            boolean startPointFlag = true;
                            while(startPointFlag){ // SET STARTPOINTFLAG TO TRUE TO INITIATE LEFT-WALL-FOLLOWER
                                leftWallFollower(robot, grid, realRun);
                                // AS LONG AS THE CELL IS NOT EXPLORED, DO LEFT-WALL-FOLLOWER
                                while(exploreChecker.getIsExplored(robot.getPosX(), robot.getPosY()) != grid.getIsExplored(robot.getPosX(), robot.getPosY())){
                                    if(grid.checkExploredPercentage() == 100){ // IF FULLEST EXPLORED, EXIT AND GO TO START
                                        break;
                                    }
                                    leftWallFollower(robot, grid, realRun);
                                    startPointFlag = false; // SETSTARTPOINT FLAG TO FALSE TO SAY THAT ROBOT IS NOT AT START POINT.
                                }
                            }
                        }else if(checkUnexplored(robot, grid, x-1, y, realRun)){ // CHECK IF NEIGHBOURS ARE REACHABLE OR NOT
                            boolean startPointFlag = true;
                            while(startPointFlag){ // SET STARTPOINTFLAG TO TRUE TO INITIATE LEFT-WALL-FOLLOWER
                                leftWallFollower(robot, grid, realRun);
                                // AS LONG AS THE CELL IS NOT EXPLORED, DO LEFT-WALL-FOLLOWER
                                while(exploreChecker.getIsExplored(robot.getPosX(), robot.getPosY()) != grid.getIsExplored(robot.getPosX(), robot.getPosY())){
                                    if(grid.checkExploredPercentage() == 100){ // IF FULLEST EXPLORED, EXIT AND GO TO START
                                        break;
                                    }
                                    leftWallFollower(robot, grid, realRun);
                                    startPointFlag = false; // SETSTARTPOINT FLAG TO FALSE TO SAY THAT ROBOT IS NOT AT START POINT.
                                }
                            }
                        }else if(checkUnexplored(robot, grid, x, y+1, realRun)){ // CHECK IF NEIGHBOURS ARE REACHABLE OR NOT
                            boolean startPointFlag = true;
                            while(startPointFlag){ // SET STARTPOINTFLAG TO TRUE TO INITIATE LEFT-WALL-FOLLOWER
                                leftWallFollower(robot, grid, realRun);
                                // AS LONG AS THE CELL IS NOT EXPLORED, DO LEFT-WALL-FOLLOWER
                                while(exploreChecker.getIsExplored(robot.getPosX(), robot.getPosY()) != grid.getIsExplored(robot.getPosX(), robot.getPosY())){
                                    if(grid.checkExploredPercentage() == 100){ // IF FULLEST EXPLORED, EXIT AND GO TO START
                                        break;
                                    }
                                    leftWallFollower(robot, grid, realRun);
                                    startPointFlag = false; // SETSTARTPOINT FLAG TO FALSE TO SAY THAT ROBOT IS NOT AT START POINT.
                                }
                            }
                        }else if(checkUnexplored(robot, grid, x, y-1, realRun)){ // CHECK IF NEIGHBOURS ARE REACHABLE OR NOT
                            boolean startPointFlag = true;
                            while(startPointFlag){ // SET STARTPOINTFLAG TO TRUE TO INITIATE LEFT-WALL-FOLLOWER
                                leftWallFollower(robot, grid, realRun);
                                // AS LONG AS THE CELL IS NOT EXPLORED, DO LEFT-WALL-FOLLOWER
                                while(exploreChecker.getIsExplored(robot.getPosX(), robot.getPosY()) != grid.getIsExplored(robot.getPosX(), robot.getPosY())){
                                    if(grid.checkExploredPercentage() == 100){ // IF FULLEST EXPLORED, EXIT AND GO TO START
                                        break;
                                    }
                                    leftWallFollower(robot, grid, realRun);
                                    startPointFlag = false; // SET STARTPOINTFLAG TO FALSE TO SAY THAT ROBOT IS NOT AT START POINT.
                                }
                            }
                        }
                    }
                }
            }
        }

        /*
        FASTEST PATH BACK TO START ONCE THE EXPLORATION IS COMPLETED.
        */
        Robot fakeRobot = new Robot(grid, new ArrayList<>());
        fakeRobot.setPosX(robot.getPosX());
        fakeRobot.setPosY(robot.getPosY());
        fakeRobot.setHeading(robot.getHeading());
        List<String> returnPath = AlgorithmRunner.runAstar(robot.getPosX(), robot.getPosY(), START_X, START_Y, grid, fakeRobot);

        if (returnPath != null) {
            System.out.println("RUNNING A* SEARCH!");
            System.out.println(returnPath.toString());

            for (String action : returnPath) {
                robot.sense(realRun);
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                            MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                                    robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                if(!realRun)
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
        }else {
            System.out.println("FASTEST PATH NOT FOUND!!");
        }

        System.out.println("EXPLORATION COMPLETED!");
        System.out.println("PERCENTAGE OF AREA EXPLORED: " + grid.checkExploredPercentage() + "%!");
    }

    private void leftWallFollower(Robot robot, Grid grid, boolean realRun){
        if (robot.isObstacleAhead()) {
            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                System.out.println("OBSTACLE DETECTED! (ALL 3 SIDES) U-TURNING");
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "U");
                robot.turn(RIGHT);
                //stepTaken();
                robot.turn(RIGHT);
                if (!realRun)
                    stepTaken();
            } else if (robot.isObstacleLeft()) {
                System.out.println("OBSTACLE DETECTED! (FRONT + LEFT) TURNING RIGHT");
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                robot.turn(RIGHT);
                if (!realRun)
                    stepTaken();

            } else {
                System.out.println("OBSTACLE DETECTED! (FRONT) TURNING LEFT");
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                if (!realRun)
                    stepTaken();
            }
            // sense here
            robot.sense(realRun);
            if (realRun)
                SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                        MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                                robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
            System.out.println("-----------------------------------------------");
        } else if (!robot.isObstacleLeft()) {
            System.out.println("NO OBSTACLES ON THE LEFT! TURNING LEFT");
            if (realRun)
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
            robot.turn(LEFT);
            if (!realRun)
                stepTaken();
            // sense here
            robot.sense(realRun);
            if (realRun)
                SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                        MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                                robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
            System.out.println("-----------------------------------------------");
        }
        if (realRun)
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M1");
        robot.move();
        // sense here
        robot.sense(realRun);
        if (!realRun)
            stepTaken();
        if (realRun) {
            SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                    MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                            robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
        }
    }

    private boolean checkUnexplored(Robot robot, Grid grid, int x, int y, boolean realRun){
        Robot fakeRobot = new Robot(grid, new ArrayList<>());
        fakeRobot.setPosX(robot.getPosX());
        fakeRobot.setPosY(robot.getPosY());
        fakeRobot.setHeading(robot.getHeading());
        List<String> returnPath = AlgorithmRunner.runAstar(robot.getPosX(), robot.getPosY(), x, y, grid, fakeRobot);
        if (returnPath != null) {
            System.out.println("Algorithm finished, executing actions");
            System.out.println(returnPath.toString());

            for (String action : returnPath) {
                robot.sense(realRun);
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                            MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                                    robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                if (!realRun)
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
}

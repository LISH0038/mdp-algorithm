package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;
import model.util.MessageGenerator;
import model.util.SocketMgr;

import static constant.CommConstants.TARGET_ANDROID;
import static constant.CommConstants.TARGET_ARDUINO;
import static constant.RobotConstants.LEFT;
import static constant.RobotConstants.RIGHT;

/**
 * Algorithm for exploration phase (full exploration)
 */
public class ExplorationAlgorithmRunner implements AlgorithmRunner {

    private int sleepDuration;
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

        // sense once first
        if (realRun)
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "S");
        robot.sense(realRun);
        if (realRun)
            SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                    MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                            robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));

        // main loop
        while (!endZoneFlag || !startZoneFlag) {
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
            if(Grid.isInEndZone(robot.getPosX(), robot.getPosY())){
                endZoneFlag = true;
            }
            if(endZoneFlag && Grid.isInStartZone(robot.getPosX()+2, robot.getPosY())){
                startZoneFlag = true;
            }
        }

        System.out.println("EXPLORATION COMPLETED!");
        System.out.println("PERCENTAGE OF AREA EXPLORED: " + grid.checkExploredPercentage() + "%!");
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

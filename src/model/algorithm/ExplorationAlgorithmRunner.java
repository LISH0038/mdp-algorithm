package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;
import model.util.MessageGenerator;
import model.util.SocketMgr;

import static constant.CommConstants.TARGET_ANDROID;
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
            String msg = SocketMgr.getInstance().receiveMessage();
            while (!msg.equals("exs")) {
                msg = SocketMgr.getInstance().receiveMessage();
            }
        }
        runExplorationAlgorithmThorough(grid, robot, realRun);
        grid.generateDescriptor();
    }

    private void runExplorationAlgorithmThorough(Grid grid, Robot robot, boolean realRun) {
        // MOVE OVER TO TOP LEFT CORNER OF ARENA.
        boolean endZoneFlag = false;
        boolean startZoneFlag = false;
        while (!endZoneFlag || !startZoneFlag) {
            robot.sense();
            if (realRun)
                SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                        MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                                robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
            if (robot.isObstacleAhead()) {
                if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                    System.out.println("OBSTACLE DETECTED! (ALL 3 SIDES) U-TURNING");
                    robot.turn(RIGHT);
                    stepTaken();
                    robot.turn(RIGHT);
                    stepTaken();
                } else if (robot.isObstacleLeft()) {
                    System.out.println("OBSTACLE DETECTED! (FRONT + LEFT) TURNING RIGHT");
                    robot.turn(RIGHT);
                    stepTaken();
                } else {
                    System.out.println("OBSTACLE DETECTED! (FRONT) TURNING LEFT");
                    robot.turn(LEFT);
                    stepTaken();
                }
                robot.sense();
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                            MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                                    robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                System.out.println("-----------------------------------------------");
            } else if (!robot.isObstacleLeft()) {
                System.out.println("NO OBSTACLES ON THE LEFT! TURNING LEFT");
                robot.turn(LEFT);
                stepTaken();
                robot.sense();
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                            MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                                    robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                System.out.println("-----------------------------------------------");
            }
            robot.move();
            if (realRun)
                SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                        MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid(),
                                robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
            stepTaken();
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

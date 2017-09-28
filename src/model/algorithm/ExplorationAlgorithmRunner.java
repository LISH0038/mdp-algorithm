package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;
import model.util.MessageGenerator;
import model.util.SocketMgr;

import static constant.CommConstants.TARGET_ANDROID;
import static constant.RobotConstants.LEFT;
import static constant.RobotConstants.RIGHT;

/**
 * Created by koallen on 27/8/17.
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
        //String msg = SocketMgr.getInstance().receiveMessage();
        //while (!msg.equals("exs")) {
        //    msg = SocketMgr.getInstance().receiveMessage();
        //}
        runExplorationAlgorithmThorough(grid, robot);
        grid.generateDescriptor();
    }

    public void runExplorationAlgorithmThorough(Grid grid, Robot robot) {
        // MOVE OVER TO TOP LEFT CORNER OF ARENA.
        while (grid.checkExploredPercentage() != 100) {
            robot.sense();
            //SocketMgr.getInstance().sendMessage(TARGET_ANDROID, MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid()));
            /*
            MAKE IT MOVE SLOWLY SO CAN SEE STEP BY STEP MOVEMENT
             */
            try {
                Thread.sleep(sleepDuration);
            } catch (Exception e) {
            }
            if (robot.isObstacleAhead()) {
                if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                    System.out.println("OBSTACLE DETECTED! (ALL 3 SIDES) U-TURNING");
                    robot.turn(RIGHT);
                    robot.turn(RIGHT);
                } else if (robot.isObstacleLeft()) {
                    System.out.println("OBSTACLE DETECTED! (FRONT + LEFT) TURNING RIGHT");
                    robot.turn(RIGHT);
                } else {
                    System.out.println("OBSTACLE DETECTED! (FRONT) TURNING LEFT");
                    robot.turn(LEFT);
                }
                robot.sense();
                //SocketMgr.getInstance().sendMessage(TARGET_ANDROID, MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid()));
                System.out.println("-----------------------------------------------");
            } else if (!robot.isObstacleLeft()) {
                System.out.println("NO OBSTACLES ON THE LEFT! TURNING LEFT");
                robot.turn(LEFT);
                robot.sense();
                //SocketMgr.getInstance().sendMessage(TARGET_ANDROID, MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid()));
                System.out.println("-----------------------------------------------");
            }
            robot.move();
        }

        while (!Grid.isInStartZone(robot.getPosX() + 2, robot.getPosY())) {
            robot.sense();
            //SocketMgr.getInstance().sendMessage(TARGET_ANDROID, MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid()));
            /*
            MAKE IT MOVE SLOWLY SO CAN SEE STEP BY STEP MOVEMENT
             */
            try {
                Thread.sleep(sleepDuration);
            } catch (Exception e) {
            }
            if (robot.isObstacleAhead()) {
                if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                    System.out.println("OBSTACLE DETECTED! (ALL 3 SIDES) U-TURNING");
                    robot.turn(RIGHT);
                    robot.turn(RIGHT);
                } else if (robot.isObstacleLeft()) {
                    System.out.println("OBSTACLE DETECTED! (FRONT + LEFT) TURNING RIGHT");
                    robot.turn(RIGHT);
                } else {
                    System.out.println("OBSTACLE DETECTED! (FRONT) TURNING LEFT");
                    robot.turn(LEFT);
                }
                robot.sense();
                //SocketMgr.getInstance().sendMessage(TARGET_ANDROID, MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid()));
                System.out.println("-----------------------------------------------");
            } else if (!robot.isObstacleLeft()) {
                System.out.println("NO OBSTACLES ON THE LEFT! TURNING LEFT");
                robot.turn(LEFT);
                robot.sense();
                //SocketMgr.getInstance().sendMessage(TARGET_ANDROID, MessageGenerator.generateMapDescriptorMsg(grid.generateForAndroid()));
                System.out.println("-----------------------------------------------");
            }
            robot.move();
        }

        System.out.println("EXPLORATION COMPLETED!");
        System.out.println("PERCENTAGE OF AREA EXPLORED: " + grid.checkExploredPercentage() + "%!");
    }
}

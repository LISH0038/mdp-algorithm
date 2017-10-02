package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;
import model.entity.Cell;
import model.util.SocketMgr;

import javax.swing.*;
import java.util.*;

import static constant.RobotConstants.LEFT;
import static constant.RobotConstants.RIGHT;

public class CoverageExplorationAlgorithmRunner implements AlgorithmRunner{

    private int sleepDuration;
    public CoverageExplorationAlgorithmRunner(int speed){
        sleepDuration = 1000 / speed;
    }

    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        grid.reset();
        robot.reset();
        if (realRun) {
            String msg = SocketMgr.getInstance().receiveMessage();
            while (!msg.equals("exs")) {
                msg = SocketMgr.getInstance().receiveMessage();
            }
        }
        int coveragePercentage = 0;
        do{
            try{
                String input = JOptionPane.showInputDialog(null, "Please enter the exploration percentage:", "Enter Percentage", JOptionPane.INFORMATION_MESSAGE);
                if(input.equals(JOptionPane.CANCEL_OPTION)){
                    break;
                }else{
                    coveragePercentage = Integer.parseInt(input);
                }
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please enter an integer more than 0!", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }while(coveragePercentage == 0);
        coverageLimitedAlgorithm(grid, robot, coveragePercentage, realRun);
        grid.generateDescriptor();
    }

    private void coverageLimitedAlgorithm(Grid grid, Robot robot, int coveragePercentage, boolean realRun){
        LinkedList<Cell> pathTaken = new LinkedList<>();

        while (grid.checkExploredPercentage() < coveragePercentage) {
            Cell position = new Cell(robot.getPosX(), robot.getPosY());
            pathTaken.push(position);
            robot.sense(realRun);
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
                robot.sense(realRun);
                System.out.println("-----------------------------------------------");
            } else if (!robot.isObstacleLeft()) {
                System.out.println("NO OBSTACLES ON THE LEFT! TURNING LEFT");
                robot.turn(LEFT);
                stepTaken();
                robot.sense(realRun);
                System.out.println("-----------------------------------------------");
            }
            robot.move();
            stepTaken();
        }


        while(!pathTaken.isEmpty()){
            Cell location = pathTaken.pop();

            if(location.getX() > robot.getPosX()){
                switch(robot.getHeading()){
                    case 0: // NORTH
                        robot.turn(RIGHT);
                        stepTaken();
                        break;
                    case 1: // EAST
                        break;
                    case 2: // SOUTH
                        robot.turn(LEFT);
                        stepTaken();
                        break;
                    case 3: // WEST
                        robot.turn(RIGHT);
                        stepTaken();
                        robot.turn(RIGHT);
                        stepTaken();
                        break;
                    default:
                        break;
                }
            }else if(location.getX() < robot.getPosX()){
                switch(robot.getHeading()){
                    case 0: // NORTH
                        robot.turn(LEFT);
                        stepTaken();
                        break;
                    case 1: // EAST
                        robot.turn(RIGHT);
                        stepTaken();
                        robot.turn(RIGHT);
                        stepTaken();
                        break;
                    case 2: // SOUTH
                        robot.turn(RIGHT);
                        stepTaken();
                        break;
                    case 3: // WEST
                        break;
                    default:
                        break;
                }
            }

            if(location.getY() > robot.getPosY()){
                switch(robot.getHeading()){
                    case 0: // NORTH
                        robot.turn(RIGHT);
                        stepTaken();
                        robot.turn(RIGHT);
                        stepTaken();
                        break;
                    case 1: // EAST
                        robot.turn(RIGHT);
                        stepTaken();
                        break;
                    case 2: // SOUTH
                        break;
                    case 3: // WEST
                        robot.turn(LEFT);
                        stepTaken();
                        break;
                    default:
                        break;
                }
            }else if(location.getY() < robot.getPosY()){
                switch(robot.getHeading()){
                    case 0: // NORTH
                        break;
                    case 1: // EAST
                        robot.turn(LEFT);
                        stepTaken();
                        break;
                    case 2: // SOUTH
                        robot.turn(LEFT);
                        stepTaken();
                        robot.turn(LEFT);
                        stepTaken();
                        break;
                    case 3: // WEST
                        robot.turn(RIGHT);
                        stepTaken();
                        break;
                    default:
                        break;
                }
            }
            robot.move();
            stepTaken();
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

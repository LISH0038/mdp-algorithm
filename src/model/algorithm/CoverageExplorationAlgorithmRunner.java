package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;
import model.entity.Cell;

import javax.swing.*;
import java.util.*;

import static constant.RobotConstants.LEFT;
import static constant.RobotConstants.RIGHT;

public class CoverageExplorationAlgorithmRunner implements AlgorithmRunner{

    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        // TODO: MAKE COVERAGEPERCENTAGE INTO TEXTBOX AS USER INPUT
        int coveragePercentage = 0;
        do{
            try{
                coveragePercentage = Integer.parseInt(JOptionPane.showInputDialog(null, "Please enter the exploration percentage:", "Enter Percentage", JOptionPane.INFORMATION_MESSAGE));
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please enter an integer more than 0!", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }while(coveragePercentage == 0);
        LinkedList<Cell> pathTaken = new LinkedList<Cell>();

        while (grid.checkExploredPercentage() < coveragePercentage) {
            Cell position = new Cell(robot.getPosX(), robot.getPosY());
            pathTaken.push(position);
            robot.sense();
            /*
            MAKE IT MOVE SLOWLY SO CAN SEE STEP BY STEP MOVEMENT
             */
            try {
                Thread.sleep(200);
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
                System.out.println("-----------------------------------------------");
            } else if (!robot.isObstacleLeft()) {
                System.out.println("NO OBSTACLES ON THE LEFT! TURNING LEFT");
                robot.turn(LEFT);
                robot.sense();
                System.out.println("-----------------------------------------------");
            }
            robot.move();
        }


        while(!pathTaken.isEmpty()){
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
            Cell location = pathTaken.pop();

            if(location.getX() > robot.getPosX()){
                switch(robot.getHeading()){
                    case 0: // NORTH
                        robot.turn(RIGHT);
                        break;
                    case 1: // EAST
                        break;
                    case 2: // SOUTH
                        robot.turn(LEFT);
                        break;
                    case 3: // WEST
                        robot.turn(RIGHT);
                        robot.turn(RIGHT);
                        break;
                    default:
                        break;
                }
            }else if(location.getX() < robot.getPosX()){
                switch(robot.getHeading()){
                    case 0: // NORTH
                        robot.turn(LEFT);
                        break;
                    case 1: // EAST
                        robot.turn(RIGHT);
                        robot.turn(RIGHT);
                        break;
                    case 2: // SOUTH
                        robot.turn(RIGHT);
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
                        robot.turn(RIGHT);
                        break;
                    case 1: // EAST
                        robot.turn(RIGHT);
                        break;
                    case 2: // SOUTH
                        break;
                    case 3: // WEST
                        robot.turn(LEFT);
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
                        break;
                    case 2: // SOUTH
                        robot.turn(LEFT);
                        robot.turn(LEFT);
                        break;
                    case 3: // WEST
                        robot.turn(RIGHT);
                        break;
                    default:
                        break;
                }
            }
            robot.move();
        }
    }

}
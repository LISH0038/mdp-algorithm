package model.algorithm;
import model.entity.Grid;
import model.entity.Robot;
import model.entity.Cell;

import javax.swing.*;
import java.util.*;

import static constant.RobotConstants.LEFT;
import static constant.RobotConstants.RIGHT;

public class TimeExplorationAlgorithmRunner implements AlgorithmRunner{

    private int sleepDuration;
    public TimeExplorationAlgorithmRunner(int speed){
        sleepDuration = 1000 / speed;
    }

    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        grid.reset();
        robot.reset();
        int minutes = -1;
        int seconds = -1;
        do{
            try{
                String input = JOptionPane.showInputDialog(null, "Please enter the time limit in MINUTES:", "Enter Time Limit (Minutes)", JOptionPane.INFORMATION_MESSAGE);
                if(input.equals(JOptionPane.CANCEL_OPTION)){
                    break;
                }else{
                    minutes = Integer.parseInt(input);
                }
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please enter an integer!", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }while(minutes < 0);

        do{
            try{
                String input = JOptionPane.showInputDialog(null, "Please enter the time limit in SECONDS:", "Enter Time Limit (Seconds)", JOptionPane.INFORMATION_MESSAGE);
                if(input.equals(JOptionPane.CANCEL_OPTION)){
                    break;
                }else{
                    seconds = Integer.parseInt(input);
                }
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please enter an integer!", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }while(seconds < 0);

        int totalTime = (minutes*60) + seconds;
        System.out.println("Total time: " + totalTime + " seconds");
        timeLimitedAlgorithm(grid, robot, totalTime);
        grid.generateDescriptor();
    }

    public void timeLimitedAlgorithm(Grid grid, Robot robot, int totalTime){
        LinkedList<Cell> pathTaken = new LinkedList<Cell>();
        System.out.println("Time-Limit = "+totalTime+" Seconds.");
        int millisecondsTotal = totalTime * 1000;
        while (millisecondsTotal > 0 && grid.checkExploredPercentage() < 100) {
            Cell position = new Cell(robot.getPosX(), robot.getPosY());
            pathTaken.push(position);
            robot.sense();
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
                    millisecondsTotal = millisecondsTotal - (sleepDuration * 2);
                } else if (robot.isObstacleLeft()) {
                    System.out.println("OBSTACLE DETECTED! (FRONT + LEFT) TURNING RIGHT");
                    robot.turn(RIGHT);
                    millisecondsTotal = millisecondsTotal - sleepDuration;
                } else {
                    System.out.println("OBSTACLE DETECTED! (FRONT) TURNING LEFT");
                    robot.turn(LEFT);
                    millisecondsTotal = millisecondsTotal - sleepDuration;
                }
                robot.sense();
                System.out.println("-----------------------------------------------");
            } else if (!robot.isObstacleLeft()) {
                System.out.println("NO OBSTACLES ON THE LEFT! TURNING LEFT");
                robot.turn(LEFT);
                millisecondsTotal = millisecondsTotal - sleepDuration;
                robot.sense();
                System.out.println("-----------------------------------------------");
            }
            robot.move();
            millisecondsTotal = millisecondsTotal - sleepDuration;
        }
        System.out.println("Time's Up! Moving back to start point.");

        while(!pathTaken.isEmpty()){
            try {
                Thread.sleep(sleepDuration);
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

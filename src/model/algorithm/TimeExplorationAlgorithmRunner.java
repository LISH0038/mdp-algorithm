package model.algorithm;
import model.entity.Grid;
import model.entity.Robot;
import model.entity.Cell;
import model.util.SocketMgr;

import javax.swing.*;
import java.util.*;

import static constant.RobotConstants.LEFT;
import static constant.RobotConstants.RIGHT;

/**
 * Algorithm for exploration (time-limited)
 */

public class TimeExplorationAlgorithmRunner implements AlgorithmRunner{

    private int sleepDuration;
    public TimeExplorationAlgorithmRunner(int speed){
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

    private void timeLimitedAlgorithm(Grid grid, Robot robot, int totalTime){
        LinkedList<Cell> pathTaken = new LinkedList<>();
        System.out.println("Time-Limit = "+totalTime+" Seconds.");
        int millisecondsTotal = totalTime * 1000;
        boolean endZoneFlag = false;
        boolean startZoneFlag = false;
        while (millisecondsTotal > 0 && (!endZoneFlag || !startZoneFlag)) {
            Cell position = new Cell(robot.getPosX(), robot.getPosY());
            pathTaken.push(position);
            robot.sense();
            if (robot.isObstacleAhead()) {
                if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                    System.out.println("OBSTACLE DETECTED! (ALL 3 SIDES) U-TURNING");
                    robot.turn(RIGHT);
                    stepTaken();
                    robot.turn(RIGHT);
                    stepTaken();
                    millisecondsTotal = millisecondsTotal - (sleepDuration * 2);
                } else if (robot.isObstacleLeft()) {
                    System.out.println("OBSTACLE DETECTED! (FRONT + LEFT) TURNING RIGHT");
                    robot.turn(RIGHT);
                    stepTaken();
                    millisecondsTotal = millisecondsTotal - sleepDuration;
                } else {
                    System.out.println("OBSTACLE DETECTED! (FRONT) TURNING LEFT");
                    robot.turn(LEFT);
                    stepTaken();
                    millisecondsTotal = millisecondsTotal - sleepDuration;
                }
                robot.sense();
                System.out.println("-----------------------------------------------");
            } else if (!robot.isObstacleLeft()) {
                System.out.println("NO OBSTACLES ON THE LEFT! TURNING LEFT");
                robot.turn(LEFT);
                stepTaken();
                millisecondsTotal = millisecondsTotal - sleepDuration;
                robot.sense();
                System.out.println("-----------------------------------------------");
            }
            robot.move();
            stepTaken();
            millisecondsTotal = millisecondsTotal - sleepDuration;
            System.out.println(millisecondsTotal);
            if(Grid.isInEndZone(robot.getPosX(), robot.getPosY())){
                endZoneFlag = true;
            }
            if(endZoneFlag && Grid.isInStartZone(robot.getPosX()+2, robot.getPosY())){
                startZoneFlag = true;
            }
        }
        System.out.println("Time's Up! Moving back to start point.");
        if(!startZoneFlag){
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

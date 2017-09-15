package model.algorithm;

import model.entity.Grid;
import model.entity.Robot;

import static constant.RobotConstants.LEFT;
import static constant.RobotConstants.RIGHT;

/**
 * Created by koallen on 27/8/17.
 */
public class ExplorationAlgorithmRunner implements AlgorithmRunner {

    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        //runExplorationAlgorithmFastest(grid, robot);
        runExplorationAlgorithmThorough(grid, robot);
    }

    public void runExplorationAlgorithmThorough(Grid grid, Robot robot) {
        // MOVE OVER TO TOP LEFT CORNER OF ARENA.
        while (grid.checkExploredPercentage() != 100) {
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

        while (!Grid.isInStartZone(robot.getPosX() + 2, robot.getPosY())) {
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

        System.out.println("EXPLORATION COMPLETED!");
        System.out.println("PERCENTAGE OF AREA EXPLORED: " + grid.checkExploredPercentage() + "%!");
    }

    public void runExplorationAlgorithmFastest(Grid grid, Robot robot) {
        /*
        WHILE THE ROBOT IS NOT IN THE END ZONE, START EXPLORING.
         */
        while (!Grid.isInEndZone(robot.getPosX(), robot.getPosY())) {
            robot.sense();
            /*
            MAKE IT MOVE SLOWLY SO CAN SEE STEP BY STEP MOVEMENT
             */
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
            while (robot.isObstacleAhead()) {
                switch (robot.getHeading()) {
                    case 0: // NORTH
                        if (robot.getPosX() <= 11) {
                        /*
                        IF ROBOT IS AT LEFT OF ARENA FACING NORTH
                        */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("NORTH TO FACING SOUTH NOW");
                                robot.turn(RIGHT);
                                robot.turn(RIGHT);
                            } else if (robot.isObstacleRight()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + RIGHT)");
                                System.out.println("NORTH TO FACING WEST NOW");
                                robot.turn(LEFT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("NORTH TO FACING EAST NOW");
                                robot.turn(RIGHT);
                            }
                        } else {
                        /*
                        IF ROBOT IS AT RIGHT OF ARENA FACING NORTH
                        */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("NORTH TO FACING SOUTH NOW");
                                robot.turn(LEFT);
                                robot.turn(LEFT);
                            } else if (robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + LEFT)");
                                System.out.println("NORTH TO FACING EAST NOW");
                                robot.turn(RIGHT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("NORTH TO FACING WEST NOW");
                                robot.turn(LEFT);
                            }
                        }
                        break;
                    case 1: // EAST
                        if (robot.getPosY() > 14) {
                        /*
                        IF ROBOT IS AT TOP OF ARENA FACING EAST
                         */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("EAST TO FACING WEST NOW");
                                robot.turn(RIGHT);
                                robot.turn(RIGHT);
                            } else if (robot.isObstacleRight()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + RIGHT)");
                                System.out.println("EAST TO FACING NORTH NOW");
                                robot.turn(LEFT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("EAST TO FACING SOUTH NOW");
                                robot.turn(RIGHT);
                            }
                        } else {
                        /*
                        IF ROBOT IS AT BOTTOM OF ARENA FACING EAST
                         */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("EAST TO FACING WEST NOW");
                                robot.turn(LEFT);
                                robot.turn(LEFT);
                            } else if (robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + LEFT)");
                                System.out.println("EAST TO FACING SOUTH NOW");
                                robot.turn(RIGHT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("EAST TO FACING NORTH NOW");
                                robot.turn(LEFT);
                            }
                        }
                        break;
                    case 2: // SOUTH
                        if (robot.getPosX() <= 1) {
                        /*
                        IF ROBOT IS AT LEFT OF ARENA FACING SOUTH
                        */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("SOUTH TO FACING NORTH NOW");
                                robot.turn(LEFT);
                                robot.turn(LEFT);
                            } else if (robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + LEFT)");
                                System.out.println("SOUTH TO FACING WEST NOW");
                                robot.turn(RIGHT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("SOUTH TO FACING EAST NOW");
                                robot.turn(LEFT);
                            }
                        } else {
                        /*
                        IF ROBOT IS AT RIGHT OF ARENA FACING SOUTH
                        */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("SOUTH TO FACING NORTH NOW");
                                robot.turn(RIGHT);
                                robot.turn(RIGHT);
                            } else if (robot.isObstacleRight()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + RIGHT)");
                                System.out.println("SOUTH TO FACING EAST NOW");
                                robot.turn(LEFT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("SOUTH TO FACING WEST NOW");
                                robot.turn(RIGHT);
                            }
                        }
                        break;
                    case 3: // WEST
                        if (robot.getPosY() > 14) {
                        /*
                        IF ROBOT IS AT TOP OF ARENA FACING WEST
                         */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("WEST TO FACING EAST NOW");
                                robot.turn(LEFT);
                                robot.turn(LEFT);
                            } else if (robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + RIGHT)");
                                System.out.println("WEST TO FACING NORTH NOW");
                                robot.turn(RIGHT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("WEST TO FACING SOUTH NOW");
                                robot.turn(LEFT);
                            }
                        } else {
                        /*
                        IF ROBOT IS AT BOTTOM OF ARENA FACING WEST
                         */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("WEST TO FACING EAST NOW");
                                robot.turn(RIGHT);
                                robot.turn(RIGHT);
                            } else if (robot.isObstacleRight()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + RIGHT)");
                                System.out.println("WEST TO FACING SOUTH NOW");
                                robot.turn(LEFT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("WEST TO FACING NORTH NOW");
                                robot.turn(RIGHT);
                            }
                        }
                        break;
                    default:
                        robot.move();
                        break;
                }
                robot.sense();
                System.out.println("-----------------------------------------");
            }
            robot.move();
        }

        while (!Grid.isInStartZone(robot.getPosX() + 2, robot.getPosY())) {
            robot.sense();
            /*
            MAKE IT MOVE SLOWLY SO CAN SEE STEP BY STEP MOVEMENT
             */
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
            while (robot.isObstacleAhead()) {
                switch (robot.getHeading()) {
                    case 0: // NORTH
                        if (robot.getPosX() <= 1) {
                        /*
                        IF ROBOT IS AT LEFT OF ARENA FACING NORTH
                        */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("NORTH TO FACING SOUTH NOW");
                                robot.turn(RIGHT);
                                robot.turn(RIGHT);
                            } else if (robot.isObstacleRight()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + RIGHT)");
                                System.out.println("NORTH TO FACING WEST NOW");
                                robot.turn(LEFT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("NORTH TO FACING EAST NOW");
                                robot.turn(RIGHT);
                            }
                        } else {
                        /*
                        IF ROBOT IS AT RIGHT OF ARENA FACING NORTH
                        */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("NORTH TO FACING SOUTH NOW");
                                robot.turn(LEFT);
                                robot.turn(LEFT);
                            } else if (robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + LEFT)");
                                System.out.println("NORTH TO FACING EAST NOW");
                                robot.turn(RIGHT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("NORTH TO FACING WEST NOW");
                                robot.turn(LEFT);
                            }
                        }
                        break;
                    case 1: // EAST
                        if (robot.getPosY() <= 16) {
                        /*
                        IF ROBOT IS AT TOP OF ARENA FACING EAST
                         */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("EAST TO FACING WEST NOW");
                                robot.turn(RIGHT);
                                robot.turn(RIGHT);
                            } else if (robot.isObstacleRight()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + RIGHT)");
                                System.out.println("EAST TO FACING NORTH NOW");
                                robot.turn(LEFT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("EAST TO FACING SOUTH NOW");
                                robot.turn(RIGHT);
                            }
                        } else {
                        /*
                        IF ROBOT IS AT BOTTOM OF ARENA FACING EAST
                         */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("EAST TO FACING WEST NOW");
                                robot.turn(LEFT);
                                robot.turn(LEFT);
                            } else if (robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + LEFT)");
                                System.out.println("EAST TO FACING SOUTH NOW");
                                robot.turn(RIGHT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("EAST TO FACING NORTH NOW");
                                robot.turn(LEFT);
                            }
                        }
                        break;
                    case 2: // SOUTH
                        if (robot.getPosX() <= 1) {
                        /*
                        IF ROBOT IS AT LEFT OF ARENA FACING SOUTH
                        */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("SOUTH TO FACING NORTH NOW");
                                robot.turn(LEFT);
                                robot.turn(LEFT);
                            } else if (robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + LEFT)");
                                System.out.println("SOUTH TO FACING WEST NOW");
                                robot.turn(RIGHT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("SOUTH TO FACING EAST NOW");
                                robot.turn(LEFT);
                            }
                        } else {
                        /*
                        IF ROBOT IS AT RIGHT OF ARENA FACING SOUTH
                        */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("SOUTH TO FACING NORTH NOW");
                                robot.turn(RIGHT);
                                robot.turn(RIGHT);
                            } else if (robot.isObstacleRight()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + RIGHT)");
                                System.out.println("SOUTH TO FACING EAST NOW");
                                robot.turn(LEFT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("SOUTH TO FACING WEST NOW");
                                robot.turn(RIGHT);
                            }
                        }
                        break;
                    case 3: // WEST
                        if (robot.getPosY() <= 16) {
                        /*
                        IF ROBOT IS AT TOP OF ARENA FACING WEST
                         */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("WEST TO FACING EAST NOW");
                                robot.turn(LEFT);
                                robot.turn(LEFT);
                            } else if (robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + RIGHT)");
                                System.out.println("WEST TO FACING NORTH NOW");
                                robot.turn(RIGHT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("WEST TO FACING SOUTH NOW");
                                robot.turn(LEFT);
                            }
                        } else {
                        /*
                        IF ROBOT IS AT BOTTOM OF ARENA FACING WEST
                         */
                            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                                System.out.println("OBSTACLE DETECTED, U-TURNING! (ALL 3 SIDES)");
                                System.out.println("WEST TO FACING EAST NOW");
                                robot.turn(RIGHT);
                                robot.turn(RIGHT);
                            } else if (robot.isObstacleRight()) {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT + RIGHT)");
                                System.out.println("WEST TO FACING SOUTH NOW");
                                robot.turn(LEFT);
                            } else {
                                System.out.println("OBSTACLE DETECTED, TURNING. (FRONT)");
                                System.out.println("WEST TO FACING NORTH NOW");
                                robot.turn(RIGHT);
                            }
                        }
                        break;
                    default:
                        robot.move();
                        break;
                }
                robot.sense();
                System.out.println("-----------------------------------------");
            }
            robot.move();
        }

        System.out.println("EXPLORATION COMPLETED!");
        System.out.println("PERCENTAGE OF AREA EXPLORED: " + grid.checkExploredPercentage() + "%!");
    }
}

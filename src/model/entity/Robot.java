package model.entity;

import java.util.List;
import java.util.Observable;

import static constant.RobotConstants.*;

/**
 * Created by koallen on 25/8/17.
 */
public class Robot extends Observable {
    private int mPosX = START_POS_X;
    private int mPosY = START_POS_Y;
    private int mHeading = NORTH;
    private Grid mGrid;
    private List<Sensor> mSensors;

    public Robot(Grid grid, List<Sensor> sensors) {
        mGrid = grid;
        mSensors = sensors;
        for (Sensor sensor : sensors) {
            sensor.setRobot(this);
        }
    }

    public int getPosX() {
        return mPosX;
    }

    public int getPosY() {
        return mPosY;
    }

    public int getHeading() {
        return mHeading;
    }

    public boolean isObstacleAhead() {
        for (int i = 0; i < ROBOT_SIZE; i++) {
            if (mHeading == NORTH) {
                // DIRECTLY IN FRONT OF ROBOT
                if (mGrid.getIsObstacle(mPosX + i, mPosY - 1)) {
                    return true;
                }
            } else if (mHeading == SOUTH) {
                // DIRECTLY IN FRONT OF ROBOT
                if (mGrid.getIsObstacle(mPosX + i, mPosY + 3)) {
                    return true;
                }
            } else if (mHeading == EAST) {
                // DIRECTLY IN FRONT OF ROBOT
                if (mGrid.getIsObstacle(mPosX + 3, mPosY + i)) {
                    return true;
                }
            } else if (mHeading == WEST) {
                // DIRECTLY IN FRONT OF ROBOT
                if (mGrid.getIsObstacle(mPosX - 1, mPosY + i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isObstacleRight() {
        for (int i = 0; i < ROBOT_SIZE; i++) {
            if (mHeading == NORTH) {
                // DIRECTLY BESIDE OF ROBOT
                if (mGrid.getIsObstacle(mPosX + 3, mPosY + i)) {
                    return true;
                }
            } else if (mHeading == SOUTH) {
                // DIRECTLY BESIDE OF ROBOT
                if (mGrid.getIsObstacle(mPosX - 1, mPosY + i)) {
                    return true;
                }
            } else if (mHeading == EAST) {
                // DIRECTLY BESIDE OF ROBOT
                if (mGrid.getIsObstacle(mPosX + i, mPosY + 3)) {
                    return true;
                }
            } else if (mHeading == WEST) {
                // DIRECTLY BESIDE OF ROBOT
                if (mGrid.getIsObstacle(mPosX + i, mPosY - 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isObstacleLeft() {
        for (int i = 0; i < ROBOT_SIZE; i++) {
            if (mHeading == NORTH) {
                // DIRECTLY BESIDE OF ROBOT
                if (mGrid.getIsObstacle(mPosX - 1, mPosY + i)) {
                    return true;
                }
            } else if (mHeading == SOUTH) {
                // DIRECTLY BESIDE OF ROBOT
                if (mGrid.getIsObstacle(mPosX + 3, mPosY + i)) {
                    return true;
                }
            } else if (mHeading == EAST) {
                // DIRECTLY BESIDE OF ROBOT
                if (mGrid.getIsObstacle(mPosX + i, mPosY - 1)) {
                    return true;
                }
            } else if (mHeading == WEST) {
                // DIRECTLY BESIDE OF ROBOT
                if (mGrid.getIsObstacle(mPosX + i, mPosY + 3)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void move() {
        // TODO: make sure it won't go beyond the arena
        if (mHeading == NORTH) { // Limit position to prevent wall crash
                mPosY--;
        } else if (mHeading == SOUTH) {// Limit position to prevent wall crash
                mPosY++;
        } else if (mHeading == WEST) { // Limit position to prevent wall crash
                mPosX--;
        } else if (mHeading == EAST) { // Limit position to prevent wall crash
                mPosX++;
        }
        setChanged();
        notifyObservers();
    }

    public void turn(int direction) {
        /*
        NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3, LEFT = 4, RIGHT = 5
         */
        if (direction == LEFT) {
            /*
            NORTH BECOMES WEST
            WEST BECOMES SOUTH
            SOUTH BECOMES EAST
            EAST BECOMES NORTH
             */
            mHeading += 4;
            mHeading = (mHeading - 1) % 4;
        } else if (direction == RIGHT) {
            /*
            NORTH BECOMES EAST
            EAST BECOMES SOUTH
            SOUTH BECOMES WEST
            WEST BECOMES NORTH
             */
            mHeading = (mHeading + 1) % 4;
        }
        setChanged();
        notifyObservers();
    }

    public void reset() {
        mPosX = START_POS_X;
        mPosY = START_POS_Y;
        mHeading = NORTH;
    }

    private void updateMap(int returnedDistance, int heading, int range, int x, int y) {
        int xToUpdate = x, yToUpdate = y;
        int distance = returnedDistance == 0 ? range : returnedDistance;
        boolean obstacleAhead = returnedDistance != 0;

        for (int i = 1; i <= distance; i++) {
            if (heading == NORTH) {
                yToUpdate = yToUpdate - 1;
            } else if (heading == SOUTH) {
                yToUpdate = yToUpdate + 1;
            } else if (heading == WEST) {
                xToUpdate = xToUpdate - 1;
            } else if (heading == EAST) {
                xToUpdate = xToUpdate + 1;
            }
            mGrid.setExplored(xToUpdate, yToUpdate, true);
            if (i == distance && obstacleAhead) {
                mGrid.setIsObstacle(xToUpdate, yToUpdate, true);
            } else {
                mGrid.setIsObstacle(xToUpdate, yToUpdate, false);
            }
        }
    }

    public void sense() {
        for (Sensor sensor : mSensors) {
            /*
            SENSE RETURNS 0 IF NO OBSTACLES IS DETECTED
             */
            int returnedDistance = sensor.sense(mGrid);
            int heading = sensor.getActualHeading();
            int range = sensor.getRange();
            int x = sensor.getActualPosX();
            int y = sensor.getActualPosY();
            updateMap(returnedDistance, heading, range, x, y);
        }
    }
}

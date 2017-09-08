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

    public int getCenterPosX() {
        return mPosX + 1;
    }

    public int getCenterPosY() {
        return mPosY + 1;
    }

    public int getHeading() {
        return mHeading;
    }

    public boolean isObstacleAhead() {
        for (int i = 0; i < ROBOT_SIZE; i++) {
            if (mHeading == NORTH) {
                if (mGrid.getIsObstacle(mPosX + i, mPosY - 1))
                    return true;
            } else if (mHeading == SOUTH) {
                if (mGrid.getIsObstacle(mPosX + i, mPosY + 3))
                    return true;
            } else if (mHeading == EAST) {
                if (mGrid.getIsObstacle(mPosX + 3, mPosY + i))
                    return true;
            } else if (mHeading == WEST) {
                if (mGrid.getIsObstacle(mPosX - 1, mPosY + i))
                    return true;
            }
        }
        return false;
    }

    public void move() {
        // TODO: make sure it won't go beyong the arena
        if (mHeading == NORTH)
            mPosY--;
        else if (mHeading == SOUTH)
            mPosY++;
        else if (mHeading == WEST)
            mPosX--;
        else if (mHeading == EAST)
            mPosX++;
        setChanged();
        notifyObservers();
    }

    public void turn(int direction) {
        if (direction == LEFT)
            mHeading = (mHeading + 3) % 4;  // java % is remainder!
        else if (direction == RIGHT)
            mHeading = (mHeading + 1) % 4;
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
            if (i == distance && obstacleAhead)
                mGrid.setIsObstacle(xToUpdate, yToUpdate, true);
            else
                mGrid.setIsObstacle(xToUpdate, yToUpdate, false);
        }
    }

    public void sense() {
        for (Sensor sensor : mSensors) {
            int returnedDistance = sensor.sense(mGrid);
            int heading = sensor.getActualHeading();
            int range = sensor.getRange();
            int x = sensor.getActualPosX();
            int y = sensor.getActualPosY();
            updateMap(returnedDistance, heading, range, x, y);
        }
    }
}

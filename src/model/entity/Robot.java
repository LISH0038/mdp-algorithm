package model.entity;

import java.util.Observable;

import static constant.RobotConstants.*;

/**
 * Created by koallen on 25/8/17.
 */
public class Robot extends Observable {
    private int mPosX = START_POS_X;
    private int mPosY = START_POS_Y;
    private int mHeading = NORTH;

    public int getPosX() {
        return mPosX;
    }

    public int getPosY() {
        return mPosY;
    }

    public int getHeading() {
        return mHeading;
    }

    public void move() {
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
            mHeading = (mHeading - 1) % 4;
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
}

package model.entity;

import static sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte1.other;

/**
 * Created by koallen on 25/8/17.
 */
public class Cell implements Comparable<Cell> {

    private int mX;
    private int mY;
    private int mDistance;
    private boolean mExplored = false;
    private boolean mIsObstacle = false;

    public Cell() {}

    public Cell(int x, int y) {
        mX = x;
        mY = y;
    }

    public void setExplored(boolean explored) {
        mExplored = explored;
    }

    public void setIsObstacle(boolean isObstacle) {
        mIsObstacle = isObstacle;
    }

    public boolean getExplored() {
        return mExplored;
    }

    public boolean getIsObstacle() {
        return mIsObstacle;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public void setDistance(int distance) {
        mDistance = distance;
    }

    public int getDistance() {
        return mDistance;
    }

    @Override
    public int compareTo(Cell o) {
        if (mDistance < o.getDistance())
            return -1;
        else if (mDistance > o.getDistance())
            return 1;
        else
            return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cell) {
            Cell otherCell = (Cell)obj;
            if (otherCell.getX() == getX() && otherCell.getY() == getY())
                return true;
        }
        return false;
    }
}

package model.entity;

/**
 * Created by koallen on 25/8/17.
 */
public class Cell {

    private boolean mExplored = false;
    private boolean mIsObstacle = false;

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
}

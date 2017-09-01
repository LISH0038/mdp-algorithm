package model.entity;

import static constant.RobotConstants.*;

/**
 * Created by koallen on 25/8/17.
 */
public class Sensor {

    private int mRange;
    private int mPosX;
    private int mPosY;
    private int mDirection;
    private Robot mRobot;

    public Sensor(int range, int posX, int posY, int direction) {
        mRange = range;
        mPosX = posX;
        mPosY = posY;
        mDirection = direction;
    }

    public int sense(Grid grid) {
        int absPosX = getActualPosX();
        int absPosY = getActualPosY();

        int actualDirection = getActualHeading();

        for (int i = 1; i <= mRange; i++) {
            if (actualDirection == NORTH) {
                if (grid.getIsObstacle(absPosX, absPosY - i))
                    return i;
            } else if (actualDirection == EAST) {
                if (grid.getIsObstacle(absPosX + i, absPosY))
                    return i;
            } else if (actualDirection == SOUTH) {
                if (grid.getIsObstacle(absPosX, absPosY + i))
                    return i;
            } else if (actualDirection == WEST) {
                if (grid.getIsObstacle(absPosX - i, absPosY))
                    return i;
            }
        }
        return 0;
    }

    public int getActualHeading() {
        int actualDirection = -1;
        if (mDirection == LEFT) {
            actualDirection = (mRobot.getHeading() - 1) % 4;
        } else if (mDirection == MIDDLE) {
            actualDirection = mRobot.getHeading();
        } else if (mDirection == RIGHT) {
            actualDirection = (mRobot.getHeading() + 1) % 4;
        }

        return actualDirection;
    }

    public int getActualPosX() {
        if (mRobot.getHeading() == NORTH) {
            return getCorrectedRobotPosX() + mPosX;
        } else if (mRobot.getHeading() == EAST) {
            return getCorrectedRobotPosX() - mPosY;
        } else if (mRobot.getHeading() == SOUTH) {
            return getCorrectedRobotPosX() - mPosX;
        } else if (mRobot.getHeading() == WEST) {
            return getCorrectedRobotPosX() + mPosY;
        }

        return 0;
    }

    public int getActualPosY() {
        if (mRobot.getHeading() == NORTH) {
            return getCorrectedRobotPosY() + mPosY;
        } else if (mRobot.getHeading() == EAST) {
            return getCorrectedRobotPosY() + mPosX;
        } else if (mRobot.getHeading() == SOUTH) {
            return getCorrectedRobotPosY() - mPosY;
        } else if (mRobot.getHeading() == WEST) {
            return getCorrectedRobotPosY() - mPosX;
        }

        return 0;
    }

    /**
     * As the sensor location is relative to the robot, the corrected robot location
     * (i.e. considering heading) needs to be calculated to be the base location for
     * calculating sensor location
     * @return
     */
    public int getCorrectedRobotPosX() {
        if (mRobot.getHeading() == EAST || mRobot.getHeading() == SOUTH)
            return mRobot.getPosX() + 2;
        return mRobot.getPosX();
    }

    /**
     * As the sensor location is relative to the robot, the corrected robot location
     * (i.e. considering heading) needs to be calculated to be the base location for
     * calculating sensor location
     * @return
     */
    public int getCorrectedRobotPosY() {
        if (mRobot.getHeading() == SOUTH || mRobot.getHeading() == WEST)
            return mRobot.getPosY() + 2;
        return mRobot.getPosY();
    }

    public int getRange() {
        return mRange;
    }

    public void setRobot(Robot robot) {
        mRobot = robot;
    }
}

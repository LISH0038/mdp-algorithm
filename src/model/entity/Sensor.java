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

    public Sensor(int range, int posX, int posY, int direction) {
        mRange = range;
        mPosX = posX;
        mPosY = posY;
        mDirection = direction;
    }

    public int sense(Grid grid, Robot robot) {
        int absPosX = robot.getPosX() + mPosX;
        int absPosY = robot.getPosY() + mPosY;

        int actualDirection = -1;
        if (mDirection == LEFT) {
            actualDirection = (robot.getHeading() - 1) % 4;
        } else if (mDirection == MIDDLE) {
            actualDirection = robot.getHeading();
        } else if (mDirection == RIGHT) {
            actualDirection = (robot.getHeading() + 1) % 4;
        }

        for (int i = 1; i <= mRange; i++) {
            if (actualDirection == NORTH) {
                if (grid.getCells()[absPosX][absPosY - i].getIsObstacle())
                    return i;
            } else if (actualDirection == EAST) {
                if (grid.getCells()[absPosX + i][absPosY].getIsObstacle())
                    return i;
            } else if (actualDirection == SOUTH) {
                if (grid.getCells()[absPosX][absPosY + i].getIsObstacle())
                    return i;
            } else if (actualDirection == WEST) {
                if (grid.getCells()[absPosX - i][absPosY].getIsObstacle())
                    return i;
            }
        }

        return 0;
    }
}

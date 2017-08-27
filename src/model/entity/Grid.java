package model.entity;

import java.util.Observable;

import static constant.MapConstants.MAP_COLS;
import static constant.MapConstants.MAP_ROWS;
import static constant.MapConstants.ZONE_SIZE;

/**
 * Created by koallen on 25/8/17.
 */
public class Grid extends Observable {

    private Cell[][] cells;

    public Grid() {
        cells = new Cell[MAP_COLS][MAP_ROWS];
        for (int x = 0; x < MAP_COLS; x++) {
            for (int y = 0; y < MAP_ROWS; y++) {
                cells[x][y] = new Cell();
            }
        }
        reset();
    }

    public Cell[][] getCells() {
        return cells;
    }

    public static boolean isInStartZone(int x, int y) {
        if (x < ZONE_SIZE && x >= 0
                && y < MAP_ROWS && y >= MAP_ROWS - ZONE_SIZE) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInEndZone(int x, int y) {
        if (x < MAP_COLS && x >= MAP_COLS - ZONE_SIZE
                && y < ZONE_SIZE && y >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setIsObstacle(int x, int y, boolean isObstacle) {
        cells[x][y].setIsObstacle(isObstacle);
        setChanged();
        notifyObservers();
    }

    public void setExplored(int x, int y, boolean explored) {
        cells[x][y].setExplored(explored);
        setChanged();
        notifyObservers();
    }

    public void loadFromDisk(String path) {

    }

    /**
     * Set all cells to unexplored
     */
    public void reset() {
        for (int x = 0; x < MAP_COLS; x++) {
            for (int y = 0; y < MAP_ROWS; y++) {
                if (!isInStartZone(x, y) && !isInEndZone(x, y))
                    cells[x][y].setExplored(false);
                else
                    cells[x][y].setExplored(true);
            }
        }
    }
}

package model.entity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Observable;

import static constant.MapConstants.*;

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
        return x < ZONE_SIZE && x >= 0
                && y < MAP_ROWS && y >= MAP_ROWS - ZONE_SIZE;
    }

    public static boolean isInEndZone(int x, int y) {
        return x < MAP_COLS && x >= MAP_COLS - ZONE_SIZE
                && y < ZONE_SIZE && y >= 0;
    }

    public boolean getIsObstacle(int x, int y) {
        return isOutOfArena(x, y) || cells[x][y].getIsObstacle();
    }

    public boolean isOutOfArena(int x, int y) {
        return x < 0 || y < 0 || x >= MAP_COLS || y >= MAP_ROWS;
    }

    public void setIsObstacle(int x, int y, boolean isObstacle) {
        if (isOutOfArena(x, y))
            return;
        cells[x][y].setIsObstacle(isObstacle);
        setChanged();
        notifyObservers();
    }

    public void setExplored(int x, int y, boolean explored) {
        if (isOutOfArena(x, y))
            return;
        cells[x][y].setExplored(explored);
        setChanged();
        notifyObservers();
    }

    public int checkExploredPercentage() {
        double totalCells = 0.0;
        double cellsExplored = 0.0;
        for (int x = 0; x < MAP_COLS; x++) {
            for (int y = 0; y < MAP_ROWS; y++) {
                if (cells[x][y].getExplored()) {
                    cellsExplored += 1;
                }
                totalCells += 1;
            }
        }
        return (int) Math.round((cellsExplored / totalCells) * 100);
    }

    public void loadFromDisk(String path) throws IOException {
        this.reset();

        BufferedReader reader = new BufferedReader(new FileReader(path));

        for (int i = 0; i < MAP_ROWS; i++) {
            String line = reader.readLine();
            String[] numberStrings = line.trim().split("\\s+");
            for (int j = 0; j < MAP_COLS; j++) {
                if (numberStrings[j].equals("1")) {
                    this.setIsObstacle(j, i, true);
                } else {
                    this.setIsObstacle(j, i, false);
                }
            }
        }
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

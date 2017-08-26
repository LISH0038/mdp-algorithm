package view;

import model.entity.Grid;

import javax.swing.*;
import java.awt.*;

/**
 * Created by koallen on 26/8/17.
 */
public class MapPanel extends JPanel {

    private static final int MAP_ROWS = 20;
    private static final int MAP_COLS = 15;
    private static final int CELL_SIZE = 30;
    private Grid mGrid;

    public MapPanel(Grid grid) {
        mGrid = grid;
        initializeMap();
    }

    private void initializeMap() {
        setPreferredSize(new Dimension(CELL_SIZE * MAP_COLS, CELL_SIZE * MAP_ROWS));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 0; i < MAP_ROWS; i++) {
            for (int j = 0; j < MAP_COLS; j++) {
                if (i <= 2 && j <= 2) {
                    g2d.setColor(Color.YELLOW);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        g2d.setColor(Color.MAGENTA);
        g2d.fillOval(4, 4, CELL_SIZE * 3 - 8, CELL_SIZE * 3 - 8);
    }
}

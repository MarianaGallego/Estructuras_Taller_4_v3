package view;

import model.GridFile;

import javax.swing.*;
import java.awt.*;

public class GridFilePanel extends JPanel {
    private GridFile grid;

    public GridFilePanel(GridFile grid) {
        this.grid = grid;
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        int cellSize = 100 / grid.getGridSize();

        g.setColor(Color.BLUE);
        int stepX = width / grid.getGridSize();
        int stepY = height / grid.getGridSize();

        // Dibujar la rejilla
        for (int i = 0; i <= grid.getGridSize(); i++) {
            int x = i * stepX;
            int y = i * stepY;
            g.drawLine(x, 0, x, height);
            g.drawLine(0, y, width, y);
        }

        // Dibujar puntos
        g.setColor(Color.RED);
        for (int[] p : grid.getAllPoints()) {
            int x = p[0] * width / 100;
            int y = p[1] * height / 100;
            g.fillOval(x - 3, y - 3, 6, 6);
        }
    }
}


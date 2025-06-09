package view;

import model.QuadTree;

import javax.swing.*;
import java.awt.*;

public class QuadTreePanel extends JPanel {
    private QuadTree tree;

    public QuadTreePanel(QuadTree tree) {
        this.tree = tree;
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        tree.draw(g, getWidth(), getHeight());

        // Dibujar puntos
        g.setColor(Color.RED);
        for (int[] p : tree.getAllPoints()) {
            int x = p[0] * getWidth() / 100;
            int y = p[1] * getHeight() / 100;
            g.fillOval(x - 3, y - 3, 6, 6);
        }
    }
}


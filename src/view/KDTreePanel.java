package view;

import model.KDTree;
import model.KDNode;

import javax.swing.*;
import java.awt.*;

public class KDTreePanel extends JPanel {
    private KDTree tree;

    public KDTreePanel(KDTree tree) {
        this.tree = tree;
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawKDTree(g, tree.getRoot(), 0, 0, 0, 100, 100); // Usamos coordenadas normalizadas
    }

    private void drawKDTree(Graphics g, KDNode node, int depth,
                            int minX, int minY, int maxX, int maxY) {
        if (node == null) return;

        int x = node.point[0];
        int y = node.point[1];

        int screenX = x * getWidth() / 100;
        int screenY = y * getHeight() / 100;

        // Dibuja punto
        g.setColor(Color.RED);
        g.fillOval(screenX - 3, screenY - 3, 6, 6);

        int axis = depth % 2;
        g.setColor(Color.BLUE);

        if (axis == 0) { // vertical
            int screenMinY = minY * getHeight() / 100;
            int screenMaxY = maxY * getHeight() / 100;
            g.drawLine(screenX, screenMinY, screenX, screenMaxY);

            drawKDTree(g, node.left, depth + 1, minX, minY, x, maxY);
            drawKDTree(g, node.right, depth + 1, x, minY, maxX, maxY);
        } else { // horizontal
            int screenMinX = minX * getWidth() / 100;
            int screenMaxX = maxX * getWidth() / 100;
            g.drawLine(screenMinX, screenY, screenMaxX, screenY);

            drawKDTree(g, node.left, depth + 1, minX, minY, maxX, y);
            drawKDTree(g, node.right, depth + 1, minX, y, maxX, maxY);
        }
    }

}

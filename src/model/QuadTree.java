package model;

import utils.MetricsTracker;

import java.util.List;

public class QuadTree {
    private QuadTreeNode root;
    private MetricsTracker metrics;

    public QuadTree(MetricsTracker metrics) {
        this.metrics = metrics;
        root = new QuadTreeNode(0, 100, 0, 100, metrics);
    }

    public MetricsTracker getMetrics() {
        return metrics;
    }

    public void insert(int[] point) {
        root.insert(point);
    }

    public List<int[]> getAllPoints() {
        return root.getPoints();
    }

    // Consulta PUNTUAL
    public boolean containsPoint(int[] point) {
        return root.containsPoint(point);
    }

    // Consulta por RANGO RECTANGULAR
    public List<int[]> rangeQuery(int xMin, int xMax, int yMin, int yMax) {
        return root.rangeQuery(xMin, xMax, yMin, yMax);
    }


    public void draw(java.awt.Graphics g, int width, int height) {
        root.draw(g, width, height);
    }
}


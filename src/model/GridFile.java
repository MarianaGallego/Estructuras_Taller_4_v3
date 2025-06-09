package model;

import utils.MetricsTracker;

import java.util.*;

public class GridFile {
    private MetricsTracker metrics;
    private int gridSize;
    private int cellSize;
    private Map<String, List<int[]>> grid;

    public GridFile(int gridSize, MetricsTracker metrics) {
        this.gridSize = gridSize; // por ejemplo: 10x10
        this.cellSize = 100 / gridSize;
        this.grid = new HashMap<>();
        this.metrics = metrics;
    }

    private String getCellKey(int x, int y) {
        int row = Math.min(x / cellSize, gridSize - 1);
        int col = Math.min(y / cellSize, gridSize - 1);
        return row + "," + col;
    }

    public void insert(int[] point) {
        metrics.incrementDiskAccess();

        String key = getCellKey(point[0], point[1]);
        grid.computeIfAbsent(key, k -> new ArrayList<>()).add(point);
    }

    public boolean containsPoint(int[] point) {
        metrics.incrementDiskAccess();

        String key = getCellKey(point[0], point[1]);
        List<int[]> cell = grid.getOrDefault(key, new ArrayList<>());
        for (int[] p : cell) {
            if (p[0] == point[0] && p[1] == point[1]) return true;
        }
        return false;
    }

    public List<int[]> rangeQuery(int xMin, int xMax, int yMin, int yMax) {
        List<int[]> result = new ArrayList<>();
        for (List<int[]> cell : grid.values()) {
            metrics.incrementDiskAccess();

            for (int[] p : cell) {
                if (p[0] >= xMin && p[0] <= xMax && p[1] >= yMin && p[1] <= yMax) {
                    result.add(p);
                }
            }
        }
        return result;
    }

    public int[] nearestNeighbor(int[] target) {
        metrics.incrementDiskAccess();

        int[] best = null;
        double bestDist = Double.MAX_VALUE;

        for (List<int[]> cell : grid.values()) {
            for (int[] p : cell) {
                double dist = Math.pow(p[0] - target[0], 2) + Math.pow(p[1] - target[1], 2);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = p;
                }
            }
        }

        return best;
    }

    public List<int[]> getAllPoints() {
        List<int[]> all = new ArrayList<>();
        for (List<int[]> cell : grid.values()) {
            all.addAll(cell);
        }
        return all;
    }

    public Set<String> getAllCellKeys() {
        return grid.keySet();
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getCellSize() {
        return cellSize;
    }
}

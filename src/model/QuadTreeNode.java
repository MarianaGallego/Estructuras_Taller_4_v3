package model;

import java.util.ArrayList;
import java.util.List;
import utils.MetricsTracker;

public class QuadTreeNode {
    private MetricsTracker metrics;

    private final int capacity = 1; // capacidad máxima antes de subdividir
    private int xMin, xMax, yMin, yMax;

    private List<int[]> points;
    private boolean divided;

    private QuadTreeNode NE, NW, SE, SW;

    public QuadTreeNode(int xMin, int xMax, int yMin, int yMax, MetricsTracker metrics) {
        this.metrics = metrics;

        this.xMin = xMin; this.xMax = xMax;
        this.yMin = yMin; this.yMax = yMax;

        this.points = new ArrayList<>();
        this.divided = false;
    }

    public boolean insert(int[] point) {
        metrics.incrementDiskAccess();

        int x = point[0], y = point[1];
        if (x < xMin || x > xMax || y < yMin || y > yMax)
            return false; // fuera de los límites

        if (points.size() < capacity && !divided) {
            points.add(point);
            return true;
        }

        if (!divided) subdivide();

        return (NE.insert(point) || NW.insert(point) ||
                SE.insert(point) || SW.insert(point));
    }

    private void subdivide() {
        int midX = (xMin + xMax) / 2;
        int midY = (yMin + yMax) / 2;

        NE = new QuadTreeNode(midX, xMax, yMin, midY, metrics);
        NW = new QuadTreeNode(xMin, midX, yMin, midY, metrics);
        SE = new QuadTreeNode(midX, xMax, midY, yMax, metrics);
        SW = new QuadTreeNode(xMin, midX, midY, yMax, metrics);


        for (int[] p : points) {
            if (NE.insert(p)) continue;
            if (NW.insert(p)) continue;
            if (SE.insert(p)) continue;
            if (SW.insert(p)) continue;
        }


        points.clear(); // ya se reubicaron
        divided = true;
    }

    public List<int[]> getPoints() {
        List<int[]> all = new ArrayList<>(points);
        if (divided) {
            all.addAll(NE.getPoints());
            all.addAll(NW.getPoints());
            all.addAll(SE.getPoints());
            all.addAll(SW.getPoints());
        }
        return all;
    }

    // Consulta PUNTUAL
    public boolean containsPoint(int[] point) {
        metrics.incrementDiskAccess();

        int x = point[0], y = point[1];

        if (x < xMin || x > xMax || y < yMin || y > yMax)
            return false;

        if (!divided) {
            for (int[] p : points) {
                if (p[0] == x && p[1] == y) return true;
            }
            return false;
        }

        // Recorrer subcuadrantes
        return (NE.containsPoint(point) || NW.containsPoint(point) ||
                SE.containsPoint(point) || SW.containsPoint(point));
    }


    // Consulta por RANGO RECTANGULAR
    public List<int[]> rangeQuery(int xMinQ, int xMaxQ, int yMinQ, int yMaxQ) {
        metrics.incrementDiskAccess();

        List<int[]> result = new ArrayList<>();

        // Si no hay intersección entre el rectángulo de este nodo y el de la consulta, termina
        if (!intersects(xMinQ, xMaxQ, yMinQ, yMaxQ)) {
            return result;
        }

        // Si no está dividido, verificar punto por punto
        if (!divided) {
            for (int[] p : points) {
                int x = p[0], y = p[1];
                if (x >= xMinQ && x <= xMaxQ && y >= yMinQ && y <= yMaxQ) {
                    result.add(p);
                }
            }
            return result;
        }

        // Si está dividido, hacer consulta recursiva en subcuadrantes
        result.addAll(NE.rangeQuery(xMinQ, xMaxQ, yMinQ, yMaxQ));
        result.addAll(NW.rangeQuery(xMinQ, xMaxQ, yMinQ, yMaxQ));
        result.addAll(SE.rangeQuery(xMinQ, xMaxQ, yMinQ, yMaxQ));
        result.addAll(SW.rangeQuery(xMinQ, xMaxQ, yMinQ, yMaxQ));

        return result;
    }

    private boolean intersects(int xMinQ, int xMaxQ, int yMinQ, int yMaxQ) {
        return !(xMax < xMinQ || xMin > xMaxQ || yMax < yMinQ || yMin > yMaxQ);
    }

    public int[] nearestNeighbor(int[] target) {
        return nearestNeighborRec(target, new double[]{Double.MAX_VALUE});
    }

    private int[] nearestNeighborRec(int[] target, double[] bestDist) {
        int[] bestPoint = null;

        for (int[] p : points) {
            double dist = distance(p, target);
            if (dist < bestDist[0]) {
                bestDist[0] = dist;
                bestPoint = p;
            }
        }

        if (!divided) return bestPoint;

        QuadTreeNode[] children = {NE, NW, SE, SW};
        List<QuadTreeNode> orden = new ArrayList<>();

        for (QuadTreeNode child : children) {
            if (child.contains(target)) {
                orden.add(0, child);
            } else {
                orden.add(child);
            }
        }

        for (QuadTreeNode child : orden) {
            if (child.boundsIntersectCircle(target, bestDist[0])) {
                int[] candidate = child.nearestNeighborRec(target, bestDist);
                if (candidate != null && distance(candidate, target) < bestDist[0]) {
                    bestDist[0] = distance(candidate, target);
                    bestPoint = candidate;
                }
            }
        }

        return bestPoint;
    }

    private boolean contains(int[] point) {
        int x = point[0], y = point[1];
        return x >= xMin && x <= xMax && y >= yMin && y <= yMax;
    }

    private boolean boundsIntersectCircle(int[] center, double radius) {
        int x = center[0], y = center[1];
        int closestX = Math.max(xMin, Math.min(x, xMax));
        int closestY = Math.max(yMin, Math.min(y, yMax));
        double dx = x - closestX;
        double dy = y - closestY;
        return (dx * dx + dy * dy <= radius * radius);
    }

    private double distance(int[] a, int[] b) {
        int dx = a[0] - b[0];
        int dy = a[1] - b[1];
        return Math.sqrt(dx * dx + dy * dy);
    }





    public void draw(java.awt.Graphics g, int panelWidth, int panelHeight) {
        g.setColor(java.awt.Color.BLUE);
        int x1 = xMin * panelWidth / 100;
        int x2 = xMax * panelWidth / 100;
        int y1 = yMin * panelHeight / 100;
        int y2 = yMax * panelHeight / 100;

        // Dibujar el rectángulo de este nodo
        g.drawRect(x1, y1, x2 - x1, y2 - y1);

        if (divided) {
            NE.draw(g, panelWidth, panelHeight);
            NW.draw(g, panelWidth, panelHeight);
            SE.draw(g, panelWidth, panelHeight);
            SW.draw(g, panelWidth, panelHeight);
        }
    }
}

package model;

import java.util.ArrayList;
import java.util.List;
import utils.MetricsTracker;

public class KDTree {
    private KDNode root;
    private static final int K = 2; // Dimensiones: x, y
    private MetricsTracker metrics;

    public KDTree() {
        root = null;
    }

    public KDNode getRoot() {
        return root;
    }

    // Inserta un punto en el árbol
    public void insert(int[] point) {
        root = insertRec(root, point, 0);
    }

    private KDNode insertRec(KDNode node, int[] point, int depth) {
        metrics.incrementDiskAccess();

        if (node == null) return new KDNode(point);

        int axis = depth % K;
        if (point[axis] < node.point[axis])
            node.left = insertRec(node.left, point, depth + 1);
        else
            node.right = insertRec(node.right, point, depth + 1);

        return node;
    }

    // ---------------------------- CONSULTAS ----------------------------

    // Consulta PUNTUAL
    public boolean search(int[] point) {
        return searchRec(root, point, 0);
    }

    private boolean searchRec(KDNode node, int[] point, int depth) {
        metrics.incrementDiskAccess();

        if (node == null) return false;
        if (isSamePoint(node.point, point)) return true;

        int axis = depth % K;
        if (point[axis] < node.point[axis])
            return searchRec(node.left, point, depth + 1);
        else
            return searchRec(node.right, point, depth + 1);
    }

    private boolean isSamePoint(int[] a, int[] b) {
        for (int i = 0; i < K; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    // Consulta por RANGO
    public List<int[]> rangeSearch(int xMin, int xMax, int yMin, int yMax) {
        List<int[]> result = new ArrayList<>();
        rangeSearchRec(root, 0, xMin, xMax, yMin, yMax, result);
        return result;
    }

    private void rangeSearchRec(KDNode node, int depth,
                                int xMin, int xMax, int yMin, int yMax,
                                List<int[]> result) {
        if (node == null) return;

        int x = node.point[0];
        int y = node.point[1];

        // Si el punto está dentro del rectángulo, lo añadimos
        if (x >= xMin && x <= xMax && y >= yMin && y <= yMax) {
            result.add(node.point);
        }

        int axis = depth % K;

        // Decidir qué ramas explorar
        if (axis == 0) { // eje x
            if (xMin <= x) rangeSearchRec(node.left, depth + 1, xMin, xMax, yMin, yMax, result);
            if (xMax >= x) rangeSearchRec(node.right, depth + 1, xMin, xMax, yMin, yMax, result);
        } else { // eje y
            if (yMin <= y) rangeSearchRec(node.left, depth + 1, xMin, xMax, yMin, yMax, result);
            if (yMax >= y) rangeSearchRec(node.right, depth + 1, xMin, xMax, yMin, yMax, result);
        }
    }

    // Consulta VECINO MAS CERCANO
    public int[] nearestNeighbor(int[] target) {
        return nearestNeighborRec(root, target, 0, root.point, distanceSquared(root.point, target));
    }

    private int[] nearestNeighborRec(KDNode node, int[] target, int depth, int[] best, double bestDist) {
        if (node == null) return best;

        double d = distanceSquared(node.point, target);
        if (d < bestDist) {
            bestDist = d;
            best = node.point;
        }

        int axis = depth % K;
        KDNode nearChild = (target[axis] < node.point[axis]) ? node.left : node.right;
        KDNode farChild = (nearChild == node.left) ? node.right : node.left;

        // Buscar primero en la rama cercana
        best = nearestNeighborRec(nearChild, target, depth + 1, best, bestDist);
        bestDist = distanceSquared(best, target);

        // ¿Es necesario explorar la rama lejana?
        double axisDist = Math.pow(node.point[axis] - target[axis], 2);
        if (axisDist < bestDist) {
            best = nearestNeighborRec(farChild, target, depth + 1, best, bestDist);
        }

        return best;
    }

    private double distanceSquared(int[] a, int[] b) {
        double sum = 0;
        for (int i = 0; i < K; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return sum;
    }

    // ---------------------------- COMPARACION DE METRICAS ----------------------------
    public KDTree(MetricsTracker metrics) {
        this.metrics = metrics;
        root = null;
    }


    // Muestra el árbol (inorder traversal con profundidad)
    public void printTree() {
        printTreeRec(root, 0);
    }

    private void printTreeRec(KDNode node, int depth) {
        if (node == null) return;
        printTreeRec(node.left, depth + 1);
        System.out.println(" ".repeat(depth * 2) + "(" + node.point[0] + ", " + node.point[1] + ")");
        printTreeRec(node.right, depth + 1);
    }
}


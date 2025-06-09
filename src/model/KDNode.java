package model;

public class KDNode {
    public int[] point; // punto [x, y]
    public KDNode left, right;

    public KDNode(int[] point) {
        this.point = point;
        this.left = null;
        this.right = null;
    }
}

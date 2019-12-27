package com.example.usb.map.graphelems;

/**
 * Node interface where all other node types inherit from.
 *
 * @author  Kazymir Rabier
 */
public abstract class Node {

    private int id;
    // Set to ID when creating nodes manually
    private static int instances = 0;
    private float xPos;
    private float yPos;
    private Graph graph;
    private boolean restricted = false;

    public Node(Graph graph) {
        this.id = ++instances;
        this.graph = graph;
    }

    public Node(int id, float xPos, float yPos, Graph graph) {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.graph = graph;
    }

    public int getId() {
        return id;
    }

    // Returns X coordinates of a given node
    public float getXPos() {
        return xPos;
    }

    public void setXPos(float xPos) { this.xPos = xPos; }

    // Returns Y coordinates of a given node
    public float getYPos() {
        return yPos;
    }

    public void setYPos(float yPos) { this.yPos = yPos; }

    public void setId(int id) {
        this.id = id;
    }

    // Returns graph which the node is a part of
    public Graph getGraph() {
        return graph;
    }

    public boolean isRestricted() { return restricted; }

    public void setRestricted(boolean restricted) { this.restricted = restricted; }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Node)) {
            return false;
        }

        Node node = (Node) o;

        return node.xPos == xPos &&
                node.yPos == yPos &&
                node.id == id;
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 31 * result + (int) xPos;
        result = 31 * result + (int) yPos;
        result = 31 * result + id;
        return result;
    }
}

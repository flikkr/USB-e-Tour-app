package com.example.usb.map.graphelems;

/**
 * Nodes that are not rooms used to traverse the graph.
 *
 * @author  Kazymir Rabier
 */
public class TransitNode extends Node {

    public TransitNode(Graph graph) { super(graph); }

    public TransitNode(int id, float xPos, float yPos, Graph graph){ super(id, xPos, yPos, graph); }

    @Override
    public void setRestricted(boolean restricted) {}

    @Override
    public String toString() {
        return "TransitNode(" + this.getXPos() + "," + this.getYPos() + "), flr: " +
                this.getGraph().getFloor().getLevel() + ", id: " + this.getId();
    }
}

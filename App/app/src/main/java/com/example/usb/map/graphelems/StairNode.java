package com.example.usb.map.graphelems;

/**
 * Node representing stairs.
 *
 * @author  Kazymir Rabier
 */
public class StairNode extends ElevationNode {

    public StairNode(Graph graph) {
        super(graph);
    }

    public StairNode(int id, float xPos, float yPos, Graph graph) {
        super(id, xPos, yPos, graph);
    }

    @Override
    public String toString() {
        return "StairNode(" + this.getXPos() + "," + this.getYPos() + "), flr: " +
                this.getGraph().getFloor().getLevel() + ", id: " + this.getId();
    }
}

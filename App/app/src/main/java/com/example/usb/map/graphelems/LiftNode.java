package com.example.usb.map.graphelems;

/**
 * Node representing lifts.
 *
 * @author  Kazymir Rabier
 */
public class LiftNode extends ElevationNode {

    // Stores name of lift
    private String liftNumber;

    public LiftNode(Graph graph) { super(graph); }

    public LiftNode(int id, float xPos, float yPos, Graph graph) { super(id, xPos, yPos, graph); }

    public String getLiftNumber() {
        return liftNumber;
    }

    public void setLiftNumber(String liftNumber) {
        this.liftNumber = liftNumber;
    }

    @Override
    public String toString() {
        return "LiftNode(" + this.getXPos() + "," + this.getYPos() + "), flr: " +
                this.getGraph().getFloor().getLevel() + ", id: " + this.getId();
    }
}

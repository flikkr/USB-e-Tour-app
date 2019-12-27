package com.example.usb.map.graphelems;

/**
 * Node interface for stairs and lifts.
 *
 * @author  Kazymir Rabier
 */
public abstract class ElevationNode extends Node {

    private ElevationNode aboveNode;
    private ElevationNode belowNode;
    private boolean hasAbove = false;
    private boolean hasBelow = false;

    public ElevationNode(Graph graph) { super(graph); }

    public ElevationNode(int id, float xPos, float yPos, Graph graph){ super(id, xPos, yPos, graph); }

    // Links two elevation nodes on different floors
    public void addLink(ElevationNode elevNode) {
        int linkedNodeLevel = elevNode.getGraph().getFloor().getLevel();
        int currentLevel = this.getGraph().getFloor().getLevel();

        if (this instanceof StairNode && elevNode instanceof StairNode) {
            if (linkedNodeLevel - 1 == currentLevel) {
                this.aboveNode = elevNode;
                this.hasAbove = true;
                elevNode.setBelowNode(this);
                elevNode.setHasBelow(true);
            } else if (linkedNodeLevel + 1 == currentLevel) {
                this.belowNode = elevNode;
                this.hasBelow = true;
                elevNode.setAboveNode(this);
                elevNode.setHasAbove(true);
            } else {
                throw new IllegalArgumentException("Can't skip floors.");
            }
        }
        else if (this instanceof LiftNode && elevNode instanceof LiftNode) {
            if (linkedNodeLevel > currentLevel) {
                this.aboveNode = elevNode;
                this.hasAbove = true;
                elevNode.setBelowNode(this);
                elevNode.setHasBelow(true);
            } else {
                this.belowNode = elevNode;
                this.hasBelow = true;
                elevNode.setAboveNode(this);
                elevNode.setHasAbove(true);
            }
        } else {
            throw new IllegalArgumentException("Can't link StairNode with LiftNode.");
        }
    }

    public void unlinkNodes() {
        if (hasAbove && hasBelow) {
            throw new IllegalArgumentException("Can't unlink if node has both above and below link");
        }

        if (aboveNode != null) {
            aboveNode.setBelowNode(null);
            aboveNode.setHasBelow(false);
            this.aboveNode = null;
            this.hasAbove = false;
        }
        else if (belowNode != null) {
            belowNode.setAboveNode(null);
            belowNode.setHasAbove(false);
            this.belowNode = null;
            this.hasBelow = false;
        }
    }

    public ElevationNode getAboveNode() {
        return aboveNode;
    }

    private void setAboveNode(ElevationNode elevNode) { this.aboveNode = elevNode; }

    public ElevationNode getBelowNode() {
        return belowNode;
    }

    private void setBelowNode(ElevationNode elevNode) { this.belowNode = elevNode; }

    public boolean hasNodeAbove() { return hasAbove; }

    private void setHasAbove(boolean bool) { this.hasAbove = bool; }

    public boolean hasNodeBelow() { return hasBelow; }

    private void setHasBelow(boolean bool) { this.hasBelow = bool; }
}

package com.example.usb.map.graphelems;

import com.example.usb.map.mapelems.Room;

/**
 * Nodes that represent rooms the user is navigating to and from.
 *
 * @author  Kazymir Rabier
 */
public class RoomNode extends Node {

    // Refers to room stored by the node
    private final Room room;

    public RoomNode(Graph graph, Room room){
        super(graph);
        this.room = room;
    }

    public RoomNode(int id, float xPos, float yPos, Graph graph, Room room){
        super(id, xPos, yPos, graph);
        this.room = room;
    }

    // Returns Room object
    public Room getRoom(){
        return room;
    }

    @Override
    public String toString() {
        return "RoomNode(" + this.getXPos() + "," + this.getYPos() + "), flr: " +
                this.getGraph().getFloor().getLevel() + ", id: " + this.getId();
    }
}

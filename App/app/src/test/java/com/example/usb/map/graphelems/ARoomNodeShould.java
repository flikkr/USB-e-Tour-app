package com.example.usb.map.graphelems;

import com.example.usb.map.mapelems.Floor;
import com.example.usb.map.mapelems.Room;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ARoomNodeShould {

    RoomNode node;
    Floor floor;
    Graph graph;
    Room room;

    @Before
    public void setUp() throws Exception {
        floor = new Floor(0);
        graph = floor.getGraph();
        room = new Room("0.001", floor);
        node = room.getRoomNode();
    }

    @Test
    public void getId() {
        Node node2 = new TransitNode(graph);

        assertEquals(2, node2.getId());
    }

    @Test
    public void setXPos() {
        node.setXPos(3);

        assertEquals(3, node.getXPos());
    }

    @Test
    public void setYPos() {
        node.setYPos(5);

        assertEquals(5, node.getYPos());
    }

    @Test
    public void getRoom() {
        assertEquals(room, node.getRoom());
    }
}
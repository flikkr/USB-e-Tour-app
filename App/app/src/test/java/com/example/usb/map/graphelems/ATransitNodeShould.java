package com.example.usb.map.graphelems;

import com.example.usb.map.mapelems.Floor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ATransitNodeShould {
    TransitNode node;
    Floor floor;
    Graph graph;

    @Before
    public void setUp() throws Exception {
        floor = new Floor(0);
        graph = floor.getGraph();
        node = new TransitNode(graph);
    }

    @Test
    public void getId() {
        assertEquals(1, node.getId());
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
}
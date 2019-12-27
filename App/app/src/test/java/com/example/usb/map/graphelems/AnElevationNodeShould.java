package com.example.usb.map.graphelems;

import com.example.usb.map.mapelems.Floor;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AnElevationNodeShould {
    ElevationNode node0;
    ElevationNode node1;
    Floor floor0;
    Floor floor1;
    Graph graph0;
    Graph graph1 ;

    @Before
    public void setUp() throws Exception {
        floor0 = new Floor(0);
        floor1 = new Floor(1);
        graph0 = floor0.getGraph();
        graph1 = floor1.getGraph();
    }

    @Test
    public void addLinkIfOtherNodeIsOneFloorAbove() {
        node0 = new StairNode(graph0);
        node1 = new StairNode(graph1);

        node0.addLink(node1);

        assertTrue(node0.getAboveNode().equals(node1) && node1.getBelowNode().equals(node0));
    }

    @Test
    public void addLinkIfOtherNodeIsOneFloorBelow() {
        node0 = new StairNode(graph0);
        node1 = new StairNode(graph1);

        node1.addLink(node0);

        assertTrue(node1.getBelowNode().equals(node0) && node0.getAboveNode().equals(node1));
    }

    @Test
    public void haveAboveAndBelowNodes() {
        node0 = new StairNode(graph0);
        node1 = new StairNode(graph1);

        node0.addLink(node1);

        assertTrue(node0.hasNodeAbove() && node1.hasNodeBelow());
    }

    @Test (expected = IllegalArgumentException.class)
    public void notAddLinkIfOtherNodeIsTwoFloorsAbove() {
        Floor floor2 = new Floor(2);
        Graph graph2 = floor2.getGraph();
        ElevationNode node2 = new LiftNode(graph2);

        node0 = new LiftNode(graph0);

        node0.addLink(node2);
    }

    @Test (expected = IllegalArgumentException.class)
    public void notAddLinkIfOtherNodeIsTwoFloorsBelow() {
        Floor floor2 = new Floor(2);
        Graph graph2 = floor2.getGraph();
        ElevationNode node2 = new LiftNode(graph2);

        node0 = new LiftNode(graph0);

        node2.addLink(node0);
    }

    @Test (expected = IllegalArgumentException.class)
    public void notAddLinkIfOtherNodeIsOfDifferentType() {
        node0 = new StairNode(graph0);
        node1 = new LiftNode(graph1);

        node0.addLink(node1);
    }

    @Test
    public void unlinkIfItHasAboveNode() {
        node0 = new StairNode(graph0);
        node1 = new StairNode(graph1);

        node0.addLink(node1);
        node0.unlinkNodes();

        assertTrue(node0.getAboveNode() == null && node1.getBelowNode() == null);
    }

    @Test
    public void unlinkIfItHasBelowNode() {
        node0 = new StairNode(graph0);
        node1 = new StairNode(graph1);

        node1.addLink(node0);
        node1.unlinkNodes();

        assertTrue(node1.getBelowNode() == null && node0.getAboveNode() == null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void notUnlinkIfItHasAboveAndBelowNode() {
        Floor floor2 = new Floor(2);
        Graph graph2 = floor2.getGraph();
        ElevationNode node2 = new StairNode(graph2);

        node0 = new StairNode(graph0);
        node1 = new StairNode(graph1);

        node0.addLink(node1);
        node1.addLink(node2);

        node1.unlinkNodes();
    }
}
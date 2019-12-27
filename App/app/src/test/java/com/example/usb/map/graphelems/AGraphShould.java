package com.example.usb.map.graphelems;

import com.example.usb.map.mapelems.Floor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AGraphShould {

    Floor floor;
    Graph graph;

    @Before
    public void setUp() throws Exception {
        floor = new Floor(0);
        graph = floor.getGraph();
    }

    @Test
    public void notBeNull() {
        assertNotNull(graph);
    }

    @Test
    public void getNodesFromGraph() {
        Node node1 = new TransitNode(graph);
        Node node2 = new StairNode(graph);

        graph.addNode(node1);
        graph.addNode(node2);

        assertTrue(graph.getNodes().contains(node1) && graph.getNodes().contains(node2));
    }

    @Test
    public void getEdgesFromGraph() {
        Node node1 = new TransitNode(graph);
        Node node2 = new StairNode(graph);
        Edge edge = new Edge(node1, node2, 3);

        graph.addNode(node1);
        graph.addNode(node2);
        graph.addEdge(edge);

        assertTrue(graph.getEdges().contains(edge));
    }

    @Test
    public void getFloor() {
        assertSame(floor, graph.getFloor());
    }

    @Test
    public void deleteNode() {
        Node node1 = new TransitNode(graph);
        Node node2 = new StairNode(graph);

        graph.addNode(node1);
        graph.addNode(node2);
        graph.getNodes().remove(node2);

        assertFalse(graph.getNodes().contains(node2));
    }
}
package com.example.usb.map;

import com.example.usb.map.graphelems.Edge;
import com.example.usb.map.graphelems.ElevationNode;
import com.example.usb.map.graphelems.Graph;
import com.example.usb.map.graphelems.LiftNode;
import com.example.usb.map.graphelems.Node;
import com.example.usb.map.graphelems.RoomNode;
import com.example.usb.map.graphelems.StairNode;
import com.example.usb.map.graphelems.TransitNode;
import com.example.usb.map.mapelems.Building;
import com.example.usb.map.mapelems.Floor;
import com.example.usb.map.mapelems.Room;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

// TODO: Add tests for restricted areas
public class DijkstraAlgorithmShould {

    Dijkstra d;

    Building testBuilding = new Building("Urban Sciences Building");

    Floor floor0 = new Floor(0);
    Floor floor1 = new Floor(1);
    Floor floor2 = new Floor(2);
    Graph graph0 = floor0.getGraph();
    Graph graph1 = floor1.getGraph();
    Graph graph2 = floor2.getGraph();

    Room room1 = new Room("G.001", floor0);
    Room room2 = new Room("G.002", floor0);
    Room room3 = new Room("1.001", floor1);
    Room room4 = new Room("2.001", floor2);
    Room room5 = new Room("2.002", floor2);
    RoomNode dest1 = room1.getRoomNode();
    RoomNode dest2 = room2.getRoomNode();
    RoomNode dest3 = room3.getRoomNode();
    RoomNode dest4 = room4.getRoomNode();
    RoomNode dest5 = room5.getRoomNode();

    List<Node> graph0Nodes;
    List<Node> graph1Nodes;
    List<Node> graph2Nodes;
    List<Edge> graph0Edges;
    List<Edge> graph1Edges;
    List<Edge> graph2Edges;


    @Before
    public void setUp() throws Exception {
        testBuilding.addFloor(floor0);
        testBuilding.addFloor(floor1);
        testBuilding.addFloor(floor2);

        floor0.addRoom(room1);
        floor0.addRoom(room2);
        floor1.addRoom(room3);
        floor2.addRoom(room4);

        graph0Nodes = Arrays.asList(dest1, new TransitNode(graph0), new StairNode(graph0),
                dest2, new LiftNode(graph0), new TransitNode(graph0));
        graph1Nodes = Arrays.asList(new StairNode(graph1), new TransitNode(graph1), new TransitNode(graph1),
                new StairNode(graph1), dest3, new LiftNode(graph1), new TransitNode(graph1));
        graph2Nodes = Arrays.asList(new StairNode(graph2), new TransitNode(graph2), new TransitNode(graph2),
                new TransitNode(graph2), dest4, new LiftNode(graph2), new TransitNode(graph2), dest5);

        ElevationNode stair0 = (ElevationNode) graph0Nodes.get(2);
        ElevationNode stair1 = (ElevationNode) graph1Nodes.get(3);
        ElevationNode stair2 = (ElevationNode) graph1Nodes.get(0);
        ElevationNode stair3 = (ElevationNode) graph2Nodes.get(0);

        ElevationNode lift0 = (ElevationNode) graph0Nodes.get(4);
        ElevationNode lift1 = (ElevationNode) graph1Nodes.get(5);
        ElevationNode lift2 = (ElevationNode) graph2Nodes.get(5);

        stair0.addLink(stair1);
        stair2.addLink(stair3);

        lift0.addLink(lift1);
        lift1.addLink(lift2);

        graph0.addNodeList(graph0Nodes);
        graph1.addNodeList(graph1Nodes);
        graph2.addNodeList(graph2Nodes);

        graph0Edges = Arrays.asList(new Edge(graph0Nodes.get(0),graph0Nodes.get(1), 5),
                new Edge(graph0Nodes.get(1),graph0Nodes.get(5), 3),
                new Edge(graph0Nodes.get(5),graph0Nodes.get(4), 5),
                new Edge(graph0Nodes.get(5),graph0Nodes.get(3), 7),
                new Edge(graph0Nodes.get(5),graph0Nodes.get(2), 8));
        graph1Edges = Arrays.asList(new Edge(graph1Nodes.get(0),graph1Nodes.get(1), 5),
                new Edge(graph1Nodes.get(1),graph1Nodes.get(2), 5),
                new Edge(graph1Nodes.get(2),graph1Nodes.get(3), 6),
                new Edge(graph1Nodes.get(1),graph1Nodes.get(6), 3),
                new Edge(graph1Nodes.get(2),graph1Nodes.get(6), 8),
                new Edge(graph1Nodes.get(6),graph1Nodes.get(4), 7),
                new Edge(graph1Nodes.get(6),graph1Nodes.get(5), 5));
        graph2Edges = Arrays.asList(new Edge(graph2Nodes.get(0),graph2Nodes.get(1), 5),
                new Edge(graph2Nodes.get(1),graph2Nodes.get(2), 6),
                new Edge(graph2Nodes.get(2),graph2Nodes.get(3), 3),
                new Edge(graph2Nodes.get(3),graph2Nodes.get(4), 8),
                new Edge(graph2Nodes.get(5),graph2Nodes.get(6), 5),
                new Edge(graph2Nodes.get(1),graph2Nodes.get(6), 3),
                new Edge(graph2Nodes.get(2),graph2Nodes.get(6), 2));

        graph0.addEdgeList(graph0Edges);
        graph1.addEdgeList(graph1Edges);
        graph2.addEdgeList(graph2Edges);

        d = new Dijkstra(testBuilding);
    }

    // These test cases are tested for correctness for each path list node by debugging them individually.
    @Test
    public void returnShortestPathOnSameFloor() {
        List<Node> path = d.shortestPath(dest1, dest2);

        assertEquals(4, path.size());
    }

    @Test
    public void returnShortestPathOnDifferentFloorUsingStairsGoingUp() {
        List<Node> path = d.shortestPath(dest1, dest4);

        assertEquals(14, path.size());
    }

    @Test
    public void returnShortestPathOnDifferentFloorUsingLiftGoingUp() {
        d.setDecisionIsStairs(false);

        List<Node> path = d.shortestPath(dest1, dest4);

        assertEquals(10, path.size());
    }

    @Test
    public void returnShortestPathOnDifferentFloorUsingStairsGoingDown() {
        List<Node> path = d.shortestPath(dest4, dest1);

        assertEquals(14, path.size());
    }

    @Test
    public void returnShortestPathOnDifferentFloorUsingLiftGoingDown() {
        d.setDecisionIsStairs(false);

        List<Node> path = d.shortestPath(dest4, dest1);

        assertEquals(10, path.size());
    }

    @Test (expected = IllegalArgumentException.class)
    public void notReturnPathIfNoPathToDest() {
        List<Node> path = d.shortestPath(dest1, dest5);
    }

    @Test
    public void test3to2() {
        d.setDecisionIsStairs(false);

        List<Node> path = d.shortestPath(dest3, dest2);
    }
}
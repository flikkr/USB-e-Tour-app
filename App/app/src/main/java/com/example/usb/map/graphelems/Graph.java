package com.example.usb.map.graphelems;

import com.example.usb.map.mapelems.Floor;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all the nodes and edges of a floor.
 *
 * @author  Kazymir Rabier
 */
public class Graph {

    private List<Node> nodes;
    private List<Edge> edges;
    private Floor floor;

    public Graph () {}

    public Graph (Floor floor) {
        this.floor = floor;
        nodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void addNode(Node node) { this.nodes.add(node); }

    public void addNodeList(List<Node> nodeList) { this.nodes = nodeList; }

    public List<Edge> getEdges() {
        return edges;
    }

    public void addEdgeList(List<Edge> edgeList) {
        for (Edge edge: edgeList) {
            addEdge(edge);
        }
    }

    public void addEdge(Edge edge) {
        if (nodes.contains(edge.getSource()) && nodes.contains(edge.getDest())) {
            this.edges.add(edge);
        }
        else {
            throw new IllegalArgumentException("Source or destination node missing from graph.");
        }
    }

    public Floor getFloor() { return floor; }

    public void deleteNode(Node node) { this.nodes.remove(node); }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Graph)) {
            return false;
        }

        Graph graph = (Graph) o;

        return graph.nodes.equals(nodes) &&
                graph.edges.equals(edges) &&
                graph.floor.equals(floor);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + nodes.hashCode();
        result = 31 * result + edges.hashCode();
        result = 31 * result + floor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Graph for floor " + floor.getLevel();
    }
}

package com.example.usb.map;

import android.util.Log;

import com.example.usb.map.graphelems.RoomNode;
import com.example.usb.map.graphelems.Edge;
import com.example.usb.map.graphelems.ElevationNode;
import com.example.usb.map.graphelems.Graph;
import com.example.usb.map.graphelems.LiftNode;
import com.example.usb.map.graphelems.Node;
import com.example.usb.map.graphelems.StairNode;
import com.example.usb.map.mapelems.Building;
import com.example.usb.map.mapelems.Floor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Algorithm class that stores all the necessary variables and methods to find
 * the shortest path given two graphToNodesMap.
 *
 * Note: the only necessary declarations necessary to use as a pathfinder are:
 *      - Dijkstra constructor
 *      - shortestPath method
 *
 * Algorithm adapted from https://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 *
 * @author  Kazymir Rabier
 */
public class Dijkstra {

    private static Map<Graph, List<Node>> graphToNodesMap;
    private static Map<Graph, List<Edge>> graphToEdgesMap;
    private static Set<Node> settledNodes;                 // Set of graphToNodesMap that are settled as part of the path
    private static Set<Node> unSettledNodes;               // Set of graphToNodesMap that are not yet settled as part of the path
    private static Map<Node, Node> predecessors;           // Stores each node as a key and their predecessor node as a value
    private static Map<Node, Integer> distance;            // Stores node as a key and their distance from their predecessor
    private static boolean decisionIsStairs = true;        // True if user wants to use stairs, False if user wants to use lift (true by default)
    private static boolean decisionIsStudent = true;       // True if user is a student, False if user is a member of staff (true by default)
    private static LinkedList<Node> finalPath;
    private static List<Node> restrictedNodes;

    public Dijkstra(Building building) {
        this.graphToNodesMap = new HashMap<Graph,List<Node>>();
        this.graphToEdgesMap = new HashMap<Graph,List<Edge>>();

        for (Floor floor: building.getFloors()) {
            List<Node> nodes = floor.getGraph().getNodes();
            List<Edge> edges = floor.getGraph().getEdges();

            // create a copy of the array so that we can operate on this array
            this.graphToNodesMap.put(floor.getGraph(), nodes);
            this.graphToEdgesMap.put(floor.getGraph(), edges);
        }
    }

    // Determines the shortest path from src node to dest node
    // Note: there are only two lift paths to choose from for the USB:
    //      - From Ground floor to level 4
    //      - From Ground floor to level 6
    public LinkedList<Node> shortestPath(RoomNode src, RoomNode dest) {
        Graph srcGraph = src.getGraph();
        Graph destGraph = dest.getGraph();

        finalPath = new LinkedList<Node>();
        restrictedNodes = new ArrayList<Node>();

        // If both graphs refer to the same graph (i.e. both graphToNodesMap are on same floor)
        if (srcGraph.equals(destGraph)) {
            return sameFloorSearch(src, dest);
        }
        else {
            return differentFloorSearch(src, dest, decisionIsStairs);
        }
    }

    // Runs shortest path algorithm on graphToNodesMap on the same floor
    private static LinkedList<Node> sameFloorSearch(Node src, Node dest) {
        // Initialise src node as starting node
        execute(src);

        LinkedList<Node> path = new LinkedList<Node>();
        Node step = dest;

        // Check if a path exists
        if (predecessors.get(step) == null) {
            throw new IllegalArgumentException("No way to access node.");
        }

        path.add(step);

        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            if (step.isRestricted()) { restrictedNodes.add(step); }

            path.add(step);
        }

        // Put it into the correct order and concatenate with final path
        Collections.reverse(path);
        finalPath.addAll(path);

        return path;
    }

    // Runs shortest path algorithm on graphToNodesMap on different floors
    private static LinkedList<Node> differentFloorSearch(Node src, Node dest, boolean decisionIsStairs) {
        Node tempSrcNode = src;
        Node tempDestNode = dest;

        int currentFloor = src.getGraph().getFloor().getLevel();
        int endFloor = dest.getGraph().getFloor().getLevel();

        int switchToUpOrDown = 0;

        // While current floor and end floor are not on the same level, repeat path finding algorithm
        while (currentFloor != endFloor) {
            boolean goUp = currentFloor < endFloor;
            boolean goDown = currentFloor > endFloor;

            int old = switchToUpOrDown;

            // If the algorithm decides to switch direction (go up and then go down), this sets a flag to avoid an infinite loop
            if (goUp) {
                switchToUpOrDown = 0;
            } else {
                switchToUpOrDown = 1;
            }


            // If there is an option to switch floor again by checking if there is a elevation node
            // above or below current floor, then change floor again
            if (tempSrcNode instanceof ElevationNode) {
                if (goUp && ((ElevationNode) tempSrcNode).hasNodeAbove() || goDown && ((ElevationNode) tempSrcNode).hasNodeBelow()) {
                    // Check if the algorithm switches direction from up to down or vice versa
                    if (old != switchToUpOrDown) {

                    } else {
                        finalPath.add(tempSrcNode);
                        tempSrcNode = changeFloor((ElevationNode) tempSrcNode, dest);
                        currentFloor = tempSrcNode.getGraph().getFloor().getLevel();

                        continue;
                    }
                }
            }

            LinkedList<Node> path = new LinkedList<>();

            // Initialise temp src node as starting node
            execute(tempSrcNode);

            // Stores the closest elevation node
            ElevationNode minDistance = null;

            boolean switchedElevNode = false;

            if (decisionIsStudent) {
                // TODO: Can optimise by searching through a stream filter instead of checking each instance of node
                // Look for all stair graphToNodesMap in graph
                for (Node node : settledNodes) {
                    if (node instanceof ElevationNode && !node.equals(tempSrcNode) && !node.isRestricted()) {
                        if (decisionIsStairs) {
                            if (node instanceof StairNode) {
                                if ((goUp && ((ElevationNode) node).hasNodeAbove())
                                        || (goDown && ((ElevationNode) node).hasNodeBelow())) {
                                    if (minDistance == null) {
                                        minDistance = (StairNode) node;
                                    } else if (getShortestDistance(node) < getShortestDistance(minDistance)) {
                                        minDistance = (StairNode) node;
                                    }
                                }
                            }
                        } else {
                            if (node instanceof LiftNode) {
                                if ((goUp && ((ElevationNode) node).hasNodeAbove())
                                        || (goDown && ((ElevationNode) node).hasNodeBelow())) {
                                    if (minDistance == null) {
                                        minDistance = (LiftNode) node;
                                    } else if (getShortestDistance(node) < getShortestDistance(minDistance)) {
                                        minDistance = (LiftNode) node;
                                    }
                                }
                            }
                        }
                    }
                }

                // If no further way by stairs or lift, try the other option
                if (minDistance == null) {
                    for (Node node : settledNodes) {
                        if (node instanceof ElevationNode && !node.equals(tempSrcNode) && !node.isRestricted()) {
                            if ((goUp && ((ElevationNode) node).hasNodeAbove()) ||
                                    (goDown && ((ElevationNode) node).hasNodeBelow())) {
                                if (minDistance == null) {
                                    minDistance = (ElevationNode) node;
                                    switchedElevNode = true;
                                } else if (getShortestDistance(node) < getShortestDistance(minDistance)) {
                                    minDistance = (ElevationNode) node;
                                }
                            }
                        }
                    }
                }
            } else {
                // TODO: Can optimise by searching through a stream filter instead of checking each instance of node
                // Look for all stair graphToNodesMap in graph
                for (Node node : settledNodes) {
                    if (node instanceof ElevationNode && !node.equals(tempSrcNode)) {
                        if (decisionIsStairs) {
                            if (node instanceof StairNode) {
                                if ((goUp && ((ElevationNode) node).hasNodeAbove())
                                        || (goDown && ((ElevationNode) node).hasNodeBelow())) {
                                    if (minDistance == null) {
                                        minDistance = (StairNode) node;
                                    } else if (getShortestDistance(node) < getShortestDistance(minDistance)) {
                                        minDistance = (StairNode) node;
                                    }
                                }
                            }
                        } else {
                            if (node instanceof LiftNode) {
                                if ((goUp && ((ElevationNode) node).hasNodeAbove())
                                        || (goDown && ((ElevationNode) node).hasNodeBelow())) {
                                    if (minDistance == null) {
                                        minDistance = (LiftNode) node;
                                    } else if (getShortestDistance(node) < getShortestDistance(minDistance)) {
                                        minDistance = (LiftNode) node;
                                    }
                                }
                            }
                        }
                    }
                }

                // If no further way by stairs or lift, try the other option
                if (minDistance == null) {
                    for (Node node : settledNodes) {
                        if (node instanceof ElevationNode && !node.equals(tempSrcNode)) {
                            if ((goUp && ((ElevationNode) node).hasNodeAbove()) ||
                                    (goDown && ((ElevationNode) node).hasNodeBelow())) {
                                if (minDistance == null) {
                                    minDistance = (ElevationNode) node;
                                    switchedElevNode = true;
                                } else if (getShortestDistance(node) < getShortestDistance(minDistance)) {
                                    minDistance = (ElevationNode) node;
                                }
                            }
                        }
                    }
                }
            }

            if (minDistance == null) {
                throw new IllegalArgumentException("No way to access node.");
            }
//            else if (switchedElevNode) {
//                // TODO: Print message saying that had to take either stair or lift since there was
//                // TODO: no further way using current mean of travel
//            }
//            else if (tookRestrictedRoute) {
//                // TODO: Print message saying that had to take a restricted path
//            }

            tempDestNode = minDistance;

            Node step = tempDestNode;

            // Check if a path exists
            if (predecessors.get(step) == null) {
                throw new IllegalArgumentException("No way to access node.");
            }

            path.add(step);

            while (predecessors.get(step) != null) {
                step = predecessors.get(step);

                if (step.isRestricted()) { restrictedNodes.add(step); }

                path.add(step);
            }

            // Put it into the correct order
            Collections.reverse(path);
            finalPath.addAll(path);

            tempSrcNode = changeFloor((ElevationNode) tempDestNode, dest);

            currentFloor = tempSrcNode.getGraph().getFloor().getLevel();
        }

        sameFloorSearch(tempSrcNode, dest);

        return finalPath;
    }

    // Initialises global vars and finds the shortest path to all graphToNodesMap in the graph from a given source node
    private static void execute(Node source) {
        settledNodes = new HashSet<Node>();
        unSettledNodes = new HashSet<Node>();
        distance = new HashMap<Node, Integer>();
        predecessors = new HashMap<Node, Node>();
        distance.put(source, 0);
        unSettledNodes.add(source);

        // Finds the shortest path to all connected graphToNodesMap from source node
        while (unSettledNodes.size() > 0) {
            Node node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    // Finds and maps the distances of each node to the original source node
    private static void findMinimalDistances(Node node) {
        List<Node> adjacentNodes = getNeighbours(node);
        for (Node target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }
    }

    // Returns the weight between two nodes
    private static int getDistance(Node node, Node target) {
        for (Edge edge : graphToEdgesMap.get(node.getGraph())) {
            if ((edge.getSource().equals(node) && edge.getDest().equals(target)) ||
                    (edge.getDest().equals(node) && edge.getSource().equals(target))) {
                return edge.getWeight();
            }
        }

        throw new RuntimeException("Should not happen.");
    }

    // Returns a list of neighbouring graphToNodesMap
    private static List<Node> getNeighbours(Node node) {
        List<Node> neighbours = new ArrayList<Node>();
        for (Edge edge : graphToEdgesMap.get(node.getGraph())) {
            if ((edge.getSource().equals(node) && !isSettled(edge.getDest())) ||
                    (edge.getDest().equals(node) && !isSettled(edge.getSource()))) {
                if (node.equals(edge.getSource())) {
                    neighbours.add(edge.getDest());
                } else {
                    neighbours.add(edge.getSource());
                }
            }
        }
        return neighbours;
    }

    // Checks through set of graphToNodesMap and returns node with the shortest distance from predecessor
    private static Node getMinimum(Set<Node> nodes) {
        Node minimum = null;
        for (Node node : nodes) {
            if (minimum == null) {
                minimum = node;
            } else {
                if (getShortestDistance(node) < getShortestDistance(minimum)) {
                    minimum = node;
                }
            }
        }
        return minimum;
    }

    // Returns true if a node is settled
    private static boolean isSettled(Node node) {
        return settledNodes.contains(node);
    }

    // Returns a graphToNodesMap distance from predecessor
    private static int getShortestDistance(Node destination) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    // Returns an ElevationNode from a new floor
    private static ElevationNode changeFloor(ElevationNode elevNode, Node finalDestination) {
        ElevationNode nextFloorNode;

        int currentFloor = elevNode.getGraph().getFloor().getLevel();
        int destinationFloor = finalDestination.getGraph().getFloor().getLevel();

        if (currentFloor < destinationFloor) {
            nextFloorNode = elevNode.getAboveNode();
        }
        else if (currentFloor > destinationFloor) {
            nextFloorNode = elevNode.getBelowNode();
        }
        else {
            throw new IllegalArgumentException("No floors above or below.");
        }

        return nextFloorNode;
    }

    // Needed for the path selection through the GUI
    public void setDecisionToStairs(boolean decisionIsStairs) {
        this.decisionIsStairs = decisionIsStairs;
    }

    // Needed for the student or staff selection through the GUI
    public void setDecisionToStudent(boolean decisionIsStudent) {
        this.decisionIsStudent = decisionIsStudent;
    }

    // Returns list of restricted nodes
    public List<Node> getRestrictedNodes() {
        return restrictedNodes;
    }
}
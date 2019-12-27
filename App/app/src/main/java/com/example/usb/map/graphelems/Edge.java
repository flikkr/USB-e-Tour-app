package com.example.usb.map.graphelems;

/**
 * Links nodes between each others with a given weight.
 *
 * @author  Kazymir Rabier
 */
public class Edge {

    private final Node src;
    private final Node dest;
    private final int weight;

    public Edge(Node source, Node destination, int weight){

        this.src = source;
        this.dest = destination;
        this.weight = weight;

    }

    public Node getSource() {
        return src;
    }

    public Node getDest() {
        return dest;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Node)) {
            return false;
        }

        Edge edge = (Edge) o;

        return edge.src.equals(src) &&
                edge.dest.equals(dest) &&
                edge.weight == weight;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + src.hashCode();
        result = 31 * result + dest.hashCode();
        result = 31 * result + weight;
        return result;
    }

    @Override
    public String toString() {
        return "Edge ID{" + src.getId() + "->" + dest.getId() + "}, weight: " + weight;
    }
}

package com.example.usb.map.mapelems;

import com.example.usb.map.graphelems.Graph;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds a reference of all rooms on that floor.
 *
 * @author  Kazymir Rabier
 */
public class Floor {

    private Set<Room> rooms;
    private Graph graph;
    private final int level;

    public Floor(int level){

        this.level = level;
        this.graph = new Graph(this);
        rooms = new HashSet<Room>();

    }

    public Floor(int level, Set<Room> rooms){

        this.level = level;
        this.rooms = rooms;
        this.graph = new Graph(this);

    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public void addRoom(Room room) { rooms.add(room); }

    public void addRoomSet(Set<Room> rooms) { this.rooms = rooms; }

    public Graph getGraph() { return graph; }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "Floor " + level;
    }
}

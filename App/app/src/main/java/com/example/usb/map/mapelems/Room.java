package com.example.usb.map.mapelems;

import com.example.usb.map.graphelems.RoomNode;

import java.util.List;

/**
 * Holds information regarding a given room.
 *
 * @author  Kazymir Rabier
 */
public class Room implements Comparable<Room> {

    private String roomNumber;                    // Room number format "G.001" for ground, "1.001" for 1st floor, etc
    private String roomName;
    private List<String> occupants;
    private final Floor floor;
    private RoomNode roomNode;

    public Room(String roomNumber, Floor floor) {
        setRoomNumber(roomNumber);
        this.floor = floor;
        this.roomNode = new RoomNode(floor.getGraph(), this);
    }

    public Room(String roomNumber, Floor floor, String roomName, List<String> occupants){

        setRoomNumber(roomNumber);
        this.roomName = roomName;
        this.occupants = occupants;
        this.floor = floor;
        this.roomNode = new RoomNode(floor.getGraph(), this);

    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        if (roomNumber.matches("^(g|G|\\d){1,2}.(\\d){3}[a-zA-Z]?$")) {
            this.roomNumber = roomNumber.toUpperCase();
        }
        else {
            throw new IllegalArgumentException("Room number does not match pattern.");
        }
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) { this.roomName = roomName; }

    public List<String> getOccupants() {
        return occupants;
    }

    public void addOccupant(String occupant) { this.occupants.add(occupant); }

    public Floor getFloor() { return floor; }

    public RoomNode getRoomNode() { return roomNode; }

    @Override
    public String toString() {
        return "Room " + roomNumber;
    }

    @Override
    public int compareTo(Room o) {

        if (floor.getLevel() < o.floor.getLevel()) {
            return -1;
        }
        else if (floor.getLevel() > o.floor.getLevel()) {
            return 1;
        }
        else {
            return 0;
        }

    }
}

package com.example.usb.map.mapelems;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to represent a building as a whole, contains all floors and rooms as well as objects from graphelems.
 *
 * @author  Kazymir Rabier
 */
public class Building {

    private String name;
    private List<Floor> floors;
    public static final int FLOOR_LIMIT = 10;

    public Building(String name) {
        this.name = name;
        floors = new ArrayList<Floor>();
    }

    public Building(String name, List<Floor> floors){

        if (floors.size() >= 10) { throw new IllegalArgumentException("Can't have more than 10 floors."); }

        this.name = name;
        this.floors = floors;

    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<Floor> getFloors() { return floors; }

    public void addFloor(Floor floor) {
        if (floors.size() >= FLOOR_LIMIT) { throw new IllegalArgumentException("Can't add anymore floors."); }
        if (floors.isEmpty() && floor.getLevel() != 0) { throw new IllegalArgumentException("Need to add ground floor first."); }

        if (canAddFloor(floor)) {
            this.floors.add(floor);
        }
        else {
            throw new IllegalArgumentException("Can't skip floors.");
        }
    }

    public void clearFloors() {
        floors.removeAll(floors);
    }

    private boolean canAddFloor(Floor floor) {
        if (floors.isEmpty()) { return true; }

        int lowestFloor = Integer.MAX_VALUE;
        int highestFloor = Integer.MIN_VALUE;

        // TODO: Change from HashSet to SortedSet to avoid having to loop through every floor
        // Finds highest and lowest floor in set of floors
        for (Floor f: floors) {
            if (f.getLevel() > highestFloor) {
                highestFloor = f.getLevel();
            }
            if (f.getLevel() < lowestFloor) {
                lowestFloor = f.getLevel();
            }
        }

        if (floor.getLevel() == lowestFloor - 1 || floor.getLevel() == highestFloor + 1) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

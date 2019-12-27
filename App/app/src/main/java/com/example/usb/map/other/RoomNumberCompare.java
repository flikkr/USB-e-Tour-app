package com.example.usb.map.other;

import com.example.usb.map.mapelems.Room;

import java.util.Comparator;

/**
 * Class used to sort room numbers in sorted order
 *
 * @author  Kazymir Rabier
 */
public class RoomNumberCompare implements Comparator<Room> {

    @Override
    public int compare(Room o1, Room o2) {
        int here = Integer.parseInt(o1.getRoomNumber().substring(2, 5));
        int other = Integer.parseInt(o2.getRoomNumber().substring(2, 5));

        // Sorts rooms by level
        if (o1.getFloor().getLevel() == o2.getFloor().getLevel()) {
            if (here < other) {
                return -1;
            }
            else if (here > other) {
                return 1;
            }
            else {
                return 0;
            }
        }

        return 0;
    }
}

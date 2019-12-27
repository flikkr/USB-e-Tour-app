package com.example.usb.map;

import com.example.usb.map.mapelems.Floor;
import com.example.usb.map.mapelems.Room;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AFloorShould {

    Floor floor;

    @Before
    public void setUp() throws Exception {
         floor = new Floor(0);
    }

    @Test
    public void addRoom() {
        Room room = new Room("3.003", floor);

        floor.addRoom(room);

        assertTrue(floor.getRooms().contains(room));
    }

    @Test
    public void getGraph() {
        assertSame(floor, floor.getGraph().getFloor());
    }
}
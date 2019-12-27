package com.example.usb.map;

import com.example.usb.map.mapelems.Floor;
import com.example.usb.map.mapelems.Room;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ARoomShould {

    Floor floor;
    Room room;

    @Before
    public void setUp() throws Exception {
        floor = new Floor(0);
        room = new Room("3.003", floor);
    }

    @Test
    public void setCorrectRoomNumber() {
        room.setRoomName("4.002");

        assertEquals("4.002", room.getRoomName());
    }

    //TODO: TEST more
    @Test (expected = IllegalArgumentException.class)
    public void notSetRoomNumberIfIncorrectPattern() {
        room.setRoomNumber("774.002a");
    }

    @Test
    public void setRoomName() {
        room.setRoomName("Office");

        assertEquals("Office", room.getRoomName());
    }

    @Test
    public void setResident() {
        room.addOccupant("Foo Bar");

        assertEquals("Foo Bar", room.getOccupants());
    }

    @Test
    public void getFloor() {
        assertSame(floor, room.getFloor());
    }
}
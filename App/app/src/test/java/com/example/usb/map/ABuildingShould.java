package com.example.usb.map;

import com.example.usb.map.mapelems.Building;
import com.example.usb.map.mapelems.Floor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ABuildingShould {

    Building usb;

    @Before
    public void setUp() throws Exception {
        usb = new Building("Urban Sciences Building");
    }

    @Test
    public void setName() {
        usb.setName("Herschel");
        assertEquals("Herschel", usb.getName());
    }

    @Test
    public void allowToAddFloor() {
        Floor level0 = new Floor(0);

        usb.addFloor(level0);

        assertTrue(usb.getFloors().contains(level0));
    }

    @Test (expected = IllegalArgumentException.class)
    public void allowToOnlyAddGroundFloorFirst() {
        Floor level1 = new Floor(1);

        usb.addFloor(level1);
    }

    @Test
    public void allowToClearAllFloors() {
        Floor level0 = new Floor(0);
        Floor level1 = new Floor(1);

        usb.addFloor(level0);
        usb.addFloor(level1);

        usb.clearFloors();

        assertTrue(usb.getFloors().isEmpty());
    }

    @Test
    public void allowToAddFloorsAboveIt() {
        Floor level0 = new Floor(0);
        Floor level1 = new Floor(1);

        usb.addFloor(level0);
        usb.addFloor(level1);

        assertTrue(usb.getFloors().contains(level1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void notAllowToSkipFloorsAboveIt() {
        Floor level0 = new Floor(0);
        Floor level2 = new Floor(2);

        usb.addFloor(level0);
        usb.addFloor(level2);
    }

    @Test
    public void allowToAddFloorsBelowIt() {
        Floor level0 = new Floor(0);
        Floor basement1 = new Floor(-1);

        usb.addFloor(level0);
        usb.addFloor(basement1);

        assertTrue(usb.getFloors().contains(basement1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void notAllowToSkipFloorsBelowIt() {
        Floor level0 = new Floor(0);
        Floor basement2 = new Floor(-2);

        usb.addFloor(level0);
        usb.addFloor(basement2);
    }

    @Test
    public void allowToAddMultipleFloors() {
        Floor level0 = new Floor(0);
        Floor basement1 = new Floor(-1);
        Floor basement2 = new Floor(-2);
        Floor level1 = new Floor(1);

        usb.addFloor(level0);
        usb.addFloor(basement1);
        usb.addFloor(level1);
        usb.addFloor(basement2);

        assertTrue(usb.getFloors().contains(level0) &&
                usb.getFloors().contains(basement1) &&
                usb.getFloors().contains(level1) &&
                usb.getFloors().contains(basement2));
    }

    @Test (expected = IllegalArgumentException.class)
    public void allowToAddMultipleFloorsButNotSkipFloor() {
        Floor basement2 = new Floor(-2);
        Floor basement1 = new Floor(-1);
        Floor level0 = new Floor(0);
        Floor level1 = new Floor(1);
        Floor level3 = new Floor(3);

        usb.addFloor(level0);
        usb.addFloor(basement1);
        usb.addFloor(level1);
        usb.addFloor(basement2);
        usb.addFloor(level3);
    }

    @Test
    public void beAbleToHave10Floors() {
        for (int i = 0; i <= 9; i++) {
            Floor f = new Floor(i);
            usb.addFloor(f);
        }

        assertEquals(10, usb.getFloors().size());
    }

    @Test (expected = IllegalArgumentException.class)
    public void notAllowMoreThan10Floors() {
        for (int i = 0; i <= 10; i++) {
            Floor f = new Floor(i);
            usb.addFloor(f);
        }
    }
}
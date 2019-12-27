package com.example.usb;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.usb.map.factory.EdgeFactory;
import com.example.usb.map.factory.FloorFactory;
import com.example.usb.map.factory.NodeFactory;
import com.example.usb.map.factory.RoomFactory;
import com.example.usb.map.graphelems.Edge;
import com.example.usb.map.graphelems.Node;
import com.example.usb.map.mapelems.Floor;
import com.example.usb.map.mapelems.Room;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AFactoryShould {

    Context appContext;

    @Before
    public void setUp() throws Exception {
        appContext = InstrumentationRegistry.getTargetContext();

        FloorFactory.fetchAllFromDB(appContext);
        RoomFactory.fetchAllFromDB(appContext);
        NodeFactory.fetchAllFromDB(appContext);
        EdgeFactory.fetchAllFromDB(appContext);
    }

    @Test
    public void containAllFloors() {
        int floorsSize = FloorFactory.getFloors().size();

        assertEquals(7, floorsSize);
    }

    @Test
    public void containAllRooms() {
        int roomsSize = RoomFactory.getRooms().size();

        assertEquals(152, roomsSize);
    }

    @Test
    // Elevation nodes have been tested to contain above and below nodes.
    // Room nodes have been to contain the Room object they refer to.
    public void containAllNodes() {
        int nodesSize = NodeFactory.getNodes().size();

        assertTrue(nodesSize > 0);
    }

    @Test
    public void containAllEdges() {
        int edgesSize = EdgeFactory.getEdges().size();

        assertTrue(edgesSize > 0);
    }
}

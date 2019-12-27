package com.example.usb.map.factory;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.usb.database.DatabaseHelper;
import com.example.usb.map.graphelems.ElevationNode;
import com.example.usb.map.graphelems.Graph;
import com.example.usb.map.graphelems.LiftNode;
import com.example.usb.map.graphelems.Node;
import com.example.usb.map.graphelems.RoomNode;
import com.example.usb.map.graphelems.StairNode;
import com.example.usb.map.graphelems.TransitNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Pulls information from database regarding nodes.
 *
 * @author  Kazymir Rabier, Francesco Galassi
 */
public final class NodeFactory {
    private static final Map<Integer, Node> NODES = new HashMap<Integer, Node>();

    // Returns instance of nodes
    public static final Node getInstance(int nodeID) {
        if (!NODES.containsKey(nodeID)) {
            throw new IllegalArgumentException("Node does not exist.");
        }

        return NODES.get(nodeID);
    }

    public static final void clear() {
        NODES.clear();
    }

    public static Map<Integer, Node> getNodes() {
        return NODES;
    }

    // Pulls all nodes from the database and initialises them as objects
    public static void fetchAllFromDB(Context context) {
        DatabaseHelper mDBHelper = new DatabaseHelper(context);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        SQLiteDatabase mDb;

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        final String qq = "SELECT IDNode, Type, Restricted FROM Node";
        Cursor cursor = mDb.rawQuery(qq, null);

        if (cursor.getCount() == 0) {
        } else {
            // General node variables initialised to arbitrary values
            String typeOfNode;
            String name = "";
            int id = -1;
            int xPos = -1;
            int yPos = -1;
            Graph graph = new Graph();
            int restricted;

            while (cursor.moveToNext()) {
                // TODO: update node type with database type
                id = cursor.getInt(0);
                typeOfNode = cursor.getString(1);
                restricted = cursor.getInt(2);

                Cursor innerCursor;

                // Set node as RoomNode object
                if (typeOfNode.equals("Room")) {
                    final String query = "SELECT XCoord, YCoord, Floor, RoomNumber FROM Rooms WHERE NodeID = ?";
                    innerCursor = mDb.rawQuery(query, new String[]{""+id});//no idea I needed a String from Int

                    RoomNode roomNode;

                    if (innerCursor.getCount() == 0) {
                    } else {
                        while (innerCursor.moveToNext()) {
                            xPos = innerCursor.getInt(0);
                            yPos = innerCursor.getInt(1);
                            graph = FloorFactory.getInstance(innerCursor.getInt(2)).getGraph();
                            name = innerCursor.getString(3);

                            try {
                                roomNode = RoomFactory.getInstance(name).getRoomNode();
                            } catch(Exception e) {
                                throw new IllegalArgumentException("Room doesn't exist.");
                            }

                            roomNode.setXPos(xPos);
                            roomNode.setYPos(yPos);
                            roomNode.setId(id);

                            if (restricted == 1) {
                                roomNode.setRestricted(true);
                            } else {
                                roomNode.setRestricted(false);
                            }

                            NODES.put(id, roomNode);
                        }
                    }
                }
                // Set node as TransitionNode object
                else if (typeOfNode.equals("Transition")) {
                    final String query = "SELECT XCoord, YCoord, Floor FROM TransitionNode WHERE NodeID = ?";
                    innerCursor = mDb.rawQuery(query, new String[]{""+id});//no idea I needed a String from Int

                    if (innerCursor.getCount() == 0) {
                    } else {
                        while (innerCursor.moveToNext()) {
                            xPos = innerCursor.getInt(0);
                            yPos = innerCursor.getInt(1);
                            graph = FloorFactory.getInstance(innerCursor.getInt(2)).getGraph();
                        }
                    }

                    NODES.put(id, new TransitNode(id, xPos, yPos, graph));
                }
                // Set node as StairNode object
                else if (typeOfNode.equals("Stair")) {
                    final String query = "SELECT XCoord, YCoord, Floor FROM Stairs WHERE NodeID = ?";
                    innerCursor = mDb.rawQuery(query, new String[]{""+id});//no idea I needed a String from Int

                    if (innerCursor.getCount() == 0) {
                    } else {
                        while (innerCursor.moveToNext()) {
                            xPos = innerCursor.getInt(0);
                            yPos = innerCursor.getInt(1);
                            graph = FloorFactory.getInstance(innerCursor.getInt(2)).getGraph();
                        }
                    }

                    StairNode stairNode = new StairNode(id, xPos, yPos, graph);

                    if (restricted == 1) {
                        stairNode.setRestricted(true);
                    } else {
                        stairNode.setRestricted(false);
                    }

                    NODES.put(id, stairNode);
                }
                // Set node as LiftNode object
                else if (typeOfNode.equals("Lift")) {
                    final String query = "SELECT XCoord, YCoord, Floor, RoomNumber FROM Rooms WHERE NodeID = ?";
                    innerCursor = mDb.rawQuery(query, new String[]{""+id});//no idea I needed a String from Int

                    if (innerCursor.getCount() == 0) {
                    } else {
                        while (innerCursor.moveToNext()) {
                            xPos = innerCursor.getInt(0);
                            yPos = innerCursor.getInt(1);
                            graph = FloorFactory.getInstance(innerCursor.getInt(2)).getGraph();
                            name = innerCursor.getString(3);
                        }
                    }

                    LiftNode liftNode = new LiftNode(id, xPos, yPos, graph);
                    liftNode.setLiftNumber(name);

                    if (restricted == 1) {
                        liftNode.setRestricted(true);
                    } else {
                        liftNode.setRestricted(false);
                    }

                    NODES.put(id, liftNode);
                }
            }
        }

        /**
         * Populates ElevationNode with above and below nodes
         */
        final String aboveBelowQuery = "SELECT IDNode, Above, Below FROM Node WHERE Type LIKE \"Stair\" OR Type LIKE \"Lift\"";
        Cursor secondCursor = mDb.rawQuery(aboveBelowQuery, null);

        if (secondCursor.getCount() == 0) {
        } else {
            while (secondCursor.moveToNext()) {
                int currentNodeID;
                int aboveID;
                int belowID;

                while (secondCursor.moveToNext()) {

                    currentNodeID = secondCursor.getInt(0);
                    aboveID = secondCursor.getInt(1);
                    belowID = secondCursor.getInt(2);

                    // If node is not an elevation node, then skip
                    if (!(NODES.get(currentNodeID) instanceof ElevationNode)) { continue; }

                    // If node has above node, link them
                    if (aboveID != 0) {
                        ((ElevationNode) NODES.get(currentNodeID)).addLink((ElevationNode) NODES.get(aboveID));
                    }

                    // If node has below node, link them
                    if (belowID != 0) {
                        ((ElevationNode) NODES.get(currentNodeID)).addLink((ElevationNode) NODES.get(belowID));
                    }
                }
            }
        }
    }
}

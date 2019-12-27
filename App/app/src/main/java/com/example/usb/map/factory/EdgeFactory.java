package com.example.usb.map.factory;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.usb.database.DatabaseHelper;
import com.example.usb.map.graphelems.Edge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pulls information from database regarding edges.
 *
 * @author  Kazymir Rabier
 */
public final class EdgeFactory {
    private static final Map<String, Edge> EDGES = new HashMap<String, Edge>();

    // Returns instance of edge
    public static final Edge getInstance(int srcID, int destID) {
        String srcIDCommaDestID = srcID + "," + destID;
        String destIDCommaSrcID = destID + "," + srcID;

        if (!EDGES.containsKey(srcIDCommaDestID) || !EDGES.containsKey(destIDCommaSrcID)) {
            throw new IllegalArgumentException("Edge does not exist.");
        }

        if (EDGES.containsKey(srcIDCommaDestID)) {
            return EDGES.get(srcIDCommaDestID);
        } else {
            return EDGES.get(destIDCommaSrcID);
        }
    }

    public static final void clear() {
        EDGES.clear();
    }

    public static Map<String, Edge> getEdges() {
        return EDGES;
    }

    // Pulls all edges from the database and initialises them as objects
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

        final String query = "SELECT srcID, destID, Weight FROM Edges";
        Cursor cursor = mDb.rawQuery(query, null);

        if (cursor.getCount() == 0) {
        } else {
            int srcID;
            int destID;
            int weight;

            while (cursor.moveToNext()) {
                srcID = cursor.getInt(0);
                destID = cursor.getInt(1);
                weight = cursor.getInt(2);

                if ((NodeFactory.getNodes().containsKey(srcID) && NodeFactory.getNodes().containsKey(destID)) &&
                        (!EDGES.containsKey(srcID + "," + destID) || !EDGES.containsKey(destID + "," + srcID))) {
                    EDGES.put(srcID + "," + destID, new Edge(NodeFactory.getInstance(srcID), NodeFactory.getInstance(destID), weight));
                }
            }
        }
    }
}

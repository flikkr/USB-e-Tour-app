package com.example.usb.map.factory;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.usb.database.DatabaseHelper;
import com.example.usb.database.DatabaseHelper;
import com.example.usb.map.mapelems.Floor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pulls information from database regarding floors
 *
 * @author  Kazymir Rabier, Francesco Galassi
 */
public final class FloorFactory {
    private static final Map<Integer, Floor> FLOORS = new HashMap<Integer, Floor>();

    // Returns instance of floor
    public static final Floor getInstance(Integer floorNumber) {
        if (!FLOORS.containsKey(floorNumber)) {
            throw new IllegalArgumentException("Floor does not exist.");
        }

        return FLOORS.get(floorNumber);
    }

    public static final void clear() {
        FLOORS.clear();
    }

    public static Map<Integer, Floor> getFloors() {
        return FLOORS;
    }

    // Pulls all floors from the database and initialises them as objects
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

        // TODO: Update query
        final String qq = "SELECT DISTINCT Floor FROM Rooms";
        Cursor cursor = mDb.rawQuery(qq, null);

        if (cursor.getCount() == 0) {
        } else {
            while (cursor.moveToNext()) {
                int floorNumber = Integer.parseInt(cursor.getString(0));

                FLOORS.put(floorNumber, new Floor(floorNumber));
            }
        }
    }
}

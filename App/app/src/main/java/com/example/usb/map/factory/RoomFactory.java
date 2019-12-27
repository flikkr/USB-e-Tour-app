package com.example.usb.map.factory;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.usb.database.DatabaseHelper;
import com.example.usb.map.mapelems.Floor;
import com.example.usb.map.mapelems.Room;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pulls information from database regarding rooms.
 *
 * @author  Kazymir Rabier
 */
public final class RoomFactory {
    private static final Map<String, Room> ROOMS = new HashMap<String, Room>();

    // Returns instance of rooms
    public static final Room getInstance(String roomNumber) {
        if (!ROOMS.containsKey(roomNumber)) {
            throw new IllegalArgumentException("Room does not exist.");
        }

        return ROOMS.get(roomNumber);
    }

    public static final void clear() {
        ROOMS.clear();
    }

    public static Map<String, Room> getRooms() {
        return ROOMS;
    }

    // Pulls all rooms from the database and initialises them as objects
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
        final String qq = "SELECT RoomNumber, Floor, Name FROM Rooms";
        Cursor cursor = mDb.rawQuery(qq, null);

        if (cursor.getCount() == 0) {//TO-DO
            throw new IllegalArgumentException("Cursor");

        } else {
            String roomNumber;
            Floor floor;
            String roomName;
            List<String> resident;

            while (cursor.moveToNext()) {
                resident = new ArrayList<String>();

                roomNumber = cursor.getString(0);
                floor = FloorFactory.getInstance(cursor.getInt(1));
                roomName = cursor.getString(2);

                String qq2 = "SELECT FirstName, SecondName FROM Staff WHERE ID IN (SELECT IDStaff FROM Occupancy WHERE RoomNumber LIKE ?)";
                Cursor cursor2 = mDb.rawQuery(qq2, new String[]{roomNumber});

                if (cursor2.getCount() == 0) {//TO-DO fail safe

                } else {
                    while (cursor2.moveToNext()) {
                        resident.add(cursor2.getString(0)+" "+cursor2.getString(1));
                    }
                }
                ROOMS.put(roomNumber, new Room(roomNumber, floor, roomName, resident));
            }
        }
    }
}

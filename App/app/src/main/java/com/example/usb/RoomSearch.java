package com.example.usb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.usb.database.DatabaseHelper;
import com.example.usb.map.factory.RoomFactory;
import com.example.usb.roomsearch.RoomRecyclerViewAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author  Kazymir Rabier, Lukas Sysojevas, Francesco Galassi
 */
public class RoomSearch extends Fragment implements RoomRecyclerViewAdapter.ItemClickListener {

    private RoomRecyclerViewAdapter adapter;

    // data to populate the RecyclerView with
    private List<String> rooms = new ArrayList<>(Arrays.asList(MainActivity.rooms));

    private SQLiteDatabase mDb;
    private EditText input;

    private RecyclerView recyclerView;

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Room Search");

        // set up the RecyclerView
        recyclerView = view.findViewById(R.id.roomRecyclerView);
        input = view.findViewById(R.id.roomSearchEditText);

        //TODO: Fix recycler view list elements getting clipped by the other view (on certain devices)
        LinearLayoutManager linearManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearManager);
        adapter = new RoomRecyclerViewAdapter(getActivity().getApplicationContext(), rooms);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        DatabaseHelper mDBHelper = new DatabaseHelper(getActivity().getApplicationContext());

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        View v = view.findViewById(R.id.roomPageView);
        v.bringToFront();
        input.bringToFront();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.content_room_search, null);

        // Updates list from the search input
        final Button button_search = v.findViewById(R.id.searchBtn);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Searching...",
                        Toast.LENGTH_LONG).show();

                List<String> result;

                try {
                    result = searchRooms();
                }
                catch (IllegalArgumentException iae) {
                    Toast.makeText(getActivity(), iae.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                adapter.updateData(result);
            }
        });

        // Resets list to original
        final Button button_clear = v.findViewById(R.id.resetBtn);
        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Resetting...",
                        Toast.LENGTH_LONG).show();

                List<String> originalRooms = new ArrayList<>(Arrays.asList(MainActivity.rooms));

                adapter.updateData(originalRooms);
            }
        });

        return v;

    }

    // Starts RoomPage fragment when clicking on a given room item
    public void onItemClick(View view, int position) {
        String roomName = adapter.getItem(position).split(" ")[0];
        MainActivity.selectedRoom = RoomFactory.getInstance(roomName);

        Fragment fragment = new RoomPage();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.screen_area, fragment ); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }

    // Method filters out when searching for room occupants, room number or room name
    public List<String> searchRooms() {
        // - you search "1.006" (full RoomNumber)
        String content = input.getText().toString();
        String qq = "SELECT RoomNumber, Name FROM Rooms WHERE RoomNumber LIKE ?";
        Cursor cursor = mDb.rawQuery(qq, new String[]{""+content});
        List<String> searchResult = new ArrayList<>();
        if (cursor.getCount() == 0) {
            // - you search "1" (partial RoomNumber)
            content = content + "%";
            qq = "SELECT RoomNumber, Name FROM Rooms WHERE RoomNumber LIKE ?";
            cursor = mDb.rawQuery(qq, new String[]{""+content});
            searchResult = new ArrayList<>();
            if (cursor.getCount() == 0) {
                // -you search "Cyber Physical Lab" (full Name)
                content = input.getText().toString();
                qq = "SELECT RoomNumber, Name FROM Rooms WHERE Name LIKE ?";
                cursor = mDb.rawQuery(qq, new String[]{""+content});
                searchResult = new ArrayList<>();
                if (cursor.getCount() == 0) {
                    // -you search "Cyber" (partial Name 'starting with')
                    content = content+"%";
                    qq = "SELECT RoomNumber, Name FROM Rooms WHERE Name LIKE ?";
                    cursor = mDb.rawQuery(qq, new String[]{""+content});
                    searchResult = new ArrayList<>();
                    if (cursor.getCount() == 0) {
                        // -you search "Physical " (partial Name 'middle')
                        content = "%"+content+"%";
                        qq = "SELECT RoomNumber, Name FROM Rooms WHERE Name LIKE ?";
                        cursor = mDb.rawQuery(qq, new String[]{""+content});
                        searchResult = new ArrayList<>();

                        if(cursor.getCount() == 0){
                            // - You search "Chris" (partial name or surname)
                            content = "%"+content+"%";
                            qq = "SELECT RoomNumber, Name FROM Rooms WHERE RoomNumber IN( SELECT RoomNumber FROM  Occupancy WHERE IDStaff IN (SELECT ID FROM Staff WHERE FirstName LIKE ? OR SecondName LIKE ?))";
                            cursor = mDb.rawQuery(qq, new String[]{""+content,""+content});
                            searchResult = new ArrayList<>();
                            if(cursor.getCount() == 0){
                                //
                                String[] splited = content.split("\\s+");
                                qq = "SELECT RoomNumber, Name FROM Rooms WHERE RoomNumber IN( SELECT RoomNumber FROM  Occupancy WHERE IDStaff IN (SELECT ID FROM Staff WHERE (FirstName LIKE ? AND SecondName LIKE ?)OR(FirstName LIKE ? AND SecondName LIKE ?)))";
                                cursor = mDb.rawQuery(qq, new String[] {"%"+splited[0]+"%","%"+splited[1]+"%","%"+splited[1]+"%","%"+splited[0]+"%"});
                                searchResult = new ArrayList<>();
                                if(cursor.getCount() == 0){
                                    // TODO: ERROR
                                    // ERROR
                                }else{
                                    while (cursor.moveToNext()) {
                                        searchResult.add(cursor.getString(0) + " " + cursor.getString(1));
                                    }
                                }
                            }else{
                                // - You search "Chris" (partial name or surname)
                                while (cursor.moveToNext()) {
                                    searchResult.add(cursor.getString(0) + " " + cursor.getString(1));
                                }
                            }
                        }
                        else{
                            // -you search "Physical " (partial Name 'middle')
                            // -you search "Lab" (partial Name 'ending with')
                            while (cursor.moveToNext()) {
                                searchResult.add(cursor.getString(0) + " " + cursor.getString(1));
                            }
                        }


                    }else{
                        // -you search "Cyber" (partial Name 'starting with')
                        // -you search "Lab" (partial Name 'ending with')
                        while (cursor.moveToNext()) {
                            searchResult.add(cursor.getString(0) + " " + cursor.getString(1));
                        }
                    }
                }else {
                    // -you search "Cyber Physical Lab" (full Name)
                    while (cursor.moveToNext()) {
                        searchResult.add(cursor.getString(0) + " " + cursor.getString(1));
                    }
                }
            }else {
                // - you search "1" (partial RoomNumber)
                while (cursor.moveToNext()) {
                    searchResult.add(cursor.getString(0) + " " + cursor.getString(1));
                }
            }
        } else {
            // - you search "1.006" (full RoomNumber)
            while (cursor.moveToNext()) {
                searchResult.add(cursor.getString(0) + " " + cursor.getString(1));
            }
        }
        return searchResult;
    }
}
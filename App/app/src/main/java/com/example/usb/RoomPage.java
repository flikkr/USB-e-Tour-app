package com.example.usb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usb.database.DatabaseHelper;
import com.example.usb.map.MapDrawer;
import com.example.usb.map.graphelems.Node;
import com.example.usb.map.mapelems.Room;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author  Kazymir Rabier, Lukas Sysojevas
 */
public class RoomPage extends Fragment implements View.OnClickListener  {

    private ImageView imageView;
    private ImageView imageView1;
    private Room currentRoom = MainActivity.selectedRoom;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(currentRoom.getRoomName());

        imageView = view.findViewById(R.id.roomPicture);

        imageView1 = getView().findViewById(R.id.background_image);
        loadImageByResourceId();

        displayRoomImage();
    }

    @Nullable
    @Override
    // Populates the page with information about the room
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.content_room_page, null);

        final Button route_button = v.findViewById(R.id.routeBtn);
        route_button.setOnClickListener(this);

        final TextView roomNumber = v.findViewById(R.id.roomNumber);
        roomNumber.setText(currentRoom.getRoomNumber());

        final TextView roomName = v.findViewById(R.id.roomNameValue);
        roomName.setText(currentRoom.getRoomName());

        final TextView roomFloor = v.findViewById(R.id.floorNumberValue);
        roomFloor.setText("" + currentRoom.getFloor().getLevel());

        // Updates the list of occupants in the room
        final TextView roomOccupants = v.findViewById(R.id.occupantsValue);
        if (currentRoom.getOccupants().size() != 0) {
            roomOccupants.setText("");
            for (int i = 0; i < currentRoom.getOccupants().size(); i++) {
                roomOccupants.append(currentRoom.getOccupants().get(i) + "\n");
            }
        }

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.routeBtn:
                switchFragment();
                break;
        }
    }

    // Takes the user to the Navigation fragment
    private void switchFragment() {
        MainActivity.routedRoom = currentRoom.getRoomNumber() + " " + currentRoom.getRoomName();

        Fragment fragment = new Navigation();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.screen_area, fragment ); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }

    // Displays the image of a given room
    private void displayRoomImage() {
        SQLiteDatabase mDb;

        try {
            mDb = initialiseDB();
        }
        catch (Error e) {
            Toast.makeText(getActivity(), "Error encountered when communicating with the database.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String content = MainActivity.selectedRoom.getRoomNumber();

        String qq = "SELECT Content FROM MultimediaContent WHERE RoomNumber LIKE ?";
        Cursor cursor = mDb.rawQuery(qq, new String[] {content});

        if(cursor.getCount() == 0) {
            int resourceID = R.drawable.no_image_available;

            GlideApp.with(this)
                    .load(resourceID)
                    .centerCrop()
                    .into(imageView);
            return;
        }else{

            String url = "";

            while (cursor.moveToNext()) {
                url = cursor.getString(0);

            }
            Picasso.get()
                    .load(url)
                    .into(imageView);
            return;
        }
    }

    // Initialises database for use
    private SQLiteDatabase initialiseDB() {
        DatabaseHelper mDBHelper = new DatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase mDb;

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

        return mDb;
    }

    // Loads background image
    public void loadImageByResourceId(){

        int resourceId = R.drawable.interior_2;

        GlideApp.with(this)
                .load(resourceId)
                .centerCrop()
                .into(imageView1);
    }
}

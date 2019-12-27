package com.example.usb;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.usb.map.Dijkstra;
import com.example.usb.map.factory.RoomFactory;
import com.example.usb.map.graphelems.Node;
import com.example.usb.map.graphelems.RoomNode;

import java.util.LinkedList;

/**
 * @author  Lukas Sysojevas, Kazymir Rabier
 */
public class Navigation extends Fragment implements View.OnClickListener  {

    private ImageView imageView1;
    private EditText startLocation;
    private EditText endLocation;
    private Switch decisionSwitchPath;
    private Switch decisionSwitchRestriction;
    private Dijkstra dijkstra;
    private RoomNode pastSource;
    private RoomNode pastDestination;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        imageView1 = getView().findViewById(R.id.roomPicture);
        startLocation = view.findViewById(R.id.startText);
        endLocation = view.findViewById(R.id.roomSearchEditText);

        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Building Navigation");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, MainActivity.rooms);
        AutoCompleteTextView startTextView = view.findViewById(R.id.startText);
        AutoCompleteTextView endTextView = view.findViewById(R.id.roomSearchEditText);

        // After entering one character, the dropdown appears
        startTextView.setThreshold(1);
        endTextView.setThreshold(1);

        startTextView.setAdapter(adapter);
        endTextView.setAdapter(adapter);

        dijkstra = new Dijkstra(MainActivity.building);

        if (!MainActivity.routedRoom.equals("")) {
            endLocation.setText(MainActivity.routedRoom);
            MainActivity.routedRoom = "";
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.content_building_navigation, null);

        final Button button_search = v.findViewById(R.id.button_search);
        button_search.setOnClickListener(this);

        final Button button_clear = v.findViewById(R.id.button_clear);
        button_clear.setOnClickListener(this);

        decisionSwitchPath = v.findViewById(R.id.decisionSwitchPath);
        decisionSwitchRestriction = v.findViewById(R.id.decisionSwitchRestriction);

        return v;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_search:
                onSearchClick();
                break;
            case R.id.button_clear:
                onClearClick();
                break;
        }
    }

    // Method used to execute the pathfinding algorithm
    public void onSearchClick(){
        String start = startLocation.getText().toString().toUpperCase().split(" ")[0];
        String end = endLocation.getText().toString().toUpperCase().split(" ")[0];

        // Checks if input is valid
        if (!RoomFactory.getRooms().containsKey(start) && !RoomFactory.getRooms().containsKey(end)) {
            Toast.makeText(getActivity(), "Both origin and destination rooms do not exist.",
                    Toast.LENGTH_LONG).show();
        }
        // If start room doesn't exist
        else if (!RoomFactory.getRooms().containsKey(start)) {
            Toast.makeText(getActivity(), "Starting room does not exist.",
                    Toast.LENGTH_LONG).show();
        }
        // If end room doesn't exist
        else if (!RoomFactory.getRooms().containsKey(end)) {
            Toast.makeText(getActivity(), "Destination room does not exist.",
                    Toast.LENGTH_LONG).show();
        }
        // If both rooms are the same
        else if (start.equals(end)) {
            Toast.makeText(getActivity(), "Can't travel if both origin and destination room are the same.",
                    Toast.LENGTH_LONG).show();
        }
        // Perform search
        else {
            RoomNode src = RoomFactory.getInstance(start).getRoomNode();
            RoomNode dest = RoomFactory.getInstance(end).getRoomNode();

            // Checks that the new search is not the same as the last performed search
            if (pastSource != null && pastDestination != null) {
                if (pastSource.equals(src) && pastDestination.equals(dest)) {
                    return;
                }
            }

            // If user wants to use lift, change decision in Dijkstra class
            if (decisionSwitchPath.isChecked()) {
                dijkstra.setDecisionToStairs(false);
            } else {
                dijkstra.setDecisionToStairs(true);
            }

            // If a user is a member of staff, give access to restricted stairs and lifts
            if (decisionSwitchRestriction.isChecked()) {
                dijkstra.setDecisionToStudent(false);
            } else {
                dijkstra.setDecisionToStudent(true);
            }

            LinkedList<Node> path;

            try {
                path = dijkstra.shortestPath(src, dest);
            }
            catch (IllegalArgumentException iae) {
                Toast.makeText(getActivity(), "There is no path to your destination, please try another route.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(this).attach(this).commit();

            MainActivity.finalPath = path;

            pastSource = src;
            pastDestination = dest;
        }
    }

    // Clears map and inputs
    public void onClearClick(){
        startLocation.setText("");
        endLocation.setText("");
        pastSource = null;
        pastDestination = null;

        MainActivity.finalPath = new LinkedList<Node>();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

}
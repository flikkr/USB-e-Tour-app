package com.example.usb;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.usb.map.factory.EdgeFactory;
import com.example.usb.map.factory.FloorFactory;
import com.example.usb.map.factory.NodeFactory;
import com.example.usb.map.factory.RoomFactory;
import com.example.usb.map.graphelems.Edge;
import com.example.usb.map.graphelems.Graph;
import com.example.usb.map.graphelems.Node;
import com.example.usb.map.mapelems.Building;
import com.example.usb.map.mapelems.Floor;
import com.example.usb.map.mapelems.Room;
import com.example.usb.map.other.RoomNumberCompare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author  Lukas Sysojevas
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Stores building object for room finder
    public static Building building;
    public static String[] rooms;
    public static Room selectedRoom;
    public static LinkedList<Node> finalPath;
    public static String routedRoom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = null;
        fragment = new MainPage();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.replace(R.id.screen_area, fragment);

        ft.commit();

        initialiseDB();
        fetchRoomNames();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if(id == R.id.nav_about){
            fragment = new About();
        } else if(id == R.id.nav_location){
            fragment = new Location();
        } else if(id == R.id.nav_building_navigation){
            fragment = new Navigation();

            MainActivity.finalPath = new LinkedList<Node>();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(fragment).attach(fragment).commit();

        } else if(id == R.id.nav_contact){
            fragment = new Contacts();
        } else if(id == R.id.nav_main_page){
            fragment = new MainPage();
        } else if(id == R.id.nav_finder){
            fragment = new RoomSearch();
        }
        else if(id == R.id.nav_maps){
            fragment = new Drawer();

            Bundle bundle = new Bundle();
            Boolean myMessage = true;
            bundle.putBoolean("message", myMessage );
            fragment.setArguments(bundle);

            MainActivity.finalPath = new LinkedList<Node>();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(fragment).attach(fragment).commit();
        }   else if(id == R.id.nav_website){
            Intent httpIntent = new Intent(Intent.ACTION_VIEW);
            httpIntent.setData(Uri.parse("http://homepages.cs.ncl.ac.uk/2018-19/CSC2022/Team16/website/"));
            startActivity(httpIntent);

        }



        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(R.id.screen_area, fragment);

            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // Initialises map objects by pulling data from database
    private void initialiseDB() {
        FloorFactory.fetchAllFromDB(getApplicationContext());
        building = new Building("Urban Sciences Building", new ArrayList<Floor>(FloorFactory.getFloors().values()));

        RoomFactory.fetchAllFromDB(getApplicationContext());
        NodeFactory.fetchAllFromDB(getApplicationContext());
        EdgeFactory.fetchAllFromDB(getApplicationContext());

        for (Floor f: building.getFloors()) {
            Graph g = f.getGraph();

            for (Room r: RoomFactory.getRooms().values()) {
                if (f.getLevel() == r.getFloor().getLevel()) {
                    f.addRoom(r);
                }
            }

            for (Node n: NodeFactory.getNodes().values()) {
                if (g.getFloor().getLevel() == n.getGraph().getFloor().getLevel()) {
                    g.addNode(n);
                }
            }

            for (Edge e: EdgeFactory.getEdges().values()) {
                if (g.getFloor().getLevel() == e.getSource().getGraph().getFloor().getLevel()) {
                    g.addEdge(e);
                }
            }
        }
    }

    private static void fetchRoomNames() {
        List<Room> roomsToSort = new ArrayList<Room>(RoomFactory.getRooms().values());

        for (int i = 0; i < roomsToSort.size(); i++) {
            if (roomsToSort.get(i).getRoomName().contains("Lift") || roomsToSort.get(i).getRoomName().equals("Lift")) {
                roomsToSort.remove(i);
            }
        }

        RoomNumberCompare numberCompare = new RoomNumberCompare();
        Collections.sort(roomsToSort);
        Collections.sort(roomsToSort, numberCompare);

        String roomNumber;
        String roomName;

        List<String> roomNumberAndName = new ArrayList<>();

        for (Room room: roomsToSort) {
            roomNumber = room.getRoomNumber();
            roomName = room.getRoomName();

            roomNumberAndName.add(roomNumber + " " + roomName);
        }

        rooms = new String[roomNumberAndName.size()];
        roomNumberAndName.toArray(rooms);
    }
}
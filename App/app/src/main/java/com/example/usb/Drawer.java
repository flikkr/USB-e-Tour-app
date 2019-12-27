package com.example.usb;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.usb.map.MapDrawer;
import com.example.usb.map.graphelems.Node;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author  Miles Nelson
 */
public class Drawer extends Fragment implements View.OnClickListener {



    //the values manipulated to move the surfaceView around ont he users screen
    private int left;
    private int top;
    private int bottom;
    private int right;

    //final values set later because different screens are different sized
    private int fLeft;
    private int fTop;
    private int fBottom;
    private int fRight;

    //this contains all the drawables for all the floors, if this was done for a different building
    //then this is the one line(s) that need to be changed according to the amount of floors
    // and their respective resource name
    private int floors[] = {R.drawable.floor0, R.drawable.floor1, R.drawable.floor2, R.drawable.floor3,
            R.drawable.floor4, R.drawable.floor5, R.drawable.floor6};

    //change this to change background
    private int backgroundDrawable = R.drawable.blurred;
    //contains the nodes for a path
    //outer arrayList represent floors so index 0 is floor 0
    //inner arrayList represent the nodes the user will go through in order start being index 0
    private ArrayList<ArrayList<Node>> path = new ArrayList<ArrayList<Node>>();

    //this will contain all the floors as drawable once initialised
    //drawable are drawn onto the canvas
    private Drawable[] maps = new Drawable[floors.length];

    private Drawable background;


    //canvas is like MS paint, it is the thing everything is being drawn onto
    private Canvas canvas;



    //surface is the container for canvas, it displays it
    private SurfaceView surface;

    //represents the floor that is being displayed on the map currently
    private int current = 0;
    //this is the ratio of the images of the floor plans so it can be scaled correctly
    private final double RATIO = 1.41;

    private boolean floorSet;
    private int finalFloor;
    private LinkedList<Node> finalPath;

    //this class draws the path the user will take onto the map
    private MapDrawer MD = new MapDrawer();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Building Navigation");
        if (this.getArguments() != null) {
            getActivity().setTitle("Check Maps");

        }
        surface = (SurfaceView) getView().findViewById(R.id.surface);

        surface.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                canvas = holder.lockCanvas();

                right = canvas.getWidth();
                bottom = (int)(canvas.getWidth()/ RATIO);
                left = 0;
                top = 0;

                fRight = right;
                fBottom = bottom;
                fLeft = left;
                fTop = top;

                surface.getHolder().setFixedSize(right,bottom);
                mapGeneration();
                surface.layout(left, top, right, bottom);
                holder.unlockCanvasAndPost(canvas);

                finalPath = MainActivity.finalPath;

                for (ArrayList<Node> floor : path) {
                    floor.clear();
                }

                floorSet = false;

                for (Node node: finalPath) {
                    path.get(node.getGraph().getFloor().getLevel()).add(node);
                    finalFloor = node.getGraph().getFloor().getLevel();

                    if(floorSet == false) {
                        current = node.getGraph().getFloor().getLevel();
                        floorSet = true;
                    }
                }


                loadMap(current);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.content_map_output, null);
        if (this.getArguments() != null) {
            if(this.getArguments().getBoolean("message")){
                v = inflater.inflate(R.layout.content_check_maps, null);
            }
        }

        final ImageButton floorUpBtn = v.findViewById(R.id.floorUpBtn);
        floorUpBtn.setOnClickListener(this);

        final ImageButton floorDownBtn = v.findViewById(R.id.floorDownBtn);
        floorDownBtn.setOnClickListener(this);

        return v;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.floorUpBtn:
                buttonOnClickUp();
                break;
            case R.id.floorDownBtn:
                buttonOnClickDown();
                break;
        }
    }

    // Loads the map
    public void loadMap(int current) {
        canvas = surface.getHolder().lockCanvas();
        surface.getHolder().setFixedSize(right, bottom);
        clearCanvas();
        maps[current].draw(canvas);

//        Boolean lastFloor = false;

        // Used for mapping the colours of the nodes
        int floorCase = 0;

        // Only choose node colours when a search has been executed
        if (finalPath.size() != 0) {

            // Case where src and dest nodes are on the same floor
            if (finalPath.get(0).getGraph().getFloor().getLevel() == finalPath.get(finalPath.size() - 1).getGraph().getFloor().getLevel()) {
                floorCase = 0;
            }
            // Case where final floor has been reached
            else if (current == finalFloor) {
                floorCase = 1;
            }
            // Case where the final floor still hasn't been reached
            else {
                floorCase = 2;
            }
        }

        MD.place(path.get(current), canvas, floorCase);

        surface.getHolder().unlockCanvasAndPost(canvas);
    }

    // Button used to display the floor above
    public void buttonOnClickUp(){
        if(current != 6) {
            current++;
            loadMap(current);
        }
    }

    // Button used to display the floor below
    public void buttonOnClickDown(){
        if(current != 0) {
            current--;
            loadMap(current);
        }
    }

    public void mapGeneration(){
        for(int i = 0 ; i < floors.length ; i++){
            maps[i] = ContextCompat.getDrawable(getContext(), floors[i]);
            maps[i].setBounds(left,top,right,bottom);
            path.add(new ArrayList<Node>());
        }
        background = ContextCompat.getDrawable(getContext(), backgroundDrawable);
        background.setBounds(left,top,right,bottom);
    }

    public void clearCanvas(){
        background.draw(canvas);
    }
}
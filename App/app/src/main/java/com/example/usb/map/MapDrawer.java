package com.example.usb.map;

import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.SurfaceView;

import com.example.usb.Drawer;
import com.example.usb.MainActivity;
import com.example.usb.map.graphelems.Node;

import java.util.ArrayList;

/**
 * Class used to draw on the surface view containing the map canvas
 *
 * @author  Miles Nelson
 */
public class MapDrawer {

    //changes the thickness of the line
    int pathThickness = 6;
    Paint front = new Paint();
    Paint back = new Paint();
    float curRotate;
    boolean startPlaced;

    // Places two nodes on a map and draws a line between them
    public void place(ArrayList<Node> nodes, Canvas canvas, int floorCase){

        startPlaced = false;

        // Set the colour for start and end node depending on the case (see Drawer class for more info about the cases)
        switch (floorCase) {
            case 0:
                back.setColor(Color.GREEN);
                front.setColor(Color.RED);
                break;
            case 1:
                back.setColor(Color.BLUE);
                front.setColor(Color.RED);
                break;
            case 2:
                back.setColor(Color.BLUE);
                front.setColor(Color.BLUE);
                break;
        }

        for (int currentNode = 0 ; currentNode < nodes.size() - 1; currentNode++){

            float[] center = findCenter(nodes.get(currentNode).getXPos(), nodes.get(currentNode).getYPos(),
                    nodes.get(currentNode+1).getXPos(),nodes.get(currentNode+1).getYPos());

            curRotate = rotation(nodes.get(currentNode).getXPos(), nodes.get(currentNode).getYPos(), nodes.get(currentNode+1).getXPos(),nodes.get(currentNode+1).getYPos());

            canvas.rotate(curRotate, center[0], center[1]);
            canvas.drawRect(center[0] - (pathThickness / 2), center[1] - (center[2] / 2), center[0] + (pathThickness / 2), center[1] + (center[2] / 2), new Paint());
            canvas.rotate(-curRotate, center[0], center[1]);

            // If start node has been place, set all transit nodes to black
            if (startPlaced == true){
                back.setColor(Color.BLACK);
            }

            // If first node, set it to green
            if (nodes.get(currentNode).equals(MainActivity.finalPath.get(0))) {
                back.setColor(Color.GREEN);
            }

            canvas.drawCircle(nodes.get(currentNode).getXPos(), nodes.get(currentNode).getYPos(),10, back);
            canvas.drawCircle(nodes.get(currentNode+1).getXPos(),nodes.get(currentNode+1).getYPos(),10, front);
            startPlaced = true;
        }

        if (nodes.size() == 1) {
            front.setColor(Color.BLUE);
            canvas.drawCircle(nodes.get(0).getXPos(), nodes.get(0).getYPos(),10, front);
        }
    }

    public float rotation(float x1, float y1, float x2,  float y2){
        float difX = (x1-x2);
        float difY = (y1-y2);

        if(difX == 0 && difY == 0){
            return 0;
        }

        return (float)(180-Math.toDegrees(Math.atan(difX/difY)));
    }

    public float[] findCenter(float x1, float y1, float x2,  float y2){
        float midX;
        float midY;
        float length;

        midX = (x1+x2)/2;
        midY = (y1+y2)/2;
        length = (float) Math.sqrt(Math.abs((x1-x2)*(x1-x2))+Math.abs((y1-y2)*(y1-y2)));

        return new float[]{midX, midY, length};
    }
}
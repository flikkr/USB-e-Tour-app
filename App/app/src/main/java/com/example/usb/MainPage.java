package com.example.usb;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.usb.map.graphelems.Node;

import java.util.LinkedList;

/**
 * @author  Lukas Sysojevas
 */
public class MainPage extends Fragment implements View.OnClickListener {

    private ImageView imageView1;
    private ImageView imageView2;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Main Page");

        imageView1 = getView().findViewById(R.id.imageView1);
        imageView2 = getView().findViewById(R.id.imageView2);
        loadImageByResourceId();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v;
        v = inflater.inflate(R.layout.content_main_page, null);

        final Button button_map = v.findViewById(R.id.button_usb_map);
        button_map.setOnClickListener(this);

        final Button button_about = v.findViewById(R.id.button_about);
        button_about.setOnClickListener(this);

        final Button button_location = v.findViewById(R.id.button_location);
        button_location.setOnClickListener(this);

        final Button button_recyclerView = v.findViewById(R.id.recycle);
        button_recyclerView.setOnClickListener(this);

        return v;

    }

    @Override
    public void onClick(View v) {
        Fragment someFragment = null;

        switch (v.getId()) {
            case R.id.button_about:
                someFragment = new About();
                break;
            case R.id.button_location:
                someFragment = new Location();
                break;
            case R.id.button_usb_map:
                someFragment = new Navigation();

                MainActivity.finalPath = new LinkedList<Node>();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(someFragment).attach(someFragment).commit();
                break;
            case R.id.recycle:
                someFragment = new RoomSearch();
                break;
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.screen_area, someFragment ); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();


    }

    public void loadImageByResourceId(){

        int resourceId = R.drawable.interior_2;

        GlideApp.with(this)
                .load(resourceId)
                .centerCrop()
                .into(imageView2);

        resourceId = R.drawable.exterior;

        GlideApp.with(this)
                .load(resourceId)
                .centerCrop()
                .into(imageView1);
    }


}
package com.example.usb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

/**
 * @author  Lukas Sysojevas, Katie Patterson
 */
public class Contacts extends Fragment {

    private ImageView imageView1;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Contacts");

        imageView1 = getView().findViewById(R.id.imageView1);
        loadImageByResourceId();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.content_contacts, null);

    }

    public void loadImageByResourceId(){

        int resourceId = R.drawable.interior_2;

        GlideApp.with(this)
                .load(resourceId)
                .centerCrop()
                .into(imageView1);
    }

}

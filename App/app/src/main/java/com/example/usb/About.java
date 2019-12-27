package com.example.usb;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author  Lukas Sysojevas, Katie Patterson
 */
public class About extends Fragment {

    private ViewPager mViewPager;
    private ViewPager mViewPagerFacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        getActivity().setTitle("About");

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(new SamplePagerAdapter());

        mViewPagerFacts = (ViewPager) view.findViewById(R.id.pagerFacts);
        mViewPagerFacts.setAdapter(new SamplePagerAdapterFacts());

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager, true);

        TabLayout tabLayoutFacts = (TabLayout) view.findViewById(R.id.tab_layoutFacts);
        tabLayoutFacts.setupWithViewPager(mViewPagerFacts, true);

    }


    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 9;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item_about,
                    container, false);
            // Add the newly created View to the ViewPager
            container.addView(view);

            // Retrieve a TextView from the inflated View, and update it's text
            TextView title = (TextView) view.findViewById(R.id.item_title);
            Resources res = getResources();
            String[] aboutUsb = res.getStringArray(R.array.about_usb);
            title.setText(aboutUsb[position]);

            // Return the View
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

class SamplePagerAdapterFacts extends PagerAdapter {

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate a new layout from our resources
        View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item_facts,
                container, false);
        // Add the newly created View to the ViewPager
        container.addView(view);

        // Retrieve a TextView from the inflated View, and update it's text
        TextView title = (TextView) view.findViewById(R.id.item_titleFacts);
        Resources res = getResources();
        String[] aboutUsbFacts = res.getStringArray(R.array.about_facts);
        title.setText(aboutUsbFacts[position]);


        // Return the View
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}

}

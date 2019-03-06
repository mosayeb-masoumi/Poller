package com.rahbarbazaar.poller.android.Controllers.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rahbarbazaar.poller.android.Ui.fragments.IntroFragment;

import java.util.ArrayList;
import java.util.List;

public class IntroPagerAdapter extends FragmentPagerAdapter {

    private List<Integer> image_id;

    public IntroPagerAdapter(FragmentManager fm) {
        super(fm);
        image_id = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {

        return IntroFragment.getInstance(image_id.get(position));
    }

    @Override
    public int getCount() {
        return image_id.size();
    }

    public void addAllImages(List<Integer> images) {

        image_id.addAll(images);
    }
}

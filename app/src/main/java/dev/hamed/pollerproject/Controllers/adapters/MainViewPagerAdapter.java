package dev.hamed.pollerproject.Controllers.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
    }

    //this function will use for add fragment
    public void addFragment(Fragment fragment){

        fragments.add(fragment);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

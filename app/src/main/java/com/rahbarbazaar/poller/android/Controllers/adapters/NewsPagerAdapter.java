package com.rahbarbazaar.poller.android.Controllers.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import com.rahbarbazaar.poller.android.Models.GetBannersListResult;
import com.rahbarbazaar.poller.android.Ui.fragments.NewsPagerFragment;

public class NewsPagerAdapter extends FragmentStatePagerAdapter {

    private List<GetBannersListResult> items;

    public NewsPagerAdapter(FragmentManager fm) {
        super(fm);
        this.items = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i) {

        return NewsPagerFragment.getInstance(items.get(i).getImage());
    }

    @Override
    public int getCount() {
        return items.size();
    }

    //add list item dynamically
    public void addItems(List<GetBannersListResult> news) {

        items.addAll(news);
    }
}

package dev.hamed.pollerproject.Controllers.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import dev.hamed.pollerproject.Models.GetNewsListResult;
import dev.hamed.pollerproject.Ui.fragments.NewsPagerFragment;

public class NewsPagerAdapter extends FragmentStatePagerAdapter {

    private List<GetNewsListResult.DataBean> items;

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
    public void addItems(List<GetNewsListResult.DataBean> news) {

        items.addAll(news);
    }
}

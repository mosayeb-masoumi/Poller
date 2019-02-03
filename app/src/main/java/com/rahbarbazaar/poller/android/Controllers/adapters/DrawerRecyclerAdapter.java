package com.rahbarbazaar.poller.android.Controllers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import com.rahbarbazaar.poller.android.Controllers.viewHolders.DrawerHolder;
import com.rahbarbazaar.poller.android.Models.GetPagesResult;
import com.rahbarbazaar.poller.android.R;

public class DrawerRecyclerAdapter extends RecyclerView.Adapter<DrawerHolder> {

    private List<GetPagesResult> items;

    public DrawerRecyclerAdapter(List<GetPagesResult> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public DrawerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DrawerHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.drawer_rv_items, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerHolder drawerHolder, int i) {

        //bind listener and data for drawer recyclerview
        drawerHolder.bindDrawerData(items.get(i));
        drawerHolder.setDrawerHolderListener(listener,items.get(i).getContent());
    }

    private OnDrawerItemClickListener listener = null;

    public interface OnDrawerItemClickListener{

        void onDrawerItemClicked(String url);
    }

    public void setListener(OnDrawerItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

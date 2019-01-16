package com.rahbarbazaar.poller.Controllers.viewHolders;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.rahbarbazaar.poller.Controllers.adapters.DrawerRecyclerAdapter;
import com.rahbarbazaar.poller.Models.GetPagesResult;
import com.rahbarbazaar.poller.R;
import com.rahbarbazaar.poller.Utilities.AsyncImageLoader;

public class DrawerHolder extends RecyclerView.ViewHolder {

    private SimpleDraweeView image_drawer;
    private TextView text_drawer;

    public DrawerHolder(@NonNull View itemView) {
        super(itemView);

        image_drawer = itemView.findViewById(R.id.image_drawer);
        text_drawer = itemView.findViewById(R.id.text_drawer);
    }


    /**
     * @param data
     * in this function we will process drawer pages data:
     */
    public void bindDrawerData(GetPagesResult data) {

        new AsyncImageLoader(image_drawer,70,70).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,data.getTitle_icon());
        //if title_icon_url doesn't available so we will set failure image:
        image_drawer.getHierarchy().setFailureImage(R.drawable.error_image_loading);
        text_drawer.setText(data.getTitle());
    }

    public void setDrawerHolderListener(DrawerRecyclerAdapter.OnDrawerItemClickListener listener, String content) {

        itemView.setOnClickListener(view ->

            listener.onDrawerItemClicked(content)
        );
    }
}

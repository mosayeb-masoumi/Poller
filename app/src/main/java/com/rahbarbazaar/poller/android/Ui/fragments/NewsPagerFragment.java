package com.rahbarbazaar.poller.android.Ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Utilities.AsyncImageLoader;

public class NewsPagerFragment extends Fragment {

    //region of property
    String image;
    //end of region

    public NewsPagerFragment() {

        //require empty constructor
    }

    public static NewsPagerFragment getInstance(String name) {

        NewsPagerFragment fragment = new NewsPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("image", name);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getContext() != null;
        Fresco.initialize(getContext()); // initial fresco library for bind image data

        if (getArguments() != null) {

            image = getArguments().getString("image"); //get image url
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_pager_items, container, false);

        SimpleDraweeView image_news = view.findViewById(R.id.image_news);
        new AsyncImageLoader(image_news, 400, 800).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, image);
        //this is async class that used fresco image request for bind image data

        return view;
    }
}

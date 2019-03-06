package com.rahbarbazaar.poller.android.Ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Utilities.AsyncImageLoader;

public class IntroFragment extends Fragment {

    //region of property
    int image_id;

    public IntroFragment() {

        //require private constructor
    }

    public static IntroFragment getInstance(int image_id) {

        IntroFragment fragment = new IntroFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("image_id", image_id);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getContext());

        if (getArguments()!=null){

            image_id = getArguments().getInt("image_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_intro,container,false);

        SimpleDraweeView simpleDraweeView = view.findViewById(R.id.image_intro);
        simpleDraweeView.setActualImageResource(image_id);

        return view;
    }

}

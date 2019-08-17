package com.rahbarbazaar.poller.android.Ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;

import io.reactivex.disposables.CompositeDisposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment1 extends Fragment {

    GetCurrencyListResult parcelable;
    String lang;
    CompositeDisposable disposable;
    ServiceProvider provider = null;

    public HomeFragment1() {
        // Required empty public constructor
    }


    public static HomeFragment1 newInstance(GetCurrencyListResult parcelable,String lang) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("parcel_data",parcelable);
        bundle.putString("lang",lang);

        HomeFragment1 fragment =new HomeFragment1();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){

            parcelable = getArguments().getParcelable("parcel_data");
            lang = getArguments().getString("lang");
        }
        provider = new ServiceProvider(getContext());
        disposable = new CompositeDisposable();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home1, container, false);





        return view;
    }

}

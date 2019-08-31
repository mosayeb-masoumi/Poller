package com.rahbarbazaar.poller.android.Ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.Models.ModelTranferDataProfileToHome;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Ui.activities.HtmlLoaderActivity;
import com.rahbarbazaar.poller.android.Ui.activities.MainActivity;
import com.rahbarbazaar.poller.android.Utilities.App;
import com.rahbarbazaar.poller.android.Utilities.LocaleManager;
import com.rahbarbazaar.poller.android.Utilities.NotSwipeableViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment1 extends Fragment implements View.OnClickListener {

    GetCurrencyListResult parcelable;
    String lang;
    CompositeDisposable disposable;
    ServiceProvider provider = null;


    CardView cardview_home_video,cardview_home_image,cardview_home_polls;
    TextView text_newpoll_digit,text_activepoll_digit,text_balance_digit,text_yourpoint_digit;

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


        // register eventbus to get posted array or etc...
        EventBus.getDefault().register(this);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home1, container, false);



         init(view);
        cardview_home_video.setOnClickListener(this);
        cardview_home_image.setOnClickListener(this);
        cardview_home_polls.setOnClickListener(this);

        return view;
    }

    @Subscribe
    public void onEvent(List<ModelTranferDataProfileToHome> event){
        text_yourpoint_digit.setText(event.get(0).getScore());
        text_balance_digit.setText(event.get(0).getBalance());
    }


    private void init(View view) {
        cardview_home_video = view.findViewById(R.id.cardview_home_video);
        cardview_home_image=view.findViewById(R.id.cardview_home_image);
        cardview_home_polls=view.findViewById(R.id.cardview_home_polls);
        text_newpoll_digit=view.findViewById(R.id.text_newpoll_digit);
        text_activepoll_digit=view.findViewById(R.id.text_activepoll_digit);
        text_balance_digit=view.findViewById(R.id.text_balance_digit);
        text_yourpoint_digit=view.findViewById(R.id.text_yourpoint_digit);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cardview_home_video:
                //todo get url from server and replace
                goToHtmlActivity("http://pollerws.rahbarbazaar.com:2296/poller/v2/support/videos/"
                        + LocaleManager.getLocale(getResources()).getLanguage(), true);
                break;
            case R.id.cardview_home_image:
                //todo get url from server and replace
                goToHtmlActivity("http://pollerws.rahbarbazaar.com:2296/poller/v2/support/videos/"
                        + LocaleManager.getLocale(getResources()).getLanguage(), true);
                break;

            case R.id.cardview_home_polls:
                ((MainActivity)getActivity()).onTabSelected(2,false);
                break;
        }
    }


    private void goToHtmlActivity(String url, boolean shouldBeLoadUrl) {

        Intent intent = new Intent(getContext(), HtmlLoaderActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("surveyDetails", false);
        intent.putExtra("isShopping", shouldBeLoadUrl);
        startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }



}

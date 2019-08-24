package com.rahbarbazaar.poller.android.Ui.fragments;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rahbarbazaar.poller.android.Controllers.adapters.CartRecyclerAdapter;
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.Models.GetTransactionResult;
import com.rahbarbazaar.poller.android.Models.RefreshBalanceEvent;
import com.rahbarbazaar.poller.android.Models.UserDetailsPrefrence;
import com.rahbarbazaar.poller.android.Network.Service;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Utilities.ClientConfig;
import com.rahbarbazaar.poller.android.Utilities.GeneralTools;
import com.rahbarbazaar.poller.android.Utilities.ProfileTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment1 extends Fragment{


    //region of params and property
    RecyclerView cart_rv;
    TextView txt_balance, tv_price_view, tv_total_earned, tv_point_tab,
            tv_indicator, tv_transaction_tab, tv_point;
    List<GetTransactionResult> cartList;
    CartRecyclerAdapter adapter;
    CompositeDisposable disposable;
    SwipeRefreshLayout cart_refresh;
    GetCurrencyListResult parcelable;
    UserDetailsPrefrence user_details;
    int windowsWidth;
    String lang;
    //end pf region

    public CartFragment1() {
        // Required empty public constructor
    }


    public static CartFragment1 newInstance(GetCurrencyListResult parcelable, String lang) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("parcel_data", parcelable);
        bundle.putString("lang", lang);

        CartFragment1 fragment = new CartFragment1();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            parcelable = this.getArguments().getParcelable("parcel_data");
            lang = getArguments().getString("lang");
        }
        disposable = new CompositeDisposable();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cart1, container, false);






        return v;
    }


}

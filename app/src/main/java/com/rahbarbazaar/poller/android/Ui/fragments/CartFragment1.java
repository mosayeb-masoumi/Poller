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
public class CartFragment1 extends Fragment implements View.OnClickListener {


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

        defineViews(v);
        configRecyclerView();
        getCartList();


//        //change refresh color
//        cart_refresh.setColorSchemeResources(R.color.colorPrimary);
//
//        GeneralTools generalTools = GeneralTools.getInstance();
//        ViewGroup.LayoutParams params = tv_indicator.getLayoutParams();
//        windowsWidth = generalTools.getWindowsWidth(getActivity());
//        params.width = windowsWidth / 2;
//        tv_indicator.setLayoutParams(params);
//

        //get user info from preference and set to views
        initialInformation();

        return v;
    }

    private void initialInformation() {

        //if user details not empty
        user_details = ProfileTools.getInstance().retriveUserInformation(getContext());

        if (user_details != null) {

//            txt_balance.setText(new StringBuilder().append(getResources().getString(R.string.inventory)).append(" ").
//                    append(user_details.getBalance()).append(" ").
//                    append(lang.equals("fa") ? parcelable.getItems().get(0).getCurrency_name()
//                            : parcelable.getItems().get(0).getEn_name()));
            txt_balance.setText(new StringBuilder().append(" ").
                    append(user_details.getBalance()).append(" ").
                    append(lang.equals("fa") ? parcelable.getItems().get(0).getCurrency_name()
                            : parcelable.getItems().get(0).getEn_name()));

            tv_point.setText(String.valueOf(user_details.getSum_points()));
        }
    }

    //define view of fragment
    private void defineViews(View view) {

        tv_indicator = view.findViewById(R.id.tv_indicator);
        tv_transaction_tab = view.findViewById(R.id.tv_transaction_tab);
        tv_point_tab = view.findViewById(R.id.tv_point_tab);
        cart_rv = view.findViewById(R.id.cart_rv);
        txt_balance = view.findViewById(R.id.txt_balance);
        cart_refresh = view.findViewById(R.id.cart_refresh);
        tv_price_view = view.findViewById(R.id.tv_cart_price_view);
        tv_total_earned = view.findViewById(R.id.tv_total_earned);
        tv_point = view.findViewById(R.id.tv_point);

        cart_refresh.setOnRefreshListener(this::getCartList);
        tv_transaction_tab.setOnClickListener(this);
        tv_point_tab.setOnClickListener(this);
    }

    //config cart recycler view
    private void configRecyclerView() {

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        cart_rv.setLayoutManager(manager);
        cart_rv.setHasFixedSize(true);
        cart_rv.setItemViewCacheSize(5);
        cart_rv.setItemAnimator(null);
        cart_rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

                if (parent.getChildLayoutPosition(view) != 0) {

                    outRect.top = 10;
                }
            }
        });
    }

    //initial cart recycler view
    private void initializeCart() {

        cartList = new ArrayList<>();
        adapter = null;
        cart_rv.setAdapter(null);
        adapter = new CartRecyclerAdapter(cartList, parcelable, lang);
        cart_rv.setAdapter(adapter);
    }

    //get transaction data and check it
    private void getCartList() {

        ServiceProvider provider = new ServiceProvider(getContext());
        Service service = provider.getmService();
        cart_refresh.post(() -> cart_refresh.setRefreshing(true));
        Single<List<GetTransactionResult>> observable = tv_transaction_tab.getTag().equals("selected") ? service.getTransactionList(ClientConfig.API_V1)
                : service.getTransactionScoreList(ClientConfig.API_V1);

        disposable.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                        subscribeWith(new DisposableSingleObserver<List<GetTransactionResult>>() {
                            @Override
                            public void onSuccess(List<GetTransactionResult> result) {

                                initializeCart();

                                if (result != null && result.size() > 0) {

                                    cartList.addAll(result);
                                    adapter.notifyDataSetChanged();
                                }

                                cart_refresh.post(() -> cart_refresh.setRefreshing(false));
                            }

                            @Override
                            public void onError(Throwable e) {

                                cart_refresh.post(() -> cart_refresh.setRefreshing(false));
                            }
                        }));
    }

    //after user click on tabs this function will be called
    private void changeTab(TextView tv_apear, TextView tv_disapear, boolean appear) {

        /*appear false for transaction price
         *appear true for transaction point
         *
         */

        if (!appear) {

            tv_price_view.setText(R.string.text_price);
            tv_point.setText(String.valueOf(user_details.getSum_points()));
            tv_total_earned.setText(R.string.total_prices_earned);

        } else {

            tv_price_view.setText(R.string.text_point);
            tv_point.setText(String.valueOf(user_details.getSum_score()));
            tv_total_earned.setText(R.string.total_points_earned);
        }

        getCartList();
        tv_apear.setTextColor(getResources().getColor(R.color.colorPrimary));
        tv_disapear.setTextColor(getResources().getColor(R.color.default_text_color));
        int half_windowsWidth = windowsWidth / 2;
        ObjectAnimator animator = ObjectAnimator.ofFloat(tv_indicator, "translationX", appear ? half_windowsWidth : 0);
        animator.setDuration(400);
        animator.start();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefeshBalance(RefreshBalanceEvent v) {

        initialInformation();
        getCartList();
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.tv_transaction_tab:
                tv_transaction_tab.setTag("selected");
                changeTab(tv_transaction_tab, tv_point_tab, false);
                tv_transaction_tab.setBackground(getResources().getDrawable(R.drawable.bg_myaccount_tabpink));
                tv_transaction_tab.setTextColor(getResources().getColor(R.color.white_gray));
                tv_point_tab.setBackground(getResources().getDrawable(R.drawable.bg_myaccount_tab));
                tv_point_tab.setTextColor(getResources().getColor(R.color.gray));
                break;

            case R.id.tv_point_tab:
                tv_transaction_tab.setTag("deselected");
                changeTab(tv_point_tab, tv_transaction_tab, true);
                tv_point_tab.setBackground(getResources().getDrawable(R.drawable.bg_myaccount_tabpink));
                tv_point_tab.setTextColor(getResources().getColor(R.color.white_gray));
                tv_transaction_tab.setBackground(getResources().getDrawable(R.drawable.bg_myaccount_tab));
                tv_transaction_tab.setTextColor(getResources().getColor(R.color.gray));
                break;
        }

//        if (v.getId() == R.id.tv_transaction_tab) {
//            tv_transaction_tab.setTag("selected");
//            changeTab(tv_transaction_tab, tv_point_tab, false);
//
//        } else {
//
//            tv_transaction_tab.setTag("deselected");
//            changeTab(tv_point_tab, tv_transaction_tab, true);
//        }
    }

}

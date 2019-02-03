package com.rahbarbazaar.poller.android.Ui.fragments;

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

import com.google.gson.Gson;
import com.rahbarbazaar.poller.android.Controllers.adapters.CartRecyclerAdapter;
import com.rahbarbazaar.poller.android.Models.GetTransactionResult;
import com.rahbarbazaar.poller.android.Models.RefreshBalanceEvent;
import com.rahbarbazaar.poller.android.Models.UserDetailsPrefrence;
import com.rahbarbazaar.poller.android.Network.Service;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class CartFragment extends Fragment {


    //region of params and property
    RecyclerView cart_rv;
    TextView text_pint, txt_balance;
    List<GetTransactionResult> cartList;
    CartRecyclerAdapter adapter;
    CompositeDisposable disposable;
    SwipeRefreshLayout cart_refresh;
    //end pf region

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {

        return new CartFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposable = new CompositeDisposable();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cart, container, false);

        defineViews(v);
        configRecyclerView();
        getCartList();

        //change refresh color
        cart_refresh.setColorSchemeResources(R.color.colorPrimary);

        //get user info from preference and set to views
        initialUserInformation();

        return v;
    }

    private void initialUserInformation() {

        PreferenceStorage storage = PreferenceStorage.getInstance();
        String user_details = storage.retriveUserDetails(getContext());

        //if user details not empty
        if (user_details != null && !user_details.equals("")) {

            UserDetailsPrefrence prefrence = new Gson().fromJson(user_details, UserDetailsPrefrence.class);
            txt_balance.setText(new StringBuilder().append("موجودی :").append(" ").append(String.valueOf(prefrence.getBalance())).append(" ").append(storage.retriveCurrency(getContext())));
            text_pint.setText(String.valueOf(prefrence.getSum_points()));
        }
    }

    //define view of fragment
    private void defineViews(View view) {

        cart_rv = view.findViewById(R.id.cart_rv);
        text_pint = view.findViewById(R.id.txt_point);
        txt_balance = view.findViewById(R.id.txt_balance);
        cart_refresh = view.findViewById(R.id.cart_refresh);

        cart_refresh.setOnRefreshListener(this::getCartList);
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
        adapter = new CartRecyclerAdapter(cartList);
        cart_rv.setAdapter(adapter);
    }

    //get transaction data and check it
    private void getCartList() {

        ServiceProvider provider = new ServiceProvider(getContext());
        Service service = provider.getmService();

        disposable.add(service.getTransactionList().subscribeOn(Schedulers.io())
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

        initialUserInformation();
        getCartList();
    }
}

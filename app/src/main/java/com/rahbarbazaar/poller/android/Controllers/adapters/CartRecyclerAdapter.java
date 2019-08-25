package com.rahbarbazaar.poller.android.Controllers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import com.rahbarbazaar.poller.android.Controllers.viewHolders.CartHolder;
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.Models.GetTransactionResult;
import com.rahbarbazaar.poller.android.R;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartHolder> {

    private List<GetTransactionResult> items;
    private GetCurrencyListResult getCurrencyListResult;
    private String lang;

    public CartRecyclerAdapter(List<GetTransactionResult> items, GetCurrencyListResult getCurrencyListResult, String lang) {
        this.items = items;
        this.getCurrencyListResult = getCurrencyListResult;
        this.lang = lang;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        return new CartHolder(LayoutInflater.from(viewGroup.getContext()).
//        inflate(R.layout.cart_rv_items,viewGroup,false), getCurrencyListResult,lang);
        return new CartHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.cart_rv_items1,viewGroup,false), getCurrencyListResult,lang);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder cartHolder, int i) {

        //bind cart data
        cartHolder.bindCartData(items.get(i));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

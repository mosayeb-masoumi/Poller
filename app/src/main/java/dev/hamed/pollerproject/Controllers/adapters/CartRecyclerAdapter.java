package dev.hamed.pollerproject.Controllers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import dev.hamed.pollerproject.Controllers.viewHolders.CartHolder;
import dev.hamed.pollerproject.Models.GetTransactionResult;
import dev.hamed.pollerproject.R;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartHolder> {

    private List<GetTransactionResult> items;

    public CartRecyclerAdapter(List<GetTransactionResult> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CartHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_rv_items,viewGroup,false));
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

package com.rahbarbazaar.poller.android.Controllers.viewHolders;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rahbarbazaar.poller.android.Models.GetTransactionResult;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;

public class CartHolder extends RecyclerView.ViewHolder {

    private TextView text_time, text_price, text_title;

    public CartHolder(@NonNull View itemView) {
        super(itemView);

        text_title = itemView.findViewById(R.id.text_cart_title);
        text_price = itemView.findViewById(R.id.text_cart_price);
        text_time = itemView.findViewById(R.id.text_cart_time);
    }

    //this function will be process transaction data
    public void bindCartData(GetTransactionResult data) {

        switch (data.getTransactionable_type()) {

            case "survey":
                text_price.setTextColor(Color.parseColor("#00b100"));
                text_title.setText(data.getTransactionable().getSurvey_title());
                break;

            case "expense":
                text_price.setTextColor(Color.parseColor("#ff1a1a"));
                text_title.setText(data.getTransactionable().getTitle());
                break;
        }

        text_time.setText(data.getCreated_at());
        String currency = PreferenceStorage.getInstance().retriveCurrency(itemView.getContext());
        text_price.setText(new StringBuilder().append("مبلغ ").append(": ").append(data.getTransaction_amount()).append(" ").append(currency));

    }

}

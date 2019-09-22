package com.rahbarbazaar.poller.android.Controllers.viewHolders;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.Models.GetTransactionResult;
import com.rahbarbazaar.poller.android.R;

public class CartHolder extends RecyclerView.ViewHolder {

    private TextView text_time, text_price, text_title;
    ImageView img;
    private GetCurrencyListResult parcelable;
    private String lang;

    public CartHolder(@NonNull View itemView, GetCurrencyListResult parcelable, String lang) {
        super(itemView);

        text_title = itemView.findViewById(R.id.text_cart_title);
        text_price = itemView.findViewById(R.id.tv_cart_price);
        text_time = itemView.findViewById(R.id.text_cart_time);
        img = itemView.findViewById(R.id.row_cart_item_img);


        this.parcelable = parcelable;
        this.lang = lang;
    }

    //this function will be process transaction data
    public void bindCartData(GetTransactionResult data) {

        text_title.setText(data.getTitle());
        text_time.setText(data.getCreated_at());
        String currency ;

        /*currency_id 1 ->price
          currency_id 2->point
         */
        if (data.getCurrency_id() == 2) {
            currency = lang.equals("fa") ? parcelable.getItems().get(1).getCurrency_name() : parcelable.getItems().get(1).getEn_name();

        } else {
            currency = lang.equals("fa") ? parcelable.getItems().get(0).getCurrency_name() : parcelable.getItems().get(0).getEn_name();
        }
//        text_price.setText(itemView.getContext().getString(R.string.transaction_price, data.getTransaction_amount(), currency));
        text_price.setText(String.valueOf(data.getTransaction_amount()));


        String a = data.getTransaction_type();


        if (data.getTransaction_type() != null) {
            switch (data.getTransaction_type()) {

                case "add":
//                    text_price.setTextColor(Color.parseColor("#00b100"));
                    img.setImageResource(R.drawable.bg_myaccount_item_up_icon);

                    break;

                case "sub":
//                    text_price.setTextColor(Color.parseColor("#ff1a1a"));
                    img.setImageResource(R.drawable.bg_myaccount_item_down_icon);
                    break;
            }
        }
    }
}

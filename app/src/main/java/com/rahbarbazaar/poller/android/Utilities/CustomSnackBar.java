package com.rahbarbazaar.poller.android.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.rahbarbazaar.poller.android.R;

public class CustomSnackBar {

    public Snackbar showResultSnackbar(View view,Context context) {

        final Snackbar mySnackbar = Snackbar.make(view, "No internet connection!", Snackbar.LENGTH_INDEFINITE);

        View snack_view = mySnackbar.getView();
        //Drawable snack_drawable = context.getResources().getDrawable(R.drawable.warning);
        //snack_drawable.setBounds(0, 0, 80, 80);

        TextView txt_snack = snack_view.findViewById(android.support.design.R.id.snackbar_text);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/BYekan.ttf");
        txt_snack.setText("امتیاز شما بعد از محاسبه به حسابتان منظور میگردد");
        TextView txt_action = snack_view.findViewById(android.support.design.R.id.snackbar_action);
        txt_action.setTypeface(typeface);
        txt_snack.setTypeface(typeface);
        txt_action.setAllCaps(false);
        txt_snack.setGravity(Gravity.CENTER);
       // txt_snack.setCompoundDrawables(snack_drawable, null, null, null);
        //txt_snack.setCompoundDrawablePadding(20);

        mySnackbar.setAction("متوجه شدم", v -> {

            mySnackbar.dismiss();
        }).setActionTextColor(Color.parseColor("#D1D1D1"));

        return mySnackbar;
    }

    public void dismisSnackbar() {

       /* if (snackbar != null) {

            snackbar.dismiss();
        }*/
    }

}

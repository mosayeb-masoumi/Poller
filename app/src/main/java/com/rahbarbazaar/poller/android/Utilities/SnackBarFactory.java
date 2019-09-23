package com.rahbarbazaar.poller.android.Utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Ui.activities.SplashScreenActivity;

import org.jetbrains.annotations.NotNull;

public class SnackBarFactory {

    private static SnackBarFactory snackBar;

    private SnackBarFactory() {
    }

    public static SnackBarFactory getInstance() {

        if (snackBar == null)
            return snackBar = new SnackBarFactory();
        else return snackBar;
    }


    public Snackbar showResultSnackbar(View view, Context context) {

        final Snackbar mySnackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

        View snack_view = mySnackbar.getView();
        //Drawable snack_drawable = context.getResources().getDrawable(R.drawable.warning);
        //snack_drawable.setBounds(0, 0, 80, 80);

        TextView txt_snack = snack_view.findViewById(android.support.design.R.id.snackbar_text);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/BYekan.ttf");
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Vazir-Medium.ttf");

        txt_snack.setText(R.string.text_calculate_point);
        TextView txt_action = snack_view.findViewById(android.support.design.R.id.snackbar_action);

        txt_action.setTypeface(typeface);
        txt_snack.setTypeface(typeface);
        txt_action.setAllCaps(false);
        txt_snack.setGravity(Gravity.CENTER);
        // txt_snack.setCompoundDrawables(snack_drawable, null, null, null);
        //txt_snack.setCompoundDrawablePadding(20);

        mySnackbar.setAction(R.string.text_understand, v -> mySnackbar.dismiss()).setActionTextColor(Color.parseColor("#D1D1D1"));

        return mySnackbar;
    }

//    //mine
//    public Snackbar showWaitSnackbar(View view,Context context){
//        final Snackbar mySnackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
//
//        View snack_view = mySnackbar.getView();
//        TextView txt_snack = snack_view.findViewById(android.support.design.R.id.snackbar_text);
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/BYekan.ttf");
////        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Vazir-Medium.ttf");
//
//        txt_snack.setText(R.string.text_calculate_point);
//        txt_snack.setTypeface(typeface);
//        return mySnackbar;
//    }






    public void showLoginIssueSnackbar(@NotNull View view, @NotNull Context context, GetCurrencyListResult parcelable) {

        final Snackbar mySnackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

        View snack_view = mySnackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snack_view.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.topMargin = 56;
        snack_view.setLayoutParams(params);

        Drawable snack_drawable = context.getResources().getDrawable(R.drawable.warning);
        snack_drawable.setColorFilter(Color.parseColor("#ffff32"), PorterDuff.Mode.SRC_ATOP);
        snack_drawable.setBounds(0, 0, 80, 80);

        TextView txt_snack = snack_view.findViewById(android.support.design.R.id.snackbar_text);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/BYekan.ttf");
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Vazir-Medium.ttf");

        txt_snack.setText(R.string.text_error_on_login);
        TextView txt_action = snack_view.findViewById(android.support.design.R.id.snackbar_action);

        txt_action.setTypeface(typeface);
        txt_snack.setTypeface(typeface);
        txt_action.setAllCaps(false);
        txt_snack.setGravity(Gravity.CENTER);
        txt_snack.setCompoundDrawables(snack_drawable, null, null, null);
        txt_snack.setCompoundDrawablePadding(20);

        if (parcelable != null && parcelable.getStatus().equalsIgnoreCase("Unauthorized") || parcelable != null && parcelable.getStatus().equalsIgnoreCase("Invalid token"))
            PreferenceStorage.getInstance(context).saveToken("");

        mySnackbar.setAction(R.string.text_try_again, v -> {
            mySnackbar.dismiss();
            context.startActivity(new Intent(context, SplashScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            if (context instanceof AppCompatActivity)
                ((AppCompatActivity) context).finish();

        }).setActionTextColor(Color.parseColor("#D1D1D1"));

        mySnackbar.show();
    }

    protected void dismisSnackbar() {

       /* if (snackbar != null) {

            snackbar.dismiss();
        }*/
    }

}

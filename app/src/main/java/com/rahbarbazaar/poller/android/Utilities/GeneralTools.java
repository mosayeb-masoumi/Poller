package com.rahbarbazaar.poller.android.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class GeneralTools {

    private static GeneralTools instance;

    private GeneralTools() {
    }

    public static GeneralTools getInstance() {

        if (instance == null) {

            return instance = new GeneralTools();
        } else {

            return instance;
        }
    }

    public boolean checkPackageInstalled(String packageName, Context context) {

        PackageManager manager = context.getPackageManager();
        Intent intent = manager.getLaunchIntentForPackage(packageName);
        return intent == null;
    }

    //check network connectivity
    public boolean checkInternetConnection(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();

            if (info != null) {

                return info.isConnected();

            } else {

                return false;
            }

        } else {

            return false;
        }
    }

    //show alert dialog after check
    public void doCheckNetwork(Context context, View view) {

        if (!checkInternetConnection(context)) {

            if (context instanceof AppCompatActivity)
                if (((AppCompatActivity) context).isFinishing())
                    return;

            new DialogFactory(context).createNoInternetDialog(new DialogFactory.DialogFactoryInteraction() {
                @Override
                public void onAcceptButtonClicked(String... params) {

                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }

                @Override
                public void onDeniedButtonClicked(boolean cancel_dialog) {

                    if (cancel_dialog) {

                        doCheckNetwork(context, view);
                    } else {

                        context.startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }
            }, view);
        }
    }

    public void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(300);
        v.startAnimation(a);
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(300);
        v.startAnimation(a);
    }

    //calculate height of screen for change dynamically height of layout
    public int getWindowsHeight(Activity activity) {

        if (activity != null) {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;

        } else return 0;
    }

    //calculate width of screen for change dynamically width of layout
    public int getWindowsWidth(Activity activity) {

        if (activity != null) {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;

        } else return 0;
    }

}

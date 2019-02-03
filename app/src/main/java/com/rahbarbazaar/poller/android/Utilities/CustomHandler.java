package com.rahbarbazaar.poller.android.Utilities;

import android.app.Activity;
import android.os.Handler;

import java.lang.ref.WeakReference;

public class CustomHandler extends Handler {

    private WeakReference<Activity> activity;

    public CustomHandler(Activity act) {

        activity = new WeakReference<>(act);
    }

}

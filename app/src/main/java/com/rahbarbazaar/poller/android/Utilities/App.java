package com.rahbarbazaar.poller.android.Utilities;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;

import io.fabric.sdk.android.Fabric;
import ir.metrix.sdk.Metrix;
import ir.metrix.sdk.MetrixConfig;
import ir.metrix.sdk.OnAttributionChangedListener;
import ir.metrix.sdk.OnReceiveUserIdListener;
import ir.metrix.sdk.network.model.AttributionModel;

import org.jetbrains.annotations.NotNull;


public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Fresco.initialize(this);// initial fresco library for bind image data

        MetrixConfig metrixConfig = new  MetrixConfig(this, "wmnqenalyyvnngb"); // ساخت نمونه‌ای از کلاس `MetrixConfig`
        // تغییر پیکربندی (دلخواه)
        metrixConfig.setFirebaseAppId("1:196753164439:android:6f64870ad121e57c"); // to count the number of unistallation
        //مشخص کردن Pre-installed Tracker
        metrixConfig.setDefaultTrackerToken("fdzlhy");  // get tracker token from panel
//        metrixConfig.setStore("bazaar"); // cafe bazar googleplay
        metrixConfig.setStore("google play"); // cafe bazar googleplay
//        metrixConfig.setStore("myket"); // cafe bazar googleplay

        Metrix.onCreate(metrixConfig); // راه‌اندازی کردن کتابخانه
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }

    public static String language = "";
    public static String videoWebUrl = "";
    @NotNull
    public static int balance;
    public static String leftDay ="";


}


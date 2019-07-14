package com.rahbarbazaar.poller.android.Ui.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.rahbarbazaar.poller.android.Utilities.LocaleManager;

public class CustomBaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
}

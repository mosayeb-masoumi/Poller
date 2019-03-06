package com.rahbarbazaar.poller.android.Ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.rahbarbazaar.poller.android.Models.GetCurrencyResult;
import com.rahbarbazaar.poller.android.Network.Service;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Utilities.CustomHandler;
import com.rahbarbazaar.poller.android.Utilities.DialogFactory;
import com.rahbarbazaar.poller.android.Utilities.GeneralTools;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;
import com.rahbarbazaar.poller.android.Utilities.ProfileTools;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SplashScreenActivity extends AppCompatActivity {

    //property region
    CompositeDisposable disposable;
    ServiceProvider provider;
    boolean isValidToken;
    GeneralTools tools;
    //end of region

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        provider = new ServiceProvider(this);
        disposable = new CompositeDisposable();
        tools = GeneralTools.getInstance();

        //check token validation
        isValidToken = !PreferenceStorage.getInstance().retriveToken(this).equals("0");
        saveCurrency();

        //get user profile information and save it in preference for access in other segment of app
        if (isValidToken)
            ProfileTools.getInstance().saveProfileInformation(this);

        //lambda expression :: you have to enable lambda expression
        new CustomHandler(this).postDelayed(this::checkAccessibility, 3500);
    }


    /**
     * check network connection state and if user token was valid he/she will go to main
     * activity in otherwise he/she go to register activity for registration
     */

    private void checkAccessibility() {

        if (tools.checkInternetConnection(this)) {

            if (!isValidToken) {

                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                SplashScreenActivity.this.finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            } else {

                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                SplashScreenActivity.this.finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        } else {

            new DialogFactory(this).createNoInternetDialog(new DialogFactory.DialogFactoryInteraction() {
                @Override
                public void onAcceptButtonClicked(String param) {

                    //go to wifi setting
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }

                @Override
                public void onDeniedButtonClicked(boolean dialog_cancel) {

                    if (dialog_cancel) {

                        checkAccessibility();

                    } else {
                        // get call back from grey button and go to mobile data page
                        startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                    }
                }
            }, findViewById(R.id.rl_root));
        }
    }

    //save dynamic currency
    private void saveCurrency() {

        Service service = provider.getmService();
        disposable.add(service.getCurrency().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<GetCurrencyResult>() {
            @Override
            public void onSuccess(GetCurrencyResult result) {

                if (result != null) {

                    PreferenceStorage.getInstance().saveCurrency(result.getCurrency_name(), SplashScreenActivity.this);
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        }));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}

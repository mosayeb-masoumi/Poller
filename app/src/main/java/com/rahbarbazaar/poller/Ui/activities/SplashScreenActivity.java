package com.rahbarbazaar.poller.Ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import com.rahbarbazaar.poller.Models.GetCurrencyResult;
import com.rahbarbazaar.poller.Models.UserConfirmAuthResult;
import com.rahbarbazaar.poller.Models.UserDetailsPrefrence;
import com.rahbarbazaar.poller.Network.Service;
import com.rahbarbazaar.poller.Network.ServiceProvider;
import com.rahbarbazaar.poller.R;
import com.rahbarbazaar.poller.Utilities.CustomHandler;
import com.rahbarbazaar.poller.Utilities.DialogFactory;
import com.rahbarbazaar.poller.Utilities.PreferenceStorage;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SplashScreenActivity extends AppCompatActivity {

    //property region
    CompositeDisposable disposable;
    ServiceProvider provider;
    boolean isValidToken;
    //end of region

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        provider = new ServiceProvider(this);
        disposable = new CompositeDisposable();

        //check token validation
        isValidToken = !PreferenceStorage.getInstance().retriveToken(this).equals("0");
        saveCurrency();

        if (isValidToken)
            saveProfileInformation();

        //lambda expression :: you have to enable lambda expression
        new CustomHandler(this).postDelayed(this::checkAccessibility, 3500);
    }


    /**
     *check network connection state and if user token was valid he/she will go to main
     * activity in otherwise he/she go to register activity for registration
     */

    private void checkAccessibility() {

        if (checkInternetConnection()) {

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

    //check network connectivity
    private boolean checkInternetConnection() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    //get user profile information and save it in preference for access in other segment of app

    private void saveProfileInformation() {

        disposable.add(new ServiceProvider(this).getmService().getUserProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UserConfirmAuthResult>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(UserConfirmAuthResult result) {
                        // Received profile info
                        if (result != null) {

                            UserDetailsPrefrence prefrence = new UserDetailsPrefrence();
                            prefrence.setBalance(result.getBalance());
                            prefrence.setName(result.getName());
                            prefrence.setSum_points(result.getSum_points());
                            prefrence.setUser_id(String.valueOf(result.getId()));
                            prefrence.setIdentity(result.getIdentity());
                            PreferenceStorage storage = PreferenceStorage.getInstance();
                            storage.saveUserDetails(new Gson().toJson(prefrence), SplashScreenActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Network error
                    }
                }));
    }
}

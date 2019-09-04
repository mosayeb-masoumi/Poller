package com.rahbarbazaar.poller.android.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.rahbarbazaar.poller.android.Models.UserConfirmAuthResult;
import com.rahbarbazaar.poller.android.Models.UserDetailsPrefrence;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.Ui.activities.SplashScreenActivity1;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ProfileTools {

    private static ProfileTools profileTools;

    private ProfileTools() {
    }

    public static ProfileTools getInstance() {

        if (profileTools == null) {

            return new ProfileTools();
        } else
            return profileTools;
    }

    private UserProfileInteraction listener = null;

    public interface UserProfileInteraction {

        void onUserDataReceived();
    }

    //get user profile information and save it in preference for access in other segment of app

    public ProfileTools saveProfileInformation(Context context) {

        CompositeDisposable disposable = new CompositeDisposable();

        disposable.add(new ServiceProvider(context).getmService().getUserProfile(ClientConfig.API_V1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UserConfirmAuthResult>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(UserConfirmAuthResult result) {
                        // Received profile info
                        if (result != null) {

                            UserDetailsPrefrence userDetailsPrefrence = new UserDetailsPrefrence();
                            userDetailsPrefrence.setBalance(result.getBalance());
                            userDetailsPrefrence.setName(result.getName());
                            userDetailsPrefrence.setSum_points(result.getSum_points());
                            userDetailsPrefrence.setType(result.getType());
                            userDetailsPrefrence.setUser_id(result.getId());
                            userDetailsPrefrence.setIdentity(result.getIdentity());
                            userDetailsPrefrence.setPhone_number(result.getMobile());
                            userDetailsPrefrence.setSum_score(result.getSum_score());
                            userDetailsPrefrence.setScore(result.getScore());
                            PreferenceStorage storage = PreferenceStorage.getInstance(context);
                            storage.saveUserDetails(new Gson().toJson(userDetailsPrefrence));
                            //after receive data
                            if (listener != null)
                                listener.onUserDataReceived();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        context.startActivity(new Intent(context, SplashScreenActivity1.class));
                    }
                }));
        return this;
    }

    public UserDetailsPrefrence retriveUserInformation(Context context) {

        PreferenceStorage storage = PreferenceStorage.getInstance(context);
        String user_details = storage.retriveUserDetails();

        //if user details not empty
        if (user_details != null && !user_details.equals(""))
            return new Gson().fromJson(user_details, UserDetailsPrefrence.class);
        else
            return null;
    }

    public void setListener(UserProfileInteraction listener) {
        this.listener = listener;
    }
}

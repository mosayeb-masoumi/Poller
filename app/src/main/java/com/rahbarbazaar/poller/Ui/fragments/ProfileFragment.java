package com.rahbarbazaar.poller.Ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rahbarbazaar.poller.Models.GeneralStatusResult;
import com.rahbarbazaar.poller.Models.UserConfirmAuthResult;
import com.rahbarbazaar.poller.Network.Service;
import com.rahbarbazaar.poller.Network.ServiceProvider;
import com.rahbarbazaar.poller.R;
import com.rahbarbazaar.poller.Ui.activities.SplashScreenActivity;
import com.rahbarbazaar.poller.Utilities.CustomToast;
import com.rahbarbazaar.poller.Utilities.DialogFactory;
import com.rahbarbazaar.poller.Utilities.PreferenceStorage;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    //region of views
    RelativeLayout rl_logout, rl_edit_profile;
    TextView text_mobile, text_gender, text_point,
            text_age, text_username, text_project_count;
    //end of region

    //region of property
    ServiceProvider provider;
    CompositeDisposable disposable;
    //end of region

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        provider = new ServiceProvider(getContext());
        disposable = new CompositeDisposable();

        //initial ui
        defineViews(view);
        defineViewClickListener();
        getUserProfile();

        return view;
    }

    //define profile fragment views will be appear here
    private void defineViews(View view) {

        rl_logout = view.findViewById(R.id.rl_logout);
        rl_edit_profile = view.findViewById(R.id.rl_edit_profile);
        text_mobile = view.findViewById(R.id.text_mobile);
        text_age = view.findViewById(R.id.text_age);
        text_gender = view.findViewById(R.id.text_gender);
        text_username = view.findViewById(R.id.text_username);
        text_point = view.findViewById(R.id.text_point);
        text_project_count = view.findViewById(R.id.text_project_count);
    }

    //define views click listener will be appear here
    private void defineViewClickListener() {

        rl_logout.setOnClickListener(this);
        rl_edit_profile.setOnClickListener(this);
    }

    //create confirm exit dialog
    private void createConfirmExitDialog() {

        DialogFactory dialogFactory = new DialogFactory(getContext());
        dialogFactory.createConfirmExitDialog(new DialogFactory.DialogFactoryInteraction() {
            @Override
            public void onAcceptButtonClicked(String param) {

                PreferenceStorage.getInstance().saveToken("0", getContext());
                assert getActivity() != null;
                startActivity(new Intent(getContext(), SplashScreenActivity.class));
            }

            @Override
            public void onDeniedButtonClicked(boolean bool) {

                //did on dialog factory
            }
        }, getView(),false);
    }

    //get user profile data and initialize textView will be implement here
    private void getUserProfile() {

        Service service = provider.getmService();
        disposable.add(service.getUserProfile().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<UserConfirmAuthResult>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(UserConfirmAuthResult result) {

                if (result != null) {

                    text_age.setText(String.valueOf(result.getBirthday()));
                    text_gender.setText(result.getGender().equals("male") ? "آقا" : "خانم");
                    text_mobile.setText(result.getMobile());
                    text_username.setText(result.getName());
                    String currency = PreferenceStorage.getInstance().retriveCurrency(getContext());
                    text_point.setText(result.getBalance() +" "+ currency);
                    text_project_count.setText("" + result.getParticipated_project_count());
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        }));
    }


    //send edit profile request for change user data:
    private void sendEditProfileRequest(String comment) {

        Service service = provider.getmService();

        disposable.add(service.editUserProfile(comment).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<GeneralStatusResult>() {
            @Override
            public void onSuccess(GeneralStatusResult result) {

                if (result != null) {

                    if (result.getStatus().equals("request sent")) {

                        new CustomToast().createToast("درخواست شما ثبت شد.", getContext());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        }));

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.rl_logout) {

            createConfirmExitDialog();
        } else {

            new DialogFactory(getContext()).createCommentDialog(new DialogFactory.DialogFactoryInteraction() {
                @Override
                public void onAcceptButtonClicked(String comment) {

                    sendEditProfileRequest(comment);
                }

                @Override
                public void onDeniedButtonClicked(boolean bool) {
                    //grey interaction does'nt used
                }
            }, getView());
        }
    }
}

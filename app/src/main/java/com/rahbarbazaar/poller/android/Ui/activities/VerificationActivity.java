package com.rahbarbazaar.poller.android.Ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rahbarbazaar.poller.android.Models.GeneralStatusResult;
import com.rahbarbazaar.poller.android.Models.UserConfirmAuthResult;
import com.rahbarbazaar.poller.android.Network.Service;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Utilities.CustomToast;
import com.rahbarbazaar.poller.android.Utilities.DialogFactory;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;
import com.rahbarbazaar.poller.android.Utilities.ProfileTools;
import com.rahbarbazaar.poller.android.Utilities.TypeFaceGenerator;
import com.wang.avi.AVLoadingIndicatorView;

import java.math.BigDecimal;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class VerificationActivity extends AppCompatActivity
        implements View.OnClickListener {

    //region of views
    TextView text_min, text_sec, text_edit_mobile,
            button_verify, text_user_mobile;
    EditText et_user_verify;
    AVLoadingIndicatorView av_verify;
    LinearLayout linear_recode;
    //end of region

    //region of property
    int min = 0;
    int sec = 0;
    String user_mobile = null;
    CustomToast customToast;
    ServiceProvider provider;
    CountDownTimer countDownTimer;
    CompositeDisposable disposable;
    //SmsObserver smsObserver;

    //end of region

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        defineViews();
        defineViewsListener();
        et_user_verify.setTypeface(TypeFaceGenerator.getInstance().getByekanFont(this));
        disposable = new CompositeDisposable();

        //get mobile number from intent
        if (getIntent() != null) {

            user_mobile = getIntent().getStringExtra("user_mobile");
            text_user_mobile.setText("+98" + user_mobile.substring(1, user_mobile.length()));
        }

        //start count down timer and disable resend code linear
        linear_recode.setEnabled(false);
        startCountDownTimer();

        //create instance from service provider for provide request
        provider = new ServiceProvider(this);

        //create instance from custom toast for show multiple message
        customToast = new CustomToast();

        //create instance of smsObserver and set callback listener and check recive sms permission
        checkReadSmsPremission();
        // smsObserver = new SmsObserver();
        //smsObserver.setOnSmsObserverListener(this);
    }

    //define views of activity
    private void defineViews() {

        text_min = findViewById(R.id.text_min);
        text_sec = findViewById(R.id.text_sec);
        text_user_mobile = findViewById(R.id.text_user_mobile);
        text_edit_mobile = findViewById(R.id.text_edit_mobile);
        button_verify = findViewById(R.id.button_verify);
        av_verify = findViewById(R.id.av_verify);
        et_user_verify = findViewById(R.id.et_user_verify);
        linear_recode = findViewById(R.id.linear_recode);
    }

    //define views of activity click listener
    private void defineViewsListener() {

        linear_recode.setOnClickListener(this);
        button_verify.setOnClickListener(this);
        text_edit_mobile.setOnClickListener(this);
    }

    //if code doesn't receive we have to call login request again
    private void sendLoginRequest() {

        Service service = provider.getmService();

        //call service with user_mobile
        if (!user_mobile.equals("")) {

            disposable.add(service.userAuthentication(user_mobile)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<GeneralStatusResult>() {
                        @Override
                        public void onSuccess(GeneralStatusResult result) {

                            if (result != null) {

                                //call service if response ok:
                                if (result.getStatus().equals("otp sent")) {

                                    new CustomToast().createToast("کد ارسال شد", VerificationActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    }));
        }
    }

    //send verify user request and check accessibility
    private void sendVerifyRequest() {

        Service service = provider.getmService();
        String verify_code = et_user_verify.getText().toString();


        if (!verify_code.equals("")) {

            verify_code = new BigDecimal(verify_code).toString();

            av_verify.smoothToShow();
            button_verify.setText("");
            button_verify.setEnabled(false);

            disposable.add(service.userConfirmAuthentication(user_mobile, verify_code).
                    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                    subscribeWith(new DisposableSingleObserver<UserConfirmAuthResult>() {
                        @Override
                        public void onSuccess(UserConfirmAuthResult result) {

                            if (result != null) {

                                if (result.getStatus() != null) {


                                    switch (result.getStatus()) {
                                        case "otp expired":

                                            customToast.createToast("کد تایید منقضی شده لطفا بر روی ارسال مجدد کلیک کنید", VerificationActivity.this);

                                            break;
                                        case "otp wrong":

                                            customToast.createToast("کد تایید اشتباه است", VerificationActivity.this);
                                            break;

                                        case "already used":

                                            customToast.createToast("کد تایید تکراری است", VerificationActivity.this);
                                            break;

                                        case "user confirmed":

                                            if (result.getToken() != null && !result.getToken().equals("")) {

                                                //call verification code and mobile if response ok:
                                                PreferenceStorage.getInstance().saveToken(result.getToken(), VerificationActivity.this);

                                                //check user agreement:
                                                checkUserAgreement();
                                            }

                                            break;
                                    }
                                }
                            }
                            av_verify.smoothToHide();
                            button_verify.setText("ورود");
                            button_verify.setEnabled(true);
                        }

                        @Override
                        public void onError(Throwable e) {

                            av_verify.smoothToHide();
                            button_verify.setText("ورود");
                            button_verify.setEnabled(true);
                            new CustomToast().createToast("مشکل در ارتباط", VerificationActivity.this);
                        }
                    }));

        }

    }

    //agreement dialog will bee appear by this function
    private void checkUserAgreement() {

        //get user profile information and save it in preference for access in other segment of app
        ProfileTools.getInstance().saveProfileInformation(this);

        new DialogFactory(this).createAgreementDialog(new DialogFactory.DialogFactoryInteraction() {
            @Override
            public void onAcceptButtonClicked(String param) {

                acceptUserAgreement();
            }

            @Override
            public void onDeniedButtonClicked(boolean bool) {

                //this callback doesn't use
            }
        }, findViewById(R.id.rl_root));
    }

    //accept user agreement
    private void acceptUserAgreement() {

        disposable.add(new ServiceProvider(this).getmService().acceptAgreement().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableSingleObserver<GeneralStatusResult>() {
                    @Override
                    public void onSuccess(GeneralStatusResult generalStatusResult) {

                        //go to main activity
                        startActivity(new Intent(VerificationActivity.this, MainActivity.class));
                        VerificationActivity.this.finish();
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }

                    @Override
                    public void onError(Throwable e) {

                        new CustomToast().createToast("عدم ارتباط با سرور", VerificationActivity.this);
                    }
                }));
    }

    //start count down timer for resend code
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {

                //initialize sec
                sec = (int) millisUntilFinished / 1000;

                //modify minute
                min = sec / 60;
                text_min.setText(String.valueOf("0" + min));

                //check if sec bigger than 60 so...
                if (sec >= 60)
                    sec = sec - 60;

                //if sec > 120 sec = sec % 60;

                //modify sec for value lower than 10
                String modifySecond = sec < 10 ? "0" + sec : String.valueOf(sec);
                text_sec.setText(modifySecond);
            }

            public void onFinish() {

                linear_recode.setEnabled(true);
            }

        };
        countDownTimer.start();
    }

    //in android 6 or higher we have to get runtime permission from user
    private void checkReadSmsPremission() {

        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 12);
        }
    }

    @Override
    protected void onDestroy() {

        //unregisterReceiver(smsObserver);
        if (countDownTimer != null) {

            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // registerReceiver(smsObserver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.linear_recode:

                linear_recode.setEnabled(false);
                sendLoginRequest();
                startCountDownTimer();
                break;

            case R.id.button_verify:

                sendVerifyRequest();
                break;

            case R.id.text_edit_mobile:

                startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
                VerificationActivity.this.finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
        }
    }

   /* @Override
    public void onSmsReceived(String msg) {

        if (msg != null && !msg.equals("")) {

            //keep only number in string
            msg = msg.replaceAll("[^\\d.]", "");
            if (msg.contains("."))
                msg = msg.replace(".", ""); // if result contain '.' it will be remove it
        }
        String finalMsg = msg;
        runOnUiThread(() -> et_user_verify.setText(finalMsg));
    }*/
}

package com.rahbarbazaar.poller.android.Ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rahbarbazaar.poller.android.BuildConfig;
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.Models.GeneralStatusResult;
import com.rahbarbazaar.poller.android.Models.UserConfirmAuthResult;
import com.rahbarbazaar.poller.android.Models.user_phonedata.UserPhoneInfo;
import com.rahbarbazaar.poller.android.Network.Service;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Utilities.App;
import com.rahbarbazaar.poller.android.Utilities.ClientConfig;
import com.rahbarbazaar.poller.android.Utilities.ToastFactory;
import com.rahbarbazaar.poller.android.Utilities.GeneralTools;
import com.rahbarbazaar.poller.android.Utilities.LocaleManager;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;
import com.rahbarbazaar.poller.android.Utilities.ProfileTools;
import com.rahbarbazaar.poller.android.Utilities.TypeFaceGenerator;
import com.wang.avi.AVLoadingIndicatorView;

import java.math.BigDecimal;
import java.util.Objects;

import co.ronash.pushe.Pushe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class VerificationActivity extends CustomBaseActivity
        implements View.OnClickListener {

    //region of views
    TextView text_min, text_sec, text_edit_mobile,
            button_verify, text_user_mobile;

    EditText et_user_verify;
    AVLoadingIndicatorView av_verify;
    LinearLayout linear_recode;
    RelativeLayout rl_recode_number;
    //end of region

    //region of property
    int min = 0;
    int sec = 0;
    String user_mobile = null;
    ToastFactory toastFactory;
    ServiceProvider provider;
    CountDownTimer countDownTimer;
    CompositeDisposable disposable;
    BroadcastReceiver connectivityReceiver;
    GetCurrencyListResult getCurrencyListResult;
    //SmsObserver smsObserver;

    //end of region

    String pushe_id = "";
    String os_version = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_1);


        Pushe.initialize(this, true);
        pushe_id = Pushe.getPusheId(VerificationActivity.this);

        defineViews();
        defineViewsListener();
        av_verify.setVisibility(View.GONE);

        if (LocaleManager.getLocale(getResources()).getLanguage().equals("fa")) {

            Typeface font = TypeFaceGenerator.getInstance().getByekanFont(this);
            et_user_verify.setTypeface(font);
            text_user_mobile.setTypeface(font);
        }

        disposable = new CompositeDisposable();

        //get mobile number from intent
        if (getIntent() != null) {

            user_mobile = getIntent().getStringExtra("user_mobile");
            text_user_mobile.setText("98" + user_mobile.substring(1));
        }

        //start count down timer and disable resend code linear
        linear_recode.setEnabled(false);
//        startCountDownTimer();
        reverseTimer(90,text_min);

        //create instance from service provider for provide request
        provider = new ServiceProvider(this);

        //create instance from custom toast for show multiple message
        toastFactory = new ToastFactory();

        //create instance of smsObserver and set callback listener and check recive sms permission
        //checkReadSmsPremission();
        // smsObserver = new SmsObserver();
        //smsObserver.setOnSmsObserverListener(this);

        //check network broadcast reciever
        GeneralTools tools = GeneralTools.getInstance();
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                tools.doCheckNetwork(VerificationActivity.this, findViewById(R.id.rl_root));
            }
        };


// event on done keyboard
        et_user_verify.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //Call your method here
                sendVerifyRequest();
                closeKeyboard();
                return true;
            }
            return false;
        });
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
    }

    //define views of activity
    private void defineViews() {

        text_min = findViewById(R.id.text_min);
        text_sec = findViewById(R.id.text_sec);
        text_user_mobile = findViewById(R.id.text_user_mobile);
//        text_edit_mobile = findViewById(R.id.text_edit_mobile);
        button_verify = findViewById(R.id.button_verify);
        av_verify = findViewById(R.id.av_verify);
        et_user_verify = findViewById(R.id.et_user_verify);
        linear_recode = findViewById(R.id.linear_recode);
        rl_recode_number = findViewById(R.id.rl_recode_number);
    }

    //define views of activity click listener
    private void defineViewsListener() {

        linear_recode.setOnClickListener(this);
        button_verify.setOnClickListener(this);
//        text_edit_mobile.setOnClickListener(this);
        text_user_mobile.setOnClickListener(this);
    }

    //if code doesn't receive we have to call login request again
    private void sendLoginRequest() {

        Service service = provider.getmService();

        //call service with user_mobile
        if (!user_mobile.equals("")) {

            disposable.add(service.userAuthentication(ClientConfig.API_V1, user_mobile)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<GeneralStatusResult>() {
                        @Override
                        public void onSuccess(GeneralStatusResult result) {

                            if (result != null) {

                                //call service if response ok:
                                if (result.getStatus().equals("otp sent")) {

                                    new ToastFactory().createToast(R.string.text_otp_send, VerificationActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    }));
        }
    }

    //send verify user request and check accessibility
    private void sendVerifyRequest() {

        button_verify.setVisibility(View.GONE);
        av_verify.setVisibility(View.VISIBLE);

        Service service = provider.getmService();
        String verify_code = et_user_verify.getText().toString().trim();

        if (!verify_code.isEmpty() && verify_code.length()==5) {

            verify_code = new BigDecimal(Long.parseLong(verify_code)).toString();

            av_verify.smoothToShow();
            button_verify.setText("");
            button_verify.setEnabled(false);

            disposable.add(service.userConfirmAuthentication(ClientConfig.API_V1, user_mobile, verify_code).
                    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                    subscribeWith(new DisposableSingleObserver<UserConfirmAuthResult>() {
                        @Override
                        public void onSuccess(UserConfirmAuthResult result) {

                            if (result != null) {

                                // save to use for upgrade userAccess
                                PreferenceStorage.getInstance(VerificationActivity.this).saveUserAccessType(result.getType());

                                if (result.getStatus() != null) {

                                    switch (result.getStatus()) {
                                        case "otp expired":

                                            toastFactory.createToast(R.string.text_otp_expired, VerificationActivity.this);
                                            button_verify.setVisibility(View.VISIBLE);
                                            av_verify.setVisibility(View.GONE);

                                            break;
                                        case "otp wrong":

                                            toastFactory.createToast(R.string.text_otp_wrong, VerificationActivity.this);
                                            button_verify.setVisibility(View.VISIBLE);
                                            av_verify.setVisibility(View.GONE);
                                            break;

                                        case "already used":

                                            toastFactory.createToast(R.string.text_otp_used, VerificationActivity.this);
                                            button_verify.setVisibility(View.VISIBLE);
                                            av_verify.setVisibility(View.GONE);
                                            break;

                                        case "user confirmed":

                                            if (result.getToken() != null && !result.getToken().equals("")) {

                                                //call verification code and mobile if response ok:
                                                PreferenceStorage.getInstance(VerificationActivity.this).saveToken(result.getToken());

                                                //check user agreement:
                                                checkUserAgreement();

                                                sendUserInfo();

                                                button_verify.setVisibility(View.VISIBLE);
                                                av_verify.setVisibility(View.GONE);
                                            }

                                            break;
                                    }
                                }


                            }
                            av_verify.smoothToHide();
                            button_verify.setText(R.string.verification_button_text);
                            button_verify.setEnabled(true);
                            button_verify.setVisibility(View.VISIBLE);
//                            linear_recode.setVisibility(View.VISIBLE);
//                            linear_recode.setEnabled(true);
//                            rl_recode_number.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {

                            av_verify.smoothToHide();
                            button_verify.setText(R.string.verification_button_text);
                            button_verify.setEnabled(true);
                            button_verify.setVisibility(View.VISIBLE);
//                            linear_recode.setVisibility(View.VISIBLE);
//                            linear_recode.setEnabled(true);
//                            rl_recode_number.setVisibility(View.GONE);
//                            new ToastFactory().createToast(R.string.text_no_service, VerificationActivity.this);
                        }
                    }));

        }else{
            toastFactory.createToast(R.string.text_otp_wrong, VerificationActivity.this);
            av_verify.smoothToHide();
            button_verify.setText(R.string.verification_button_text);
            button_verify.setEnabled(true);
            button_verify.setVisibility(View.VISIBLE);
        }

    }

    private void sendApkVersion() {

        String versioCode= String.valueOf(BuildConfig.VERSION_CODE);
        ServiceProvider provider = new ServiceProvider(this);
        disposable.add(provider.getmService().sendApkVersion(ClientConfig.API_V1, versioCode)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<GeneralStatusResult>() {
                    @Override
                    public void onSuccess(GeneralStatusResult generalStatusResult) {
                        System.out.print("ok");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.print("error");
                    }
                }));

    }

    //agreement dialog will bee appear by this function
    private void checkUserAgreement() {

        saveCurrency();
        sendApkVersion();
        /////////////////////////////////////////////////////////////////
        //get user profile information and save it in preference for access in other segment of app
        /* todo main
        ProfileTools.getInstance().saveProfileInformation(this).setListener(() ->
                new DialogFactory(this).createAgreementDialog(new DialogFactory.DialogFactoryInteraction() {
            @Override
            public void onAcceptButtonClicked(String... params) {

                acceptUserAgreement();
            }

            @Override
            public void onDeniedButtonClicked(boolean bool) {

                //this callback doesn't use
            }
        }, findViewById(R.id.rl_root)));

        */
        ////////////////////////////////////////////////////////////////
        // TODO: 8/3/2019 added instead of upper lines

        ProfileTools.getInstance().saveProfileInformation(this).setListener(this::acceptUserAgreement);
//                startActivity(new Intent(VerificationActivity.this, AgreementActivity1.class)));
    }

    private void saveCurrency() {

        ServiceProvider provider = new ServiceProvider(this);
        disposable.add(provider.getmService().getCurrency(ClientConfig.API_V2).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<GetCurrencyListResult>() {
                    @Override
                    public void onSuccess(GetCurrencyListResult result) {

                        getCurrencyListResult = result;
                        Log.e("hamed_verify", "msg : result :" + result.getStatus());

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("hamed_verify", "msg : " + e.getMessage());
                    }
                }));
    }

    //accept user agreement
    private void acceptUserAgreement() {

        disposable.add(new ServiceProvider(this).getmService().acceptAgreement(ClientConfig.API_V1).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableSingleObserver<GeneralStatusResult>() {
                    @Override
                    public void onSuccess(GeneralStatusResult generalStatusResult) {

                        if (getCurrencyListResult == null) {

                            Log.e("hamed_verify", "msg : result null");
                        } else {

                            Log.e("hamed_verify", "msg : " + getCurrencyListResult.getStatus());
                        }

                        //go to main activity
//                        Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                        Intent intent = new Intent(VerificationActivity.this, AgreementActivity1.class);
                        intent.putExtra("parcel_data", getCurrencyListResult);
                        startActivity(intent);
                        VerificationActivity.this.finish();
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }

                    @Override
                    public void onError(Throwable e) {

                        new ToastFactory().createToast(R.string.text_no_service, VerificationActivity.this);
                    }
                }));
    }

    //start count down timer for resend code
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(90000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {

                //initialize sec
                sec = (int) millisUntilFinished / 1000;

                //modify minute
                min = sec / 60;
                text_min.setText("0" + min);

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
                linear_recode.setVisibility(View.VISIBLE);
                rl_recode_number.setVisibility(View.GONE);
            }

        };
        countDownTimer.start();
    }

    public void reverseTimer(int Seconds, final TextView tv) {

        new CountDownTimer(Seconds * 1000 + 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);

                int hours = seconds / (60 * 60);
                int tempMint = (seconds - (hours * 60 * 60));
                int minutes = tempMint / 60;
                seconds = tempMint - (minutes * 60);

                tv.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                linear_recode.setEnabled(true);
                linear_recode.setVisibility(View.VISIBLE);
                rl_recode_number.setVisibility(View.GONE);
            }
        }.start();
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
        unregisterReceiver(connectivityReceiver);
        disposable.dispose();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        // registerReceiver(smsObserver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.linear_recode:

                linear_recode.setEnabled(false);
                linear_recode.setVisibility(View.GONE);
                rl_recode_number.setVisibility(View.VISIBLE);

                sendLoginRequest();
//                startCountDownTimer();
                reverseTimer(90,text_min);
                break;

            case R.id.button_verify:

                sendVerifyRequest();
                break;

//            case R.id.text_edit_mobile:
            case R.id.text_user_mobile:

                startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
                VerificationActivity.this.finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
        }
    }

    //todo send user info
    private void sendUserInfo() {


        String apk_version = String.valueOf(BuildConfig.VERSION_CODE);
        String model = Build.MODEL;
        String brand = Build.BRAND;

        int sdk = Build.VERSION.SDK_INT;

        os_version = String.valueOf(sdk);

//        if (sdk == 14) {
//            os_version = "android 4.0";
//        } else if (sdk == 15) {
//            os_version = "android 4.0.3";
//        } else if (sdk == 16) {
//            os_version = "android 4.1";
//        } else if (sdk == 17) {
//            os_version = "android 4.2";
//        } else if (sdk == 18) {
//            os_version = "android 4.3";
//        } else if (sdk == 19) {
//            os_version = "android 4.4";
//        } else if (sdk == 20) {
//            os_version = "android 4.4W";
//        } else if (sdk == 21) {
//            os_version = "android 5.0";
//        } else if (sdk == 22) {
//            os_version = "android 5.1";
//        } else if (sdk == 23) {
//            os_version = "android 6.0";
//        } else if (sdk == 24) {
//            os_version = "android 7.0";
//        } else if (sdk == 25) {
//            os_version = "android 7.1";
//        } else if (sdk == 26) {
//            os_version = "android 8.0";
//        } else if (sdk == 27) {
//            os_version = "android 8.1";
//        } else if (sdk == 28) {
//            os_version = "android 9.0";
//        } else if (sdk == 29) {
//            os_version = "android 10";
//        }

        ServiceProvider provider = new ServiceProvider(this);
        Service service = provider.getmService();
        disposable.add(service.sendUserIfo(ClientConfig.API_V2, apk_version, pushe_id, brand, model, "android", os_version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UserPhoneInfo>() {
                    @Override
                    public void onSuccess(UserPhoneInfo result) {
                        String a = result.status;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }


    private void goToHtmlActivity(String url, boolean shouldBeLoadUrl) {

        Intent intent = new Intent(VerificationActivity.this, HtmlLoaderActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("surveyDetails", false);
        intent.putExtra("isShopping", shouldBeLoadUrl);
        startActivity(intent);
        VerificationActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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

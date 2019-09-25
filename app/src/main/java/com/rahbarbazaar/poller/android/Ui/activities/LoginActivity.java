package com.rahbarbazaar.poller.android.Ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.os.ConfigurationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rahbarbazaar.poller.android.BuildConfig;
import com.rahbarbazaar.poller.android.Models.GeneralStatusResult;
import com.rahbarbazaar.poller.android.Models.user_phonedata.UserPhoneInfo;
import com.rahbarbazaar.poller.android.Network.Service;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Utilities.App;
import com.rahbarbazaar.poller.android.Utilities.ClientConfig;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;
import com.rahbarbazaar.poller.android.Utilities.ToastFactory;
import com.rahbarbazaar.poller.android.Utilities.GeneralTools;
import com.rahbarbazaar.poller.android.Utilities.LocaleManager;
import com.rahbarbazaar.poller.android.Utilities.TypeFaceGenerator;
import com.wang.avi.AVLoadingIndicatorView;

import co.ronash.pushe.Pushe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends CustomBaseActivity implements View.OnClickListener {

    //region of views
    AVLoadingIndicatorView av_login;
    TextView button_submit;
    EditText et_user_login;
    RelativeLayout rl_av_login;
    //end of region

    //******************************************

    //region of property
    BroadcastReceiver connectivityReceiver;
    CompositeDisposable disposable;
    ToastFactory toastFactory;
    //end of region
    String pushe_id = "";
    String os_version = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_1);


        defineViews();
        defineViewsListener();

        if (LocaleManager.getLocale(getResources()).getLanguage().equals("fa"))
            et_user_login.setTypeface(TypeFaceGenerator.getInstance().getByekanFont(this));

        toastFactory = new ToastFactory();
        disposable = new CompositeDisposable();

        //check network broadcast reciever
        GeneralTools tools = GeneralTools.getInstance();
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tools.doCheckNetwork(LoginActivity.this, findViewById(R.id.login_root));
            }
        };


        av_login.setVisibility(View.GONE);

// event on done keyboard
        et_user_login.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendLoginRequest();
                return true;
            }
            return false;
        });

    }

    //define views of activity
    private void defineViews() {

        button_submit = findViewById(R.id.button_submit);
        et_user_login = findViewById(R.id.et_user_login);
        av_login = findViewById(R.id.av_login);
        rl_av_login = findViewById(R.id.rl_av_login);
    }

    //define views of activity listener
    private void defineViewsListener() {

        button_submit.setOnClickListener(this);
    }

    //send login request and get response
    private void sendLoginRequest() {

        ServiceProvider provider = new ServiceProvider(this);
        Service service = provider.getmService();
        String phone = et_user_login.getText().toString().trim();


        if (!phone.equals("")) {

//            Snackbar.make(findViewById(R.id.login_root), R.string.text_login_progress, Snackbar.LENGTH_INDEFINITE).show();
            av_login.smoothToShow();
            rl_av_login.setVisibility(View.VISIBLE);
            button_submit.setText("");
            button_submit.setEnabled(false);
            button_submit.setVisibility(View.GONE);

            disposable.add(service.userAuthentication(ClientConfig.API_V1, phone).
                    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                    subscribeWith(new DisposableSingleObserver<GeneralStatusResult>() {
                        @Override
                        public void onSuccess(GeneralStatusResult result) {

                            if (result != null) {

                                // save to use for upgrade userAccess
                                PreferenceStorage.getInstance(LoginActivity.this).savePhone(phone);

                                //if response ok:
                                if (result.getStatus().equals("otp sent") || result.getStatus().equals("unknown number")) {

                                    Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                                    intent.putExtra("user_mobile", et_user_login.getText().toString());
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                    finish();
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                }
                            }
                            button_submit.setText(R.string.login_button_text);
                            button_submit.setEnabled(true);
                            rl_av_login.setVisibility(View.GONE);
                            av_login.smoothToHide();
                            button_submit.setVisibility(View.VISIBLE);
                            av_login.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            button_submit.setText(R.string.login_button_text);
                            button_submit.setEnabled(true);
                            av_login.smoothToHide();
                            rl_av_login.setVisibility(View.GONE);
                            button_submit.setVisibility(View.VISIBLE);
                            av_login.setVisibility(View.GONE);
//                            new ToastFactory().createToast(R.string.text_no_service, LoginActivity.this);
//                            Snackbar.make(findViewById(R.id.login_root), R.string.text_login_progress, Snackbar.LENGTH_INDEFINITE).dismiss();

                        }
                    }));
        } else {

            toastFactory.createToast(R.string.text_input_mobile, this);
        }
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_submit) {

            sendLoginRequest();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        unregisterReceiver(connectivityReceiver);
        super.onDestroy();
    }


}

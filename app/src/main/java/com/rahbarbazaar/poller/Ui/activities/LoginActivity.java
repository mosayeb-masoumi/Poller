package com.rahbarbazaar.poller.Ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import com.rahbarbazaar.poller.Models.GeneralStatusResult;
import com.rahbarbazaar.poller.Network.Service;
import com.rahbarbazaar.poller.Network.ServiceProvider;
import com.rahbarbazaar.poller.R;
import com.rahbarbazaar.poller.Utilities.CustomToast;
import com.rahbarbazaar.poller.Utilities.TypeFaceGenerator;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //region of views
    AVLoadingIndicatorView av_login;
    TextView button_submit;
    EditText et_user_login;
    //end of region

    //******************************************

    //region of property
    CompositeDisposable disposable;
    CustomToast customToast;
    //end of region

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        defineViews();
        defineViewsListener();
        et_user_login.setTypeface(TypeFaceGenerator.getInstance().getByekanFont(this));
        customToast = new CustomToast();
        disposable = new CompositeDisposable();
    }

    //define views of activity
    private void defineViews() {

        button_submit = findViewById(R.id.button_submit);
        et_user_login = findViewById(R.id.et_user_login);
        av_login = findViewById(R.id.av_login);
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

            av_login.smoothToShow();
            button_submit.setText("");

            disposable.add(service.userAuthentication(phone).
                    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                    subscribeWith(new DisposableSingleObserver<GeneralStatusResult>() {
                        @Override
                        public void onSuccess(GeneralStatusResult result) {

                            if (result != null) {

                                //if response ok:
                                if (result.getStatus().equals("otp sent")) {

                                    Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                                    intent.putExtra("user_mobile", et_user_login.getText().toString());
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                }
                            }
                            button_submit.setText("ثبت");
                            av_login.smoothToHide();
                        }

                        @Override
                        public void onError(Throwable e) {

                            button_submit.setText("ثبت");
                            av_login.smoothToHide();
                        }
                    }));

        } else {

            customToast.createToast("لطفا شماره موبایل خود را وارد کنید", this);
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_submit) {

            sendLoginRequest();
        }
    }
}

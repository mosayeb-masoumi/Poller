package com.rahbarbazaar.poller.android.Ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rahbarbazaar.poller.android.Models.GeneralStatusResult;
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Utilities.App;
import com.rahbarbazaar.poller.android.Utilities.ClientConfig;
import com.rahbarbazaar.poller.android.Utilities.GeneralTools;
import com.rahbarbazaar.poller.android.Utilities.LocaleManager;
import com.rahbarbazaar.poller.android.Utilities.ToastFactory;
import com.wang.avi.AVLoadingIndicatorView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class AgreementActivity1 extends CustomBaseActivity {

    CheckBox checkbox_agreement;
    ImageView img_page_icon_rules, img_rules_enter_icon;
    WebView webview_agreement;
    AVLoadingIndicatorView av_loading;
    TextView btn_send;
    BroadcastReceiver connectivityReceiver;
    CompositeDisposable disposable;
    GetCurrencyListResult getCurrencyListResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement1);


        //check network broadcast reciever
        GeneralTools tools = GeneralTools.getInstance();
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                tools.doCheckNetwork(AgreementActivity1.this, findViewById(R.id.rl_root));
            }
        };

        initview();


        if(App.language.equals("fa")){
            webview_agreement.loadUrl("https://test.rahbarbazar.com/poller/v2/support/agreement/fa");
        }else if(App.language.equals("en")){
            webview_agreement.loadUrl("https://test.rahbarbazar.com/poller/v2/support/agreement/en");
        }
//        webview_agreement.loadUrl("http://pollerws.rahbarbazaar.com:2296/poller/v2/support/agreement/" +
//                LocaleManager.getLocale(getApplicationContext().getResources()).getLanguage());




        webview_agreement.setBackgroundColor(Color.TRANSPARENT);

//        webview_agreement.loadUrl("javascript:document.body.style.color=\"white\";");// to change webview text


        webview_agreement.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                av_loading.smoothToHide();
            }
        });



        btn_send.setOnClickListener(view -> {


            if (checkbox_agreement.isChecked()){
                GetCurrencyListResult parcelable = getIntent().getParcelableExtra("parcel_data");

//                Intent intent = new Intent(AgreementActivity1.this, MainActivity.class);
                Intent intent = new Intent(AgreementActivity1.this, GifActivity.class);
                intent.putExtra("parcel_data", parcelable);
                startActivity(intent);
                AgreementActivity1.this.finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }

            else
            Toast.makeText(this, ""+R.string.text_accept_terms, Toast.LENGTH_SHORT).show();

        });


        checkbox_agreement.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                img_rules_enter_icon.setVisibility(View.VISIBLE);
                img_page_icon_rules.setVisibility(View.GONE);
            } else {
                img_rules_enter_icon.setVisibility(View.GONE);
                img_page_icon_rules.setVisibility(View.VISIBLE);
            }

        });

        if (checkbox_agreement.isChecked()) {
            img_rules_enter_icon.setVisibility(View.VISIBLE);
            img_page_icon_rules.setVisibility(View.GONE);
        } else {
            img_page_icon_rules.setVisibility(View.VISIBLE);
            img_rules_enter_icon.setVisibility(View.GONE);
        }

    }

    private void initview() {
        checkbox_agreement = findViewById(R.id.checkbox_agreement);
        img_page_icon_rules = findViewById(R.id.img_page_icon_rules);
        img_rules_enter_icon = findViewById(R.id.img_rules_enter_icon);

        webview_agreement = findViewById(R.id.webview_agreement);
        checkbox_agreement = findViewById(R.id.checkbox_agreement);
        av_loading = findViewById(R.id.av_loading);
        btn_send = findViewById(R.id.btn_login_dialog);
//        TextView btn_cancel_dialog = findViewById(R.id.btn_cancel_dialog);
    }





}

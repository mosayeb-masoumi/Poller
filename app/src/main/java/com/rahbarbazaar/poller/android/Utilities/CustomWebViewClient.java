package com.rahbarbazaar.poller.android.Utilities;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.rahbarbazaar.poller.android.Ui.activities.HtmlLoaderActivity;
import com.rahbarbazaar.poller.android.Ui.activities.MainActivity;

public class CustomWebViewClient extends WebViewClient {

//    HtmlLoaderActivity htmlLoaderActivity;
    Context context;
    public CustomWebViewClient(HtmlLoaderActivity htmlLoaderActivity) {

//       this.htmlLoaderActivity = htmlLoaderActivity;
       context = htmlLoaderActivity;
    }

    ////
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {


        if(url.startsWith("poller://home")) {

            context.startActivity(new Intent(context, MainActivity.class));

            return true;
        }

        return false;
    }
}

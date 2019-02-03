package com.rahbarbazaar.poller.android.Network;

import android.content.Context;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.rahbarbazaar.poller.android.Utilities.ClientConfig;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceProvider {

    private Service mService;

    public ServiceProvider(Context context) {

        //config client and retrofit:

        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder();
        clientBuilder.connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES);
        clientBuilder.cache(null);

        if (!PreferenceStorage.getInstance().retriveToken(context).equals("0")) {//user token was not null so we add access token in all of request

            clientBuilder.addInterceptor(chain -> {
                Request request = chain.request().newBuilder().addHeader("token", PreferenceStorage.getInstance().retriveToken(context)).build();
                return chain.proceed(request);
            });
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ClientConfig.BASE_ADDRESS).client(clientBuilder.build()).
                addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();

        mService = retrofit.create(Service.class);
    }

    public Service getmService() {
        return mService;
    }
}

package com.rahbarbazaar.poller.android.Network;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.rahbarbazaar.poller.android.Models.refresh_token.RefreshToken;
import com.rahbarbazaar.poller.android.Ui.activities.LoginActivity;
import com.rahbarbazaar.poller.android.Utilities.ClientConfig;
import com.rahbarbazaar.poller.android.Utilities.ErrorHandling;
import com.rahbarbazaar.poller.android.Utilities.LocaleManager;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
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

        PreferenceStorage storage = PreferenceStorage.getInstance(context);
        if (!storage.retriveToken().equals("0")) {//user token was not null so we add access token in all of request

            clientBuilder.addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("lang", storage.retriveLanguage())
                        .addHeader("token", PreferenceStorage.getInstance(context).retriveToken())
                        .build();
                return chain.proceed(request);
            });
        }


        // handle error 401
        clientBuilder.authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                //check if there is mobilephone

                String phone = PreferenceStorage.getInstance(context).retrivePhone();

                if (!phone.equals("0")) {

                    Service service = new ServiceProvider(context).mService;
                    Call<RefreshToken> call = service.requsetRefreshToken(ClientConfig.API_V1);
                    retrofit2.Response<RefreshToken> tokenModelResponse = call.execute();
                    //sync request
                    if (tokenModelResponse.isSuccessful()) {
                        //save token
                        PreferenceStorage.getInstance(context).saveToken(tokenModelResponse.body().getToken());
                        return response.request().newBuilder()
                                .removeHeader("lang")
                                .removeHeader("token")
                                .addHeader("lang", storage.retriveLanguage())
                                .addHeader("token", PreferenceStorage.getInstance(context).retriveToken())
                                .build();

                    } else {
                        return null;
                    }

                } else {
                    return null;
                }
            }
        });


//         handle  error handling
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);

                ErrorHandling errorHandling = new ErrorHandling(context);

                int a = response.code();
                if (response.code() == 403) {
                    PreferenceStorage.getInstance(context).saveToken("0");
                    context.startActivity(new Intent(context, LoginActivity.class));
                    return response;
                } else if (response.code() == 422) {
                    errorHandling.getErrorCode(422);
                    return response;
                }
                else if(response.code() == 201) {
                    return response;
                }
                else if(response.code() == 202) {
                    return response;

                }else if(response.code() == 203) {
                    return response;
                }else if (response.code() != 200 && response.code() != 401) {
                    errorHandling.getErrorCode(0);
                    return response;
                }
               return response;
            }
        }).build();


        Retrofit retrofit = new Retrofit.Builder().baseUrl(ClientConfig.BASE_ADDRESS).client(clientBuilder.build()).
                addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();

        mService = retrofit.create(Service.class);
    }

    public Service getmService() {
        return mService;
    }
}

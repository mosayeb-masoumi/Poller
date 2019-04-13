package com.rahbarbazaar.poller.android.Network;

import com.rahbarbazaar.poller.android.Models.ChangeSurveyStatusResult;
import com.rahbarbazaar.poller.android.Models.GeneralStatusResult;
import com.rahbarbazaar.poller.android.Models.GetCurrencyResult;
import com.rahbarbazaar.poller.android.Models.GetDownloadResult;
import com.rahbarbazaar.poller.android.Models.GetNewsListResult;
import com.rahbarbazaar.poller.android.Models.GetNotificationListResult;
import com.rahbarbazaar.poller.android.Models.GetPagesResult;
import com.rahbarbazaar.poller.android.Models.GetReferralResult;
import com.rahbarbazaar.poller.android.Models.GetShopListResult;
import com.rahbarbazaar.poller.android.Models.GetSurveyHistoryListResult;
import com.rahbarbazaar.poller.android.Models.GetTransactionResult;
import com.rahbarbazaar.poller.android.Models.SurveyMainModel;
import com.rahbarbazaar.poller.android.Models.UserConfirmAuthResult;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Service {

    @POST("authentication")
    Single<GeneralStatusResult> userAuthentication(@Query("phone_number") String phone);

    @POST("authentication/confirm")
    Single<UserConfirmAuthResult> userConfirmAuthentication(@Query("phone_number") String phone,
                                                            @Query("otp") String otp);

    @POST("user/agreement")
    Single<GeneralStatusResult> acceptAgreement();

    @GET("user/profile")
    Single<UserConfirmAuthResult> getUserProfile();

    @POST("user/edit/request")
    Single<GeneralStatusResult> editUserProfile(@Query("comment") String comment);

    @GET("survey/user/histories")
    Single<GetSurveyHistoryListResult> getSurveyHistoryList();

    @GET("survey/latest")
    Single<List<SurveyMainModel>> getSurveyLatestList(@Query("page") String page);

    @GET("survey")
    Single<SurveyMainModel> getSurveyDetails(@Query("survey_id") String survey_id);

    @GET("news")
    Single<GetNewsListResult> getNewsList();

    @GET("user/referral")
    Single<GetReferralResult> getReferral();

    @GET("user/transaction/histories")
    Single<List<GetTransactionResult>> getTransactionList();

    @GET("pages")
    Single<List<GetPagesResult>> getDrawerPages();

    @GET("currency")
    Single<GetCurrencyResult> getCurrency();

    @POST("survey")
    Single<ChangeSurveyStatusResult> changeSurveyStatus(@Query("survey_id") String survey_id,
                                                        @Query("user_id") String user_id,
                                                        @Query("status") String status);

    @GET("settings/apk")
    Single<GetDownloadResult> checkUpdate();

    @GET
    Single<ResponseBody> downloadWithUrl(@Url String url);

    @GET("shop/items")
    Single<List<GetShopListResult>> getShopItems();

    @GET("messages")
    Single<GetNotificationListResult> getNotificationList();

    @Multipart
    @POST("message/seen")
    Single<GeneralStatusResult> seenMessage(@Part("message_id") RequestBody message_id);

    @Multipart
    @POST("support/request")
    Completable reportIssues(@Part("title") RequestBody title,
                             @Part("description") RequestBody description
    );
}

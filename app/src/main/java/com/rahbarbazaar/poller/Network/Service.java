package com.rahbarbazaar.poller.Network;

import java.util.List;

import com.rahbarbazaar.poller.Models.ChangeSurveyStatusResult;
import com.rahbarbazaar.poller.Models.GetCurrencyResult;
import com.rahbarbazaar.poller.Models.GetNewsListResult;
import com.rahbarbazaar.poller.Models.GetPagesResult;
import com.rahbarbazaar.poller.Models.GetReferralResult;
import com.rahbarbazaar.poller.Models.GetSurveyHistoryListResult;
import com.rahbarbazaar.poller.Models.GetTransactionResult;
import com.rahbarbazaar.poller.Models.SurveyMainModel;
import com.rahbarbazaar.poller.Models.GeneralStatusResult;
import com.rahbarbazaar.poller.Models.UserConfirmAuthResult;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Service {

    @POST("authentication")
    Single<GeneralStatusResult> userAuthentication(@Query("phone_number") String phone);

    @POST("authentication/confirm")
    Single<UserConfirmAuthResult> userConfirmAuthentication(@Query("phone_number") String phone,
                                                          @Query("otp") String otp);

    @POST("user/agreement")
    Single<GeneralStatusResult> acceptAgreement();

    @GET("user/profile")
    Single <UserConfirmAuthResult> getUserProfile();

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
    Single<ChangeSurveyStatusResult> changeSurveyStatus(@Query("survey_id") String survey_id, @Query("user_id") String user_id, @Query("status") String status);

}

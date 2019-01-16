package dev.hamed.pollerproject.Network;

import java.util.List;

import dev.hamed.pollerproject.Models.ChangeSurveyStatusResult;
import dev.hamed.pollerproject.Models.GetCurrencyResult;
import dev.hamed.pollerproject.Models.GetGlobalInfoResult;
import dev.hamed.pollerproject.Models.GetNewsListResult;
import dev.hamed.pollerproject.Models.GetPagesResult;
import dev.hamed.pollerproject.Models.GetReferralResult;
import dev.hamed.pollerproject.Models.GetSurveyHistoryListResult;
import dev.hamed.pollerproject.Models.GetTransactionResult;
import dev.hamed.pollerproject.Models.SurveyMainModel;
import dev.hamed.pollerproject.Models.GeneralStatusResult;
import dev.hamed.pollerproject.Models.UserConfirmAuthResult;
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

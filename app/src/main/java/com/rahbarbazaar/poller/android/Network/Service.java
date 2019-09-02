package com.rahbarbazaar.poller.android.Network;

import com.rahbarbazaar.poller.android.Models.ChangeSurveyStatusResult;
import com.rahbarbazaar.poller.android.Models.GeneralStatusResult;
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.Models.GetDownloadResult;
import com.rahbarbazaar.poller.android.Models.GetBannersListResult;
import com.rahbarbazaar.poller.android.Models.GetLotteryListResult;
import com.rahbarbazaar.poller.android.Models.GetNotificationListResult;
import com.rahbarbazaar.poller.android.Models.GetPagesResult;
import com.rahbarbazaar.poller.android.Models.GetReferralResult;
import com.rahbarbazaar.poller.android.Models.GetShopListResult;
import com.rahbarbazaar.poller.android.Models.GetSurveyHistoryListResult;
import com.rahbarbazaar.poller.android.Models.GetTransactionResult;
import com.rahbarbazaar.poller.android.Models.LotterySettingResult;
import com.rahbarbazaar.poller.android.Models.SurveyMainModel;
import com.rahbarbazaar.poller.android.Models.UserConfirmAuthResult;
import com.rahbarbazaar.poller.android.Models.getimages.GetImages;
import com.rahbarbazaar.poller.android.Models.user_phonedata.UserPhoneInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Service {

    @POST("{api}/authentication")
    Single<GeneralStatusResult> userAuthentication(@Path("api") String version, @Query("phone_number") String phone);

    @POST("{api}/authentication/confirm")
    Single<UserConfirmAuthResult> userConfirmAuthentication(
            @Path("api") String version,
            @Query("phone_number") String phone,
            @Query("otp") String otp);

    @POST("{api}/user/agreement")
    Single<GeneralStatusResult> acceptAgreement(@Path("api") String version);

    @GET("{api}/user/profile")
    Single<UserConfirmAuthResult> getUserProfile(@Path("api") String version);

    @POST("{api}/user/edit/request")
    Single<GeneralStatusResult> editUserProfile(
            @Path("api") String version,
            @Query("comment") String comment);

    @GET("{api}/survey/user/histories")
    Single<GetSurveyHistoryListResult> getSurveyHistoryList(@Path("api") String version);

    @GET("{api}/survey/latest")
    Single<List<SurveyMainModel>> getSurveyLatestList(
            @Path("api") String version,
            @Query("page") String page);

    @GET("{api}/survey")
    Single<SurveyMainModel> getSurveyDetails(
            @Path("api") String version,
            @Query("survey_id") String survey_id);

    @GET("{api}/news")
    Single<GetBannersListResult> getNewsList(@Path("api") String version); // for pre application version

    @GET("{api}/banners")
    Single<List<GetBannersListResult>> getBannerList(@Path("api") String version, @Query("lang") String lang);


    @GET("{api}/user/referral")
    Single<GetReferralResult> getReferral(@Path("api") String version);

    @GET("{api}/user/transaction/histories")
    Single<List<GetTransactionResult>> getTransactionList(@Path("api") String version);

    @GET("{api}/user/transaction/score")
    Single<List<GetTransactionResult>> getTransactionScoreList(@Path("api") String version);

    @GET("{api}/pages")
    Single<List<GetPagesResult>> getDrawerPages(@Path("api") String version); // for pre application version

    @GET("{api}/pages")
    Single<List<GetPagesResult>> getDrawerPages(
            @Path("api") String version,
            @Query("lang") String lang);

    @GET("{api}/currency")
    Single<GetCurrencyListResult> getCurrency(@Path("api") String version);

    @POST("{api}/survey")
    Single<ChangeSurveyStatusResult> changeSurveyStatus(
            @Path("api") String version,
            @Query("survey_id") String survey_id,
            @Query("user_id") String user_id,
            @Query("status") String status);

    @GET("{api}/settings/apk")
    Single<GetDownloadResult> checkUpdate(@Path("api") String version);

    @POST("{api}/user/apk-v")
    Single<GeneralStatusResult> sendApkVersion(@Path("api") String version,@Query("version") String v);

    @GET
    Single<ResponseBody> downloadWithUrl(@Url String url);

    @GET("{api}/shop/items")
    Single<List<GetShopListResult>> getShopItems(@Path("api") String version);

    @GET("{api}/messages")
    Single<GetNotificationListResult> getNotificationList(@Path("api") String version);

    @Multipart
    @POST("{api}/message/seen")
    Single<GeneralStatusResult> seenMessage(
            @Path("api") String version,
            @Part("message_id") RequestBody message_id);

    @Multipart
    @POST("{api}/support/request")
    Completable reportIssues(
            @Path("api") String version,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description
    );

    @GET("{api}/lottery/current")
    Single<List<GetLotteryListResult>> getCurrentLottery(@Path("api") String version);

    @GET("{api}/lottery/history")
    Single<List<GetLotteryListResult>> getLotteryHistory(@Path("api") String version);

    @POST("{api}/lottery/join")
    Single<GeneralStatusResult> joinToLottery(@Path("api") String version,
                                              @Query("lottery_id") String id,
                                              @Query("amount") String amount);

    @POST("{api}/lottery/cancel")
    Single<GeneralStatusResult> cancelLottery(@Path("api") String version,
                                              @Query("lottery_id") String id
    );

    @POST("{api}/lottery/convert-balance")
    Single<GeneralStatusResult> convertBalanceToPoint(@Path("api") String version,
                                                      @Query("amount") String amount
    );

    @POST("{api}/lottery/convert-point")
    Single<GeneralStatusResult> convertPointToBalance(@Path("api") String version,
                                                      @Query("amount") String amount
    );

    @GET("{api}/settings/convert-point")
    Single<LotterySettingResult> checkAllowExchange(@Path("api") String version
    );


    @GET("{api}/user/dashboard")
    Single<GetImages> getImages(@Path("api") String version
    );

    @POST("{api}/user/apk-info")
    Single<UserPhoneInfo> sendUserIfo(@Path("api") String version,
                                      @Query("apk_version") String apk_version,
                                      @Query("pushe_id") String pushe_id,
                                      @Query("brand") String brand,
                                      @Query("model") String model,
                                      @Query("os_type") String os_type,
                                      @Query("os_version") String os_version
    );
}

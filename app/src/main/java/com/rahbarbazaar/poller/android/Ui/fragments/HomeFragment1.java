package com.rahbarbazaar.poller.android.Ui.fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.Models.SurveyMainModel;
import com.rahbarbazaar.poller.android.Models.eventbus.ModelActiveSurveyCount;
import com.rahbarbazaar.poller.android.Models.eventbus.ModelTranferDataProfileToHome;
import com.rahbarbazaar.poller.android.Models.getimages.GetImages;
import com.rahbarbazaar.poller.android.Network.Service;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Ui.activities.HtmlLoaderActivity;
import com.rahbarbazaar.poller.android.Ui.activities.MainActivity;
import com.rahbarbazaar.poller.android.Utilities.App;
import com.rahbarbazaar.poller.android.Utilities.ClientConfig;
import com.rahbarbazaar.poller.android.Utilities.DialogFactory;
import com.rahbarbazaar.poller.android.Utilities.LocaleManager;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment1 extends Fragment implements View.OnClickListener {

    GetCurrencyListResult parcelable;
    String lang;
    CompositeDisposable disposable;
    ServiceProvider provider = null;
    CardView cardview_home_video, cardview_home_image, cardview_home_polls;
    TextView text_leftdays_digit, text_activepoll_digit, text_balance_digit, text_yourpoint_digit;
    SimpleDraweeView img_home_whats_up, img_home_video, img_home_polls;
    RelativeLayout rlHomeScore, rlHomeBalance, rlHomeActiveSurveys, rlHomeLeftDays;
    int activeSurveys;
    int balance;
    int lotteryDays;
    int score;
    String newsImgUrl = "";
    String surveyImgUrl = "";
    String videoImgUrl = "";
    String videoWebUrl = "";
    String newsWebUrl = "";


    LinearLayout homeFragment1;

    public HomeFragment1() {
        // Required empty public constructor
    }


    public static HomeFragment1 newInstance(GetCurrencyListResult parcelable, String lang) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("parcel_data", parcelable);
        bundle.putString("lang", lang);

        HomeFragment1 fragment = new HomeFragment1();
        fragment.setArguments(bundle);


        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            parcelable = getArguments().getParcelable("parcel_data");
            lang = getArguments().getString("lang");
        }
        provider = new ServiceProvider(getContext());
        disposable = new CompositeDisposable();

        Fresco.initialize(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // register eventbus to get posted array or etc...
        EventBus.getDefault().register(this);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home1, container, false);


        init(view);
        getImages();

//        setImages();
        cardview_home_video.setOnClickListener(this);
        cardview_home_image.setOnClickListener(this);
        cardview_home_polls.setOnClickListener(this);

        rlHomeScore.setOnClickListener(this);
        rlHomeBalance.setOnClickListener(this);
        rlHomeActiveSurveys.setOnClickListener(this);
        rlHomeLeftDays.setOnClickListener(this);


        showGuidePopup();

        return view;
    }

    private void showGuidePopup() {


        if (PreferenceStorage.getInstance(getContext()).getFirstLaunch()) {
            PreferenceStorage.getInstance(getContext()).setFirstLaunch(false);
            DialogFactory dialogFactory = new DialogFactory(getContext());
            dialogFactory.createGuideFirstLaunchDialog(homeFragment1, new DialogFactory.DialogFactoryInteraction() {
                @Override
                public void onAcceptButtonClicked(String... strings) {

                }

                @Override
                public void onDeniedButtonClicked(boolean cancel_dialog) {

                }
            });

        }
    }


    @Subscribe
    public void onEvent(List<ModelTranferDataProfileToHome> event) {
        text_yourpoint_digit.setText(event.get(0).getScore());
        text_balance_digit.setText(event.get(0).getBalance());
    }


    private void init(View view) {
        cardview_home_video = view.findViewById(R.id.cardview_home_video);
        cardview_home_image = view.findViewById(R.id.cardview_home_image);
        cardview_home_polls = view.findViewById(R.id.cardview_home_polls);
        text_leftdays_digit = view.findViewById(R.id.text_leftdays_digit);

        text_activepoll_digit = view.findViewById(R.id.text_activepoll_digit);
        text_balance_digit = view.findViewById(R.id.text_balance_digit);
        text_yourpoint_digit = view.findViewById(R.id.text_yourpoint_digit);
        img_home_whats_up = view.findViewById(R.id.img_home_whats_up);
        img_home_video = view.findViewById(R.id.img_home_video);
        img_home_polls = view.findViewById(R.id.img_home_polls);


        rlHomeScore = view.findViewById(R.id.rlHomeScore);
        rlHomeBalance = view.findViewById(R.id.rlHomeBalance);
        rlHomeActiveSurveys = view.findViewById(R.id.rlHomeActiveSurveys);
        rlHomeLeftDays = view.findViewById(R.id.rlHomeLeftDays);

        homeFragment1 = view.findViewById(R.id.homeFragment1);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardview_home_video:

                goToHtmlActivity(videoWebUrl + "/" + LocaleManager.getLocale(getResources()).getLanguage(), true);

                break;
            case R.id.cardview_home_image:
                goToHtmlActivity(newsWebUrl + "/" + LocaleManager.getLocale(getResources()).getLanguage(), true);
                break;

            case R.id.cardview_home_polls:
                ((MainActivity) getActivity()).onTabSelected(2, false);
                break;


            case R.id.rlHomeScore:
                String title1 = "homeScore";
                showHomeInfoDialog(title1);
                break;

            case R.id.rlHomeBalance:
                String title2 = "homeBalance";
                showHomeInfoDialog(title2);
                break;

            case R.id.rlHomeActiveSurveys:
                String title3 = "homeActiveSurveys";
                showHomeInfoDialog(title3);
                break;

            case R.id.rlHomeLeftDays:
                String title4 = "homeLeftDays";
                showHomeInfoDialog(title4);
                break;


        }
    }

    private void showHomeInfoDialog(String title) {

        DialogFactory dialogFactory = new DialogFactory(getContext());
        dialogFactory.createHomeInfoDialog(homeFragment1, title, new DialogFactory.DialogFactoryInteraction() {
            @Override
            public void onAcceptButtonClicked(String... strings) {

            }

            @Override
            public void onDeniedButtonClicked(boolean cancel_dialog) {

            }
        });
    }


    private void goToHtmlActivity(String url, boolean shouldBeLoadUrl) {

        Intent intent = new Intent(getContext(), HtmlLoaderActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("surveyDetails", false);
        intent.putExtra("isShopping", shouldBeLoadUrl);
        startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


    private void getImages() {
        Service service = new ServiceProvider(getContext()).getmService();
//        String locale_name = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
        disposable.add(service.getImages(ClientConfig.API_V2).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableSingleObserver<GetImages>() {

                    @Override
                    public void onSuccess(GetImages result) {
                        if (result != null) {

                            activeSurveys = result.getImagesDetail.getActiveSurveys();
                            balance = result.getImagesDetail.getBalance();
                            lotteryDays = result.getImagesDetail.getLotteryDays();
                            score = result.getImagesDetail.getScore();
                            newsImgUrl = result.getImagesDetail.getNews();
                            surveyImgUrl = result.getImagesDetail.getSurvey();
                            videoImgUrl = result.getImagesDetail.getVideo();
                            newsWebUrl = result.getImagesDetail.getNews_url();
                            videoWebUrl = result.getImagesDetail.getVideo_url();

                            App.videoWebUrl = videoWebUrl;

                            if (activeSurveys != 0 && interaction != null)
                                interaction.activeSurveysCount(String.valueOf(activeSurveys));//badge count for survey page


                            text_yourpoint_digit.setText(String.valueOf(score));
                            text_balance_digit.setText(String.valueOf(balance));
                            text_activepoll_digit.setText(String.valueOf(activeSurveys));
                            text_leftdays_digit.setText(String.valueOf(lotteryDays));

                            App.leftDay = String.valueOf(lotteryDays);

                            setImages(newsImgUrl, surveyImgUrl, videoImgUrl);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
//                        int error = ((HttpException) e).code();
//                        if(error ==401){
//                            startActivity(new Intent(getActivity(), SplashScreenActivity.class));
//                        }else if(error ==403){
//                            PreferenceStorage.getInstance(getContext()).saveToken("0");
//                            startActivity(new Intent(getContext(), SplashScreenActivity.class));
//                            Objects.requireNonNull(getActivity()).finish();
//                        }
                    }
                }));
    }

    private void setImages(String newsImgUrl, String surveyImgUrl, String videoImgUrl) {
        //loading image from url
        Uri uriNewsUrl = Uri.parse(newsImgUrl);
        Uri uriVideoUrl = Uri.parse(videoImgUrl);
        Uri uriSurveyUrl = Uri.parse(surveyImgUrl);

        img_home_whats_up.setImageURI(uriNewsUrl);
        img_home_video.setImageURI(uriVideoUrl);
        img_home_polls.setImageURI(uriSurveyUrl);
    }


    HomeFragment1.ActiveSurveysInteraction interaction; //survey interaction interface

    public interface ActiveSurveysInteraction {

        void activeSurveysCount(String count);
    }

    @Override
    public void onAttach(Context context) {
        interaction = (HomeFragment1.ActiveSurveysInteraction) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if (interaction != null)
            interaction = null;
        super.onDetach();
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ModelActiveSurveyCount modelActiveSurveyCount) {
        text_activepoll_digit.setText(modelActiveSurveyCount.getActiveSurveyCount());
    }
}

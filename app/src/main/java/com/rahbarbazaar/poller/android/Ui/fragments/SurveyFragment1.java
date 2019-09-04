package com.rahbarbazaar.poller.android.Ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.rahbarbazaar.poller.android.Controllers.adapters.SurveyRecyclerAdapter;
import com.rahbarbazaar.poller.android.Controllers.adapters.SurveyRecyclerAdapter1;
import com.rahbarbazaar.poller.android.Controllers.viewHolders.SurveyItemInteraction;
import com.rahbarbazaar.poller.android.Models.ChangeSurveyStatusResult;
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.Models.GetSurveyHistoryListResult;
import com.rahbarbazaar.poller.android.Models.RefreshBalanceEvent;
import com.rahbarbazaar.poller.android.Models.RefreshSurveyEvent;
import com.rahbarbazaar.poller.android.Models.SurveyMainModel;
import com.rahbarbazaar.poller.android.Models.UserDetailsPrefrence;
import com.rahbarbazaar.poller.android.Network.Service;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Ui.activities.HtmlLoaderActivity;
import com.rahbarbazaar.poller.android.Ui.activities.SplashScreenActivity;
import com.rahbarbazaar.poller.android.Utilities.ClientConfig;
import com.rahbarbazaar.poller.android.Utilities.SnackBarFactory;
import com.rahbarbazaar.poller.android.Utilities.ToastFactory;
import com.rahbarbazaar.poller.android.Utilities.DialogFactory;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;
import com.rahbarbazaar.poller.android.Utilities.ProfileTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class SurveyFragment1 extends Fragment implements SurveyItemInteraction {

    //property and params region

    RecyclerView home_survey_rv;
//    LinearLayout linear_header;
    SurveyRecyclerAdapter1 adapter;
    TextView text_no_active_survey;
    SwipeRefreshLayout swipe_refesh;
    CompositeDisposable disposable;
    final static int SURVEY_ANSWER_REQ = 12;
    List<SurveyMainModel> surveyList;
    boolean isSurveyItemClickable = true;
    UserDetailsPrefrence userDetailsPrefrence;
    GetCurrencyListResult parcelable;
    String lang;
    //end of region

    public SurveyFragment1() {
        // Required empty public constructor
    }

    public static SurveyFragment1 newInstance(GetCurrencyListResult parcelable, String lang) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("parcel_data",parcelable);
        bundle.putString("lang",lang);

        SurveyFragment1 fragment =new SurveyFragment1();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments()!=null){
            parcelable = this.getArguments().getParcelable("parcel_data");
            lang = getArguments().getString("lang");
        }
        disposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey1, container, false);

        defineViews(view);
        configRecyclerview();
        getUserSurveyHistory();

        if (getActivity() != null) {

            //calculate height of screen for change dynamically height of layout
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//            int height = displayMetrics.heightPixels;
//            ((LinearLayout.LayoutParams) linear_header.getLayoutParams()).height = height / 3;

        }

        PreferenceStorage storage = PreferenceStorage.getInstance(getContext());
        String user_details = storage.retriveUserDetails();

        if (user_details != null && !user_details.equals("")) {
            userDetailsPrefrence = new Gson().fromJson(user_details, UserDetailsPrefrence.class);
        }

        return view;
    }

    //define view of fragment and set property
    private void defineViews(View view) {

        home_survey_rv = view.findViewById(R.id.survey_rv);
//        linear_header = view.findViewById(R.id.linear_header);
        swipe_refesh = view.findViewById(R.id.swipe_refresh);
        text_no_active_survey = view.findViewById(R.id.text_no_active_survey);

        //set scheme color for swipe refreshing and refresh listener
        swipe_refesh.setColorSchemeResources(R.color.colorPrimary);
        swipe_refesh.setOnRefreshListener(this::getUserSurveyHistory);
    }

    //config home recycler view
    private void configRecyclerview() {

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        home_survey_rv.setLayoutManager(manager);
        home_survey_rv.setHasFixedSize(true);
        home_survey_rv.setItemViewCacheSize(5);
        home_survey_rv.setItemAnimator(null);
        home_survey_rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

                if (parent.getChildLayoutPosition(view) != 0) {

                    outRect.top = 10;
                }
            }
        });
    }

    //initial survey list :
    private void initializSurveys() {

        surveyList = new ArrayList<>();
        adapter = null;
        home_survey_rv.setAdapter(null);
        adapter = new SurveyRecyclerAdapter1(surveyList);
        adapter.setListener(this);
        home_survey_rv.setAdapter(adapter);
    }

    //get user survey history and check response
    private void getUserSurveyHistory() {

        ServiceProvider provider = new ServiceProvider(getContext());
        Service service = provider.getmService();

        disposable.add(service.getSurveyHistoryList(ClientConfig.API_V1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                        subscribeWith(new DisposableSingleObserver<GetSurveyHistoryListResult>() {
                            @Override
                            public void onSuccess(GetSurveyHistoryListResult result) {

                                if (result != null) {

                                    initializSurveys();
                                    bindSurveyHistory(result);
                                }
                                swipe_refesh.post(() -> swipe_refesh.setRefreshing(false));
                            }

                            @Override
                            public void onError(Throwable e) {

//                                int error = ((HttpException) e).code();
//                                if(error ==401){
//                                    startActivity(new Intent(getActivity(), SplashScreenActivity.class));
//                                }else if(error ==403){
//                                    PreferenceStorage.getInstance(getContext()).saveToken("0");
//                                    startActivity(new Intent(getContext(), SplashScreenActivity.class));
//                                    getActivity().finish();
//                                }

                                swipe_refesh.post(() -> swipe_refesh.setRefreshing(false));
                            }
                        }));
    }

    //bind data of user survey history
    private void bindSurveyHistory(GetSurveyHistoryListResult data) {

        List<SurveyMainModel> items = new ArrayList<>();

        //get active survey
        if (data.getActives() != null) {

            if (data.getActives().size() > 0) {

                items.addAll(data.getActives());
                int active_count = 0;
                for (SurveyMainModel model : data.getActives()) {

                    if (model.getStatus() == 1)
                        active_count++;
                }
                if (active_count != 0 && interaction != null)
                    interaction.activeSurveyCount(String.valueOf(active_count));//badge count for survey page

            } else if (data.getActives().size() == 0)
                text_no_active_survey.setVisibility(View.VISIBLE); //if user doesn't have any active survey , no active survey text will be visible
        }

        //get expired data
        if (data.getExpired() != null && data.getExpired().size() > 0) {

            List<SurveyMainModel> expireds = data.getExpired();

            for (int i = 0; i < expireds.size(); i++) {

                expireds.get(i).setExpired(true);
            }

            items.addAll(expireds);
        }

        surveyList.addAll(items);
        adapter.notifyDataSetChanged();
    }

    //get survey details by survey id
    private void getSurveyDetails(String survey_id, String button_status, int url_type) {

        Service service = new ServiceProvider(getContext()).getmService();

        disposable.add(service.getSurveyDetails(ClientConfig.API_V1, survey_id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                        subscribeWith(new DisposableSingleObserver<SurveyMainModel>() {
                            @Override
                            public void onSuccess(SurveyMainModel result) {

                                if (result != null) {

                                    showDetailsSurveyDialog(result, button_status, url_type);
                                }
                                isSurveyItemClickable = true;
                            }

                            @Override
                            public void onError(Throwable e) {
//                                int error = ((HttpException) e).code();
//                                if(error ==401){
//                                    startActivity(new Intent(getActivity(), SplashScreenActivity.class));
//                                }else if(error ==403){
//                                    PreferenceStorage.getInstance(getContext()).saveToken("0");
//                                    startActivity(new Intent(getContext(), SplashScreenActivity.class));
//                                    getActivity().finish();
//                                }

                                isSurveyItemClickable = true;
                            }
                        }));
    }

    //this function will be create details survey dialog
    private void showDetailsSurveyDialog(SurveyMainModel result, String button_status, int url_type) {

        new DialogFactory(getActivity()).createSurveyDetailsDialog(new DialogFactory.DialogFactoryInteraction() {
            @Override
            public void onAcceptButtonClicked(String... params) {

                if (userDetailsPrefrence.getType().equalsIgnoreCase("1")) {

                    if (result.getPoint() == 0) {

                        sendToHtmlActivity(params[0], url_type, result);

                    } else {

                        new DialogFactory(getContext()).createNoRegisterDialog(getView(), new DialogFactory.DialogFactoryInteraction() {
                            @Override
                            public void onAcceptButtonClicked(String... params) {

                                //params
                            }

                            @Override
                            public void onDeniedButtonClicked(boolean cancel_dialog) {

                                //params
                            }
                        });
                    }
                } else {

                    sendToHtmlActivity(params[0], url_type, result);
                }
            }

            @Override
            public void onDeniedButtonClicked(boolean bool) {

                //doesn't use here
            }
        }, result, getView(), button_status);
    }

    private void sendToHtmlActivity(String url, int url_type, SurveyMainModel result) {

        if (!url.equals("")) {

            //initial intent for html loader activity
            /**
             * url : according to website or in better definition survey link
             * id : survey id
             * survey details : according to way that we go to html_loader activity
             */
            Intent intent = new Intent(getContext(), HtmlLoaderActivity.class);
            String phone_number = null;

            if (userDetailsPrefrence != null)
                phone_number = userDetailsPrefrence.getPhone_number();

            if (url_type == 2)
                url = url + "?login=" + phone_number;

            intent.putExtra("url", url);
            intent.putExtra("id", result.getId());
            intent.putExtra("type", url_type);
            intent.putExtra("surveyDetails", true);

            startActivityForResult(intent, SURVEY_ANSWER_REQ);
            assert getActivity() != null;
            getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        } else
            new ToastFactory().createToast(R.string.text_no_address, getContext());
    }

    //after user back to app we have to call change survey status service
    private void changeSurveyStatus(String id, String qStatus) {

        Service service = new ServiceProvider(getContext()).getmService();
        UserDetailsPrefrence user_details = new Gson().fromJson(PreferenceStorage.getInstance(getContext()).retriveUserDetails(), UserDetailsPrefrence.class);


        disposable.add(service.changeSurveyStatus(ClientConfig.API_V1, id, user_details.getUser_id(), qStatus.equals("2") ? "3" : "2")
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                        subscribeWith(new DisposableSingleObserver<ChangeSurveyStatusResult>() {
                            @Override
                            public void onSuccess(ChangeSurveyStatusResult result) {

                                if (result != null) {

                                    if (result.getStatus() != null && !result.getStatus().equals("")) {

                                        getUserSurveyHistory();
                                        EventBus.getDefault().post(new RefreshSurveyEvent());
                                        ProfileTools.getInstance().saveProfileInformation(getContext()).setListener(() -> EventBus.getDefault().post(new RefreshBalanceEvent()));
                                        SnackBarFactory.getInstance().showResultSnackbar(getView(), getContext()).show();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
//                                int error = ((HttpException) e).code();
//                                if(error ==401){
//                                    startActivity(new Intent(getActivity(), SplashScreenActivity.class));
//                                }else if(error ==403){
//                                    PreferenceStorage.getInstance(getContext()).saveToken("0");
//                                    startActivity(new Intent(getContext(), SplashScreenActivity.class));
//                                    getActivity().finish();
//                                }

                            }
                        }));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SURVEY_ANSWER_REQ && resultCode == RESULT_OK) {

            if (getView() != null) {

                boolean isUserStartSurvey = data.getBooleanExtra("isUserStartSurvey", false);
                String qStatus = data.getStringExtra("qstatus");
                String id = String.valueOf(data.getIntExtra("id", 0));

                if (isUserStartSurvey) {
                    changeSurveyStatus(id, qStatus);
                }
            }
        }
    }

    @Override
    public void onClicked(int survey_id, boolean isExpired, int url_type, String status) {

        if (isSurveyItemClickable) {

            if (isExpired)
                status = getString(R.string.text_expired);

            getSurveyDetails(String.valueOf(survey_id), status, url_type);
            isSurveyItemClickable = !isSurveyItemClickable;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null)
            adapter.setListener(null);
    }

    SurveyFragment.ActiveSurveyInteraction interaction; //survey interaction interface

    public interface ActiveSurveyInteraction {

        void activeSurveyCount(String count);
    }

    @Override
    public void onAttach(Context context) {
        interaction = (SurveyFragment.ActiveSurveyInteraction) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if (interaction != null)
            interaction = null;
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshSurveyEvent(RefreshSurveyEvent event) {

        getUserSurveyHistory();
    }
}


package com.rahbarbazaar.poller.Ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.rahbarbazaar.poller.Controllers.adapters.NewsPagerAdapter;
import com.rahbarbazaar.poller.Controllers.adapters.SurveyRecyclerAdapter;
import com.rahbarbazaar.poller.Controllers.viewHolders.SurveyItemInteraction;
import com.rahbarbazaar.poller.Models.ChangeSurveyStatusResult;
import com.rahbarbazaar.poller.Models.GetNewsListResult;
import com.rahbarbazaar.poller.Models.SurveyMainModel;
import com.rahbarbazaar.poller.Models.UserDetailsPrefrence;
import com.rahbarbazaar.poller.Network.Service;
import com.rahbarbazaar.poller.Network.ServiceProvider;
import com.rahbarbazaar.poller.R;
import com.rahbarbazaar.poller.Ui.activities.HtmlLoaderActivity;
import com.rahbarbazaar.poller.Utilities.CustomHandler;
import com.rahbarbazaar.poller.Utilities.CustomSnackBar;
import com.rahbarbazaar.poller.Utilities.CustomToast;
import com.rahbarbazaar.poller.Utilities.DialogFactory;
import com.rahbarbazaar.poller.Utilities.PreferenceStorage;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements SurveyItemInteraction {


    //region of property and params:
    RecyclerView home_survey_rv;
    RelativeLayout rl_news_pager;
    SurveyRecyclerAdapter adapter;
    List<SurveyMainModel> surveyList;
    SwipeRefreshLayout swipe_refesh;
    CustomHandler handler = new CustomHandler(getActivity());
    Runnable runnable;
    TextView text_balance, text_no_active_survey;
    ViewPager pager;
    int count = -1;
    ServiceProvider provider = null;
    private final static int SURVEY_ANSWER_REQ = 10;
    CompositeDisposable disposable;
    boolean isSurveyItemClickable = true;
    //end of region

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        defineViews(v);
        provider = new ServiceProvider(getContext());
        disposable = new CompositeDisposable();
        getNewsData();
        defineHomeSurveyRecycler();
        getUserLatestSurvey();
        defineViewsListener();

        //calculate height of screen for change dynamically height of layout
        if (getActivity() != null) {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            ((LinearLayout.LayoutParams) rl_news_pager.getLayoutParams()).height = (height) / 3;
        }

        PreferenceStorage storage = PreferenceStorage.getInstance();
        String user_details = storage.retriveUserDetails(getContext());

        if (user_details != null && !user_details.equals("")) {
            UserDetailsPrefrence prefrence = new Gson().fromJson(user_details, UserDetailsPrefrence.class);
            text_balance.setText(new StringBuilder().append("موجودی :").append(" ").append(prefrence.getBalance()).append(" ").append(storage.retriveCurrency(getContext())));
        }

        return v;
    }

    //define view and set some property
    private void defineViews(View view) {
        rl_news_pager = view.findViewById(R.id.rl_news_pager);
        home_survey_rv = view.findViewById(R.id.home_survey_rv);
        pager = view.findViewById(R.id.news_pager);
        text_balance = view.findViewById(R.id.text_balance);
        text_no_active_survey = view.findViewById(R.id.text_no_active_survey);

        swipe_refesh = view.findViewById(R.id.swipe_refresh);
        //set scheme color for swipe refreshing
        swipe_refesh.setColorSchemeResources(R.color.colorPrimary);

    }

    //define views click listener here
    private void defineViewsListener() {

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                count = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        swipe_refesh.setOnRefreshListener(this::getUserLatestSurvey
        );
    }

    //define home page survey recycler view
    private void defineHomeSurveyRecycler() {

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

    //send news request and initial view pager
    private void getNewsData() {

        Service service = new ServiceProvider(getContext()).getmService();
        disposable.add(service.getNewsList().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableSingleObserver<GetNewsListResult>() {
                    @Override
                    public void onSuccess(GetNewsListResult result) {

                        if (result != null) {

                            if (result.getData() != null && result.getData().size() > 0) {

                                NewsPagerAdapter adapter = new NewsPagerAdapter(getChildFragmentManager());
                                adapter.addItems(result.getData());
                                pager.setAdapter(adapter);
                                pager.setOffscreenPageLimit(4);
                                slidShow(adapter);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));
    }

    //show sliding news pager
    private void slidShow(FragmentStatePagerAdapter adapter) {

        runnable = new Runnable() {
            @Override
            public void run() {

                if (count == adapter.getCount()) {

                    count = -1;
                }

                count++;
                pager.setCurrentItem(count);
                handler.postDelayed(this, 5000);
            }
        };
        runnable.run();
    }

    //initial home list and data
    private void initializeHomeSurvey() {

        surveyList = new ArrayList<>();
        adapter = null;
        home_survey_rv.setAdapter(null);
        adapter = new SurveyRecyclerAdapter(surveyList);
        adapter.setListener(this);
        home_survey_rv.setAdapter(adapter);
    }

    //get user latest survey and check response
    private void getUserLatestSurvey() {

        Service service = provider.getmService();
        disposable.add(service.getSurveyLatestList("1").
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<SurveyMainModel>>() {
                    @Override
                    public void onSuccess(List<SurveyMainModel> result) {

                        if (result != null) {

                            initializeHomeSurvey();
                            surveyList.addAll(result);
                            adapter.notifyDataSetChanged();

                            if (surveyList.size() == 0)
                                text_no_active_survey.setVisibility(View.VISIBLE);
                            else
                                text_no_active_survey.setVisibility(View.GONE);

                        } else {

                            text_no_active_survey.setVisibility(View.VISIBLE); // show no survey layout
                        }

                        swipe_refesh.post(() -> swipe_refesh.setRefreshing(false));
                    }

                    @Override
                    public void onError(Throwable e) {

                        swipe_refesh.post(() -> swipe_refesh.setRefreshing(false));

                        if (surveyList == null)
                            text_no_active_survey.setVisibility(View.VISIBLE);
                        else {

                            if (surveyList.size() == 0)
                                text_no_active_survey.setVisibility(View.VISIBLE);
                        }
                    }
                }));

    }

    //get survey details by survey id, if response was ok we will show dialog
    private void getSurveyDetails(String survey_id, String button_status) {

        Service service = new ServiceProvider(getContext()).getmService();

        disposable.add(service.getSurveyDetails(survey_id)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<SurveyMainModel>() {
                    @Override
                    public void onSuccess(SurveyMainModel result) {

                        if (result != null) {

                            showDetailsSurveyDialog(result, button_status);
                        }
                        isSurveyItemClickable = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSurveyItemClickable = true;
                    }
                }));


    }

    //survey details dialog will be appear by this function
    private void showDetailsSurveyDialog(SurveyMainModel result, String button_status) {

        new DialogFactory(getActivity()).createSurveyDetailsDialog(new DialogFactory.DialogFactoryInteraction() {
            @Override
            public void onAcceptButtonClicked(String url) {

                if (!url.equals("")) {

                    //initial intent for html loader activity
                    /**
                     * url : according to website or in better definition survey link
                     * id : survey id
                     * survey details : according to way that we go to html_loader activity
                     */

                    Intent intent = new Intent(getContext(), HtmlLoaderActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("id", result.getId());
                    intent.putExtra("surveyDetails", true);
                    startActivityForResult(intent, SURVEY_ANSWER_REQ);

                    assert getActivity() != null;
                    changeSurveyStatus(String.valueOf(String.valueOf(result.getId())));
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                } else
                    new CustomToast().createToast("آدرس موجود نیست", getContext());
            }

            @Override
            public void onDeniedButtonClicked(boolean bool) {

                //doesn't use here
            }
        }, result, getView(), button_status);
    }

    //after user back to app we have to call change survey status service
    private void changeSurveyStatus(String id) {

        Service service = new ServiceProvider(getContext()).getmService();
        UserDetailsPrefrence user_details = new Gson().fromJson(PreferenceStorage.getInstance().retriveUserDetails(getContext()), UserDetailsPrefrence.class);

        disposable.add(service.changeSurveyStatus(id, user_details.getUser_id(), "2").
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ChangeSurveyStatusResult>() {
                    @Override
                    public void onSuccess(ChangeSurveyStatusResult result) {

                        if (result != null) {

                            if (result.getStatus() != null && !result.getStatus().equals("")) {

                                new CustomSnackBar().showResultSnackbar(getView(), getContext()).show();
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (runnable != null) {

            if (!isVisibleToUser)
                handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SURVEY_ANSWER_REQ && resultCode == RESULT_OK) {

            if (getView() != null) {

                changeSurveyStatus(String.valueOf(data.getIntExtra("id", 0)));
            }
        }
    }

    @Override
    public void onClicked(int survey_id, boolean isExpired) {

        if (isSurveyItemClickable) {
            String button_status = isExpired ? "منقضی شده" : "مشاهده نظرسنجی";
            getSurveyDetails(String.valueOf(survey_id), button_status);
            isSurveyItemClickable = !isSurveyItemClickable;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null)
            adapter.setListener(null);
    }
}

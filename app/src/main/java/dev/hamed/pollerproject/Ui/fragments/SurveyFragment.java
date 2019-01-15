package dev.hamed.pollerproject.Ui.fragments;

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

import java.util.ArrayList;
import java.util.List;

import dev.hamed.pollerproject.Controllers.adapters.SurveyRecyclerAdapter;
import dev.hamed.pollerproject.Controllers.viewHolders.SurveyItemInteraction;
import dev.hamed.pollerproject.Models.ChangeSurveyStatusResult;
import dev.hamed.pollerproject.Models.GetSurveyHistoryListResult;
import dev.hamed.pollerproject.Models.SurveyMainModel;
import dev.hamed.pollerproject.Models.UserDetailsPrefrence;
import dev.hamed.pollerproject.Network.Service;
import dev.hamed.pollerproject.Network.ServiceProvider;
import dev.hamed.pollerproject.R;
import dev.hamed.pollerproject.Ui.activities.HtmlLoaderActivity;
import dev.hamed.pollerproject.Utilities.CustomSnackBar;
import dev.hamed.pollerproject.Utilities.CustomToast;
import dev.hamed.pollerproject.Utilities.DialogFactory;
import dev.hamed.pollerproject.Utilities.PreferenceStorage;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SurveyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveyFragment extends Fragment implements SurveyItemInteraction {

    //property and params region

    RecyclerView home_survey_rv;
    LinearLayout linear_header;
    SurveyRecyclerAdapter adapter;
    TextView text_no_active_survey;
    SwipeRefreshLayout swipe_refesh;
    CompositeDisposable disposable;
    final static int SURVEY_ANSWER_REQ = 12;
    List<SurveyMainModel> surveyList;
    boolean isSurveyItemClickable = true;

    //end of region

    public SurveyFragment() {
        // Required empty public constructor
    }

    public static SurveyFragment newInstance() {
        return new SurveyFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey, container, false);

        defineViews(view);
        configRecyclerview();
        getUserSurveyHistory();

        if (getActivity() != null) {

            //calculate height of screen for change dynamically height of layout
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            ((LinearLayout.LayoutParams) linear_header.getLayoutParams()).height = height / 3;

        }

        return view;
    }

    //define view of fragment and set property
    private void defineViews(View view) {

        home_survey_rv = view.findViewById(R.id.survey_rv);
        linear_header = view.findViewById(R.id.linear_header);
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
        adapter = new SurveyRecyclerAdapter(surveyList);
        adapter.setListener(this);
        home_survey_rv.setAdapter(adapter);
    }

    //get user survey history and check response
    private void getUserSurveyHistory() {

        ServiceProvider provider = new ServiceProvider(getContext());
        Service service = provider.getmService();

        disposable.add(service.getSurveyHistoryList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                        subscribeWith(new DisposableSingleObserver<GetSurveyHistoryListResult>() {
                            @Override
                            public void onSuccess(GetSurveyHistoryListResult result) {

                                if (result != null) {

                                    initializSurveys();
                                    bindHomeSurveys(result);
                                }
                                swipe_refesh.post(() -> swipe_refesh.setRefreshing(false));
                            }

                            @Override
                            public void onError(Throwable e) {

                                swipe_refesh.post(() -> swipe_refesh.setRefreshing(false));
                            }
                        }));
    }

    //bind data of user survey history
    private void bindHomeSurveys(GetSurveyHistoryListResult data) {

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
                if (active_count != 0)
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
    private void getSurveyDetails(String survey_id, String button_status) {

        Service service = new ServiceProvider(getContext()).getmService();

        disposable.add(service.getSurveyDetails(survey_id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                        subscribeWith(new DisposableSingleObserver<SurveyMainModel>() {
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

    //this function will be create details survey dialog
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
                    intent.putExtra("url", result.getUrl());
                    intent.putExtra("id", result.getId());
                    intent.putExtra("surveyDetails", true);
                    startActivityForResult(intent, SURVEY_ANSWER_REQ);
                    assert getActivity() != null;
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

        disposable.add(service.changeSurveyStatus(id, user_details.getUser_id(), "2")
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                        subscribeWith(new DisposableSingleObserver<ChangeSurveyStatusResult>() {
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

    ActiveSurveyInteraction interaction; //survey interaction interface

    public interface ActiveSurveyInteraction {

        void activeSurveyCount(String count);
    }

    @Override
    public void onAttach(Context context) {
        interaction = (ActiveSurveyInteraction) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if (interaction != null)
            interaction = null;
        super.onDetach();
    }
}

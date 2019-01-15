package dev.hamed.pollerproject.Ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dev.hamed.pollerproject.Controllers.adapters.DrawerRecyclerAdapter;
import dev.hamed.pollerproject.Controllers.adapters.MainViewPagerAdapter;
import dev.hamed.pollerproject.Models.GetPagesResult;
import dev.hamed.pollerproject.Models.GetReferralResult;
import dev.hamed.pollerproject.Models.UserDetailsPrefrence;
import dev.hamed.pollerproject.Network.Service;
import dev.hamed.pollerproject.Network.ServiceProvider;
import dev.hamed.pollerproject.R;
import dev.hamed.pollerproject.Ui.fragments.CartFragment;
import dev.hamed.pollerproject.Ui.fragments.HomeFragment;
import dev.hamed.pollerproject.Ui.fragments.ProfileFragment;
import dev.hamed.pollerproject.Ui.fragments.SurveyFragment;
import dev.hamed.pollerproject.Utilities.CustomToast;
import dev.hamed.pollerproject.Utilities.NotSwipeableViewPager;
import dev.hamed.pollerproject.Utilities.PreferenceStorage;
import dev.hamed.pollerproject.Utilities.SolarCalendar;
import dev.hamed.pollerproject.Utilities.TypeFaceGenerator;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, AHBottomNavigation.OnTabSelectedListener,
        SurveyFragment.ActiveSurveyInteraction, DrawerRecyclerAdapter.OnDrawerItemClickListener {

    //region of views

    AHBottomNavigation bottom_navigation;
    ImageView image_drawer;
    DrawerLayout drawer_layout_home;
    NotSwipeableViewPager main_view_pager;
    TextView text_header_date, text_username, text_point;
    LinearLayout linear_invite_friend, linear_exit;
    RecyclerView drawer_rv;

    //end of region

    /*********** *** *** *** *** *** *** ***********/

    //region of property
    CompositeDisposable disposable;
    UserDetailsPrefrence prefrence;
    List<GetPagesResult> pagesList;
    DrawerRecyclerAdapter adapter;
    //end of region

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);
        disposable = new CompositeDisposable();

        //call init methods
        defineViews();
        defineViewsListener();
        configDrawerRv();
        getDrawerPages();
        initializeBottomNavigation();
        initializeViewPager();

        //set current date to header text:
        SolarCalendar calendar = new SolarCalendar();
        StringBuilder builder = new StringBuilder();
        text_header_date.setText(builder.append(calendar.getStrWeekDay()).append(" ").
                append(calendar.getCurrentShamsiDay()).append(" ").append(calendar.getStrMonth()));

        main_view_pager.setOffscreenPageLimit(4);

        PreferenceStorage storage = PreferenceStorage.getInstance();
        String user_details = storage.retriveUserDetails(this);

        if (user_details != null && !user_details.equals("")) {

            prefrence = new Gson().fromJson(user_details, UserDetailsPrefrence.class);
            text_username.setText(prefrence.getName());
            text_point.setText(String.valueOf(prefrence.getBalance()) + " " + storage.retriveCurrency(this));
        }
    }

    //define view of activity here
    private void defineViews() {

        bottom_navigation = findViewById(R.id.bottom_navigation);
        image_drawer = findViewById(R.id.image_drawer);
        drawer_layout_home = findViewById(R.id.drawer_layout_home);
        main_view_pager = findViewById(R.id.main_view_pager);
        linear_invite_friend = findViewById(R.id.linear_invite_friend);
        linear_exit = findViewById(R.id.linear_exit);
        text_header_date = findViewById(R.id.text_header_date);
        drawer_rv = findViewById(R.id.drawer_rv);

        text_username = findViewById(R.id.text_username);
        text_point = findViewById(R.id.text_point);
    }

    //define view of activity click listener here
    private void defineViewsListener() {

        image_drawer.setOnClickListener(this);
        linear_invite_friend.setOnClickListener(this);
        linear_exit.setOnClickListener(this);
        // Set bottom navigation listener
        bottom_navigation.setOnTabSelectedListener(this);

    }

    //initialize bottom navigation will be implement by this function
    private void initializeBottomNavigation() {

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("صفحه اصلی", R.drawable.home);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("نظرسنجی", R.drawable.survey);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("حساب من", R.drawable.my_cart);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("پروفایل", R.drawable.user_login);

        // Add items
        bottom_navigation.addItem(item4);
        bottom_navigation.addItem(item3);
        bottom_navigation.addItem(item2);
        bottom_navigation.addItem(item1);

        // Change colors
        bottom_navigation.setAccentColor(Color.parseColor("#4587b6"));

        // Manage titles
        bottom_navigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);

        // Set current item programmatically
        bottom_navigation.setCurrentItem(3);

        // Set typeFace to bottomNavigation item
        bottom_navigation.setTitleTypeface(TypeFaceGenerator.getInstance().getByekanFont(this));

    }

    //initial main view pager will be implement by this function
    private void initializeViewPager() {

        main_view_pager.setPagingEnabled(false);
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(HomeFragment.newInstance());
        adapter.addFragment(SurveyFragment.newInstance());
        adapter.addFragment(CartFragment.newInstance());
        adapter.addFragment(ProfileFragment.newInstance());

        main_view_pager.setAdapter(adapter);
    }

    //same as method name we will generate invite link and send for sharing app
    private void generateInviteLink() {

        Service service = new ServiceProvider(this).getmService();
        disposable.add(service.getReferral().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableSingleObserver<GetReferralResult>() {
                    @Override
                    public void onSuccess(GetReferralResult result) {

                        if (result != null) {

                            ShareCompat.IntentBuilder
                                    .from(MainActivity.this)
                                    .setText(new StringBuilder().append("شما از طرف").append(prefrence.getName()).
                                            append("برای عضویت در اپلیکیشن نظرسنجی پولر دعوت شدید،درصورت تمایل از طریق لینک زیر اقدام نمایید").
                                            append("\n").append(result.getUrl()))
                                    .setType("text/plain")
                                    .setChooserTitle("اشتراک با دوستان")
                                    .startChooser();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));

    }

    //config drawer recylerView
    private void configDrawerRv() {

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        drawer_rv.setLayoutManager(manager);
        drawer_rv.setHasFixedSize(true);
        drawer_rv.setItemViewCacheSize(5);
        drawer_rv.setItemAnimator(null);
    }


    //initial drawer recycler view
    private void initializePagesList() {

        pagesList = new ArrayList<>();
        adapter = null;
        drawer_rv.setAdapter(null);
        adapter = new DrawerRecyclerAdapter(pagesList);
        adapter.setListener(this);
        drawer_rv.setAdapter(adapter);
    }

    private void getDrawerPages() {

        Service service = new ServiceProvider(this).getmService();


        disposable.add(service.getDrawerPages().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                        subscribeWith(new DisposableSingleObserver<List<GetPagesResult>>() {
                            @Override
                            public void onSuccess(List<GetPagesResult> results) {

                                initializePagesList();

                                if (results != null && results.size() > 0) {

                                    pagesList.addAll(results);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }));

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.image_drawer:
                drawer_layout_home.openDrawer(Gravity.END);
                break;

            case R.id.linear_invite_friend:

                drawer_layout_home.closeDrawers();
                generateInviteLink();
                break;

            case R.id.linear_exit:
                finish();
                break;
        }

    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {

        if (!wasSelected) {
            main_view_pager.setCurrentItem(3 - position, true);

            if (position == 2)
                bottom_navigation.setNotification("", 2);
        }

        return true;
    }

    long EXIT_TIME_NEED = 0;

    @Override
    public void onBackPressed() {

        if (drawer_layout_home.isDrawerOpen(Gravity.END)) {

            drawer_layout_home.closeDrawers();

        } else {

            long current_time = System.currentTimeMillis();

            if (EXIT_TIME_NEED > current_time) {

                finish();
                if (adapter != null)
                    adapter.setListener(null);

            } else {

                new CustomToast().createToast("برای خروج دوباره کلیک کنید", this);
            }
            EXIT_TIME_NEED = current_time + 2000;
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        if (adapter != null)
            adapter.setListener(null);

        //TODO: if you have trouble with shared preference you can delete them when activity destroy
        //getSharedPreferences("currency", Context.MODE_PRIVATE).edit().remove("currency").apply();
        //getSharedPreferences("user_details", Context.MODE_PRIVATE).edit().remove("user_details").apply();
        super.onDestroy();
    }

    @Override
    public void activeSurveyCount(String count) {

        //set active survey badge:
        bottom_navigation.setNotification(count, 2);
    }

    @Override
    public void onDrawerItemClicked(String content) {


        //if drawer item url content was not null so we going to htmLoader activity
        if (content != null && !content.equals("")) {

            Intent intent = new Intent(MainActivity.this, HtmlLoaderActivity.class);
            intent.putExtra("url", content);
            intent.putExtra("surveyDetails", false);
            startActivity(intent);
            MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        } else {

            new CustomToast().createToast("آدرس موجود نیست", MainActivity.this);
        }
    }
}

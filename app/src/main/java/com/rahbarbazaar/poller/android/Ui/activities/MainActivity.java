package com.rahbarbazaar.poller.android.Ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.rahbarbazaar.poller.android.BuildConfig;
import com.rahbarbazaar.poller.android.Controllers.adapters.DrawerRecyclerAdapter;
import com.rahbarbazaar.poller.android.Controllers.adapters.MainViewPagerAdapter;
import com.rahbarbazaar.poller.android.Models.DownloadQueue;
import com.rahbarbazaar.poller.android.Models.GetDownloadResult;
import com.rahbarbazaar.poller.android.Models.GetNotificationListResult;
import com.rahbarbazaar.poller.android.Models.GetPagesResult;
import com.rahbarbazaar.poller.android.Models.GetReferralResult;
import com.rahbarbazaar.poller.android.Models.RefreshBalanceEvent;
import com.rahbarbazaar.poller.android.Models.UserDetailsPrefrence;
import com.rahbarbazaar.poller.android.Network.Service;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Ui.fragments.CartFragment;
import com.rahbarbazaar.poller.android.Ui.fragments.HomeFragment;
import com.rahbarbazaar.poller.android.Ui.fragments.ProfileFragment;
import com.rahbarbazaar.poller.android.Ui.fragments.SurveyFragment;
import com.rahbarbazaar.poller.android.Utilities.CustomToast;
import com.rahbarbazaar.poller.android.Utilities.DialogFactory;
import com.rahbarbazaar.poller.android.Utilities.GeneralTools;
import com.rahbarbazaar.poller.android.Utilities.NotSwipeableViewPager;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;
import com.rahbarbazaar.poller.android.Utilities.SolarCalendar;
import com.rahbarbazaar.poller.android.Utilities.TypeFaceGenerator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import co.ronash.pushe.Pushe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, AHBottomNavigation.OnTabSelectedListener, DialogFactory.DialogFactoryInteraction,
        SurveyFragment.ActiveSurveyInteraction, DrawerRecyclerAdapter.OnDrawerItemClickListener {

    //region of views

    AHBottomNavigation bottom_navigation;
    ImageView image_drawer;
    DrawerLayout drawer_layout_home;
    NotSwipeableViewPager main_view_pager;
    TextView text_header_date, text_username, text_point, text_notify_count;
    LinearLayout linear_invite_friend, linear_exit,
            linear_telegram, linear_instagram, linear_shopping;
    RelativeLayout rl_notification;
    RecyclerView drawer_rv;

    //end of region

    /*********** *** *** *** *** *** *** ***********/

    //region of property

    CompositeDisposable disposable;
    UserDetailsPrefrence prefrence;
    List<GetPagesResult> pagesList;
    DrawerRecyclerAdapter adapter;
    Service service;
    DialogFactory dialogFactory;
    String download_url, update_version = null;
    final int WRITE_PERMISSION_REQUEST = 14;
    final int NOTIFIY_ACTIVITY_REQUEST = 18;
    AlertDialog dialog;
    int selectedTabPosition;
    BroadcastReceiver connectivityReceiver = null;
    //end of region

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initial fresco , pushe services
        Fresco.initialize(this);
        Pushe.initialize(this, true);

        disposable = new CompositeDisposable();
        service = new ServiceProvider(this).getmService();

        //call init methods
        defineViews();
        defineViewsListener();
        configDrawerRv();
        getDrawerPages();
        initializeBottomNavigation();
        initializeViewPager();
        getNotifyCount();

        //initial Dialog factory
        dialogFactory = new DialogFactory(MainActivity.this);

        //set current date to header text:
        SolarCalendar calendar = new SolarCalendar();
        StringBuilder builder = new StringBuilder();
        text_header_date.setText(builder.append(calendar.getStrWeekDay()).append(" ").
                append(calendar.getCurrentShamsiDay()).append(" ").append(calendar.getStrMonth()));

        main_view_pager.setOffscreenPageLimit(4);

        //get user info from preference and initial views
        setInitialUserInformation();

        //check update availbility:
        checkUpdateNeeded();

        //check network broadcast reciever
        GeneralTools tools = GeneralTools.getInstance();
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                tools.doCheckNetwork(MainActivity.this, findViewById(R.id.drawer_layout_home));
            }
        };

        if (!tools.checkPackageInstalled("org.telegram.messenger", this)) {

            linear_telegram.setVisibility(View.GONE);
        }

        if (!tools.checkPackageInstalled("com.instagram.android", this)) {

            linear_instagram.setVisibility(View.GONE);
        }
    }

    //define view of activity here
    private void defineViews() {

        bottom_navigation = findViewById(R.id.bottom_navigation);
        image_drawer = findViewById(R.id.image_drawer);
        drawer_layout_home = findViewById(R.id.drawer_layout_home);
        main_view_pager = findViewById(R.id.main_view_pager);
        linear_invite_friend = findViewById(R.id.linear_invite_friend);
        linear_shopping = findViewById(R.id.linear_shopping);
        rl_notification = findViewById(R.id.rl_notification);
        linear_instagram = findViewById(R.id.linear_instagram);
        linear_telegram = findViewById(R.id.linear_telegram);
        linear_exit = findViewById(R.id.linear_exit);
        text_header_date = findViewById(R.id.text_header_date);
        drawer_rv = findViewById(R.id.drawer_rv);
        text_notify_count = findViewById(R.id.text_notify_count);
        text_username = findViewById(R.id.text_username);
        text_point = findViewById(R.id.text_point);
    }

    //define view of activity click listener here
    private void defineViewsListener() {

        image_drawer.setOnClickListener(this);
        linear_invite_friend.setOnClickListener(this);
        linear_instagram.setOnClickListener(this);
        linear_telegram.setOnClickListener(this);
        linear_shopping.setOnClickListener(this);
        rl_notification.setOnClickListener(this);
        linear_exit.setOnClickListener(this);
        // Set bottom navigation listener
        bottom_navigation.setOnTabSelectedListener(this);

        //declare page change listener for view pager
        /**
         *
         * @param selectedTabPosition
         */
        main_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                selectedTabPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

        disposable.add(service.getReferral().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableSingleObserver<GetReferralResult>() {
                    @Override
                    public void onSuccess(GetReferralResult result) {

                        if (result != null) {

                            ShareCompat.IntentBuilder
                                    .from(MainActivity.this)
                                    .setText(new StringBuilder().append(" شما از طرف ").append(prefrence.getName()).
                                            append(" برای عضویت در اپلیکیشن نظرسنجی پولر دعوت شدید،درصورت تمایل از طریق لینک زیر اقدام نمایید ").
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

    private void checkUpdateNeeded() {

        disposable.add(service.checkUpdate().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableSingleObserver<GetDownloadResult>() {
                    @Override
                    public void onSuccess(GetDownloadResult result) {

                        String current_version = BuildConfig.VERSION_NAME;

                        if (!result.getVersion().equals(current_version)) {

                            //params doesn't useful here
                            download_url = result.getUrl();
                            update_version = result.getVersion();
                            dialog = dialogFactory.createCheckUpdateDialog(drawer_layout_home, MainActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));
    }

    private void downloadApkQeue() {

        JobManager manager = new JobManager(new Configuration.Builder(MainActivity.this).build());
        DownloadQueue downloadQueue = new DownloadQueue();
        downloadQueue.setDownload_url(download_url);
        downloadQueue.setUpdate_version(update_version);
        manager.addJobInBackground(downloadQueue);
    }

    private boolean checkWriteStoragePermission() {

        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
                return false;
            }

        } else
            return true;
    }

    @SuppressLint("SetTextI18n")
    private void setInitialUserInformation() {

        PreferenceStorage storage = PreferenceStorage.getInstance();
        String user_details = storage.retriveUserDetails(this);

        if (user_details != null && !user_details.equals("")) {

            prefrence = new Gson().fromJson(user_details, UserDetailsPrefrence.class);
            text_username.setText(prefrence.getName());
            text_point.setText(String.valueOf(prefrence.getBalance()) + " " + storage.retriveCurrency(this));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            downloadApkQeue();

        } else {

            new CustomToast().createToast("لطفا دسترسی های مورد نیاز برنامه را بدهید و دوباره تلاش کنید", MainActivity.this);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.image_drawer:
                drawer_layout_home.openDrawer(Gravity.END);
                break;

            case R.id.linear_invite_friend:

                drawer_layout_home.closeDrawers();

                if (prefrence.getType().equals("1")) //user guest state
                    dialogFactory.createNoRegisterDialog(drawer_layout_home, MainActivity.this);
                else
                    generateInviteLink();
                break;

            case R.id.linear_exit:
                createConfirmExitDialog();
                break;

            case R.id.linear_instagram:

                Uri uriInstagram = Uri.parse("http://instagram.com/_u/poller.ir");
                Intent intentInstagram = new Intent(Intent.ACTION_VIEW, uriInstagram);
                intentInstagram.setPackage("com.instagram.android");

                try {
                    startActivity(intentInstagram);

                } catch (ActivityNotFoundException e) {

                    e.printStackTrace();
                }
                break;

            case R.id.linear_telegram:

                Uri uriTelegram = Uri.parse("https://t.me/Polleriran");
                Intent intentTelegram = new Intent(Intent.ACTION_VIEW, uriTelegram);
                intentTelegram.setPackage("org.telegram.messenger");

                try {
                    startActivity(intentTelegram);

                } catch (ActivityNotFoundException e) {

                    e.printStackTrace();
                }
                break;

            case R.id.linear_shopping:

                startActivity(new Intent(this, ShopActivity.class));
                break;

            case R.id.rl_notification:

                startActivityForResult(new Intent(this, NotificationActivity.class),NOTIFIY_ACTIVITY_REQUEST);
                break;
        }

    }

    //create confirm exit dialog
    private void createConfirmExitDialog() {

        Context context = MainActivity.this;
        dialogFactory.createConfirmExitDialog(new DialogFactory.DialogFactoryInteraction() {
            @Override
            public void onAcceptButtonClicked(String param) {

                PreferenceStorage.getInstance().saveToken("0", context);

                startActivity(new Intent(context, SplashScreenActivity.class));
                MainActivity.this.finish();
            }

            @Override
            public void onDeniedButtonClicked(boolean bool) {

                //did on dialog factory
            }
        }, drawer_layout_home, false);
    }

    //get notification count by calling this api
    private void getNotifyCount() {

        disposable.add(service.getNotificationList().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableSingleObserver<GetNotificationListResult>() {
                    @Override
                    public void onSuccess(GetNotificationListResult result) {

                        int unReadCount = result.getUnread();

                        if (unReadCount != 0) {

                            text_notify_count.setVisibility(View.VISIBLE);
                            if (unReadCount > 99)
                                text_notify_count.setText("...");
                            else
                                text_notify_count.setText(String.valueOf(unReadCount));

                        } else {

                            text_notify_count.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {


        if (!wasSelected) {

            if (position == 0 && prefrence.getType().equals("1"))
                dialogFactory.createNoRegisterDialog(drawer_layout_home, MainActivity.this);
            else
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
        unregisterReceiver(connectivityReceiver);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==NOTIFIY_ACTIVITY_REQUEST)
            getNotifyCount();
    }

    //this is callback of update dialog:
    @Override
    public void onAcceptButtonClicked(String param) {

        if (checkWriteStoragePermission())
            downloadApkQeue();
        dialog.dismiss();
    }

    @Override
    public void onDeniedButtonClicked(boolean cancel_dialog) {

        //when no register dialog dismissed this callback will be called
        bottom_navigation.setCurrentItem(3 - selectedTabPosition);
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

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefeshBalance(RefreshBalanceEvent event) {

        setInitialUserInformation();

    }
}

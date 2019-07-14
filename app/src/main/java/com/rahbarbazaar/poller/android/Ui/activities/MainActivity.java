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
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.os.ConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.gson.Gson;
import com.rahbarbazaar.poller.android.BuildConfig;
import com.rahbarbazaar.poller.android.Controllers.adapters.DrawerRecyclerAdapter;
import com.rahbarbazaar.poller.android.Controllers.adapters.MainViewPagerAdapter;
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
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
import com.rahbarbazaar.poller.android.Utilities.ClientConfig;
import com.rahbarbazaar.poller.android.Utilities.SnackBarFactory;
import com.rahbarbazaar.poller.android.Utilities.ToastFactory;
import com.rahbarbazaar.poller.android.Utilities.DialogFactory;
import com.rahbarbazaar.poller.android.Utilities.GeneralTools;
import com.rahbarbazaar.poller.android.Utilities.LocaleManager;
import com.rahbarbazaar.poller.android.Utilities.NotSwipeableViewPager;
import com.rahbarbazaar.poller.android.Utilities.PreferenceStorage;
import com.rahbarbazaar.poller.android.Utilities.ProfileTools;
import com.rahbarbazaar.poller.android.Utilities.SolarCalendar;
import com.rahbarbazaar.poller.android.Utilities.TypeFaceGenerator;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.ronash.pushe.Pushe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MainActivity extends CustomBaseActivity implements
        View.OnClickListener, AHBottomNavigation.OnTabSelectedListener, DialogFactory.DialogFactoryInteraction,
        SurveyFragment.ActiveSurveyInteraction, DrawerRecyclerAdapter.OnDrawerItemClickListener {

    //region of views

    AHBottomNavigation bottom_navigation;
    ImageView image_drawer, image_instagram, image_telegram;
    DrawerLayout drawer_layout_home;
    NotSwipeableViewPager main_view_pager;
    TextView text_header_date, text_username, text_point, text_notify_count;
    LinearLayout linear_invite_friend, linear_exit, linear_shopping, linear_notify_drawer, linear_change_lang,
            linear_support, linear_report_issue, linear_faq, linear_videos, linear_submenu, linear_lottery;
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
    final int LOTTERY_ACTIVITY_REGUEST = 20;
    final int SHOP_ACTIVITY_REQUEST = 19;
    AlertDialog dialog;
    boolean isSupportLayoutClicked = false;
    int selectedTabPosition;
    GeneralTools tools;
    BroadcastReceiver connectivityReceiver = null;
    //end of region

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //pushe services
        Pushe.initialize(this, true);

        disposable = new CompositeDisposable();
        service = new ServiceProvider(this).getmService();
        String locale_name = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();

        //call init methods
        configViews();
        configDrawerRv();
        getDrawerPages(locale_name);
        initializeBottomNavigation();
        getNotifyCount();

        GetCurrencyListResult parcelable = getIntent().getParcelableExtra("parcel_data");

        if (parcelable != null && parcelable.getItems() != null && parcelable.getStatus().equalsIgnoreCase("ok"))
            initializeViewPager(parcelable, locale_name);
        else
            SnackBarFactory.getInstance().showLoginIssueSnackbar(findViewById(R.id.app_bar), this, parcelable);

        //initial Dialog factory
        dialogFactory = new DialogFactory(MainActivity.this);

        //set current date to header text:
        setCurrentDate(locale_name);
        main_view_pager.setOffscreenPageLimit(4);

        //get user info from preference and initial views
        setInitialUserInformation();

        //check update availbility:
        checkUpdateNeeded();

        //check network broadcast reciever
        tools = GeneralTools.getInstance();
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                tools.doCheckNetwork(MainActivity.this, findViewById(R.id.drawer_layout_home));
            }
        };

        if (tools.checkPackageInstalled("org.telegram.messenger", this))
            image_telegram.setVisibility(View.INVISIBLE);

        if (tools.checkPackageInstalled("com.instagram.android", this))
            image_instagram.setVisibility(View.INVISIBLE);

    }

    //define view and click listener of activity here
    private void configViews() {

        bottom_navigation = findViewById(R.id.bottom_navigation);
        image_drawer = findViewById(R.id.image_drawer);
        drawer_layout_home = findViewById(R.id.drawer_layout_home);
        main_view_pager = findViewById(R.id.main_view_pager);
        linear_invite_friend = findViewById(R.id.linear_invite_friend);
        linear_shopping = findViewById(R.id.linear_shopping);
        linear_notify_drawer = findViewById(R.id.linear_notify_drawer);
        linear_support = findViewById(R.id.linear_support);
        linear_report_issue = findViewById(R.id.linear_report_issue);
        linear_change_lang = findViewById(R.id.linear_change_lang);
        linear_lottery = findViewById(R.id.linear_lottery);
        linear_faq = findViewById(R.id.linear_faq);
        linear_videos = findViewById(R.id.linear_videos);
        linear_submenu = findViewById(R.id.linear_submenu);
        rl_notification = findViewById(R.id.rl_notification);
        image_instagram = findViewById(R.id.image_instagram);
        image_telegram = findViewById(R.id.image_telegram);
        linear_exit = findViewById(R.id.linear_exit);
        text_header_date = findViewById(R.id.text_header_date);
        drawer_rv = findViewById(R.id.drawer_rv);
        text_notify_count = findViewById(R.id.text_notify_count);
        text_username = findViewById(R.id.text_username);
        //text_point = findViewById(R.id.text_point);

        image_drawer.setOnClickListener(this);
        image_instagram.setOnClickListener(this);
        image_telegram.setOnClickListener(this);
        linear_shopping.setOnClickListener(this);
        rl_notification.setOnClickListener(this);
        linear_exit.setOnClickListener(this);
        linear_faq.setOnClickListener(this);
        linear_videos.setOnClickListener(this);
        linear_lottery.setOnClickListener(this);
        linear_support.setOnClickListener(this);
        linear_change_lang.setOnClickListener(this);
        linear_report_issue.setOnClickListener(this);
        linear_invite_friend.setOnClickListener(this);
        linear_notify_drawer.setOnClickListener(this);
        // Set bottom navigation listener
        bottom_navigation.setOnTabSelectedListener(this);

        //declare page change listener for view pager
        /**
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
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.home_tab, R.drawable.home, 0);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.survey_tab, R.drawable.survey, 0);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.transaction_tab, R.drawable.my_cart, 0);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.profile_tab, R.drawable.user_login, 0);

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

    private void setCurrentDate(String locale_name) {

        SolarCalendar calendar = new SolarCalendar();
        StringBuilder builder = new StringBuilder();

        if (locale_name.equals("fa")) {

            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/BYekan.ttf");
            text_header_date.setTypeface(font);
            text_header_date.setText(builder.append(calendar.getStrWeekDay()).append(" ").
                    append(calendar.getCurrentShamsiDay()).append(" ").append(calendar.getStrMonth()));
        } else {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            String formatDate = df.format(c);
            text_header_date.setText(formatDate);
        }
    }

    //initial main view pager will be implement by this function
    private void initializeViewPager(GetCurrencyListResult parcelable, String locale) {

        main_view_pager.setPagingEnabled(false);
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(HomeFragment.newInstance(parcelable, locale));
        adapter.addFragment(SurveyFragment.newInstance(parcelable,locale));
        adapter.addFragment(CartFragment.newInstance(parcelable, locale));
        adapter.addFragment(ProfileFragment.Companion.createInstance(parcelable, locale));

        main_view_pager.setAdapter(adapter);
    }

    //same as method name we will generate invite link and send for sharing app
    private void generateInviteLink() {

        disposable.add(service.getReferral(ClientConfig.API_V1).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableSingleObserver<GetReferralResult>() {
                    @Override
                    public void onSuccess(GetReferralResult result) {

                        if (result != null) {

                            ShareCompat.IntentBuilder
                                    .from(MainActivity.this)
                                    .setText(new StringBuilder().append(getString(R.string.text_invite_from)).append(" ").append(prefrence.getName()).append(" ").
                                            append(getString(R.string.text_invite_friend)).
                                            append("\n").append(result.getUrl()))
                                    .setType("text/plain")
                                    .setChooserTitle(R.string.share_poller)
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

    private void getDrawerPages(String locale_name) {

        Service service = new ServiceProvider(this).getmService();
        disposable.add(service.getDrawerPages(ClientConfig.API_V2,
                locale_name.equalsIgnoreCase("en") ? "en" : "fa").
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
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

        disposable.add(service.checkUpdate(ClientConfig.API_V1).
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

        //JobManager manager = new JobManager(new Configuration.Builder(MainActivity.this).build());
        DownloadQueue downloadQueue = new DownloadQueue();
        downloadQueue.setDownload_url(download_url);
        downloadQueue.setUpdate_version(update_version);
        //manager.addJobInBackground(downloadQueue);
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

        PreferenceStorage storage = PreferenceStorage.getInstance(this);
        String user_details = storage.retriveUserDetails();

        if (user_details != null && !user_details.equals("")) {

            prefrence = new Gson().fromJson(user_details, UserDetailsPrefrence.class);
            text_username.setText(prefrence.getName());
            Log.e("main_log", "msg score : " + prefrence.getSum_score());
            //text_point.setText(prefrence.getBalance() + " " + storage.retriveCurrency());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //downloadApkQeue();

        } else {

            new ToastFactory().createToast("لطفا دسترسی های مورد نیاز برنامه را بدهید و دوباره تلاش کنید", MainActivity.this);
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

                if (prefrence != null && prefrence.getType().equals("1")) //user guest state
                    dialogFactory.createNoRegisterDialog(drawer_layout_home, MainActivity.this);
                else
                    generateInviteLink();
                break;

            case R.id.linear_exit:
                createConfirmExitDialog();
                break;

            case R.id.image_instagram:

                Uri uriInstagram = Uri.parse("http://instagram.com/_u/poller.ir");
                Intent intentInstagram = new Intent(Intent.ACTION_VIEW, uriInstagram);
                intentInstagram.setPackage("com.instagram.android");

                try {
                    startActivity(intentInstagram);

                } catch (ActivityNotFoundException e) {

                    e.printStackTrace();
                }
                break;

            case R.id.image_telegram:

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

                startActivityForResult(new Intent(this, ShopActivity.class), SHOP_ACTIVITY_REQUEST);
                MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

            case R.id.rl_notification:
            case R.id.linear_notify_drawer: {

                startActivityForResult(new Intent(this, NotificationActivity.class), NOTIFIY_ACTIVITY_REQUEST);
                MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            }

            case R.id.linear_support:

                if (!isSupportLayoutClicked) {

                    tools.expand(linear_submenu);
                } else
                    tools.collapse(linear_submenu);

                isSupportLayoutClicked = !isSupportLayoutClicked;
                break;

            case R.id.linear_faq:

                goToHtmlActivity("http://pollerws.rahbarbazaar.com:2296/poller/v2/support/faq/" + LocaleManager.getLocale(getResources()).getLanguage(), true);
                break;

            case R.id.linear_report_issue:
                dialogFactory.createReportIssueDialog(new DialogFactory.DialogFactoryInteraction() {
                    @Override
                    public void onAcceptButtonClicked(String... params) {

                        sendReportIssueRequest(params[0], params[1]);
                    }

                    @Override
                    public void onDeniedButtonClicked(boolean cancel_dialog) {

                    }
                }, drawer_layout_home);
                break;

            case R.id.linear_videos:
                goToHtmlActivity("http://pollerws.rahbarbazaar.com:2296/poller/v2/support/videos/" + LocaleManager.getLocale(getResources()).getLanguage(), true);
                break;

            case R.id.linear_change_lang:
                dialogFactory.createSelectLangDialog(drawer_layout_home, new DialogFactory.DialogFactoryInteraction() {
                    @Override
                    public void onAcceptButtonClicked(String... strings) {

                        LocaleManager.setNewLocale(MainActivity.this, "fa");
                        Intent i = new Intent(MainActivity.this, SplashScreenActivity.class);
                        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        System.exit(0);
                    }

                    @Override
                    public void onDeniedButtonClicked(boolean cancel_dialog) {
                        LocaleManager.setNewLocale(MainActivity.this, "en");
                        Intent i = new Intent(MainActivity.this, SplashScreenActivity.class);
                        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        System.exit(0);
                    }
                });
                break;

            case R.id.linear_lottery:
                startActivityForResult(new Intent(this, LotteryActivity.class), LOTTERY_ACTIVITY_REGUEST);
                MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

        }

    }

    //create confirm exit dialog
    private void createConfirmExitDialog() {

        Context context = MainActivity.this;
        dialogFactory.createConfirmExitDialog(new DialogFactory.DialogFactoryInteraction() {
            @Override
            public void onAcceptButtonClicked(String... params) {

                PreferenceStorage.getInstance(context).saveToken("0");

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

        disposable.add(service.getNotificationList(ClientConfig.API_V1).
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

    private void sendReportIssueRequest(String t, String d) {

        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), t);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), d);

        disposable.add(service.reportIssues(ClientConfig.API_V1, title, description).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableCompletableObserver() {

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                }));
    }

    private void goToHtmlActivity(String url, boolean shouldBeLoadUrl) {

        Intent intent = new Intent(MainActivity.this, HtmlLoaderActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("surveyDetails", false);
        intent.putExtra("isShopping", shouldBeLoadUrl);
        startActivity(intent);
        MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {

        if (!wasSelected) {

            if (position == 0 && prefrence != null && prefrence.getType().equals("1"))
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

                new ToastFactory().createToast(R.string.text_double_click_exit, this);
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

            goToHtmlActivity(content, false);
            MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        } else {

            new ToastFactory().createToast(R.string.text_no_address, MainActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {

            case NOTIFIY_ACTIVITY_REQUEST:
                getNotifyCount();
                break;

            case LOTTERY_ACTIVITY_REGUEST:
                if (resultCode != RESULT_OK)
                    break;

            case SHOP_ACTIVITY_REQUEST:

                ProfileTools.getInstance().saveProfileInformation(this).setListener(() ->
                        EventBus.getDefault().post(new RefreshBalanceEvent())
                );
                break;
        }

    }

    //this is callback of update dialog:
    @Override
    public void onAcceptButtonClicked(String... params) {

        if (checkWriteStoragePermission())
            //downloadApkQeue();
            dialog.dismiss();
    }

    @Override
    public void onDeniedButtonClicked(boolean cancel_dialog) {

        //when no register dialog dismissed this callback will be called
        bottom_navigation.setCurrentItem(3 - selectedTabPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
}

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
import android.os.Handler;
import android.os.Process;
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
import android.widget.Toast;

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
import com.rahbarbazaar.poller.android.Models.eventbus.ModelActiveSurveyCount;
import com.rahbarbazaar.poller.android.Network.Service;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Ui.fragments.CartFragment1;
import com.rahbarbazaar.poller.android.Ui.fragments.HomeFragment1;
import com.rahbarbazaar.poller.android.Ui.fragments.ProfileFragment1;
import com.rahbarbazaar.poller.android.Ui.fragments.SurveyFragment1;
import com.rahbarbazaar.poller.android.Utilities.App;
import com.rahbarbazaar.poller.android.Utilities.ClientConfig;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class MainActivity extends CustomBaseActivity implements
        View.OnClickListener, AHBottomNavigation.OnTabSelectedListener, DialogFactory.DialogFactoryInteraction,
        SurveyFragment1.ActiveSurveyInteraction, DrawerRecyclerAdapter.OnDrawerItemClickListener,
        HomeFragment1.ActiveSurveysInteraction {

    //region of views
    AHBottomNavigation bottom_navigation;
    ImageView image_drawer, image_instagram, image_telegram, img_backbtmbar_left, img_backbtmbar_centerleft, img_backbtmbar_centerright, img_backbtmbar_right, img_arrow;
    ;
    DrawerLayout drawer_layout_home;
    NotSwipeableViewPager main_view_pager;
    TextView text_header_date, text_username, text_point, text_notify_count, text_follow_us;
    LinearLayout linear_invite_friend, linear_exit, linear_shopping, linear_notify_drawer, linear_change_lang,
            linear_support, linear_report_issue, linear_faq, linear_videos, linear_submenu, linear_lottery, ll_drawer;
    RelativeLayout rl_notification, rl_curvedbottom;
    LinearLayout ll_notify_count;
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
    String download_url, update_version = null, locale_name, bazzar_url;
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

    int a = 0;



    boolean doubleBackToExitPressedOnce = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        //pushe services
        Pushe.initialize(this, true);
        String pusheId = Pushe.getPusheId(MainActivity.this);

        EventBus.getDefault().register(this);

        disposable = new CompositeDisposable();
        service = new ServiceProvider(this).getmService();
        locale_name = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();

        //call init methods
        configViews();
        configDrawerRv();
        getDrawerPages(locale_name);
        initializeBottomNavigation();


        if (locale_name.equals("fa")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            ll_drawer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (locale_name.equals("en")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            ll_drawer.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }


        getNotifyCount();

        GetCurrencyListResult parcelable = getIntent().getParcelableExtra("parcel_data");

        if (parcelable != null && parcelable.getItems() != null && parcelable.getStatus().equalsIgnoreCase("ok"))
            initializeViewPager(parcelable, locale_name);
        else {
//            SnackBarFactory.getInstance().showLoginIssueSnackbar(findViewById(R.id.app_bar), this, parcelable);
        }
//


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

        if (tools.checkPackageInstalled("org.telegram.messenger", this)) {
            image_telegram.setVisibility(View.INVISIBLE);
        }


        if (tools.checkPackageInstalled("com.instagram.android", this)) {
            image_instagram.setVisibility(View.INVISIBLE);
        }


        if (tools.checkPackageInstalled("org.telegram.messenger", this)) { //no telegram
            if (tools.checkPackageInstalled("com.instagram.android", this)) { // no instagram
                text_follow_us.setVisibility(View.INVISIBLE);
            }
        }
        if (tools.checkPackageInstalled("com.instagram.android", this)) { //no instagram
            if (tools.checkPackageInstalled("org.telegram.messenger", this)) { // no telegram
                text_follow_us.setVisibility(View.INVISIBLE);
            }
        }


//        if(check_package){
//            text_follow_us.setVisibility(View.VISIBLE);
//        }else{
//            text_follow_us.setVisibility(View.VISIBLE);
//        }


        if (locale_name.equals("fa"))
            img_arrow.setImageResource(R.drawable.arrow_left);
        else
            img_arrow.setImageResource(R.drawable.arrow_right);


        if (prefrence != null && prefrence.getType().equals("1")) {
            linear_invite_friend.setVisibility(View.GONE);
        } else {
            linear_invite_friend.setVisibility(View.VISIBLE);
        }



    }

    //define view and click listener of activity here
    private void configViews() {


        img_backbtmbar_left = findViewById(R.id.img_backbtmbar_left);
        img_backbtmbar_centerleft = findViewById(R.id.img_backbtmbar_centerleft);
        img_backbtmbar_centerright = findViewById(R.id.img_backbtmbar_centerright);
        img_backbtmbar_right = findViewById(R.id.img_backbtmbar_right);


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
        text_follow_us = findViewById(R.id.text_follow_us);
        img_arrow = findViewById(R.id.img_arrow);
        ll_drawer = findViewById(R.id.ll_drawer);
        rl_curvedbottom = findViewById(R.id.rl_curvedbottom);
        ll_notify_count = findViewById(R.id.ll_notify_count);
        //text_point = findViewById(R.id.text_point);

        image_drawer.setOnClickListener(this);
        image_instagram.setOnClickListener(this);
        image_telegram.setOnClickListener(this);
        linear_shopping.setOnClickListener(this);
        rl_notification.setOnClickListener(this);
        linear_exit.setOnClickListener(this);
        linear_faq.setOnClickListener(this);
//        linear_videos.setOnClickListener(this);
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

    //    initialize bottom navigation will be implement by this function
    private void initializeBottomNavigation() {

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.home_tab, R.drawable.home1, 0);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.survey_tab, R.drawable.survey1, 0);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.transaction_tab, R.drawable.my_cart1, 0);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.profile_tab, R.drawable.user_login1, 0);

        // Add items
        bottom_navigation.addItem(item4);
        bottom_navigation.addItem(item3);
        bottom_navigation.addItem(item2);
        bottom_navigation.addItem(item1);


        // Change colors
//        bottom_navigation.setAccentColor(Color.parseColor("#4587b6"));

        bottom_navigation.setAccentColor(Color.parseColor("#fff200"));
        bottom_navigation.setInactiveColor(Color.parseColor("#FFFFFF"));

        bottom_navigation.setDefaultBackgroundResource(R.drawable.bg_toolbar1);


        //requred api level min 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bottom_navigation.setElevation(0f);
        }

        // Manage titles
        bottom_navigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

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

//        adapter.addFragment(HomeFragment.newInstance(parcelable, locale));
        adapter.addFragment(HomeFragment1.newInstance(parcelable, locale));
//        adapter.addFragment(SurveyFragment.newInstance(parcelable, locale));
        adapter.addFragment(SurveyFragment1.newInstance(parcelable, locale));
//        adapter.addFragment(CartFragment.newInstance(parcelable, locale));
        adapter.addFragment(CartFragment1.newInstance(parcelable, locale));
//        adapter.addFragment(ProfileFragment.Companion.createInstance(parcelable, locale));
        adapter.addFragment(ProfileFragment1.Companion.createInstance(parcelable, locale));

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
//                                            append("\n").append(result.getUrl()))
                                            append("\n")
//                                            .append(bazzar_url))
//                                            .append("https://cafebazaar.ir/app/com.rahbarbazaar.poller.android/?l=fa"))
//                                            .append("https://myket.ir/app/com.rahbarbazaar.poller.android?lang=en"))
                                            .append("https://play.google.com/store/apps/details?id=com.rahbarbazaar.poller.android"))
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

//                        String current_version = BuildConfig.VERSION_NAME;
                        int current_version = BuildConfig.VERSION_CODE;
                        int min_version = Integer.parseInt(result.getForce_update());
                        int server_version = Integer.parseInt(result.getVersion());

                        bazzar_url = result.getBazaar_url();

//                        if (!result.getVersion().equals(current_version)) {
//
//                            //params doesn't useful here
//                            download_url = result.getUrl();
//                            update_version = result.getVersion();
////                            dialog = dialogFactory.createCheckUpdateDialog(drawer_layout_home, MainActivity.this);
//                            dialog = dialogFactory.createCheckUpdateDialog(drawer_layout_home, MainActivity.this);
//                        }

                        if (current_version < min_version) {
//                        if (current_version < 17) {
//                            Toast.makeText(MainActivity.this, "force", Toast.LENGTH_SHORT).show();
                            download_url = result.getUrl();
                            update_version = result.getVersion();
//                            dialog = dialogFactory.createCheckUpdateDialog(drawer_layout_home, MainActivity.this);
                            dialog = dialogFactory.createCheckUpdateDialog(drawer_layout_home, MainActivity.this);
                        }

                        if (current_version >= min_version && current_version < server_version) {
//                        if (current_version > min_version && current_version < 17) {
                            download_url = result.getUrl();
                            update_version = result.getVersion();
//                            Toast.makeText(MainActivity.this, "optional", Toast.LENGTH_SHORT).show();
                            dialog = dialogFactory.createCheckUpdateOptionalDialog(drawer_layout_home, MainActivity.this);

                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
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

//                if (App.balance>= 1000) {
//                    linear_shopping.setVisibility(View.VISIBLE);
//                }else{
//                    linear_shopping.setVisibility(View.GONE);
//                }

                break;

            case R.id.linear_invite_friend:

                drawer_layout_home.closeDrawers();

                if (prefrence != null && prefrence.getType().equals("1")) //user guest state
                    dialogFactory.createNoRegisterDialog1(drawer_layout_home, MainActivity.this);
                else
                    generateInviteLink();
                break;


            case R.id.linear_exit:
                createConfirmExitDialog();
                break;

            case R.id.image_instagram:

                drawer_layout_home.closeDrawers();
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
                drawer_layout_home.closeDrawers();
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
                drawer_layout_home.closeDrawers();
//                startActivityForResult(new Intent(this, ShopActivity.class), SHOP_ACTIVITY_REQUEST);
                startActivityForResult(new Intent(this, ShopActivity1.class), SHOP_ACTIVITY_REQUEST);
                MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

            case R.id.rl_notification:
            case R.id.linear_notify_drawer: {
                drawer_layout_home.closeDrawers();
//                startActivityForResult(new Intent(this, NotificationActivity.class), NOTIFIY_ACTIVITY_REQUEST);
                startActivityForResult(new Intent(this, NotificationActivity1.class), NOTIFIY_ACTIVITY_REQUEST);
                MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            }

            case R.id.linear_support:

                if (!isSupportLayoutClicked) {
                    tools.expand(linear_submenu);
                    img_arrow.setImageResource(R.drawable.arrow_down);
                } else {
                    tools.collapse(linear_submenu);
                    if (locale_name.equals("fa"))
                        img_arrow.setImageResource(R.drawable.arrow_left);
                    else
                        img_arrow.setImageResource(R.drawable.arrow_right);

                }


                isSupportLayoutClicked = !isSupportLayoutClicked;
                break;

            case R.id.linear_faq:
                drawer_layout_home.closeDrawers();
//                goToHtmlActivity("http://pollerws.rahbarbazaar.com:2296/poller/v2/support/faq/" + LocaleManager.getLocale(getResources()).getLanguage(), true);
                goToHtmlActivity(ClientConfig.HTML_ADDRESS+"v2/support/faq/" + LocaleManager.getLocale(getResources()).getLanguage(),true);
//                goToHtmlActivity("https://test.rahbarbazar.com/poller/v2/support/faq/" + LocaleManager.getLocale(getResources()).getLanguage(), true);
                break;

            case R.id.linear_report_issue:
//                drawer_layout_home.closeDrawers();
                dialogFactory.createReportIssueDialog(new DialogFactory.DialogFactoryInteraction() {
                    @Override
                    public void onAcceptButtonClicked(String... params) {

                        if(params[0].equals("") || params[1].equals("")){
                            Toast.makeText(MainActivity.this, R.string.empetyReportIssue, Toast.LENGTH_SHORT).show();
                        }else{
                            sendReportIssueRequest(params[0], params[1]);
                            drawer_layout_home.closeDrawer(Gravity.END);
                        }

                    }

                    @Override
                    public void onDeniedButtonClicked(boolean cancel_dialog) {
                        drawer_layout_home.closeDrawer(Gravity.END);
                    }
                }, drawer_layout_home);
                break;

//            case R.id.linear_videos:
//                goToHtmlActivity(App.videoWebUrl+"/" + LocaleManager.getLocale(getResources()).getLanguage(), true);
//                break;

            case R.id.linear_change_lang:
                dialogFactory.createSelectLangDialog(drawer_layout_home, new DialogFactory.DialogFactoryInteraction() {
                    @Override
                    public void onAcceptButtonClicked(String... strings) {

                        if (locale_name.equals("fa")) {
                            drawer_layout_home.closeDrawer(Gravity.END);
                        } else {
                            LocaleManager.setNewLocale(MainActivity.this, "fa");
//                        App.language = "fa";
                            Intent i = new Intent(MainActivity.this, SplashScreenActivity1.class);
                            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            System.exit(0);
                        }

                    }

                    @Override
                    public void onDeniedButtonClicked(boolean cancel_dialog) {

                        if (locale_name.equals("en")) {
                            drawer_layout_home.closeDrawer(Gravity.END);
                        } else {
                            LocaleManager.setNewLocale(MainActivity.this, "en");
//                        App.language = "en";
                            Intent i = new Intent(MainActivity.this, SplashScreenActivity1.class);
                            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            System.exit(0);
                        }

                    }
                });
                break;

            case R.id.linear_lottery:
                drawer_layout_home.closeDrawers();
//                startActivityForResult(new Intent(this, LotteryActivity.class), LOTTERY_ACTIVITY_REGUEST);
                startActivityForResult(new Intent(MainActivity.this, LotteryActivity1.class), LOTTERY_ACTIVITY_REGUEST);
                MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

        }

    }


    //create confirm exit dialog
    private void createConfirmExitDialog() {

        Context context = MainActivity.this;
        dialogFactory.createConfirmExitDialog2(new DialogFactory.DialogFactoryInteraction() {
            @Override
            public void onAcceptButtonClicked(String... params) {

                drawer_layout_home.closeDrawers();
                PreferenceStorage.getInstance(context).saveToken("0");
                startActivity(new Intent(context, SplashScreenActivity1.class));
                MainActivity.this.finish();
            }

            @Override
            public void onDeniedButtonClicked(boolean bool) {
                drawer_layout_home.closeDrawers();

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
                            ll_notify_count.setVisibility(View.VISIBLE);
                            if (unReadCount > 99) {
                                text_notify_count.setText("...");

                                //app icon badge
                                ShortcutBadger.applyCount(MainActivity.this, unReadCount); //for 1.1.4+
//                            ShortcutBadger.with(getApplicationContext()).count(badgeCount); //for 1.1.3
                            }
                            else{
                                text_notify_count.setText(String.valueOf(unReadCount));
                                //app icon badge
                                ShortcutBadger.applyCount(MainActivity.this, unReadCount); //for 1.1.4+
                            }


                        } else {

                            ShortcutBadger.removeCount(MainActivity.this);
                            text_notify_count.setVisibility(View.GONE);
                            ll_notify_count.setVisibility(View.GONE);
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
                        new ToastFactory().createToast(R.string.text_request_submitted, MainActivity.this);
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


        if (locale_name.equals("fa")) {
            if (position == 3) {
                img_backbtmbar_right.setVisibility(View.VISIBLE);
                img_backbtmbar_centerleft.setVisibility(View.GONE);
                img_backbtmbar_centerright.setVisibility(View.GONE);
                img_backbtmbar_left.setVisibility(View.GONE);
            } else if (position == 2) {
                img_backbtmbar_right.setVisibility(View.GONE);
                img_backbtmbar_centerleft.setVisibility(View.GONE);
                img_backbtmbar_centerright.setVisibility(View.VISIBLE);
                img_backbtmbar_left.setVisibility(View.GONE);
            } else if (position == 1) {
                img_backbtmbar_right.setVisibility(View.GONE);
                img_backbtmbar_centerleft.setVisibility(View.VISIBLE);
                img_backbtmbar_centerright.setVisibility(View.GONE);
                img_backbtmbar_left.setVisibility(View.GONE);
            } else if (position == 0) {
                img_backbtmbar_right.setVisibility(View.GONE);
                img_backbtmbar_centerleft.setVisibility(View.GONE);
                img_backbtmbar_centerright.setVisibility(View.GONE);
                img_backbtmbar_left.setVisibility(View.VISIBLE);
            }
        } else if (locale_name.equals("en")) {
            if (position == 3) {
                img_backbtmbar_right.setVisibility(View.GONE);
                img_backbtmbar_centerleft.setVisibility(View.GONE);
                img_backbtmbar_centerright.setVisibility(View.GONE);
                img_backbtmbar_left.setVisibility(View.VISIBLE);
            } else if (position == 2) {
                img_backbtmbar_right.setVisibility(View.GONE);
                img_backbtmbar_centerleft.setVisibility(View.VISIBLE);
                img_backbtmbar_centerright.setVisibility(View.GONE);
                img_backbtmbar_left.setVisibility(View.GONE);
            } else if (position == 1) {
                img_backbtmbar_right.setVisibility(View.GONE);
                img_backbtmbar_centerleft.setVisibility(View.GONE);
                img_backbtmbar_centerright.setVisibility(View.VISIBLE);
                img_backbtmbar_left.setVisibility(View.GONE);
            } else if (position == 0) {
                img_backbtmbar_right.setVisibility(View.VISIBLE);
                img_backbtmbar_centerleft.setVisibility(View.GONE);
                img_backbtmbar_centerright.setVisibility(View.GONE);
                img_backbtmbar_left.setVisibility(View.GONE);
            }
        }


//        if (!wasSelected) { //remove this clause bacause of onclick in home fragment

//        if (position == 0 && prefrence != null && prefrence.getType().equals("1")){
//                        dialogFactory.createNoRegisterDialog(drawer_layout_home, MainActivity.this);
//        }
//        else
        main_view_pager.setCurrentItem(3 - position, true);


        if (position == 2) {
//            bottom_navigation.setNotification("", 2);
        }


        //to remove bug active bottombar when we choose from fragment home by touching imageview
        if (position == 0 || position == 1 || position == 3) {
            a = 0;
        }
        if (position == 2) {
            if (a == 0) {
                a++;
                bottom_navigation.setCurrentItem(2);
            }
        }

        return true;
    }

    long EXIT_TIME_NEED = 0;

    @Override
    public void onBackPressed() {


        if (drawer_layout_home.isDrawerOpen(Gravity.END)) {
            drawer_layout_home.closeDrawers();
        }else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();

                exitApp();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.text_double_click_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }



//        if (drawer_layout_home.isDrawerOpen(Gravity.END)) {
//
//            drawer_layout_home.closeDrawers();
//
//        } else {
//
//            long current_time = System.currentTimeMillis();
//
//            if (EXIT_TIME_NEED > current_time) {
//
//                finish();
//                if (adapter != null)
//                    adapter.setListener(null);
//
//            } else {
//
//                new ToastFactory().createToast(R.string.text_double_click_exit, this);
//            }
//            EXIT_TIME_NEED = current_time + 2000;
//        }
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

////        set active survey badge:
//        bottom_navigation.setNotification(count, 2);
    }


    @Override
    public void activeSurveysCount(String count) {
        //        set active survey badge:
// for set badge in persian
        if (locale_name.equals("fa")) {
            count = count.replace("1", "١")
                    .replace("2", "٢")
                    .replace("3", "٣")
                    .replace("4", "۴")
                    .replace("5", "۵")
                    .replace("6", "۶")
                    .replace("7", "۷")
                    .replace("8", "۸")
                    .replace("9", "۹");
            bottom_navigation.setNotification(count, 2);
        } else {
            bottom_navigation.setNotification(count, 2);
        }


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
        drawer_layout_home.closeDrawer(Gravity.END);
    }


//    public void replaceFragment(Fragment fragment){
//        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
//        t.replace(R.id.main_view_pager, fragment).addToBackStack(null);
//        t.commit();
//    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ModelActiveSurveyCount modelActiveSurveyCount) {

        String activeCount = modelActiveSurveyCount.getActiveSurveyCount();
        if (locale_name.equals("fa")) {
            if(!activeCount.equals("0")){
                activeCount = activeCount.replace("1", "١")
                        .replace("2", "٢")
                        .replace("3", "٣")
                        .replace("4", "۴")
                        .replace("5", "۵")
                        .replace("6", "۶")
                        .replace("7", "۷")
                        .replace("8", "۸")
                        .replace("9", "۹");
                bottom_navigation.setNotification(activeCount, 2);
            }

        } else {
            if(!activeCount.equals("0")){
                bottom_navigation.setNotification(activeCount, 2);
            }

        }

    }


    private void exitApp() {
        finish();
        startActivity(new Intent(Intent.ACTION_MAIN).
                addCategory(Intent.CATEGORY_HOME).
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
        Process.killProcess(Process.myPid());
        super.finish();
    }
}

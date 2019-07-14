package com.rahbarbazaar.poller.android.Ui.activities;

/*
public class AppIntroActivity extends AppCompatActivity {


    int current_pos = 0;
    BroadcastReceiver connectivityReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);

        final ViewPager pager = findViewById(R.id.intro_pager);
        List<Integer> items = new ArrayList<Integer>() {{

            add(R.drawable.poller_icon);
            add(R.drawable.poller_icon);
            add(R.drawable.poller_icon);
        }};

        final IntroPagerAdapter adapter = new IntroPagerAdapter(getSupportFragmentManager());
        adapter.addAllImages(items);
        pager.setAdapter(adapter);

        final TextView txt_next = findViewById(R.id.button_login);
        txt_next.setOnClickListener(v -> {

            if (adapter.getCount() - 1 == current_pos) {

                startActivity(new Intent(AppIntroActivity.this, LoginActivity.class));
                AppIntroActivity.this.finish();
            } else {

                current_pos++;
                if (current_pos == 2) {
                    txt_next.setText("ورود");
                }
                pager.setCurrentItem(current_pos);
            }
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                current_pos = position;
                if (position < 2)
                    txt_next.setText("بعدی");
                if (position == 2)
                    txt_next.setText("ورود");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //check network broadcast reciever
        GeneralTools tools = GeneralTools.getInstance();
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                tools.doCheckNetwork(AppIntroActivity.this, findViewById(R.id.linear_root));
            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(connectivityReceiver);
        super.onDestroy();
    }
}
*/

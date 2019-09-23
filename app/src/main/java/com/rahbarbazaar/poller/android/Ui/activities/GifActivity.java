package com.rahbarbazaar.poller.android.Ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult;
import com.rahbarbazaar.poller.android.R;

public class GifActivity extends AppCompatActivity {

    ImageView img_gif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);


        img_gif = findViewById(R.id.img_gif);

        showGif();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }

                runOnUiThread(() -> gotoMainActivity());
            }
        };
        thread.start(); //start the thread
    }

    private void gotoMainActivity() {
        GetCurrencyListResult parcelable = getIntent().getParcelableExtra("parcel_data");
        Intent intent = new Intent(GifActivity.this, MainActivity.class);
        intent.putExtra("parcel_data", parcelable);
        startActivity(intent);
        GifActivity.this.finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    private void showGif() {

        Glide.with(GifActivity.this)
                .asGif()
                .load(R.drawable.gif_hq)
                .placeholder(R.drawable.gif_hq)
                .into(img_gif);
    }


}

package com.rahbarbazaar.poller.android.Utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.os.ConfigurationCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
        textTypeFace();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textTypeFace();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        textTypeFace();
    }

    private void textTypeFace(){

        String locale_name = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
//        Typeface typeface = locale_name.equals("fa")?Typeface.createFromAsset(getContext().getAssets(),"fonts/BYekan.ttf"): Typeface.DEFAULT;
        Typeface typeface = locale_name.equals("fa")?Typeface.createFromAsset(getContext().getAssets(),"fonts/Vazir-Medium.ttf"):
                                                     Typeface.createFromAsset(getContext().getAssets(),"fonts/arial.ttf");
        setTypeface(typeface);
    }
}

package dev.hamed.pollerproject.Utilities;

import android.content.Context;
import android.graphics.Typeface;
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

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"fonts/BYekan.ttf");
        setTypeface(typeface);
    }
}

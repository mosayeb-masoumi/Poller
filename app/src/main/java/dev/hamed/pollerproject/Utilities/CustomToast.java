package dev.hamed.pollerproject.Utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by hamed-mac on 3/3/18.
 */

public class CustomToast {

    public void createToast(String message,Context context){

        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/BYekan.ttf");
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        TextView textView = toast.getView().findViewById(android.R.id.message);
        textView.setTypeface(typeface);
        toast.show();
    }
}

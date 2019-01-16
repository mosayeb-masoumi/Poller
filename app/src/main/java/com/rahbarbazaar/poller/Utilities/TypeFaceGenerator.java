package com.rahbarbazaar.poller.Utilities;

import android.content.Context;
import android.graphics.Typeface;

public class TypeFaceGenerator {

    private static TypeFaceGenerator typeFaceGenerator;

    private TypeFaceGenerator() {
    }

    public static TypeFaceGenerator getInstance(){

        if (typeFaceGenerator!=null){

            return typeFaceGenerator;

        }else{

            return typeFaceGenerator = new TypeFaceGenerator();
        }
    }

    public Typeface getByekanFont(Context context){

     return Typeface.createFromAsset(context.getAssets(),"fonts/BYekan.ttf");
    }
}

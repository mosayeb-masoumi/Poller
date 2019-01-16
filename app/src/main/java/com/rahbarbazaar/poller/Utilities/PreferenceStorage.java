package com.rahbarbazaar.poller.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceStorage {

    private static PreferenceStorage preferenceStorage;

    private PreferenceStorage() {
    }

    public static PreferenceStorage getInstance(){

        if (preferenceStorage ==null){

            return preferenceStorage = new PreferenceStorage();

        }else {

            return preferenceStorage;
        }
    }

    public void saveToken(String token, Context context){

        SharedPreferences preferences = context.getSharedPreferences("token",Context.MODE_PRIVATE);
        preferences.edit().putString("token",token).apply();
    }

    public String retriveToken(Context context){

        SharedPreferences preference = context.getSharedPreferences("token",Context.MODE_PRIVATE);
        return preference.getString("token","0");
    }

    public void saveCurrency(String currency, Context context){

        SharedPreferences preferences = context.getSharedPreferences("currency",Context.MODE_PRIVATE);
        preferences.edit().putString("currency",currency).apply();
    }

    public String retriveCurrency(Context context){

        SharedPreferences preference = context.getSharedPreferences("currency",Context.MODE_PRIVATE);
        return preference.getString("currency","تومان");
    }


    public void saveUserDetails(String user_details, Context context){

        SharedPreferences preferences = context.getSharedPreferences("user_details",0);
        preferences.edit().putString("user_details",user_details).apply();
    }

    public String retriveUserDetails(Context context){

        SharedPreferences preference = context.getSharedPreferences("user_details",0);
        return preference.getString("user_details","");
    }


}

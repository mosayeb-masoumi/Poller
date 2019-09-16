package com.rahbarbazaar.poller.android.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceStorage {

    private static PreferenceStorage preferenceStorage;
    private SharedPreferences preferences;

    private PreferenceStorage(Context context) {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceStorage getInstance(Context context) {

        if (preferenceStorage == null) {

            return preferenceStorage = new PreferenceStorage(context);

        } else {

            return preferenceStorage;
        }
    }

    public void saveToken(String token) {
        preferences.edit().putString("token", token).apply();
    }

    public String retriveToken() {
        return preferences.getString("token", "0");
    }

    void saveUserDetails(String user_details) {

        preferences.edit().putString("user_details", user_details).apply();
    }

    public String retriveUserDetails() {

        return preferences.getString("user_details", "");
    }


    public boolean isUserLangEmpty() {

        return preferences.getString("language_key", "").equals("");
    }

    public String retriveLanguage() {

        return preferences.getString("language_key", "");
    }


//    public void saveToken(String token) {
//        preferences.edit().putString("token", token).apply();
//    }
//
//    public String retriveToken() {
//        return preferences.getString("token", "0");
//    }






    public  void setInt(String Key, int value) {
        preferences.edit().putInt(Key, value).apply();
    }

    public  int getInt(String Key, int DefaultValue) {
        return preferences.getInt(Key, DefaultValue);
    }




    public void savePhone(String phone) {
        preferences.edit().putString("phone", phone).apply();
    }

    public String retrivePhone() {
        return preferences.getString("phone", "0");
    }

    public void saveUserAccessType(String userAccessType) {
        preferences.edit().putString("userAccessType", userAccessType).apply();
    }

    public String retriveUserAccessType() {
        return preferences.getString("userAccessType", "0");
    }


}

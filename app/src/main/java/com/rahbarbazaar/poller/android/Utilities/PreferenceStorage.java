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

}

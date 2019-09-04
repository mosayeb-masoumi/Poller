package com.rahbarbazaar.poller.android.Models.refresh_token;

import com.google.gson.annotations.SerializedName;

public class RefreshToken {

    @SerializedName("token")
    public String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

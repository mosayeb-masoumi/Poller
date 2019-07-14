package com.rahbarbazaar.poller.android.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class CurrencyListParcelable implements Parcelable {

    private int id;
    private String currency_name;
    private String en_name;
    private int currency_value;
    private String created_at;
    private String updated_at;

    private CurrencyListParcelable(Parcel in) {
        id = in.readInt();
        currency_name = in.readString();
        en_name = in.readString();
        currency_value = in.readInt();
        created_at = in.readString();
        updated_at = in.readString();
    }

    public static final Creator<CurrencyListParcelable> CREATOR = new Creator<CurrencyListParcelable>() {
        @Override
        public CurrencyListParcelable createFromParcel(Parcel in) {
            return new CurrencyListParcelable(in);
        }

        @Override
        public CurrencyListParcelable[] newArray(int size) {
            return new CurrencyListParcelable[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public String getEn_name() {
        return en_name;
    }

    public int getCurrency_value() {
        return currency_value;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(currency_name);
        dest.writeString(en_name);
        dest.writeInt(currency_value);
        dest.writeString(created_at);
        dest.writeString(updated_at);
    }
}

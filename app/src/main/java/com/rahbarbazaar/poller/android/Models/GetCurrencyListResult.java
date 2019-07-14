package com.rahbarbazaar.poller.android.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class GetCurrencyListResult implements Parcelable {

    private String status;
    private List<CurrencyListParcelable> data;

    public List<CurrencyListParcelable> getItems() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    private GetCurrencyListResult(Parcel in) {
        data = in.createTypedArrayList(CurrencyListParcelable.CREATOR);
        status = in.readString();
    }

    public static final Creator<GetCurrencyListResult> CREATOR = new Creator<GetCurrencyListResult>() {
        @Override
        public GetCurrencyListResult createFromParcel(Parcel in) {
            return new GetCurrencyListResult(in);
        }

        @Override
        public GetCurrencyListResult[] newArray(int size) {
            return new GetCurrencyListResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
        dest.writeString(status);
    }
}

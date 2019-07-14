package com.rahbarbazaar.poller.android.Controllers.viewHolders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rahbarbazaar.poller.android.Models.SurveyMainModel;
import com.rahbarbazaar.poller.android.R;

public class SurveyHolder extends RecyclerView.ViewHolder {

    private TextView text_description, text_price, text_title, text_number, text_date;
    private ImageView image_expired;

    public SurveyHolder(@NonNull View itemView) {
        super(itemView);

        text_title = itemView.findViewById(R.id.text_survey_title);
        text_description = itemView.findViewById(R.id.text_survey_description);
        text_price = itemView.findViewById(R.id.text_survey_price);
        text_number = itemView.findViewById(R.id.txt_survey_number);
        text_date = itemView.findViewById(R.id.txt_survey_date);
        image_expired = itemView.findViewById(R.id.image_expired);
    }

    /**
     * @param data in this function we will process survey data:
     */
    @SuppressLint("SetTextI18n")
    public void bindSurveyData(SurveyMainModel data, int pos) {

        Context context = itemView.getContext();
        text_title.setText(data.getTitle());
        text_description.setText(data.getDescription());
        text_price.setText(new StringBuilder().append(data.getPoint()).append(" ").append(data.getCurrency().getName()));
        text_number.setText(String.valueOf(pos + 1));
        int remaining_day = getRemainingDate(data.getCurrent_date(), data.getEnd_date());

        if (remaining_day != 0)
            text_date.setText(remaining_day +" "+ context.getString(R.string.text_remaining_day));
        else
            text_date.setText(context.getString(R.string.text_expired));
        if (remaining_day <= 1)
            text_date.setTextColor(Color.parseColor("#ff1a1a"));

        if (data.isExpired() || remaining_day == 0) {

            text_number.setText("");
            image_expired.setVisibility(View.VISIBLE);
        }

        switch (data.getStatus()) {

            case 2:
                text_number.setText("");
                image_expired.setVisibility(View.VISIBLE);
                image_expired.setImageResource(R.drawable.inprogress_survey);
                break;

            case 3:
                text_number.setText("");
                image_expired.setVisibility(View.VISIBLE);
                image_expired.setImageResource(R.drawable.done_survey);

                break;
        }

    }

    private int getRemainingDate(String start, String end) {

        // get start day and month
        String[] start_array = start.substring(0, 10).split("-");
        String[] end_array = end.substring(0, 10).split("-");

        int start_day = Integer.valueOf(start_array[2]);
        int start_month = Integer.valueOf(start_array[1]);
        int start_year = Integer.valueOf(start_array[0]);

        int end_day = Integer.valueOf(end_array[2]);
        int end_month = Integer.valueOf(end_array[1]);
        int end_year = Integer.valueOf(end_array[0]);

        int sum;

        if (start_year>end_year){

            sum = 0;
        }else {

            if (end_month > start_month) { //if this condition was true, so we Certainly have remaining day

                sum = (end_month - start_month) * 30;

                if (start_day > end_day) {

                    sum = sum - (start_day - end_day);

                } else if (end_day > start_day) {

                    sum = sum + (end_day - start_day);
                }

            } else if (start_month > end_month) {// if this condition was true, so survey certainly expired

                sum = 0;

            } else {// if this condition was true, so end and start month is equal so we have to check days

                if (end_day > start_day)
                    sum = end_day - start_day;
                else
                    sum = 0;
            }
        }
        return sum;
    }

    public void setOnSurveyHolderListener(SurveyItemInteraction listener, SurveyMainModel data) {

        Context context = itemView.getContext();

        if (!data.isExpired()) {

            //TODO: this body can be improve

            String status = null;
            switch (data.getStatus()) {

                case 1:
                    status = context.getString(R.string.text_survey_view);
                    break;

                case 2:
                    status = context.getString(R.string.text_survey_view);
                    break;

                case 0:
                    status = context.getString(R.string.text_survey_incomplete);
                    break;
            }

            if (data.getStatus() < 3) {

                String finalStatus = status;
                itemView.setOnClickListener(view -> listener.onClicked(data.getId(), getRemainingDate(data.getCurrent_date(), data.getEnd_date()) == 0, data.getUrl_type(), finalStatus));

            } else {

                // this is complete survey
                itemView.setOnClickListener(view -> listener.onClicked(data.getId(), false, 1, context.getString(R.string.text_survey_complete)));
            }

        } else {

            //this is expired state / url type is not important
            itemView.setOnClickListener(view -> listener.onClicked(data.getId(), data.getStatus() != 3, 1, data.getStatus() == 3 ? context.getString(R.string.text_survey_complete) : context.getString(R.string.text_expired)));
        }
    }
}

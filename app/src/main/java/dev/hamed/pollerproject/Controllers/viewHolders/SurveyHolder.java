package dev.hamed.pollerproject.Controllers.viewHolders;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dev.hamed.pollerproject.Models.SurveyMainModel;
import dev.hamed.pollerproject.R;
import dev.hamed.pollerproject.Utilities.CustomToast;

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

        text_title.setText(data.getTitle());
        text_description.setText(data.getDescription());
        text_price.setText(new StringBuilder().append("مبلغ ").append(": ").append(data.getPoint()).append(" ").append(data.getCurrency().getName()));
        text_number.setText(String.valueOf(pos + 1));
        int remaining_day = (getRemainingDate(data.getCurrent_date(), data.getEnd_date()));

        if(remaining_day!=0)
        text_date.setText(String.valueOf(remaining_day) + " روز باقی مانده");
        else
            text_date.setText("منقضی شده");
        if (remaining_day <= 1)
            text_date.setTextColor(Color.parseColor("#ff1a1a"));

        if (data.isExpired()||remaining_day==0)
            image_expired.setVisibility(View.VISIBLE);
        else
            image_expired.setVisibility(View.GONE);

    }

    private int getRemainingDate(String start, String end) {

        // get start day and month
        String[] start_array = start.substring(0, 10).split("-");
        String[] end_array = end.substring(0, 10).split("-");

        int start_day = Integer.valueOf(start_array[2]);
        int start_month = Integer.valueOf(start_array[1]);

        int end_day = Integer.valueOf(end_array[2]);
        int end_month = Integer.valueOf(end_array[1]);

        int sum;

        if (end_month > start_month) { //if this condition was true, so we Certainly have remaining day

            sum = (end_month - start_month) * 30;

            if (start_day > end_day) {

                sum = sum - (start_day - end_day);

            } else if (end_day > start_day) {

                sum = sum + (end_day - start_day);
            }

        } else if (start_month>end_month){// if this condition was true, so survey certainly expired

            sum=0;

        }else {// if this condition was true, so end and start month is equal so we have to check days

            if (end_day > start_day)
                sum = end_day - start_day;
            else
                sum = 0;
        }

        return sum;
    }

    public void setOnSurveyHolderListener(SurveyItemInteraction listener, SurveyMainModel data) {


        if (!data.isExpired()) {

            if (data.getStatus() < 3) {

                itemView.setOnClickListener(view -> listener.onClicked(data.getId(),false));

            }else {

                //this is expired state
                itemView.setOnClickListener(view -> listener.onClicked(data.getId(),true));
            }

        }else {

            //this is expired state
            itemView.setOnClickListener(view -> listener.onClicked(data.getId(),true));
        }
    }
}

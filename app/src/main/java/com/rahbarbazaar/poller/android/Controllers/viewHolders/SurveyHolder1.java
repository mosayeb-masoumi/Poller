package com.rahbarbazaar.poller.android.Controllers.viewHolders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rahbarbazaar.poller.android.Models.SurveyMainModel;
import com.rahbarbazaar.poller.android.R;

public class SurveyHolder1 extends RecyclerView.ViewHolder {

    //    private TextView text_description, text_price, text_title, text_number, text_date;
//    private ImageView image_expired;
    private ImageView row_survey_imgmobile, row_survey_imgCoin, row_survey_imgLeftday, row_survey_underImg_left, row_survey_underImg_right;
    private TextView text_survey_title, text_survey_price, txt_survey_date, text_survey_unit;
    private LinearLayout row_survey_LLbackground;

    public SurveyHolder1(@NonNull View itemView) {
        super(itemView);


        row_survey_imgmobile = itemView.findViewById(R.id.row_survey_imgmobile);
        row_survey_imgCoin = itemView.findViewById(R.id.row_survey_imgCoin);
        row_survey_imgLeftday = itemView.findViewById(R.id.row_survey_imgLeftday);
        text_survey_title = itemView.findViewById(R.id.text_survey_title);
        text_survey_price = itemView.findViewById(R.id.text_survey_price);
        text_survey_unit = itemView.findViewById(R.id.text_survey_unit);
        txt_survey_date = itemView.findViewById(R.id.txt_survey_date);
        row_survey_LLbackground = itemView.findViewById(R.id.row_survey_LLbackground);
        row_survey_underImg_left = itemView.findViewById(R.id.row_survey_underImg_left);
        row_survey_underImg_right = itemView.findViewById(R.id.row_survey_underImg_right);


//        text_title = itemView.findViewById(R.id.text_survey_title);
//        text_description = itemView.findViewById(R.id.text_survey_description);
//        text_price = itemView.findViewById(R.id.text_survey_price);
//        text_number = itemView.findViewById(R.id.txt_survey_number);
//        text_date = itemView.findViewById(R.id.txt_survey_date);
//        image_expired = itemView.findViewById(R.id.image_expired);


    }

    /**
     * @param data in this function we will process survey data:
     */
    @SuppressLint("SetTextI18n")
    public void bindSurveyData(SurveyMainModel data, int pos) {

        String currentDate = data.getCurrent_date();
        int currentYear = Integer.parseInt(currentDate.substring(0, 4));
        int currentMonth = Integer.parseInt(currentDate.substring(5, 7));
        int currentDay = Integer.parseInt(currentDate.substring(8, 10));
        int currentHour = Integer.parseInt(currentDate.substring(11, 13));
        int currentMin = Integer.parseInt(currentDate.substring(14, 16));
        int totalCurrentMinute = (currentHour * 60) + currentMin;


        String endDate = data.getEnd_date();
        int endYear = Integer.parseInt(endDate.substring(0, 4));
        int endMonth = Integer.parseInt(endDate.substring(5, 7));
        int endDay = Integer.parseInt(endDate.substring(8, 10));
        int endHour = Integer.parseInt(endDate.substring(11, 13));
        int endMin = Integer.parseInt(endDate.substring(14, 16));
        int totalEndMinute = (endHour * 60) + endMin;


        String startDate = data.getStart_date();
        int startYear = Integer.parseInt(startDate.substring(0, 4));
        int startMonth = Integer.parseInt(startDate.substring(5, 7));
        int startDay = Integer.parseInt(startDate.substring(8, 10));
        int startHour = Integer.parseInt(startDate.substring(11, 13));
        int startMin = Integer.parseInt(startDate.substring(14, 16));
        int totalStartMinute = (startHour * 60) + startMin;


        Context context = itemView.getContext();
        text_survey_title.setText(data.getTitle());
//        text_description.setText(data.getDescription());
//        text_survey_price.setText(new StringBuilder().append(data.getPoint()).append(" ").append(data.getCurrency().getName()));
        text_survey_price.setText(new StringBuilder().append(data.getPoint()));
        text_survey_price.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
//        text_number.setText(String.valueOf(pos + 1));
        int remaining_day = getRemainingDate(data.getCurrent_date(), data.getEnd_date());


        String startdate = (data.getStart_date());
        String[] start_array = startdate.substring(0, 10).split("-");
        int start_day = Integer.valueOf(start_array[2]);
        int start_month = Integer.valueOf(start_array[1]);
        int start_year = Integer.valueOf(start_array[0]);


        String currentdate = (data.getCurrent_date());
        String[] current_array = currentdate.substring(0, 10).split("-");
        int current_day = Integer.valueOf(current_array[2]);
        int current_month = Integer.valueOf(current_array[1]);
        int current_year = Integer.valueOf(current_array[0]);


//        if(start_year == current_year && start_month==current_month && (current_day-start_day)<=1){// gold status
//            // gold status
//            Toast.makeText(context, "gold status", Toast.LENGTH_SHORT).show();
//            row_survey_LLbackground.setBackground(ContextCompat.getDrawable(context, R.drawable.row_survey_gold_bg));
//        }

        if (data.getStatus() == 3) {
            row_survey_imgmobile.setImageResource(R.drawable.survey_item_ok_icon2);
//            row_survey_imgLeftday.setImageResource(R.drawable.survey_item_expired_icon);
            text_survey_title.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
            row_survey_LLbackground.setBackground(ContextCompat.getDrawable(context, R.drawable.row_survey_gray_bg));
            row_survey_imgCoin.setImageResource(R.drawable.survey_item_graycoin);
//            row_survey_underImg_left.setImageResource(R.drawable.row_survey_under_deepgray);
//            row_survey_underImg_right.setImageResource(R.drawable.row_survey_under_lightgray);

        } else if (start_year == current_year && start_month == current_month &&
                ((current_day - start_day) == +1) || (current_day - start_day == 0)) { //gold
            row_survey_imgmobile.setImageResource(R.drawable.survey_item_brown_mobile);
            row_survey_imgLeftday.setImageResource(R.drawable.survey_item_leftday_gold_icon);
            text_survey_title.setTextColor(ContextCompat.getColor(context, R.color.brown));
            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.brown));
            row_survey_LLbackground.setBackground(ContextCompat.getDrawable(context, R.drawable.row_survey_gold_bg));

        } else if (start_year == current_year && start_month == current_month &&
                (current_day - start_day) == +2 && totalCurrentMinute < totalStartMinute) {

            row_survey_imgmobile.setImageResource(R.drawable.survey_item_brown_mobile);
            row_survey_imgLeftday.setImageResource(R.drawable.survey_item_leftday_gold_icon);
            text_survey_title.setTextColor(ContextCompat.getColor(context, R.color.brown));
            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.brown));
            row_survey_LLbackground.setBackground(ContextCompat.getDrawable(context, R.drawable.row_survey_gold_bg));
        } else if ((remaining_day > 0 || (remaining_day == 0 && currentYear == endYear && currentMonth == endMonth &&
                currentDay == endDay && totalCurrentMinute < totalEndMinute))
                && (data.getStatus() == 1 || data.getStatus() == 2)) {
            row_survey_imgmobile.setImageResource(R.drawable.survey_item_gradient_mobile);
            row_survey_imgLeftday.setImageResource(R.drawable.survey_item_leftday_icon);
            text_survey_title.setTextColor(ContextCompat.getColor(context, R.color.blue));
            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.blue));
            row_survey_LLbackground.setBackground(ContextCompat.getDrawable(context, R.drawable.row_survey_gradient_border));
            row_survey_underImg_left.setImageResource(R.drawable.row_survey_under_deepblue);
            row_survey_underImg_right.setImageResource(R.drawable.row_survey_under_pink);
        } else if ((remaining_day > 0 || (remaining_day == 0 && currentYear == endYear && currentMonth == endMonth &&
                currentDay == endDay && totalCurrentMinute > totalEndMinute))
                && (data.getStatus() == 1 || data.getStatus() == 2)) {
            row_survey_imgmobile.setImageResource(R.drawable.survey_item_delete_icon2);
//            row_survey_imgLeftday.setImageResource(R.drawable.survey_item_expired_icon);
            text_survey_title.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
            row_survey_LLbackground.setBackground(ContextCompat.getDrawable(context, R.drawable.row_survey_gray_bg));
            text_survey_title.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
//            row_survey_underImg_left.setImageResource(R.drawable.row_survey_under_deepgray);
//            row_survey_underImg_right.setImageResource(R.drawable.row_survey_under_lightgray);
        } else if ((remaining_day == 0 && (currentYear != endYear || currentMonth != endMonth || currentDay != endDay))
                && (data.getStatus() == 1 || data.getStatus() == 2)) {
            row_survey_imgmobile.setImageResource(R.drawable.survey_item_delete_icon2);
//            row_survey_imgLeftday.setImageResource(R.drawable.survey_item_expired_icon);
            text_survey_title.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
            row_survey_LLbackground.setBackground(ContextCompat.getDrawable(context, R.drawable.row_survey_gray_bg));
            text_survey_title.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
//            row_survey_underImg_left.setImageResource(R.drawable.row_survey_under_deepgray);
//            row_survey_underImg_right.setImageResource(R.drawable.row_survey_under_lightgray);
        }


//        else if ((remaining_day < 0 ) && (data.getStatus() == 1 || data.getStatus() == 2)) {
//            row_survey_imgmobile.setImageResource(R.drawable.survey_item_delete_icon2);
//            row_survey_imgLeftday.setImageResource(R.drawable.survey_item_expired_icon);
//            text_survey_title.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
//            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
//            row_survey_LLbackground.setBackground(ContextCompat.getDrawable(context, R.drawable.row_survey_gray_bg));
//        }


//        else if ((remaining_day < 0 ||(remaining_day==0 && totalCurrentMinute>totalEndMinute))&& data.getStatus() == 3) {
//            row_survey_imgmobile.setImageResource(R.drawable.survey_item_ok_icon2);
//            row_survey_imgLeftday.setImageResource(R.drawable.survey_item_expired_icon);
//            text_survey_title.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
//            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
//            row_survey_LLbackground.setBackground(ContextCompat.getDrawable(context, R.drawable.row_survey_gray_bg));
//        }


        if ((data.getCurrency().getName().equals("پاپاسی") || data.getCurrency().getName().equals("Papasi"))
                && remaining_day > 0 && data.getStatus() != 3) {

            row_survey_imgCoin.setImageResource(R.drawable.survey_item_purplecoin);
            row_survey_underImg_left.setImageResource(R.drawable.row_survey_under_deepblue);
            row_survey_underImg_right.setImageResource(R.drawable.row_survey_under_pink);

        } else if ((data.getCurrency().getName().equals("تومان") || data.getCurrency().getName().equals("Toman"))
                && remaining_day > 0 && data.getStatus() != 3) {

            row_survey_imgCoin.setImageResource(R.drawable.survey_item_bluecoin);
            row_survey_underImg_left.setImageResource(R.drawable.row_survey_under_deepblue);
            row_survey_underImg_right.setImageResource(R.drawable.row_survey_under_lightblue);
        }


        if (remaining_day != 0 && data.getStatus() != 3) {
            txt_survey_date.setText(remaining_day + " " + context.getString(R.string.text_remaining_day));
        }


        text_survey_unit.setText(data.getCurrency().getName());
        text_survey_unit.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));


//        else {
//            txt_survey_date.setText(context.getString(R.string.text_expired));
//            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
//        }

        if (remaining_day <= 1)
            txt_survey_date.setTextColor(Color.parseColor("#ff1a1a"));

        if (data.isExpired() || remaining_day == 0) {
//            text_number.setText("");
//            image_expired.setVisibility(View.VISIBLE);
            row_survey_imgLeftday.setVisibility(View.VISIBLE);
            txt_survey_date.setTextColor(ContextCompat.getColor(context, R.color.gray_deep));
        }

//        switch (data.getStatus()) {
//
//            case 2:
////                text_number.setText("");
//                row_survey_imgLeftday.setVisibility(View.VISIBLE);
//                row_survey_imgLeftday.setImageResource(R.drawable.survey_item_expired_icon);
//                break;
//            case 3:
////                text_number.setText("");
//                row_survey_imgLeftday.setVisibility(View.VISIBLE);
//                row_survey_imgLeftday.setImageResource(R.drawable.survey_item_expired_icon);
//                break;
//        }

    }


    int sum = 0;
//    public int getRemainingDate(String start, String end) {
//
//        // get start day and month
//        String[] start_array = start.substring(0, 10).split("-");
//        String[] end_array = end.substring(0, 10).split("-");
//
//        int start_day = Integer.valueOf(start_array[2]);
//        int start_month = Integer.valueOf(start_array[1]);
//        int start_year = Integer.valueOf(start_array[0]);
//
//        int end_day = Integer.valueOf(end_array[2]);
//        int end_month = Integer.valueOf(end_array[1]);
//        int end_year = Integer.valueOf(end_array[0]);
//
//
//        if (start_year > end_year) {
//
//            sum = 0;
//        } else {
//
//
//           if (end_month > start_month) { //if this condition was true, so we Certainly have remaining day
//
//                sum = (end_month - start_month) * 30;
//
//                if (start_day > end_day) {
//
//                    sum = sum - (start_day - end_day);
//
//                } else if (end_day > start_day) {
//
//                    sum = sum + (end_day - start_day);
//                }
//
//            } else if (start_month > end_month) {// if this condition was true, so survey certainly expired
//
//                sum = 0;
//
//            } else {// if this condition was true, so end and start month is equal so we have to check days
//
//                if (end_day > start_day)
//                    sum = end_day - start_day;
//                else
//                    sum = 0;
//            }
//        }
//
//
//        return sum;
//    }


    int sum1 = 0;
    int remainNewYearDays = 0;

    public int getRemainingDate(String start, String end) {

        // get start day and month
        String[] start_array = start.substring(0, 10).split("-");
        String[] end_array = end.substring(0, 10).split("-");

        int start_day = Integer.valueOf(start_array[2]);
        int start_month = Integer.valueOf(start_array[1]);
        int start_year = Integer.valueOf(start_array[0]);

        int end_day = Integer.valueOf(end_array[2]);
        int end_month = Integer.valueOf(end_array[1]);
        int end_year = Integer.valueOf(end_array[0]);


        if (start_year > end_year) {

            sum = 0;
        } else if (start_year == end_year) {


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


        } else if (end_year > start_year) {


            if (end_month > start_month) { //if this condition was true, so we Certainly have remaining day

                sum1 = sum1 + (end_month - start_month) * 30;

                if (start_day > end_day) {

                    sum1 = sum1 - (start_day - end_day);

                } else if (end_day > start_day) {

                    sum1 = sum1 + (end_day - start_day);
                }

                int year = ((end_year - start_year) - 1) * 365;

                remainNewYearDays = ((end_month - 1) * 30) + 5 + end_day;

                sum = sum1 + (year + remainNewYearDays);


            } else if (start_month > end_month) {// if this condition was true, so survey certainly expired

                int currentYearRemainDays = 365-(((start_month-1)*30)+start_day+5);


                int year = ((end_year - start_year) - 1) * 365;
                remainNewYearDays = ((end_month - 1) * 30) + 5 + end_day;

                if(end_month<7){
                    sum = currentYearRemainDays +((end_month-1)*30)+end_day+(end_month-1)+year;
                }else{
                    sum = currentYearRemainDays +((end_month-1)*30)+end_day+5+year;
                }



                int a = sum;


            } else {// if this condition was true, so end and start month is equal so we have to check days

                if (end_day > start_day)
                    sum1 = end_day - start_day;
                else
                    sum1 = 0;
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

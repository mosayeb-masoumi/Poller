package com.rahbarbazaar.poller.android.Controllers.viewHolders

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.rahbarbazaar.poller.android.Models.GetNotificationListResult
import com.rahbarbazaar.poller.android.R
import com.rahbarbazaar.poller.android.Utilities.CustomTextView

class NotificationViewHolder(view: View, val listener: GeneralItemIntraction<GetNotificationListResult.Messages>) : RecyclerView.ViewHolder(view) {

    private val notifyImageStatus = view.findViewById<ImageView>(R.id.image_msg_status)
    private val notifyText = view.findViewById<CustomTextView>(R.id.text_msg_text)
    private val notifyDate = view.findViewById<CustomTextView>(R.id.text_msg_date)
    private val rl_row_message = view.findViewById<RelativeLayout>(R.id.rl_row_message)

    fun bindNotifyData(data: GetNotificationListResult.Messages) {

        with(data) {

            if (pivot?.status == "0"){
                //                notifyImageStatus.setImageResource(R.drawable.close_message)  // old verion
                notifyImageStatus.setImageResource(R.drawable.message_close_icon)
                rl_row_message.setBackgroundResource(R.drawable.message_rv_bg_blue)
                notifyText.setTextColor(Color.parseColor("#ffffff"))
                notifyDate.setTextColor(Color.parseColor("#ffffff"))
            }



            else{
//                notifyImageStatus.setImageResource(R.drawable.open_message)
                notifyImageStatus.setImageResource(R.drawable.message_open_icon)
                rl_row_message.setBackgroundResource(R.drawable.message_rv_bg_bluelight)// old verion
                notifyText.setTextColor(Color.parseColor("#264796"))
                notifyDate.setTextColor(Color.parseColor("#264796"))
            }
//

            notifyText.text = title
            notifyDate.text = created_at
        }

        itemView.setOnClickListener { listener.invokeItem(data) }
    }

}
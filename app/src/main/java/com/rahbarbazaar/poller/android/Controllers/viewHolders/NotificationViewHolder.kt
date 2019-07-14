package com.rahbarbazaar.poller.android.Controllers.viewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.rahbarbazaar.poller.android.Models.GetNotificationListResult
import com.rahbarbazaar.poller.android.R
import com.rahbarbazaar.poller.android.Utilities.CustomTextView

class NotificationViewHolder(view: View, val listener: GeneralItemIntraction<GetNotificationListResult.Messages>) : RecyclerView.ViewHolder(view) {

    private val notifyImageStatus = view.findViewById<ImageView>(R.id.image_msg_status)
    private val notifyText = view.findViewById<CustomTextView>(R.id.text_msg_text)
    private val notifyDate = view.findViewById<CustomTextView>(R.id.text_msg_date)

    fun bindNotifyData(data: GetNotificationListResult.Messages) {

        with(data) {

            if (pivot?.status=="0")
                notifyImageStatus.setImageResource(R.drawable.close_message)
            else
                notifyImageStatus.setImageResource(R.drawable.open_message)

            notifyText.text = title
            notifyDate.text = created_at
        }

        itemView.setOnClickListener { listener.invokeItem(data) }
    }

}
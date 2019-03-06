package com.rahbarbazaar.poller.android.Controllers.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rahbarbazaar.poller.android.Controllers.viewHolders.GeneralItemIntraction
import com.rahbarbazaar.poller.android.Controllers.viewHolders.NotificationViewHolder
import com.rahbarbazaar.poller.android.Models.GetNotificationListResult
import com.rahbarbazaar.poller.android.R

class NotificationAdapter(private val notifyList: List<GetNotificationListResult.Messages>,
                          private val listener: GeneralItemIntraction) : RecyclerView.Adapter<NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.notify_rv_items, parent, false)

        return NotificationViewHolder(view = view, listener = listener)
    }

    override fun getItemCount(): Int = notifyList.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        holder.bindNotifyData(notifyList[position])
    }

}
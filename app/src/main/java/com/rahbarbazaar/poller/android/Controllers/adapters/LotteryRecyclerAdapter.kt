package com.rahbarbazaar.poller.android.Controllers.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rahbarbazaar.poller.android.Controllers.viewHolders.GeneralItemIntraction
import com.rahbarbazaar.poller.android.Controllers.viewHolders.LotteryViewHolder
import com.rahbarbazaar.poller.android.Models.GetLotteryListResult
import com.rahbarbazaar.poller.android.R

class LotteryRecyclerAdapter(private val items: List<GetLotteryListResult>,
                             private val itemInteraction: GeneralItemIntraction<String>) :
        RecyclerView.Adapter<LotteryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LotteryViewHolder {

//        val view = LayoutInflater.from(parent.context).inflate(R.layout.lottery_rv_items, parent, false)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lottery_rv_items1, parent, false)
        return LotteryViewHolder(view, itemIntraction = itemInteraction)
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: LotteryViewHolder, position: Int) {

        holder.bindData(items[position])
    }
}
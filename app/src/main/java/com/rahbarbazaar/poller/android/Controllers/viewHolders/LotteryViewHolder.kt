package com.rahbarbazaar.poller.android.Controllers.viewHolders

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.rahbarbazaar.poller.android.Models.GetLotteryListResult
import com.rahbarbazaar.poller.android.R

class LotteryViewHolder(view: View, val itemIntraction: GeneralItemIntraction<String>) : RecyclerView.ViewHolder(view) {

    private val tvLotteryDate: TextView = view.findViewById(R.id.tv_lottery_date)
    private val tvLotteryTitle: TextView = view.findViewById(R.id.tv_lottery_title)
    private val tvLotteryPrice: TextView = view.findViewById(R.id.tv_lottery_price)

    fun bindData(data: GetLotteryListResult) {

        with(data) {

            tvLotteryDate.text = end
            tvLotteryTitle.text = title
            tvLotteryPrice.text = amount.toString()

            when(win){

                //case win
                1->itemView.setBackgroundColor(Color.parseColor("#00b100"))

                //case defeat
                2->itemView.setBackgroundColor(Color.parseColor("#ff1a1a"))
            }
        }

        if(data.conditions==null){

        }else{
            itemView.setOnClickListener { itemIntraction.invokeItem(data.conditions!!) }
        }

    }
}
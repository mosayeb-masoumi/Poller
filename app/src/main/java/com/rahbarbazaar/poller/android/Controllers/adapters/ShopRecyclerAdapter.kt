package com.rahbarbazaar.poller.android.Controllers.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rahbarbazaar.poller.android.Controllers.viewHolders.GeneralItemIntraction
import com.rahbarbazaar.poller.android.Controllers.viewHolders.ShopViewHolder
import com.rahbarbazaar.poller.android.Models.GetShopListResult
import com.rahbarbazaar.poller.android.R

class ShopRecyclerAdapter(private val shopList: List<GetShopListResult>, val listener: GeneralItemIntraction<GetShopListResult>) :
        RecyclerView.Adapter<ShopViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {

        val shopView = LayoutInflater.from(parent.context).inflate(R.layout.shop_rv_items, parent, false)

        return ShopViewHolder(shopView, listener)
    }

    override fun getItemCount(): Int = shopList.size

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {

        holder.bindShopData(shopList[position])
    }
}
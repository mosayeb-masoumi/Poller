package com.rahbarbazaar.poller.android.Controllers.viewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.facebook.drawee.view.SimpleDraweeView
import com.rahbarbazaar.poller.android.Models.GetShopListResult
import com.rahbarbazaar.poller.android.R
import com.rahbarbazaar.poller.android.Ui.fragments.ProfileFragment
import com.rahbarbazaar.poller.android.Utilities.AsyncImageLoader
import com.rahbarbazaar.poller.android.Utilities.CustomTextView
import io.fabric.sdk.android.services.concurrency.AsyncTask

class ShopViewHolder(view: View, val listener: GeneralItemIntraction<GetShopListResult>) : RecyclerView.ViewHolder(view) {

    private val shopImage = view.findViewById<SimpleDraweeView>(R.id.image_shop)
    private val shopName = view.findViewById<CustomTextView>(R.id.text_shop_name)

    fun bindShopData(data: GetShopListResult) {


        Glide
                .with(itemView.context)
                .load(data.icon_url)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(shopImage)

        shopName.text = data.title

//        with(data) {
//
//            shopName.text = title
//            AsyncImageLoader(shopImage, 100, 100).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, icon_url)
//        }
        itemView.setOnClickListener { listener.invokeItem(data) }
    }

}
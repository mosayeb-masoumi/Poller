package com.rahbarbazaar.poller.android.Models

data class GetShopListResult(val id: Int,
                             val title: String,
                             val url: String,
                             val icon_url: String,
                             val status: Int,
                             val created_at: String,
                             val updated_at: String
)
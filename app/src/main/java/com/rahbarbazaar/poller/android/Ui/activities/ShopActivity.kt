package com.rahbarbazaar.poller.android.Ui.activities

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.rahbarbazaar.poller.android.Controllers.adapters.ShopRecyclerAdapter
import com.rahbarbazaar.poller.android.Controllers.viewHolders.GeneralItemIntraction
import com.rahbarbazaar.poller.android.Models.GetShopListResult
import com.rahbarbazaar.poller.android.Models.RefreshBalanceEvent
import com.rahbarbazaar.poller.android.Network.ServiceProvider
import com.rahbarbazaar.poller.android.R
import com.rahbarbazaar.poller.android.Utilities.ClientConfig
import com.rahbarbazaar.poller.android.Utilities.ProfileTools
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_shop.*
import org.greenrobot.eventbus.EventBus

class ShopActivity : CustomBaseActivity(), GeneralItemIntraction<GetShopListResult> {

    val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        getShopItems()
        image_exit.setOnClickListener { finish() }
    }

    fun getShopItems() {

        val shopRecyclerView: RecyclerView = findViewById(R.id.shop_recycler)

        fun configeRecyclerView() {

            shopRecyclerView.apply {

                setHasFixedSize(true)
                itemAnimator = null
                setItemViewCacheSize(10)
                drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
                layoutManager = LinearLayoutManager(this@ShopActivity, LinearLayoutManager.VERTICAL, false)

                addItemDecoration(object : RecyclerView.ItemDecoration() {

                    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {

                        if (parent!!.getChildLayoutPosition(view) != 0)
                            outRect!!.top = 10
                    }
                })
            }
        }
        configeRecyclerView()

        val serviceProvider = ServiceProvider(this)
        disposable.add(serviceProvider.getmService().getShopItems(ClientConfig.API_V1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableSingleObserver<List<GetShopListResult>>() {

                    override fun onSuccess(result: List<GetShopListResult>) {

                        shopRecyclerView.adapter = ShopRecyclerAdapter(result, this@ShopActivity) // initial adapter by result
                        av_loading.hide() // hide loading after get response
                        disposable.dispose()
                    }

                    override fun onError(e: Throwable) {
                        disposable.dispose()
                    }
                }))
    }

    override fun invokeItem (data: GetShopListResult) {

        val userInformation = ProfileTools.getInstance().retriveUserInformation(this)

        Intent(this, HtmlLoaderActivity::class.java).let {

            it.putExtra("url", data.url + userInformation.user_id)
            it.putExtra("surveyDetails", false)
            it.putExtra("isShopping", true)
            startActivity(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.apply {

            dispose()
            clear()
        }
    }
}

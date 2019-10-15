package com.rahbarbazaar.poller.android.Ui.activities

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.rahbarbazaar.poller.android.Controllers.adapters.NotificationAdapter
import com.rahbarbazaar.poller.android.Controllers.viewHolders.GeneralItemIntraction
import com.rahbarbazaar.poller.android.Models.GeneralStatusResult
import com.rahbarbazaar.poller.android.Models.GetNotificationListResult
import com.rahbarbazaar.poller.android.Network.ServiceProvider
import com.rahbarbazaar.poller.android.R
import com.rahbarbazaar.poller.android.Utilities.ClientConfig
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_notification.*
import okhttp3.MediaType
import okhttp3.RequestBody

class NotificationActivity : CustomBaseActivity(), GeneralItemIntraction<GetNotificationListResult.Messages> {

    private val disposable = CompositeDisposable()
    private val MessagePreviewRequest = 13
    private lateinit var serviceProvider: ServiceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        serviceProvider = ServiceProvider(this)
        getNotificationList()
        image_exit.setOnClickListener { finish() }
    }

    private fun getNotificationList() {

        val notifyRecyclerView: RecyclerView = findViewById(R.id.notify_recycler)

        fun configNotifyRecyclerView() {

            notifyRecyclerView.apply {

                setHasFixedSize(true)
                drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
                layoutManager = LinearLayoutManager(this@NotificationActivity, LinearLayoutManager.VERTICAL, false)
                itemAnimator = null
                setItemViewCacheSize(10)

//                addItemDecoration(object : RecyclerView.ItemDecoration() {
//
//                    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
//                        super.getItemOffsets(outRect, view, parent, state)
//
//                        if (parent!!.getChildLayoutPosition(view) != 0)
//                            outRect!!.top = 10
//                    }
//                })
            }
        }
        configNotifyRecyclerView()

        disposable.add(serviceProvider.getmService().getNotificationList(ClientConfig.API_V1)
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribeWith(object : DisposableSingleObserver<GetNotificationListResult>() {

                    override fun onSuccess(result: GetNotificationListResult) {
                        av_loading.hide()
                        notifyRecyclerView.adapter = NotificationAdapter(result.messages!!, this@NotificationActivity)
                    }

                    override fun onError(e: Throwable) {
                        av_loading.hide()
                    }
                }))
    }

    /**
     *
     * @param[0] url content
     * @param[1] message id
     */

    override fun invokeItem(data: GetNotificationListResult.Messages) {

        fun seenMessageById() {

            av_loading.smoothToShow()
            val messageId = RequestBody.create(MediaType.parse("text/plain"), data.pivot?.message_id.toString())

            disposable.add(serviceProvider.getmService().seenMessage(ClientConfig.API_V1, messageId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableSingleObserver<GeneralStatusResult>() {

                        override fun onSuccess(result: GeneralStatusResult) {
                            av_loading.smoothToHide()

                            Intent(this@NotificationActivity, HtmlLoaderActivity::class.java).let {

                                it.putExtra("isSurveyDetails", false)
                                it.putExtra("url", data.content)
                                startActivityForResult(it, MessagePreviewRequest)
                            }
                        }

                        override fun onError(e: Throwable) {
                            av_loading.smoothToHide()
                        }
                    }))
        }

        seenMessageById()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MessagePreviewRequest) {
            getNotificationList()
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

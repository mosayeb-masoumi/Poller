package com.rahbarbazaar.poller.android.Ui.activities


import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.rahbarbazaar.poller.android.Controllers.adapters.LotteryRecyclerAdapter
import com.rahbarbazaar.poller.android.Controllers.viewHolders.GeneralItemIntraction
import com.rahbarbazaar.poller.android.Network.ServiceProvider
import com.rahbarbazaar.poller.android.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lottery.*
import android.view.*
import com.google.gson.Gson
import com.rahbarbazaar.poller.android.Models.*
import com.rahbarbazaar.poller.android.Utilities.*
import kotlinx.android.synthetic.main.activity_lottery.av_loading
import kotlinx.android.synthetic.main.activity_lottery.image_exit
import kotlinx.android.synthetic.main.activity_lottery.img_lottery_info
import kotlinx.android.synthetic.main.activity_lottery.linear_actions
import kotlinx.android.synthetic.main.activity_lottery.tv_cancel
import kotlinx.android.synthetic.main.activity_lottery.tv_lottery_amount
import kotlinx.android.synthetic.main.activity_lottery.tv_lottery_title
import kotlinx.android.synthetic.main.activity_lottery.tv_take_part
import kotlinx.android.synthetic.main.activity_lottery1.*

class LotteryActivity1 : CustomBaseActivity(),
        View.OnClickListener,
        GeneralItemIntraction<String> {

    lateinit var lotteryRecycler: RecyclerView
    lateinit var disposable: CompositeDisposable
    private var htmlContent: String? = null
    private var lotteryId: String? = null
    private var isLotteryStatusChanged = false
    private var userScore: Int = 0
    val toastFactory: Lazy<ToastFactory> = lazy { ToastFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lottery1)
        setSupportActionBar(toolbar)

        fun initViews() {

            image_exit.setOnClickListener(this)
            img_lottery_info.setOnClickListener(this)
            tv_cancel.setOnClickListener(this)
            tv_take_part.setOnClickListener(this)
        }
        initViews()

        fun initialUserInfo() {

            val userDetailsPrefrence = Gson().fromJson(PreferenceStorage.getInstance(this).retriveUserDetails(),
                    UserDetailsPrefrence::class.java)
//            tv_point.text = userDetailsPrefrence.score.toString()
            userScore = userDetailsPrefrence.score
        }
        initialUserInfo()

        disposable = CompositeDisposable()
        lotteryRecycler = findViewById(R.id.lottery_recycler)
        getCurrentLottery()
        getLotteryHistory()


      //todo make a clause to set yearly relativelayout visible
        rl_yearly_lottery.visibility = View.GONE

    }

    private fun getCurrentLottery() {

        av_loading.smoothToHide()
        val service = ServiceProvider(this).getmService()
        disposable.add(service.getCurrentLottery(ClientConfig.API_V1).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribeWith(object : DisposableSingleObserver<List<GetLotteryListResult>>() {
                    override fun onError(e: Throwable) {
                        Log.e("lottery_error", "msg :${e.message}")
                    }

                    override fun onSuccess(result: List<GetLotteryListResult>) {

                        if (result.isNotEmpty()) {

                            val data = result[0]
                            with(data) {

                                htmlContent = conditions
                                tv_lottery_title.text = title
//                                tv_lottery_date.text = end
                                tv_lottery_amount.text = amount.toString()
                                lotteryId = id.toString()

                                when (status) {

                                    0 -> tv_cancel.visibility = View.GONE
                                    else -> tv_cancel.visibility = View.VISIBLE
                                }

                                when (expire) {

                                    1 -> linear_actions.visibility = View.GONE
                                }
                            }
                        }

                        fun hideCurrentLotteryLayout(bool: Boolean) { //mikhastam khafan bashe :D vagarna mishod ye 'else' dar nazar gereft

                            if (bool) {
                                rl_current_lottery.visibility = View.GONE
                                img_lottery_info.isEnabled = false
                            }
                        }
                        hideCurrentLotteryLayout(result.isEmpty())
                    }
                }))

    }

    private fun getLotteryHistory() {

        fun configLotteryRecycler() {

            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            lotteryRecycler.apply {

                setHasFixedSize(true)
                setLayoutManager(layoutManager)
                itemAnimator = null
                setItemViewCacheSize(5)
                drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
                addItemDecoration(object : RecyclerView.ItemDecoration() {

                    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {

                        if (parent?.getChildLayoutPosition(view) != 0)
                            outRect?.top = 10
                    }
                })
            }
        }
        configLotteryRecycler()

        val service = ServiceProvider(this).getmService()
        disposable.add(service.getLotteryHistory(ClientConfig.API_V1).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribeWith(object : DisposableSingleObserver<List<GetLotteryListResult>>() {
                    override fun onError(e: Throwable) {
                        av_loading.smoothToHide()
                        Log.e("lottery_error", "msg :${e.message}")
                    }

                    override fun onSuccess(result: List<GetLotteryListResult>) {

                        if (result.isNotEmpty()) {

                            av_loading.smoothToHide()
                            lotteryRecycler.adapter = LotteryRecyclerAdapter(result, this@LotteryActivity1)
                        }
                    }
                }))

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.image_exit -> {
                if (isLotteryStatusChanged)
                    setResult(Activity.RESULT_OK)

                finish()
            }
            R.id.tv_take_part -> createJoinToLotteryDialog()
            R.id.tv_cancel -> cancelLottery()
            R.id.img_lottery_info -> gotoHtmlActivity(htmlContent)
        }
    }

    private fun cancelLottery() {

        val service = ServiceProvider(this).getmService()
        disposable.add(service.cancelLottery(ClientConfig.API_V1, lotteryId).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribeWith(object : DisposableSingleObserver<GeneralStatusResult>() {
                    override fun onError(e: Throwable) {

                        Log.e("lottery_error", "msg :${e.message}")
                    }

                    override fun onSuccess(data: GeneralStatusResult) {

                        var message = 0
                        when (data.status) {

                            "canceled successful" -> {
                                isLotteryStatusChanged = true
                                tv_cancel.visibility = View.GONE
                                tv_lottery_amount.text = "0"
                                tv_point.text = userScore.toString()
                                message = R.string.text_success_cancel
                            }
                            "lottery date expired" -> message = R.string.text_date_expired
                            "not joined" -> message = R.string.text_not_joined
                            "has already been canceled" -> message = R.string.text_already_cancel
                            "wrong data" -> message = R.string.text_wrong_data
                            "process failed" -> message = R.string.text_process_failed
                        }
                        toastFactory.value.createToast(message, this@LotteryActivity1)
                    }
                }))
    }

    private fun createJoinToLotteryDialog() {

        fun joinToLottery(amount: String) {

            val service = ServiceProvider(this).getmService()
            disposable.add(service.joinToLottery(ClientConfig.API_V1, lotteryId, amount).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io()).subscribeWith(object : DisposableSingleObserver<GeneralStatusResult>() {
                        override fun onError(e: Throwable) {
                            Log.e("lottery_error", "msg :${e.message}")
                        }

                        override fun onSuccess(data: GeneralStatusResult) {

                            var message = 0
                            when (data.status) {

                                "not enough score" -> message = R.string.text_not_enough_score
                                "amount is lower than minimum" -> message = R.string.text_lower_minimum
                                "not changed" -> message = R.string.text_not_changed
                                "process failed" -> message = R.string.text_process_failed
                                "lottery date expired" -> message = R.string.text_date_expired
                                "insert successful" -> {

                                    message = R.string.text_success_done
                                    tv_lottery_amount.text = amount
                                    isLotteryStatusChanged = true
                                    tv_point.text = (userScore - amount.toInt()).toString()
                                    tv_cancel.visibility = View.VISIBLE
                                }
                            }
                            toastFactory.value.createToast(message, this@LotteryActivity1)
                        }
                    }))
        }

        val dialogFactory = DialogFactory(this)
        dialogFactory.createjoinToLotteryDialog(lottery_root, object : DialogFactory.DialogFactoryInteraction {
            override fun onDeniedButtonClicked(cancel_dialog: Boolean) {

            }

            override fun onAcceptButtonClicked(vararg strings: String?) {

                joinToLottery(strings[0]!!)
            }
        })

    }

    private fun gotoHtmlActivity(data: String?) {

        Intent(this@LotteryActivity1, HtmlLoaderActivity::class.java).let {

            it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            it.putExtra("url", data)
            it.putExtra("surveyDetails", false)
            it.putExtra("isShopping", false)
            startActivity(it)
        }
    }

    override fun invokeItem(data: String) {
        gotoHtmlActivity(data)
    }

    override fun onBackPressed() {

        if (isLotteryStatusChanged)
            setResult(Activity.RESULT_OK)

        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        disposable.apply {
            dispose()
            clear()
        }
        super.onDestroy()
    }

}
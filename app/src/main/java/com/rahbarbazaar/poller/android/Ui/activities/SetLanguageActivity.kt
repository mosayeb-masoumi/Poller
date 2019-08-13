package com.rahbarbazaar.poller.android.Ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult
import com.rahbarbazaar.poller.android.Network.ServiceProvider
import com.rahbarbazaar.poller.android.R
import com.rahbarbazaar.poller.android.Utilities.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_set_language.*
import kotlinx.android.synthetic.main.activity_splash_screen.*


class SetLanguageActivity : AppCompatActivity() {

    internal lateinit var disposable: CompositeDisposable
    private var isValidToken: Boolean = false
    internal lateinit var tools: GeneralTools
    private lateinit var dialogFactory: DialogFactory
    private var getCurrencyListResult: GetCurrencyListResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_language)

        val provider = ServiceProvider(this)
        disposable = CompositeDisposable()
        tools = GeneralTools.getInstance()
        dialogFactory = DialogFactory(this@SetLanguageActivity)
        val preferenceStorage = PreferenceStorage.getInstance(this)

        //check token validation
        isValidToken = preferenceStorage.retriveToken() != "0"



        //todo add these below4 lines to set font (because )
        val type = Typeface.createFromAsset(assets, "fonts/Vazir-Medium.ttf")
        text_titleFa.setTypeface(type)
        btn_fa1.setTypeface(type)

        val type2 = Typeface.createFromAsset(assets, "fonts/arial.ttf")
        text_titleEn.setTypeface(type2)
        btn_en1.setTypeface(type2)



        btn_en1.setOnClickListener {
            LocaleManager.setNewLocale(this@SetLanguageActivity, "en")
            checkAccessibility()
            App.language = "en"
        }

        btn_fa1.setOnClickListener {
            LocaleManager.setNewLocale(this@SetLanguageActivity, "fa")
            checkAccessibility()
            App.language = "fa"
        }



        //save dynamic currency
        fun saveCurrency() {

            val service = provider.getmService()
            disposable.add(service.getCurrency(ClientConfig.API_V2).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<GetCurrencyListResult>() {
                        override fun onSuccess(result: GetCurrencyListResult) {

                            getCurrencyListResult = result
                        }

                        override fun onError(e: Throwable) {

                            Log.e("splash_service", "msg : failed :${e.message}")
                        }
                    }))
        }
        saveCurrency()
    }

    private fun checkAccessibility() {

        if (tools.checkInternetConnection(this)) {

//            av_loading.hide()

            if (!isValidToken) {

                startActivity(Intent(this@SetLanguageActivity, LoginActivity::class.java))
                this@SetLanguageActivity.finish()
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)

            } else {

                Intent(this@SetLanguageActivity, MainActivity::class.java).let {

                    it.putExtra("parcel_data", getCurrencyListResult)
                    startActivity(it)
                }.also {
                    this@SetLanguageActivity.finish()
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
            }

        } else {

            if (!isFinishing) {

                dialogFactory.createNoInternetDialog(object : DialogFactory.DialogFactoryInteraction {
                    override fun onAcceptButtonClicked(vararg params: String) {

                        //go to wifi setting
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }

                    override fun onDeniedButtonClicked(dialog_cancel: Boolean) {

                        if (dialog_cancel) {

                            checkAccessibility()

                        } else {
                            // get call back from grey button and go to mobile data page
                            startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                        }
                    }
                }, findViewById<View>(R.id.rl_root))
            }
        }
    }

}

package com.rahbarbazaar.poller.android.Ui.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.rahbarbazaar.poller.android.BuildConfig
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult
import com.rahbarbazaar.poller.android.Models.CurrencyListParcelable

import com.rahbarbazaar.poller.android.Network.ServiceProvider
import com.rahbarbazaar.poller.android.R
import com.rahbarbazaar.poller.android.Utilities.*
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.android.synthetic.main.activity_splash_screen.av_loading
import kotlinx.android.synthetic.main.activity_splash_screen1.*

class SplashScreenActivity : AppCompatActivity() {

    //property region
    internal lateinit var disposable: CompositeDisposable
    private var isValidToken: Boolean = false
    internal lateinit var tools: GeneralTools
    private lateinit var dialogFactory: DialogFactory
    private var getCurrencyListResult: GetCurrencyListResult? = null
    //end of region

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen1)



        txtVersion.setText(BuildConfig.VERSION_NAME)

        val provider = ServiceProvider(this)
        disposable = CompositeDisposable()
        tools = GeneralTools.getInstance()
        dialogFactory = DialogFactory(this@SplashScreenActivity)
        val preferenceStorage = PreferenceStorage.getInstance(this)

        //check token validation
        isValidToken = preferenceStorage.retriveToken() != "0"

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

        //get user profile information and save it in preference for access in other segment of app
        if (isValidToken)
            ProfileTools.getInstance().saveProfileInformation(this)

        if (preferenceStorage.isUserLangEmpty) {

            if (!isFinishing) {


                //todo added those 3 below lines insteade of instead of below commented
                // lines to use activity for set language instead of dialog
                // and created a new katlin class under the name of setLaguage activity
                //mine
                startActivity(Intent(this@SplashScreenActivity, SetLanguageActivity::class.java))
                this@SplashScreenActivity.finish()
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)


//                dialogFactory.createSelectLangDialog(findViewById<View>(R.id.rl_root), object : DialogFactory.DialogFactoryInteraction {
//                    override fun onAcceptButtonClicked(vararg strings: String) {
//                        LocaleManager.setNewLocale(this@SplashScreenActivity, "fa")
//                        checkAccessibility()
//                    }
//                    override fun onDeniedButtonClicked(cancel_dialog: Boolean) {
//                        LocaleManager.setNewLocale(this@SplashScreenActivity, "en")
//                        checkAccessibility()
//                    }
//                })
            }

        } else
            CustomHandler(this).postDelayed({ this.checkAccessibility() }, 3500)//lambda expression :: you have to enable lambda expression
    }

    /**
     * check network connection state if available and if user token was valid he/she will go to main
     * activity in otherwise he/she go to register activity for registration
     */

    private fun checkAccessibility() {

        if (tools.checkInternetConnection(this)) {

            av_loading.hide()

            if (!isValidToken) {

                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                this@SplashScreenActivity.finish()
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)

            } else {

                Intent(this@SplashScreenActivity, MainActivity::class.java).let {

                    it.putExtra("parcel_data", getCurrencyListResult)
                    startActivity(it)
                }.also {
                    this@SplashScreenActivity.finish()
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





    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}

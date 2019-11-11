package com.rahbarbazaar.poller.android.Ui.activities


import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.rahbarbazaar.poller.android.BuildConfig
import com.rahbarbazaar.poller.android.Models.GetCurrencyListResult
import com.rahbarbazaar.poller.android.Network.ServiceProvider
import com.rahbarbazaar.poller.android.R
import com.rahbarbazaar.poller.android.Utilities.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash_screen1.*
import kotlinx.android.synthetic.main.activity_splash_screen1.view.*

class SplashScreenActivity1 : AppCompatActivity() {

    internal lateinit var disposable: CompositeDisposable
    private var isValidToken: Boolean = false
    internal lateinit var tools: GeneralTools
    private lateinit var dialogFactory: DialogFactory
    private var getCurrencyListResult: GetCurrencyListResult? = null


//    internal var connectivityReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen1)


//        //check network broadcast reciever
//        val tools2 = GeneralTools.getInstance()
//        connectivityReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                tools2.doCheckNetwork(this@SplashScreenActivity1, findViewById(R.id.rl_root))
//            }
//
//        }


        txtVersion.text = BuildConfig.VERSION_NAME

//        val provider = ServiceProvider(this)
        disposable = CompositeDisposable()
        tools = GeneralTools.getInstance()
        dialogFactory = DialogFactory(this@SplashScreenActivity1)
        val preferenceStorage = PreferenceStorage.getInstance(this)

        rl_root.av_loading.visibility = View.VISIBLE

        var token: String = preferenceStorage.retriveToken()



//
        if (preferenceStorage.isUserLangEmpty) {
            startActivity(Intent(this@SplashScreenActivity1, SetLanguageActivity::class.java))
            this@SplashScreenActivity1.finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }else if (token == "" || token.isEmpty() || token == "0") {

            startActivity(Intent(this@SplashScreenActivity1, LoginActivity::class.java))
            this@SplashScreenActivity1.finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)


        } else {

            if (tools.checkInternetConnection(this)) {
//                saveProfileInformation()
                saveCurrency()
            } else {
                rl_root.av_loading.visibility = View.GONE
                dialogFactory.createNoInternetDialog(object : DialogFactory.DialogFactoryInteraction {
                    override fun onAcceptButtonClicked(vararg params: String) {
                        //go to wifi setting
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }

                    override fun onDeniedButtonClicked(dialog_cancel: Boolean) {
                        if (dialog_cancel) {

                            startActivity(Intent(this@SplashScreenActivity1, SplashScreenActivity1::class.java))

                        } else {
                            // get call back from grey button and go to mobile data page
                            startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                        }
                    }
                }, findViewById<View>(R.id.rl_root))
            }

        }










//


//
//        if(preferenceStorage.isUserLangEmpty){
//            startActivity(Intent(this@SplashScreenActivity1, SetLanguageActivity::class.java))
//            this@SplashScreenActivity1.finish()
//            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
//        }else{
//            CustomHandler(this).postDelayed({
//
//                Intent(this@SplashScreenActivity1, MainActivity::class.java).let {
//                    it.putExtra("parcel_data", getCurrencyListResult)
//                    startActivity(it)
//                }.also {
//                    this@SplashScreenActivity1.finish()
//                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
//                }
//
//            }, 3500)
//        }

    }

    private fun saveCurrency() {

        if (tools.checkInternetConnection(this)) {



        val provider = ServiceProvider(this)
        val service = provider.getmService()
        disposable.add(service.getCurrency(ClientConfig.API_V2).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GetCurrencyListResult>() {
                    override fun onSuccess(result: GetCurrencyListResult) {
                        getCurrencyListResult = result
                        ProfileTools.getInstance().saveProfileInformation(this@SplashScreenActivity1)
                        nextActivity()
                    }

                    override fun onError(e: Throwable) {
                        rl_root.av_loading.visibility = View.GONE

                    }

                }))


        }else{
//            if (!isFinishing) {

                dialogFactory.createNoInternetDialog(object : DialogFactory.DialogFactoryInteraction {
                    override fun onAcceptButtonClicked(vararg params: String) {

                        //go to wifi setting
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }

                    override fun onDeniedButtonClicked(dialog_cancel: Boolean) {

                        if (dialog_cancel) {

                            saveCurrency()

                        } else {
                            // get call back from grey button and go to mobile data page
                            startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                        }
                    }
                }, findViewById<View>(R.id.rl_root))
//            }
        }
    }


    private fun nextActivity() {

        val preferenceStorage = PreferenceStorage.getInstance(this)
        if (preferenceStorage.isUserLangEmpty) {
            startActivity(Intent(this@SplashScreenActivity1, SetLanguageActivity::class.java))
            this@SplashScreenActivity1.finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        } else {
            CustomHandler(this).postDelayed({

                Intent(this@SplashScreenActivity1, MainActivity::class.java).let {
                    it.putExtra("parcel_data", getCurrencyListResult)
                    startActivity(it)
                }.also {
                    this@SplashScreenActivity1.finish()
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
            }, 3500)
        }
    }


//    private fun requestRefreshToken() {
//        val provider = ServiceProvider(this)
//        disposable = CompositeDisposable()
//
//        val service = provider.getmService()
//
//        disposable.add(service.requsetRefreshToken(ClientConfig.API_V1)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(object : DisposableSingleObserver<RefreshToken>() {
//                    override fun onSuccess(result: RefreshToken) {
//
//                        val preferenseStrorage = PreferenceStorage.getInstance(this@SplashScreenActivity1)
//                        preferenseStrorage.saveToken(result.getToken())
//
//                        val intent = Intent(this@SplashScreenActivity1, SplashScreenActivity1::class.java)
//                        startActivity(intent)
//                        this@SplashScreenActivity1.finish()
//                    }
//
//                    override fun onError(e: Throwable) {
//
//                    }
//                }))
//
//    }


//    private fun createCloseAppDialog() {
//
//        val dialog = Dialog(this)
//        dialog.setContentView(R.layout.close_app_dialog1)
//        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
//        val txt = dialog.findViewById<TextView>(R.id.text_close_app)
//        dialog.setCancelable(false)
//
//
//        txt.setOnClickListener {
//            dialog.dismiss()
//            finishAffinity()
//            System.exit(0)
//        }
//
//        dialog.show()
//    }
//
//
//    private fun createTryAgainDialog() {
//        val dialog = Dialog(this)
//        dialog.setContentView(R.layout.try_again_dialog1)
//        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
//        val txt = dialog.findViewById<TextView>(R.id.text_tryAgain)
//        dialog.setCancelable(false)
//
//
//        txt.setOnClickListener {
//            saveCurrency()
//            dialog.dismiss()
//            rl_root.av_loading.visibility = View.VISIBLE
//        }
//
//        dialog.show()
//    }


//    override fun onDestroy() {
//        disposable.dispose()
//        unregisterReceiver(connectivityReceiver)
//        super.onDestroy()
//    }
//
//
//    override fun onResume() {
//        super.onResume()
//        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
//    }


}
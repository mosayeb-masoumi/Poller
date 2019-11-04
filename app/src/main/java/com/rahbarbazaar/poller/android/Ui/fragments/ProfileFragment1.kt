package com.rahbarbazaar.poller.android.Ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rahbarbazaar.poller.android.Models.*
import com.rahbarbazaar.poller.android.Network.ServiceProvider
import com.rahbarbazaar.poller.android.R
import com.rahbarbazaar.poller.android.Ui.activities.SplashScreenActivity
import com.rahbarbazaar.poller.android.Utilities.*
import io.reactivex.Single
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import android.support.v4.os.ConfigurationCompat
import android.widget.LinearLayout
import com.rahbarbazaar.poller.android.Models.eventbus.ModelTranferDataProfileToHome
import com.rahbarbazaar.poller.android.Models.eventbus.ModelUserType
import com.rahbarbazaar.poller.android.Ui.activities.HtmlLoaderActivity
import com.rahbarbazaar.poller.android.Ui.activities.MainActivity
import com.rahbarbazaar.poller.android.Ui.activities.SplashScreenActivity1
import kotlinx.android.synthetic.main.fragment_profile.profile_root
import kotlinx.android.synthetic.main.fragment_profile.rl_balance_point
import kotlinx.android.synthetic.main.fragment_profile.rl_edit_profile
import kotlinx.android.synthetic.main.fragment_profile.rl_point_balance
import kotlinx.android.synthetic.main.fragment_profile.text_age
import kotlinx.android.synthetic.main.fragment_profile.text_gender
import kotlinx.android.synthetic.main.fragment_profile.text_mobile
import kotlinx.android.synthetic.main.fragment_profile.text_project_count
import kotlinx.android.synthetic.main.fragment_profile.text_username
import kotlinx.android.synthetic.main.fragment_profile1.*
import kotlinx.android.synthetic.main.fragment_profile1.view.*
import java.util.ArrayList


class ProfileFragment1 : Fragment(), View.OnClickListener {


    //region of property
    lateinit var provider: ServiceProvider
    lateinit var disposable: CompositeDisposable
    val toastFactory: Lazy<ToastFactory> = lazy { ToastFactory() }
    var getCurrencyListResult: GetCurrencyListResult? = null
    var lang: String? = null
    //end of region

    var type: String = ""

    internal var goto_splash = false

    var balance: String? = null
    var score: String? = null

    var preferenceStorage: PreferenceStorage? = PreferenceStorage.getInstance(context)

    companion object {

        fun createInstance(getCurrencyListResult: GetCurrencyListResult, lang: String): ProfileFragment1 {

            val bundle = Bundle().apply {

                putParcelable("parcel_data", getCurrencyListResult)
                putString("lang", lang)
            }

            return ProfileFragment1().apply { arguments = bundle }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            getCurrencyListResult = it.getParcelable("parcel_data")
            lang = it.getString("lang")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile1, container, false)


        var locale_name = ConfigurationCompat.getLocales(resources.configuration).get(0).language

        if (locale_name.equals("en")) {
            view.arrow_username.rotation = 180f
            view.arrow_mobile.rotation = 180F
            view.arrow_age.rotation = 180F
            view.arrow_sex.rotation = 180F
            view.arrow_project_count.rotation = 180F
        }



        provider = ServiceProvider(context)
        disposable = CompositeDisposable()

        //initial ui
        getUserProfile()
        checkUserAllowingExchange()




        return view
    }

    //define views click listener will be appear here
    private fun defineViewClickListener() {

//        rl_logout.setOnClickListener(this)
        rl_edit_profile.setOnClickListener(this)
        rl_balance_point.setOnClickListener(this)
        rl_point_balance.setOnClickListener(this)
        rl_user_access_upgrade.setOnClickListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        defineViewClickListener()
    }

    //create confirm exit dialog
    private fun createConfirmExitDialog() {

        val dialogFactory = DialogFactory(context)
        dialogFactory.createConfirmExitDialog(object : DialogFactory.DialogFactoryInteraction {
            override fun onAcceptButtonClicked(vararg params: String) {

                PreferenceStorage.getInstance(context).saveToken("0")
                if (activity != null) {
                    startActivity(Intent(context, SplashScreenActivity::class.java))
                    activity!!.finish()
                }
            }

            override fun onDeniedButtonClicked(bool: Boolean) {

                //did on dialog factory
            }
        }, view, false)
    }

    //get user profile data and initialize textView will be implement here
    private fun getUserProfile() {

        val service = provider.getmService()
        disposable.add(service.getUserProfile(ClientConfig.API_V1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<UserConfirmAuthResult>() {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(result: UserConfirmAuthResult) {

                        type = result.type
                        if(type.equals("1")){
                            text_age.text = "---"
                            text_gender.text = "---"
                        }else{
                            text_age.text = result.birthday.toString()


                            var languag = ConfigurationCompat.getLocales(resources.configuration).get(0).language
                            if(languag.equals("fa"))
                            text_gender.text = if (result.gender == "male") "آقا" else "خانم"
                            else
                                text_gender.text = if (result.gender =="male") "Male" else "Female"
                        }


                        text_mobile.text = result.mobile
                        text_username.text = result.name


                        if (type.equals("1") || type.equals("4")) {
                            rl_user_access_upgrade.visibility = View.VISIBLE
                        } else {
                            rl_user_access_upgrade.visibility = View.GONE
                        }

                        getCurrencyListResult?.let {

                            with(it.items[0]) {

                                //                        text_point.text = result.balance.toString() + " " + if (lang == "fa")
//                            currency_name
//                        else
//                            en_name

                                balance = result.balance.toString()
                                score = result.score.toString()

                                App.balance = result.balance

                                // to send balance and score in profile fragment
                                val modelDataProfileTohome = ArrayList<ModelTranferDataProfileToHome>()
                                modelDataProfileTohome.add(ModelTranferDataProfileToHome(balance, score))
                                EventBus.getDefault().post(modelDataProfileTohome)



                                val modelUserType = ModelUserType()
                                modelUserType.user_type = result.type
                                EventBus.getDefault().post(modelUserType)

                            }

                            with(it.items[1]) {

                                //                        text_score.text = result.score.toString() + " " + if (lang == "fa")
//                            currency_name
//                        else
//                            en_name
                            }
                        }

                        text_project_count.text = result.participated_project_count.toString()
//                text_user_state.text = result.membership

                        set_nameBg(type)

                    }

                    override fun onError(e: Throwable) {
                        Log.e("profile_tag", "msg profile: ${e.message}")
                    }
                }))
    }

    private fun set_nameBg(type: String?) {
        when (type) {

            "1" -> {
//                rl_name_state.setBackgroundDrawable(resources.getDrawable(R.drawable.dialog_btn_shape1))
                rl_name_state.background = context?.let { ContextCompat.getDrawable(it, R.drawable.dialog_btn_shape1) }
//                img_star.setImageDrawable(resources.getDrawable(R.drawable.star_gradient))
//                img_star.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.star_gradient) })
                img_star.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.bg_info_guest) })
            }
            "2", "3", "5" -> {
                rl_name_state.background = context?.let { ContextCompat.getDrawable(it, R.drawable.bg_silver) }
//                img_star.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.star_silver) })
                img_star.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.bg_info_silver) })
            }

            "4" -> {
                rl_name_state.background = context?.let { ContextCompat.getDrawable(it, R.drawable.bg_boronze) }
//                img_star.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.star_boronze) })
                img_star.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.bg_info_boronze) })
            }

            "6" -> {
                rl_name_state.background = context?.let { ContextCompat.getDrawable(it, R.drawable.bg_golden) }
//                img_star.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.star_gold) })
                img_star.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.bg_info_boronze) })

            }
        }
    }

    //send edit profile request for change user data:
    private fun sendEditProfileRequest(comment: String) {

        val service = provider.getmService()

        disposable.add(service.editUserProfile(ClientConfig.API_V1, comment).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableSingleObserver<GeneralStatusResult>() {
            override fun onSuccess(result: GeneralStatusResult) {

                if (result.status == "request sent") {

                    ToastFactory().createToast(R.string.text_request_submitted, context)
                }
            }

            override fun onError(e: Throwable) {
                Log.e("profile_tag", "msg edit: ${e.message}")
            }
        }))
    }

    fun checkUserAllowingExchange() {

        disposable.add(provider.getmService().checkAllowExchange(ClientConfig.API_V1).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribeWith(object : DisposableSingleObserver<LotterySettingResult>() {
                    override fun onError(e: Throwable) {

                        Log.e("profile_tag", "msg allow exchange :${e.message}")
                    }

                    override fun onSuccess(data: LotterySettingResult) {

                        if (data.allow.equals("1"))
                            rl_point_balance.visibility = View.VISIBLE
                    }
                }))
    }

    private fun createExchangeDialog(isBalance: Boolean) {

        var observable: Single<GeneralStatusResult>
        val service = provider.getmService()

        fun sendExchangeRequest(amount: String) {

            observable = if (isBalance)
                service.convertBalanceToPoint(ClientConfig.API_V1, amount)
            else
                service.convertPointToBalance(ClientConfig.API_V1, amount)

            disposable.add(observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io()).subscribeWith(object : DisposableSingleObserver<GeneralStatusResult>() {
                        override fun onError(e: Throwable) {
//                            val error = (e as HttpException).code()
//                            if (error == 401) {
//                                startActivity(Intent(activity, SplashScreenActivity::class.java))
//                            } else if (error == 403) {
//                                PreferenceStorage.getInstance(context).saveToken("0")
//                                startActivity(Intent(context, SplashScreenActivity::class.java))
//                                activity!!.finish()
//                            }

                            Log.e("profile_tag", "msg exchange :${e.message}")
                        }

                        override fun onSuccess(data: GeneralStatusResult) {

                            var message = 0

                            when (data.status) {

                                "not enough balance" -> message = R.string.text_not_enough_balance
                                "not allowed process" -> message = R.string.text_not_allow
                                "not enough point" -> message = R.string.text_not_enough_score
                                "amount must be a multiple of 100" -> message = R.string.text_multiple_100
                                "insert successful" -> {
                                    message = R.string.text_success_done
                                    ProfileTools.getInstance().saveProfileInformation(context).setListener { EventBus.getDefault().post(RefreshBalanceEvent()) }
                                }
                                "process failed" -> message = R.string.text_process_failed

                            }
                            toastFactory.value.createToast(message, context)
                        }
                    }))
        }


        DialogFactory(context).createExchangeDialog(isBalance, profile_root, object : DialogFactory.DialogFactoryInteraction {
            override fun onDeniedButtonClicked(cancel_dialog: Boolean) {
            }

            override fun onAcceptButtonClicked(vararg strings: String?) {
                sendExchangeRequest(strings[0]!!)
            }
        })
    }

    override fun onClick(view: View) {

        when (view.id) {

//            R.id.rl_logout -> createConfirmExitDialog()
            R.id.rl_edit_profile -> {

                DialogFactory(context).createCommentDialog(object : DialogFactory.DialogFactoryInteraction {
                    override fun onAcceptButtonClicked(vararg params: String) {

                        sendEditProfileRequest(params[0])
                    }

                    override fun onDeniedButtonClicked(bool: Boolean) {
                        //grey interaction does'nt used
                    }
                }, getView())
            }
            R.id.rl_balance_point -> createExchangeDialog(true)
            R.id.rl_point_balance -> createExchangeDialog(false)

            R.id.rl_user_access_upgrade -> {

                goto_splash = true

                if (type.equals("1")) {
//                    var a = preferenceStorage?.retrivePhone()
                    goToHtmlActivity(ClientConfig.HTML_ADDRESS+"v2/user/register?mobile="+
                            preferenceStorage?.retrivePhone(), true)
                } else if (type.equals("4")) {
                    goToHtmlActivity(ClientConfig.HTML_ADDRESS+"v2/user/upgrade/"
                            + preferenceStorage?.retrivePhone(), true)
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefeshBalanceEvent(event: RefreshBalanceEvent) {

        print(event.toString())
        getUserProfile()
    }


    private fun goToHtmlActivity(url: String, shouldBeLoadUrl: Boolean) {
        val intent = Intent(context, HtmlLoaderActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("surveyDetails", false)
        intent.putExtra("isShopping", shouldBeLoadUrl)
        startActivity(intent)
//        this@ProfileFragment1.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }


    override fun onResume() {
        super.onResume()
        getUserProfile()

        if (goto_splash) {
            startActivity(Intent(context, SplashScreenActivity1::class.java))
        }
    }
}


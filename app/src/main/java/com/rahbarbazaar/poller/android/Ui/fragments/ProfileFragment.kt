package com.rahbarbazaar.poller.android.Ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
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
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

// Required empty public constructor
class ProfileFragment : Fragment(), View.OnClickListener {

    //region of property
    lateinit var provider: ServiceProvider
    lateinit var disposable: CompositeDisposable
    val toastFactory: Lazy<ToastFactory> = lazy { ToastFactory() }
    var getCurrencyListResult: GetCurrencyListResult? = null
    var lang: String? = null
    //end of region

    companion object {

        fun createInstance(getCurrencyListResult: GetCurrencyListResult, lang: String): ProfileFragment {

            val bundle = Bundle().apply {

                putParcelable("parcel_data", getCurrencyListResult)
                putString("lang", lang)
            }

            return ProfileFragment().apply { arguments = bundle }
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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        provider = ServiceProvider(context)
        disposable = CompositeDisposable()

        //initial ui
        getUserProfile()
        checkUserAllowingExchange()

        return view
    }

    //define views click listener will be appear here
    private fun defineViewClickListener() {

        rl_logout.setOnClickListener(this)
        rl_edit_profile.setOnClickListener(this)
        rl_balance_point.setOnClickListener(this)
        rl_point_balance.setOnClickListener(this)
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
        disposable.add(service.getUserProfile(ClientConfig.API_V1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableSingleObserver<UserConfirmAuthResult>() {

            @SuppressLint("SetTextI18n")
            override fun onSuccess(result: UserConfirmAuthResult) {

                text_age.text = result.birthday.toString()
                text_gender.text = if (result.gender == "male") "آقا" else "خانم"
                text_mobile.text = result.mobile
                text_username.text = result.name

                getCurrencyListResult?.let {

                    with(it.items[0]) {

                        text_point.text = result.balance.toString() + " " + if (lang == "fa")
                            currency_name
                        else
                            en_name
                    }

                    with(it.items[1]) {

                        text_score.text = result.score.toString() + " " + if (lang == "fa")
                            currency_name
                        else
                            en_name
                    }
                }

                text_project_count.text = result.participated_project_count.toString()
                text_user_state.text = result.membership
            }

            override fun onError(e: Throwable) {
                Log.e("profile_tag", "msg profile: ${e.message}")
            }
        }))
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
                        var a: Int = (e as HttpException).code()
                        if (a == 401) {
                            activity?.let{
                                val intent = Intent (it, SplashScreenActivity::class.java)
                                it.startActivity(intent)
                            }
                        }
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
                            var a: Int = (e as HttpException).code()
                            if (a == 401) {
                                activity?.let{
                                    val intent = Intent (it, SplashScreenActivity::class.java)
                                    it.startActivity(intent)
                                }
                            }
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

            R.id.rl_logout -> createConfirmExitDialog()
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
}

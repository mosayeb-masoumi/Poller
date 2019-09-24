package com.rahbarbazaar.poller.android.Utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.os.ConfigurationCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rahbarbazaar.poller.android.Models.SurveyMainModel;
import com.rahbarbazaar.poller.android.Models.UserDetailsPrefrence;
import com.rahbarbazaar.poller.android.R;
import com.rahbarbazaar.poller.android.Ui.activities.SplashScreenActivity1;
import com.wang.avi.AVLoadingIndicatorView;

public class DialogFactory {

    private Context context;

    public interface DialogFactoryInteraction {

        void onAcceptButtonClicked(String... strings);

        void onDeniedButtonClicked(boolean cancel_dialog);
    }

    public DialogFactory(Context ctx) {

        this.context = ctx;
    }

    public void createSurveyDetailsDialog(DialogFactoryInteraction listener, SurveyMainModel data, View root, String button_title) {

        if (context != null) {
//            View customLayout = LayoutInflater.from(context).inflate(R.layout.survey_details_dialog, (ViewGroup) root, false);
            View customLayout = LayoutInflater.from(context).inflate(R.layout.survey_details_dialog1, (ViewGroup) root, false);
            //define views inside of dialog
            TextView text_title = customLayout.findViewById(R.id.text_title);
            TextView text_time = customLayout.findViewById(R.id.text_time);
            TextView text_point = customLayout.findViewById(R.id.text_point);
            TextView text_status = customLayout.findViewById(R.id.text_status);
            TextView text_description = customLayout.findViewById(R.id.text_description);
            TextView btn_go_dialog = customLayout.findViewById(R.id.btn_go_dialog);
            TextView btn_cancel_dialog = customLayout.findViewById(R.id.btn_cancel_dialog);

            //initialize views
            text_title.setText(data.getTitle());
            btn_go_dialog.setText(button_title);

            if (button_title.contains("منقضی"))
                btn_go_dialog.setEnabled(false);

            text_time.setText(new StringBuilder().append(context.getString(R.string.text_survey_time)).append(" ")
                    .append(context.getString(R.string.text_from)).append(data.getStart_date(), 0, 10).append(" ").append(context.getString(R.string.text_until)).append(" ")
                    .append(data.getEnd_date(), 0, 10));
            text_point.setText(new StringBuilder().append(context.getString(R.string.text_point)).append(" ")
                    .append(data.getPoint()).append(" ").append(data.getCurrency().getName()));

            String status = null;
            switch (data.getStatus()) {

                case 1:
                    status = context.getString(R.string.text_no_answer);
                    break;

                case 2:
                    status = context.getString(R.string.text_pending);
                    break;

                case 3:
                    status = context.getString(R.string.text_survey_complete);
                    btn_go_dialog.setEnabled(false);
                    text_time.setText(new StringBuilder().append(context.getString(R.string.text_survey_time)).append(" ").append(context.getString(R.string.text_in)).append(data.getComplete_date(), 0, 10).append(context.getString(R.string.text_answer)));
                    break;

                case 0:
                    status = context.getString(R.string.text_survey_incomplete);
                    break;
            }

            text_status.setText(new StringBuilder().append(context.getString(R.string.text_survey_status)).append(" ").append(status));
            String description = data.getDescription() == null || data.getDescription().equals("") ? context.getString(R.string.text_does_not_have) : data.getDescription();
            text_description.setText(new StringBuilder().append(context.getString(R.string.text_survey_description)).append(" ").append(description));

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(customLayout);

            //create dialog and set background transparent
            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            //set click listener for views inside of dialog
            btn_go_dialog.setOnClickListener(view -> {

                dialog.dismiss();
                listener.onAcceptButtonClicked(data.getUrl());

            });
            btn_cancel_dialog.setOnClickListener(view -> dialog.dismiss());
            dialog.show();
        }
    }





    public void createSurveyDetailsDialogExpired(DialogFactoryInteraction listener, SurveyMainModel data, View root, String button_title) {

        if (context != null) {
//            View customLayout = LayoutInflater.from(context).inflate(R.layout.survey_details_dialog, (ViewGroup) root, false);
            View customLayout = LayoutInflater.from(context).inflate(R.layout.survey_details_dialog1_expired, (ViewGroup) root, false);
            //define views inside of dialog
            TextView text_title = customLayout.findViewById(R.id.text_title);
            TextView text_time = customLayout.findViewById(R.id.text_time);
            TextView text_point = customLayout.findViewById(R.id.text_point);
            TextView text_status = customLayout.findViewById(R.id.text_status);
            TextView text_description = customLayout.findViewById(R.id.text_description);
            TextView btn_go_dialog = customLayout.findViewById(R.id.btn_go_dialog);
            TextView btn_cancel_dialog = customLayout.findViewById(R.id.btn_cancel_dialog);

            //initialize views
            text_title.setText(data.getTitle());
            btn_go_dialog.setText(button_title);

            if (button_title.contains("منقضی"))
                btn_go_dialog.setEnabled(false);

            text_time.setText(new StringBuilder().append(context.getString(R.string.text_survey_time)).append(context.getString(R.string.text_from))
                    .append(data.getStart_date(), 0, 10).append(" ")
                    .append(context.getString(R.string.text_until)).append(" ").append(data.getEnd_date(), 0, 10));
            text_point.setText(new StringBuilder().append(context.getString(R.string.text_point))
                    .append(" ").append(data.getPoint()).append(" ")
                    .append(data.getCurrency().getName()));

            String status = null;
            switch (data.getStatus()) {

                case 1:
                    status = context.getString(R.string.text_no_answer);
                    break;

                case 2:
                    status = context.getString(R.string.text_pending);
                    break;

                case 3:
                    status = context.getString(R.string.text_survey_complete);
                    btn_go_dialog.setEnabled(false);
                    text_time.setText(new StringBuilder().append(context.getString(R.string.text_survey_time)).append(context.getString(R.string.text_in)).append(data.getComplete_date(), 0, 10).append(context.getString(R.string.text_answer)));
                    break;

                case 0:
                    status = context.getString(R.string.text_survey_incomplete);
                    break;
            }

            text_status.setText(new StringBuilder().append(context.getString(R.string.text_survey_status)).append(" ").append(status));
            String description = data.getDescription() == null || data.getDescription().equals("") ? context.getString(R.string.text_does_not_have) : data.getDescription();
            text_description.setText(new StringBuilder().append(context.getString(R.string.text_survey_description)).append(" ").append(description));

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(customLayout);

            //create dialog and set background transparent
            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            //set click listener for views inside of dialog
            btn_go_dialog.setOnClickListener(view -> {

                dialog.dismiss();
                listener.onAcceptButtonClicked(data.getUrl());

            });
            btn_cancel_dialog.setOnClickListener(view -> dialog.dismiss());
            dialog.show();
        }
    }

    public void createConfirmExitDialog(DialogFactoryInteraction listener, View view, boolean survey_exit_confirm) {

//        View customLayout = LayoutInflater.from(context).inflate(R.layout.confirm_exit_dialog, (ViewGroup) view, false);
        View customLayout = LayoutInflater.from(context).inflate(R.layout.confirm_exit_dialog1, (ViewGroup) view, false);
        //define views inside of dialog
        TextView btn_exit_dialog = customLayout.findViewById(R.id.btn_exit_dialog);
        TextView btn_cancel_dialog = customLayout.findViewById(R.id.btn_cancel_dialog);
        TextView text_body = customLayout.findViewById(R.id.text_body);

        if (survey_exit_confirm)
            text_body.setText(R.string.survey_exit_confirm);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        //set click listener for views inside of dialog
        btn_cancel_dialog.setOnClickListener(v -> dialog.dismiss());
        btn_exit_dialog.setOnClickListener(v -> listener.onAcceptButtonClicked("")
        );

        dialog.show();
    }


    public void createConfirmExitDialog2(DialogFactoryInteraction listener, View view, boolean survey_exit_confirm) {

//        View customLayout = LayoutInflater.from(context).inflate(R.layout.confirm_exit_dialog, (ViewGroup) view, false);
        View customLayout = LayoutInflater.from(context).inflate(R.layout.confirm_exit_dialog2, (ViewGroup) view, false);
        //define views inside of dialog
        TextView btn_exit_dialog = customLayout.findViewById(R.id.btn_exit_dialog);
        TextView btn_cancel_dialog = customLayout.findViewById(R.id.btn_cancel_dialog);
        TextView text_body = customLayout.findViewById(R.id.text_body);

        if (survey_exit_confirm)
            text_body.setText(R.string.survey_exit_confirm);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        //set click listener for views inside of dialog
//        btn_cancel_dialog.setOnClickListener(v -> dialog.dismiss());
        btn_cancel_dialog.setOnClickListener(v ->{
                    dialog.dismiss();
                    listener.onDeniedButtonClicked(false);
                });

        btn_exit_dialog.setOnClickListener(v -> listener.onAcceptButtonClicked("")
        );

        dialog.show();
    }



    public void createNoInternetDialog(DialogFactoryInteraction listener, View root) {

//        View customLayout = LayoutInflater.from(context).inflate(R.layout.confirm_exit_dialog, (ViewGroup) root, false);
        View customLayout = LayoutInflater.from(context).inflate(R.layout.confirm_exit_dialog1, (ViewGroup) root, false);
//        View customLayout = LayoutInflater.from(context).inflate(R.layout.no_internet_dialog1, (ViewGroup) root, false);

        //define views inside of dialog
//        ImageView image_dialog = customLayout.findViewById(R.id.image_dialog);
        TextView text_body = customLayout.findViewById(R.id.text_body);
        TextView btn_wifi_dialog = customLayout.findViewById(R.id.btn_exit_dialog);
        TextView btn_data_dialog = customLayout.findViewById(R.id.btn_cancel_dialog);

        //set default values of views
        text_body.setText(R.string.text_no_internet_connection);
        btn_wifi_dialog.setText(R.string.text_internet_setting);
        btn_data_dialog.setText(R.string.text_data_setting);
//        image_dialog.setImageResource(R.drawable.no_wifi);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        //set click listener for views inside of dialog
        btn_wifi_dialog.setOnClickListener(view -> listener.onAcceptButtonClicked(""));
        btn_data_dialog.setOnClickListener(view -> listener.onDeniedButtonClicked(false));

        //if dialog dismissed, this action will be called
        dialog.setOnDismissListener(dialogInterface -> listener.onDeniedButtonClicked(true));

        dialog.show();




// make change the size of dialog
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.height=300;
//        lp.width = 900;
//        dialog.getWindow().setAttributes(lp);

    }

    public void createCommentDialog(DialogFactoryInteraction listener, View root) {

        View customLayout = LayoutInflater.from(context).inflate(R.layout.edit_profile_comment_dialog1, (ViewGroup) root, false);
        //define views inside of dialog
        EditText edt_comment = customLayout.findViewById(R.id.edt_comment);
        TextView btn_send = customLayout.findViewById(R.id.btn_send_dialog);
//        TextView btn_cancel_dialog = customLayout.findViewById(R.id.btn_cancel_dialog);

        //set default typeface of views
//        edt_comment.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BYekan.ttf"));
        edt_comment.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Vazir-Medium.ttf"));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        //set click listener for views inside of dialog

        btn_send.setOnClickListener(view -> {

            String comment = edt_comment.getText().toString();

            if (comment.trim().length() > 0) {
                listener.onAcceptButtonClicked(comment);
                dialog.dismiss();
            } else
                new ToastFactory().createToast(R.string.text_subject_empty_error, context);
        });
//        btn_cancel_dialog.setVisibility(ViewGroup.GONE);

        dialog.show();
    }

    public void createReportIssueDialog(DialogFactoryInteraction listener, View root) {

        View customLayout = LayoutInflater.from(context).inflate(R.layout.report_issue_dialog1, (ViewGroup) root, false);


        //define views inside of dialog
        EditText edt_title = customLayout.findViewById(R.id.edt_title);
        EditText edt_description = customLayout.findViewById(R.id.edt_description);
        TextView btn_send = customLayout.findViewById(R.id.btn_send_dialog);
        TextView btn_cancel_dialog = customLayout.findViewById(R.id.btn_cancel_dialog);

        //set default values of views
        edt_title.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BYekan.ttf"));
        edt_description.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BYekan.ttf"));
//        edt_title.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ Vazir-Medium.ttf"));
//        edt_description.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ Vazir-Medium.ttf"));


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        //set click listener for views inside of dialog

        btn_send.setOnClickListener(view -> {

            listener.onAcceptButtonClicked(edt_title.getText().toString(), edt_description.getText().toString());
            dialog.dismiss();
        });

        btn_cancel_dialog.setOnClickListener(view ->{
            listener.onDeniedButtonClicked(false);
            dialog.dismiss();});

        dialog.show();
    }


    public void createAgreementDialog(DialogFactoryInteraction listener, View root) {

        View customLayout = LayoutInflater.from(context).inflate(R.layout.agreement_dialog, (ViewGroup) root, false);
        //String url_content = "<div style=\"margin:20px;\">\n<p dir=\"RTL\"><strong>سیاست حفظ حریم خصوصی</strong></p>\n\n<p dir=\"RTL\"><strong></strong>سیاست حفظ حریم خصوصی نشان دهنده چگونگی استفاده از اطلاعات شماست که توسط اپلیکیشن جمع آوری می شود. لطفا پیش از استفاده از اپلیکیشن و یا ارسال هرگونه اطلاعات، این قوانین را مطالعه فرمایید. سیاست حفظ حریم خصوصی تکمیل کننده شرایط راهبر بازار بوده و توافقی بین شرکت راهبر بازار و کاربرانش محسوب می شود.</p>\n\n<p dir=\"RTL\">قوانین حفظ حریم شخصی، کلیه وبسایت ها، محصولات و خدمات شرکت راهبر بازار و سایت ها و شرکت ها و نمایندگی های تابعه را شامل می شود، مگر در مواردی که صراحتا ذکر شده باشد. داده های شما هرگز با اشخاص ثالث به اشتراک گذارده نمی شود، مگر با رضایت شما و یا درخواست مراجع قانونی. ما به عنوان پردازشگر داده&zwnj;ها ممکن است اطلاعات شما را توسط خود و شرکت های زیر مجموعه خود، برای اهداف داخلی که در این سند به طور شفاف به آن&zwnj;ها اشاره شده است مورد استفاده و تحلیل قرار دهیم. در راهبر بازار افراد می&zwnj;توانند هر زمان که خودشان بخواهد اطلاعات خود را به روز رسانی ، حذف، اضافه و یا کلا تغییر دهند.</p>\n\n<p dir=\"RTL\">ما هرگز اطلاعات فردی شما را برای اهداف تبلیغاتی و بازاریابی به شرکت های دیگر نمی فروشیم و در اختیار اشخاص خارجی قرار نخواهیم داد. این امر شامل وبسایت&zwnj;ها و یا سایر اشخاصی که به ما در اداره کردن سایت، انجام کار و یا خدمات رسانی به کاربران مان کمک می کنند، تا زمانی که پذیرفته باشند این اطلاعات را محرمانه نگه دارند نمی شود. همچنین ممکن است براساس قانون و یا در جهت اعمال خط مشی ها و یا برای حفظ قانون مالکیت و یا امنیت خود و دیگران، اطلاعاتی را منتشر کنیم.</p>\n\n<p dir=\"RTL\">&nbsp;</p>\n\n<p dir=\"RTL\"><strong>حفظ اطلاعات شخصی</strong></p>\n\n<p dir=\"RTL\"><strong></strong>راهبر بازار اطلاعات فردی شما را با توجه به حساب کاربری&zwnj;تان ذخیره سازی می کند. اطلاعات شخصی شما، شامل اطلاعات نظرسنجی و یا هرگونه اطلاعات دیگری است که برخی اوقات وارد اپلیکیشن می کنید. ما اطلاعات شما را تا زمانی که نزد ما حساب کاربری داشته باشید حفظ خواهیم کرد. در صورت لغو و یا بسته شدن حساب کاربری تمامی اطلاعات شخصی شما و کپی آن&zwnj;ها پاک خواهد شد. اگر بنا به دلایل فنی و یا هر دلیل دیگری، ما موفق به پاک کردن اطلاعات&nbsp; شخصی شما نشویم مطمئن باشید تمامی راهکارها را به کار خواهیم بست تا از پردازش اطلاعات شما در آینده جلوگیری نماییم. این امر شامل اطلاعاتی که پس از پاک کردن حساب کاربری جهت حسابرسی ملزم به حفظ آن هستیم نمی شود.</p>\n\n<p dir=\"RTL\">ما با اتخاذ بالاترین سطح استانداردها، از به خطر افتادن اطلاعات شما در هر مقطعی از زمان جلوگیری خواهیم کرد. تقویت زیرساخت های <span dir=\"LTR\">IT</span> ، پلتفرم&zwnj;های امنیت داده&zwnj;ها و خط مشی&zwnj;های فناوری اطلاعات، به ما این امکان را داده است امنیت پیشرفته دو-سویه را به مشتریان خود ارائه دهیم.</p>\n\n<p dir=\"RTL\">&nbsp;</p>\n\n<p dir=\"RTL\"><strong>حقوق شما</strong></p>\n\n<p dir=\"RTL\"><strong></strong>شما می توانید مشکلات و دغدغه های خود را در هر زمان از طریق <a href=\"mailto:info@rahbarbazaar.com\"><span dir=\"LTR\">info@rahbarbazaar.com</span></a> با ما درمیان بگذارید. ما متعهد می شویم حداکثر ظرف 15 روز کاری با شما تماس بگیریم. این فهرستی از حقوقی می&zwnj;باشد که قوانین حفظ حریم خصوصی برای شما قائل شده است:</p>\n\n<ul dir=\"rtl\">\n\t<li>&nbsp;حق دسترسی به اطلاعاتتان</li>\n\t<li>&nbsp;حق محدود کردن و یا جلوگیری از پردازش اطلاعاتتان</li>\n\t<li>&nbsp;حق به روز رسانی و یا تغییر اطلاعاتتان</li>\n\t<li>&nbsp;حق پاک کردن اطلاعاتتان</li>\n\t<li>&nbsp;حق عدم پذیرش جهت دریافت ایمیل&zwnj;های بازاریابی ما</li>\n\t<li>حق انتقال اطلاعاتتان به پردازشگری دیگر</li>\n</ul>\n\n<p dir=\"RTL\">&nbsp;</p>\n\n<p dir=\"RTL\"><strong>اصلاح سیاست های حفظ حریم شخصی</strong></p>\n\n<p dir=\"RTL\"><strong></strong>راهبر بازار می&zwnj;تواند هر زمان، سیاست&zwnj;های حفظ حریم شخصی خود را تغییر دهد. ما نسخه تغییر یافته را بر روی سایت خود قرار خواهیم داد و در صورت بروز هرگونه تغییر در نحوه جمع آوری و بکارگیری اطلاعات، این تغییرات را از طریق برنامه به اطلاع شما خواهیم رساند.ادامه استفاده شما از خدمات ما به منزله موافقت شما با اصلاحات صورت گرفته در سیاست&zwnj;های حریم خصوصی ماست.</p>\n</div>"; //html url content

        //define views inside of dialog
        WebView webview_agreement = customLayout.findViewById(R.id.webview_agreement);
        CheckBox checkbox_agreement = customLayout.findViewById(R.id.checkbox_agreement);
        AVLoadingIndicatorView av_loading = customLayout.findViewById(R.id.av_loading);
        TextView btn_send = customLayout.findViewById(R.id.btn_login_dialog);
        TextView btn_cancel_dialog = customLayout.findViewById(R.id.btn_cancel_dialog);

        /*String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/BYekan.ttf\")}body {font-family: MyFont;font-size: medium;text-align: justify;}</style></head><body>";
        String pas = "</body></html>";*/

        //webview_agreement.loadDataWithBaseURL("", pish + url_content + pas, "text/html", "UTF-8", "");
        webview_agreement.loadUrl("http://pollerws.rahbarbazaar.com:2296/poller/v2/support/agreement/" + LocaleManager.getLocale(context.getResources()).getLanguage());
        webview_agreement.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                av_loading.smoothToHide();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        //set click listener for views inside of dialog
        btn_send.setOnClickListener(view -> {

            if (checkbox_agreement.isChecked())
                listener.onAcceptButtonClicked("");
            else
                new ToastFactory().createToast(R.string.text_accept_terms, context);

        });
        btn_cancel_dialog.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    public void createTokenDialog(View root) {

        View customLayout = LayoutInflater.from(context).inflate(R.layout.token_verify_dialog1, (ViewGroup) root, false);

        //define views inside of dialog
        TextView text_body = customLayout.findViewById(R.id.text_body);
        TextView btn_exit_dialog = customLayout.findViewById(R.id.btn_exit_dialog);
        PreferenceStorage preference = PreferenceStorage.getInstance(context);
        String user_details = preference.retriveUserDetails();

        UserDetailsPrefrence userDetails = new Gson().fromJson(user_details, UserDetailsPrefrence.class);
        text_body.setText(new StringBuilder().append("توکن (نشانه) شما این ").append(userDetails.getIdentity() == null || userDetails.getIdentity().equals("") ? "- - - -" : userDetails.getIdentity()).append(" است لطفا آن را در جای خالی وارد کنید."));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        btn_exit_dialog.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    //we can use create token dialog with little modify too:
    public void createNoRegisterDialog(View root, DialogFactoryInteraction listener) {

        View customLayout = LayoutInflater.from(context).inflate(R.layout.no_register_dialog, (ViewGroup) root, false);

        //define views inside of dialog
        TextView text_body = customLayout.findViewById(R.id.text_body);

        SpannableString ss = new SpannableString(context.getResources().getString(R.string.text_access_level));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://landing.poller.ir/register"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(context.getResources().getColor(R.color.colorPrimary));
            }
        };

        String locale_name = ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0).getLanguage();
        ss.setSpan(clickableSpan, locale_name.equals("fa") ? 53 : 51, locale_name.equals("fa") ? 58 : 59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        text_body.setText(ss);
        text_body.setMovementMethod(LinkMovementMethod.getInstance());
        text_body.setHighlightColor(Color.TRANSPARENT);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setOnDismissListener(dialogInterface -> {

            listener.onDeniedButtonClicked(true);
        });
        dialog.show();
    }
    //we can use create token dialog with little modify too:
    public void createNoRegisterDialog1(View root, DialogFactoryInteraction listener) {

        View customLayout = LayoutInflater.from(context).inflate(R.layout.no_register_dialog1, (ViewGroup) root, false);

        //define views inside of dialog
        TextView text_body = customLayout.findViewById(R.id.text_body);
        TextView btn_membership = customLayout.findViewById(R.id.btn_membership);

        btn_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://landing.poller.ir/register"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

//        SpannableString ss = new SpannableString(context.getResources().getString(R.string.text_access_level));
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View textView) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("http://landing.poller.ir/register"));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                context.startActivity(intent);
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setUnderlineText(false);
//                ds.setColor(context.getResources().getColor(R.color.colorPrimary));
//            }
//        };

//        String locale_name = ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0).getLanguage();
//        ss.setSpan(clickableSpan, locale_name.equals("fa") ? 53 : 51, locale_name.equals("fa") ? 58 : 59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        text_body.setText(ss);
//        text_body.setMovementMethod(LinkMovementMethod.getInstance());
//        text_body.setHighlightColor(Color.TRANSPARENT);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setOnDismissListener(dialogInterface -> {

            listener.onDeniedButtonClicked(true);
        });
        dialog.show();
    }

    public AlertDialog createCheckUpdateDialog(View view, DialogFactoryInteraction listener) {

//        View customLayout = LayoutInflater.from(context).inflate(R.layout.check_update_dialog, (ViewGroup) view, false);
        View customLayout = LayoutInflater.from(context).inflate(R.layout.check_update_dialog1, (ViewGroup) view, false);
        //define views inside of dialog
        TextView btn_dl_dialog = customLayout.findViewById(R.id.btn_dl_dialog);
//        ConstraintLayout google_layout = customLayout.findViewById(R.id.layout_gplay);  // main (old) dialog
//        ConstraintLayout bazaar_layout = customLayout.findViewById(R.id.layout_bazaar);  //  main (old) dialog
        RelativeLayout google_layout = customLayout.findViewById(R.id.layout_gplay);
        RelativeLayout bazaar_layout = customLayout.findViewById(R.id.layout_bazaar);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);
        builder.setCancelable(false);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        //set click listener for views inside of dialog
        btn_dl_dialog.setOnClickListener(v -> listener.onAcceptButtonClicked("")
        );

        google_layout.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.rahbarbazaar.poller.android"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        bazaar_layout.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://cafebazaar.ir/app/com.rahbarbazaar.poller.android/?l=fa"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        dialog.show();
        return dialog;
    }


    public AlertDialog createCheckUpdateOptionalDialog(View view, DialogFactoryInteraction listener) {

//        View customLayout = LayoutInflater.from(context).inflate(R.layout.check_update_dialog, (ViewGroup) view, false);
        View customLayout = LayoutInflater.from(context).inflate(R.layout.check_update_dialog_optional, (ViewGroup) view, false);
        //define views inside of dialog
        TextView btn_dl_dialog = customLayout.findViewById(R.id.btn_dl_dialog);
//        ConstraintLayout google_layout = customLayout.findViewById(R.id.layout_gplay);  // main (old) dialog
//        ConstraintLayout bazaar_layout = customLayout.findViewById(R.id.layout_bazaar);  //  main (old) dialog
        RelativeLayout google_layout = customLayout.findViewById(R.id.layout_gplay);
        RelativeLayout bazaar_layout = customLayout.findViewById(R.id.layout_bazaar);
        ImageView img_close = customLayout.findViewById(R.id.img_close);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);
        builder.setCancelable(false);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        //set click listener for views inside of dialog
        btn_dl_dialog.setOnClickListener(v -> listener.onAcceptButtonClicked("")
        );

        google_layout.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.rahbarbazaar.poller.android"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        bazaar_layout.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://cafebazaar.ir/app/com.rahbarbazaar.poller.android/?l=fa"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        return dialog;
    }





    public void createSelectLangDialog(View view, DialogFactoryInteraction interaction) {

        View customView = LayoutInflater.from(context).inflate(R.layout.select_lang_dialog1, (ViewGroup) view, false);

        TextView btn_en = customView.findViewById(R.id.btn_eng);
        TextView btn_fa = customView.findViewById(R.id.btn_fa);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_en.setOnClickListener(v -> {
            interaction.onDeniedButtonClicked(false);
            dialog.dismiss();
        });

        btn_fa.setOnClickListener(v -> {
            interaction.onAcceptButtonClicked();
            dialog.dismiss();
        });
        dialog.show();
    }

    public void createjoinToLotteryDialog(View view, DialogFactoryInteraction interaction) {

//        View customView = LayoutInflater.from(context).inflate(R.layout.join_to_lottery_dialog, (ViewGroup) view, false);
        View customView = LayoutInflater.from(context).inflate(R.layout.join_to_lottery_dialog1, (ViewGroup) view, false);

//        ImageView img_join_lottery = customView.findViewById(R.id.img_join_lottery);
        TextView img_join_lottery = customView.findViewById(R.id.txt_join_lottery);
        EditText edt_lottery_amount = customView.findViewById(R.id.edt_lottery_amount);
        edt_lottery_amount.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Vazir-Medium.ttf"));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        img_join_lottery.setOnClickListener(v -> {

            String amount = edt_lottery_amount.getText().toString().trim();

            if (amount.equals("")) {

                new ToastFactory().createToast(R.string.join_lottery_error, context);

            } else {

                interaction.onAcceptButtonClicked(edt_lottery_amount.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void createExchangeDialog(boolean isBalance, View view, DialogFactoryInteraction interaction) {

        View customView = LayoutInflater.from(context).inflate(R.layout.exchange_dialog1, (ViewGroup) view, false);

//        ImageView img_exchange_submit = customView.findViewById(R.id.img_exchange_submit);  R.layout.exchange_dialog
        CustomTextView img_exchange_submit = customView.findViewById(R.id.img_exchange_submit);
        TextView text_title = customView.findViewById(R.id.text_title);
        EditText edt_lottery_amount = customView.findViewById(R.id.edt_exchange_amount);

        if (isBalance) {
            text_title.setText(R.string.exchange_dialog_title_price);
            edt_lottery_amount.setHint(R.string.exchange_dialog_price_hint);
        }
        else {
            text_title.setText(R.string.exchange_dialog_title_point);
            edt_lottery_amount.setHint(R.string.exchange_dialog_point_hint);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        img_exchange_submit.setOnClickListener(v -> {

            String amount = edt_lottery_amount.getText().toString().trim();

            if (amount.equals("")) {

                new ToastFactory().createToast(R.string.join_lottery_error, context);

            } else {

                interaction.onAcceptButtonClicked(edt_lottery_amount.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }





    public void createTryAgainDialog(View view, DialogFactoryInteraction interaction) {

        View customView = LayoutInflater.from(context).inflate(R.layout.try_again_dialog1, (ViewGroup) view, false);

        TextView text_tryAgain = customView.findViewById(R.id.text_tryAgain);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


         text_tryAgain.setOnClickListener(v -> {
             dialog.dismiss();
         });

        dialog.show();
    }


    public void createCloseAppDialog(View view, DialogFactoryInteraction interaction) {

        View customView = LayoutInflater.from(context).inflate(R.layout.close_app_dialog1, (ViewGroup) view, false);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        dialog.show();
    }
}

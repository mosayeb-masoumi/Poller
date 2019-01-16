package com.rahbarbazaar.poller.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsObserver extends BroadcastReceiver {

    OnSmsObserverListener onSmsObserverListener;

    public interface OnSmsObserverListener{

        void onSmsReceived(String msg);
    }

    public void setOnSmsObserverListener(OnSmsObserverListener onSmsObserverListener) {
        this.onSmsObserverListener = onSmsObserverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;

            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    assert pdus != null;
                    msgs = new SmsMessage[pdus.length];

                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        //msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        //send sms body to main activity
                        onSmsObserverListener.onSmsReceived(msgBody);
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
    }
}

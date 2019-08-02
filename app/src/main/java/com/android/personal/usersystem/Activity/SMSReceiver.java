package com.android.personal.usersystem.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.common.SMSAPI.SMSAPI;

public class SMSReceiver  extends BroadcastReceiver {

    String TAG = "SMSReceiver";

    SMSAPI smsapi;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.e(TAG, "on receive sms");
        if(smsapi == null){
            smsapi = new SMSAPI();
        }

        final Handler handler = new Handler(context.getMainLooper());

        Thread waiter = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Exception while sleeping markSmsAsReadThread: " + e.getMessage());
                }

                final String message = smsapi.read(context);
                handler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     Log.e(TAG, message);
                                     Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                 }
                             }
                );

                Log.e(TAG, "on done logging received sms");
            }
        });
        waiter.start();
        Log.e(TAG, "on done receive sms");
    }
}

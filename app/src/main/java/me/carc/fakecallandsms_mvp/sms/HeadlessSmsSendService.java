package me.carc.fakecallandsms_mvp.sms;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import me.carc.fakecallandsms_mvp.alarm.SmsIntentService;
import me.carc.fakecallandsms_mvp.common.C;

/**
 * // TODO: 13/02/2018
 * Created by bamptonm on 7/4/17.
 */

public class HeadlessSmsSendService extends IntentService {

    private static final String TAG = HeadlessSmsSendService.class.getName();

    public HeadlessSmsSendService() {
        super(TAG);
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(C.DEBUG_ENABLED) {
            String action = intent.getAction();
            if (!TelephonyManager.ACTION_RESPOND_VIA_MESSAGE.equals(action)) {
                return;
            }

            Bundle extras = intent.getExtras();

            if (extras == null) {
                return;
            }

            Intent intentSms = new Intent();
            intentSms.setClass(getApplicationContext(), SmsIntentService.class);
//            intentSms.putExtra(C.TIME, contact.getTime() != 0 ? contact.getTime() : System.currentTimeMillis());
            startService(intentSms);

            FakeSmsReceiver.smsNotification(getApplicationContext(), intentSms);
        }
    }
}
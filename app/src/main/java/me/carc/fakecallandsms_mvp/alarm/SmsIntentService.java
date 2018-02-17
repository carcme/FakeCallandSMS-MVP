package me.carc.fakecallandsms_mvp.alarm;

import android.app.IntentService;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.Nullable;

import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.sms.FakeSmsReceiver;

/**
 * Add scheduled SMS to the default sms app
 * Created by bamptonm on 7/3/17.
 */

public class SmsIntentService extends IntentService {
    public SmsIntentService() {
        super("CallIntentService");
    }

    private static final int NOTIFY_ID = 1333;

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(NOTIFY_ID, new Notification());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent == null)
            return;

        if (Build.VERSION.SDK_INT >= 26)
            FakeSmsReceiver.smsNotification(getApplicationContext(), intent);

        String number = intent.hasExtra(C.NUMBER) ? intent.getStringExtra(C.NUMBER) : "Unknown";
        String message = intent.hasExtra(C.MESSAGE) ? intent.getStringExtra(C.MESSAGE) : "Unknown";
        String where = intent.hasExtra(C.SMS_TYPE) ? intent.getStringExtra(C.SMS_TYPE) : C.SMS_INBOX;

        //Put content values
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.ADDRESS, number);
        values.put(Telephony.Sms.BODY, message);

        //Insert the message
        if (C.HAS_L) {
            switch (where) {
                case C.SMS_INBOX:
                    getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);
                    break;
                case C.SMS_SENT:
                    getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, values);
                    break;
                case C.SMS_OUTBOX:
                    getContentResolver().insert(Telephony.Sms.Outbox.CONTENT_URI, values);
                    break;
                case C.SMS_DRAFT:
                    getContentResolver().insert(Telephony.Sms.Draft.CONTENT_URI, values);
                    break;
            }
        } else {
            getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        }
    }
}

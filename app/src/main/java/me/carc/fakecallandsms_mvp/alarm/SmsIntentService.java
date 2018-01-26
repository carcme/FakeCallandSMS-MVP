package me.carc.fakecallandsms_mvp.alarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import me.carc.fakecallandsms_mvp.R;
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

        Intent notificationIntent = new Intent(this, FakeSmsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "MY_CHANNEL")
                .setSmallIcon(R.drawable.ic_sms)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        startForeground(NOTIFY_ID, new Notification());
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent == null)
            return;

        if (Build.VERSION.SDK_INT >= 26) {
            FakeSmsReceiver.smsNotification(getApplicationContext(), intent);
        }

        String number = intent.hasExtra(C.NUMBER) ? intent.getStringExtra(C.NUMBER) : "Unknown";
        String message = intent.hasExtra(C.NUMBER) ? intent.getStringExtra(C.MESSAGE) : "Unknown";
        String where = intent.hasExtra(C.NUMBER) ? intent.getStringExtra(C.SMS_TYPE) : C.SMS_INBOX;
        long time = intent.getLongExtra(C.TIME, System.currentTimeMillis());

        //Put content values
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.ADDRESS, number);
        values.put(Telephony.Sms.DATE, time);
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

//        //Change my sms app to the last default sms
//        Intent smsPackageintent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//        //Get default sms app
//        smsPackageintent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultApp);
//        smsPackageintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(smsPackageintent);

//        FakeSmsReceiver.completeWakefulIntent(intent);
    }
}

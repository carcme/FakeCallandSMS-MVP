package me.carc.fakecallandsms_mvp.alarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import me.carc.fakecallandsms_mvp.CallIncomingActivity;
import me.carc.fakecallandsms_mvp.R;

/**
 * Catch an alarm and start activity
 *
 * Created by bamptonm on 7/3/17.
 */

public class CallIntentService extends IntentService {
    public CallIntentService() {
        super("CallIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        sendNotification();

        Intent i = new Intent(this, CallIncomingActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtras(intent.getExtras());
        startActivity(i);

//        CallAlarmReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);

        nBuilder.setDefaults(Notification.DEFAULT_ALL);
        nBuilder.setSmallIcon(R.drawable.ic_call, 1);
        nBuilder.setContentText("CallIntentService");
        nBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        nBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));//Color.rgb(255, 87, 34));

        nm.notify(2002, nBuilder.build());
    }
}

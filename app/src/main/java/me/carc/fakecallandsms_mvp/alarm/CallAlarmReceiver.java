package me.carc.fakecallandsms_mvp.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import me.carc.fakecallandsms_mvp.CallIncomingActivity;
import me.carc.fakecallandsms_mvp.R;

/**
 * Catch an alarm and pass to intent service
 */
public class CallAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = CallAlarmReceiver.class.getName();

    @Override
    public void onReceive(Context ctx, Intent intent) {

        CarcWakeLockHolder wl = new CarcWakeLockHolder(ctx);
        wl.aquireWakeLock();

        Intent callIntentService = new Intent(ctx, CallIncomingActivity.class);
        if(intent != null)
            callIntentService.putExtras(intent.getExtras());
        callIntentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(callIntentService);

        sendNotification(ctx);
    }

    private void sendNotification(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);

        nBuilder.setDefaults(Notification.DEFAULT_ALL);
        nBuilder.setSmallIcon(R.drawable.ic_call, 1);
        nBuilder.setContentTitle("CallAlarmReceiver");
        nBuilder.setContentText("CallAlarmReceiver");
        nBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        nBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));//Color.rgb(255, 87, 34));

        nm.notify(2002, nBuilder.build());
    }
}

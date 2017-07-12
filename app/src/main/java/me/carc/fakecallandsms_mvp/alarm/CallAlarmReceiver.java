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
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.utils.U;

/**
 * Catch an alarm and pass to intent service
 */
public class CallAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = C.DEBUG + U.getTag();

    @Override
    public void onReceive(Context ctx, Intent intent) {

//        sendNotification(ctx);



        CarcWakeLockHolder wl = new CarcWakeLockHolder(ctx);
        wl.aquireWakeLock();



/*

        Intent startCallIntent = new Intent(ctx, CallIncomingActivity.class);
        startCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startCallIntent.putExtras(intent.getExtras());
        ctx.startActivity(startCallIntent);
*/


        Intent callIntentService = new Intent(ctx, CallIncomingActivity.class);
        callIntentService.putExtras(intent.getExtras());
        callIntentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(callIntentService);
//        startWakefulService(ctx, callIntentService);

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

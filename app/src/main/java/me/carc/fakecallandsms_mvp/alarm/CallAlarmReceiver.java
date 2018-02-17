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
        if(intent != null && intent.getExtras() != null) {
            if(intent.hasExtra(C.NAME))
                callIntentService.putExtra(C.NAME, intent.getStringExtra(C.NAME));
            if(intent.hasExtra(C.NUMBER))
                callIntentService.putExtra(C.NUMBER, intent.getStringExtra(C.NUMBER));

            if(intent.hasExtra(C.IMAGE))
                callIntentService.putExtra(C.IMAGE, intent.getStringExtra(C.IMAGE));

            if(intent.hasExtra(C.VIBRATE))
                callIntentService.putExtra(C.VIBRATE, intent.getBooleanExtra(C.VIBRATE, false));

            if(intent.hasExtra(C.DURATION))
                callIntentService.putExtra(C.DURATION, intent.getLongExtra(C.DURATION, C.MAX_CALL_DURATION_DEFAULT));

            if(intent.hasExtra(C.CALLLOGS))
                callIntentService.putExtra(C.CALLLOGS, intent.getBooleanExtra(C.CALLLOGS, false));

            if(intent.hasExtra(C.RINGTONE))
                callIntentService.putExtra(C.RINGTONE, intent.getStringExtra(C.RINGTONE));

//            callIntentService.putExtras(intent.getExtras());
        }

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

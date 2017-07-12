package me.carc.fakecallandsms_mvp.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import me.carc.fakecallandsms_mvp.CallIncomingActivity;
import me.carc.fakecallandsms_mvp.R;

/**
 * Created by bamptonm on 7/7/17.
 */

public class CallService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        sendNotification();

        Intent startCallIntent = new Intent(this, CallIncomingActivity.class);
        startCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startCallIntent.putExtras(intent.getExtras());
        startActivity(startCallIntent);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }


    private void sendNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);

        nBuilder.setDefaults(Notification.DEFAULT_ALL);
        nBuilder.setSmallIcon(R.drawable.ic_call, 1);
        nBuilder.setContentTitle("CallService");
        nBuilder.setContentText("CallService");
        nBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        nBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));//Color.rgb(255, 87, 34));

        nm.notify(2002, nBuilder.build());
    }
}

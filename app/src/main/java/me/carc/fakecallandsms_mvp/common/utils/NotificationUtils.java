package me.carc.fakecallandsms_mvp.common.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;

/**
 * Created by bamptonm on 24/01/2018.
 */

@TargetApi(Build.VERSION_CODES.O)
public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;
    public static final String ANDROID_CHANNEL_ID = "me.carc.fakecallandsms_mvp.SMS";
    public static final String ANDROID_CHANNEL_NAME = "SMS CHANNEL";

    public NotificationUtils(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {

        NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID, ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        androidChannel.enableLights(true);
        androidChannel.enableVibration(true);
        androidChannel.setLightColor(Color.GREEN);
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        String uriTone = TinyDB.getTinyDB().getString(C.PREF_SMS_RING_TONE);
        if(!TextUtils.isEmpty(uriTone)) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build();
            androidChannel.setSound(Uri.parse(uriTone), audioAttributes);
        }

        getManager().createNotificationChannel(androidChannel);
    }

    public NotificationChannel getChannel() {
        return getManager().getNotificationChannel(ANDROID_CHANNEL_ID);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
}
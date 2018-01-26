package me.carc.fakecallandsms_mvp.stealth;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Calendar;

import me.carc.fakecallandsms_mvp.CallIncomingActivity;
import me.carc.fakecallandsms_mvp.common.C;


/**
 * Created by Carc.me on 18.02.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class PowerSendMessage extends BroadcastReceiver {

    public static final int POwER_MSG_ID = 777;

    private final String TAG = PowerSendMessage.class.getName();
    private static int mCode = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            resetCode();

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

            mCode++;

        } else if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            if (mCode == 2) {
                Log.d(TAG, "Unlocked");
                resetCode();
                doChecks(context);

            } else if (mCode > 2)
                resetCode();

            mCode++;

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            // Do nothing

        } else
            resetCode();
    }


    /**
     * Reset the unlock code
     */
    public void resetCode() {
        mCode = 0;
    }


    /**
     * Check it is ok to start the fake call
     *
     * @return true if ok
     */
    private void doChecks(Context ctx) {
        // Exit if user trying to adjust mp3 volume??
        AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isMusicActive())
            return;

        // Init this now - wasn't required previously
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Activity.ALARM_SERVICE);

        // Exit if on-going or pending call
        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {

            Intent intentAlarm = new Intent(ctx, CallIncomingActivity.class);

            final PendingIntent pendingIntent = PendingIntent.getActivity(ctx,
                    POwER_MSG_ID, intentAlarm, PendingIntent.FLAG_ONE_SHOT);
            
            long timeout;

            if (C.DEBUG_ENABLED)
                timeout = C.SECOND_MILLIS * 7; // 7 secs
            else
                timeout = C.MINUTE_MILLIS * 7; // 7 mins

            long now = Calendar.getInstance().getTime().getTime();

            alarmManager.set(AlarmManager.RTC_WAKEUP, now + timeout, pendingIntent);
        }
    }
}

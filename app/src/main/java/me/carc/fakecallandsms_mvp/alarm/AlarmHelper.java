package me.carc.fakecallandsms_mvp.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.utils.U;
import me.carc.fakecallandsms_mvp.model.FakeContact;

import static android.content.Context.ALARM_SERVICE;

public class AlarmHelper {

    private static final String TAG = C.DEBUG + U.getTag();

    public static final int CALL_ALARM_ID = 123;
    public static final int SMS_ALARM_ID = 124;

    private static AlarmHelper instance;
    private Context mContext;
    private AlarmManager alarmManager;

    public static AlarmHelper getInstance() {
        if (instance == null) {
            instance = new AlarmHelper();
        }
        return instance;
    }

    public void init(Context context) {
        this.mContext = context;
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    }


    public void setCallAlarmActivity(FakeContact contact) {
        // Setup the alarm with the new incoming call
//        Intent intentAlarm = new Intent(mContext, me.carc.fakecallandsms_mvp.CallIncomingActivity.class);
        Intent intentAlarm = new Intent();
        intentAlarm.setClassName("me.carc.fakecallandsms_mvp", "me.carc.fakecallandsms_mvp.CallIncomingActivity");

        intentAlarm.putExtra(C.NAME, contact.getName());
        intentAlarm.putExtra(C.NUMBER, contact.getNumber());
        intentAlarm.putExtra(C.IMAGE, contact.getImage());
        intentAlarm.putExtra(C.VIBRATE, contact.isVibrate());
        intentAlarm.putExtra(C.DURATION, contact.getDuration());
        intentAlarm.putExtra(C.CALLLOGS, contact.useCallLogs());
        intentAlarm.putExtra(C.RINGTONE, contact.getRingtone());

        final PendingIntent pendingIntent = PendingIntent.getActivity(mContext, CALL_ALARM_ID, intentAlarm,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        // if time has already occured while user is dithering, add 2secs and start alarm
        long now = Calendar.getInstance().getTime().getTime();

        if (contact.getTime() > now)
            setAlarm(mContext, contact.getTime(), pendingIntent);
        else
            setAlarm(mContext, now + 2000, pendingIntent);
    }


    private static void setAlarm(Context context, long time, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (C.HAS_M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        else if(C.HAS_L)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }



    private void setAlarmClock(long delta, PendingIntent pi) {

        AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(delta, pi);

        alarmManager.setAlarmClock(info, pi);

//        if (C.HAS_M) {
//            alarmManager.setAlarmClock(info, pi);
//        } else {
//            alarmManager.setAlarmClock(info, pi);
//        }
    }

    private void setAlarmRTC_WakeUp(long delta, PendingIntent pi) {
        if (C.HAS_M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, delta, pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, delta, pi);
        }
    }


    private void setAlarmERT_WakeUp(long delta, PendingIntent pi) {
        if (C.HAS_M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, delta, pi);
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, delta, pi);
        }
    }
}

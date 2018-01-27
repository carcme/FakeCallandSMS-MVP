package me.carc.fakecallandsms_mvp.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.model.FakeContact;
import me.carc.fakecallandsms_mvp.sms.FakeSmsReceiver;

import static android.content.Context.ALARM_SERVICE;

public class AlarmHelper {

    private static final String TAG = AlarmHelper.class.getName();

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

        Intent intentAlarm = getCallIntent();
        intentAlarm.putExtra(C.NAME, contact.getName());
        intentAlarm.putExtra(C.NUMBER, contact.getNumber());
        intentAlarm.putExtra(C.IMAGE, contact.getImage());
        intentAlarm.putExtra(C.VIBRATE, contact.isVibrate());
        intentAlarm.putExtra(C.DURATION, contact.getDuration());
        intentAlarm.putExtra(C.CALLLOGS, contact.useCallLogs());
        intentAlarm.putExtra(C.RINGTONE, contact.getRingtone());

        final PendingIntent pendingIntent = PendingIntent.getActivity(mContext, contact.getIndex(), intentAlarm,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        // if time has already occured while user is dithering, add 2secs and start alarm
        long now = Calendar.getInstance().getTime().getTime();

        if (contact.getTime() > now)
            setAlarm(mContext, contact.getTime(), pendingIntent);
        else
            setAlarm(mContext, now + 2000, pendingIntent);
    }

    public void setSmsAlarmActivity(FakeContact contact) {

        Intent intentSms = getSmsIntent(contact);

        // send notification now
        if (contact.getTime() < System.currentTimeMillis()) {
            intentSms.setClass(mContext, SmsIntentService.class);
            intentSms.putExtra(C.TIME, contact.getTime());
            mContext.startService(intentSms);

            FakeSmsReceiver.smsNotification(mContext, intentSms);

        } else {
            // send notification later
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), contact.getIndex(), intentSms,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

            setAlarm(mContext, contact.getTime(), pendingIntent);
        }
    }

    private Intent getCallIntent() {
        Intent intentAlarm = new Intent();
        intentAlarm.setClassName("me.carc.fakecallandsms_mvp", "me.carc.fakecallandsms_mvp.CallIncomingActivity");
        return intentAlarm;
    }

    private Intent getSmsIntent(FakeContact fakeContact) {

        Intent i = new Intent(mContext.getApplicationContext(), FakeSmsReceiver.class);
        i.putExtra(C.NAME, fakeContact.getName());
        i.putExtra(C.NUMBER, fakeContact.getNumber());
        i.putExtra(C.MESSAGE, fakeContact.getSmsMsg());
        i.putExtra(C.IMAGE, fakeContact.getImage());
        i.putExtra(C.SMS_TYPE, fakeContact.getSmsType());
        i.putExtra(C.SMS_DEFAULT_APP, TinyDB.getTinyDB().getString(C.SMS_DEFAULT_PACKAGE_KEY));
        return i;
    }

    public void removeAlarm(int id, FakeContact contact) {
        PendingIntent pendingIntent;
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT;
        if(id == C.TYPE_CALL) {
            pendingIntent = PendingIntent.getActivity(mContext, contact.getIndex(), getCallIntent(), flags);
        } else if(id == C.TYPE_SMS) {
            pendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), contact.getIndex(), getSmsIntent(contact), flags);
        } else {
            throw new RuntimeException(TAG + " - Shouldn't be here");
        }

        alarmManager.cancel(pendingIntent);
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
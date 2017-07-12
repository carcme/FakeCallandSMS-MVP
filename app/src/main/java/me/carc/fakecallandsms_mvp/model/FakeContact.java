package me.carc.fakecallandsms_mvp.model;

import me.carc.fakecallandsms_mvp.common.C;

/**
 * Created by bamptonm on 7/3/17.
 */

public class FakeContact {

    private int callType;
    private String name;
    private String number;
    private String image;
    private long date;
    private long time;
    private long duration;
    private boolean vibrate;
    private boolean useCallLogs;
    private String ringtone;


    private String smsMsg;
    private String smsType;

    public FakeContact() {
        // Init Default preferences
        this.smsType = C.SMS_INBOX;
        this.callType = C.CALL_INCOMING;
        this.date = 0;
        this.time = System.currentTimeMillis();
    }

    public String debug() {

        return name + ":"
                + number + ":"
                + image + ":"
                + time + ":"
                + vibrate + ":"
                + ringtone;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) { this.callType = callType; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) { this.time = time; }

    public long getDuration() { return duration; }

    public void setDuration(long duration) { this.duration = duration * C.SECOND_MILLIS; }


    public void setDate(long date) { this.date = date; }


    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public boolean useCallLogs() {
        return useCallLogs;
    }

    public void setUseCallLogs(boolean logs) {
        this.useCallLogs = logs;
    }


    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }


    /**
     * SMS ONLY
     */

    public String getSmsMsg() {
        return smsMsg;
    }

    public void setSmsMsg(String smsMsg) { this.smsMsg = smsMsg; }

    public String getSmsType() { return smsType; }

    public void setSmsType(String smsType) { this.smsType = smsType; }
}

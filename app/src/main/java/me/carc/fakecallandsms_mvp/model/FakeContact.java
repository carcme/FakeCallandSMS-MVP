package me.carc.fakecallandsms_mvp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

import me.carc.fakecallandsms_mvp.common.C;

/**
 * Holder for fake caller and database entry
 * Created by bamptonm on 7/3/17.
 */
@Keep
@Entity(tableName = "FakeContact")
public class FakeContact {

    @PrimaryKey
    @ColumnInfo(name = "keyID")
    private int index;
    @ColumnInfo(name = "callType")
    private int callType = -1;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "number")
    private String number;
    @ColumnInfo(name = "image")
    private String image;
    @ColumnInfo(name = "date")
    private long date;
    @ColumnInfo(name = "time")
    private long time;
    @ColumnInfo(name = "duration")
    private long duration;
    @ColumnInfo(name = "vibrate")
    private boolean vibrate;
    @ColumnInfo(name = "useCallLogs")
    private boolean useCallLogs;
    @ColumnInfo(name = "ringtone")
    private String ringtone;
    @ColumnInfo(name = "smsMsg")
    private String smsMsg;
    @ColumnInfo(name = "smsType")
    private String smsType = "";

    @ColumnInfo(name = "databaseType")
    private String databaseType;

    @Ignore
    public FakeContact() {
        // Init Default preferences
        this.smsType = C.SMS_INBOX;
        this.callType = C.CALL_INCOMING;
        this.date = 0;
        this.time = 0;
    }

    public FakeContact(int index, int callType, String name, String number, String image, long date, long time,
                       long duration, boolean vibrate, boolean useCallLogs, String ringtone, String smsMsg, String smsType) {
        this.index = index;
        this.callType = callType;
        this.name = name;
        this.number = number;
        this.image = image;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.vibrate = vibrate;
        this.useCallLogs = useCallLogs;
        this.ringtone = ringtone;
        this.smsMsg = smsMsg;
        this.smsType = smsType;
    }

    public String debug() {

        return name + ":"
                + number + ":"
                + image + ":"
                + time + ":"
                + vibrate + ":"
                + ringtone;
    }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public int getCallType() {
        return callType;
    }
    public void setCallType(int callType) {
        this.callType = callType;
    }

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
    public void setTime(long time) {
        this.time = time;
    }

    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration * C.SECOND_MILLIS;
    }

    public long getDate() { return date; }
    public void setDate(long date) {
        this.date = date;
    }

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
    public void setSmsMsg(String smsMsg) {
        this.smsMsg = smsMsg;
    }

    public String getSmsType() {
        return smsType;
    }
    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }


    /**
     * Database HELPER
     */
    public String getDatabaseType() { return databaseType; }

    public void setDatabaseType(String databaseType) { this.databaseType = databaseType; }
}

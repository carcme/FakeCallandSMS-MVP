package me.carc.fakecallandsms_mvp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

/**
 * Holder for fake caller and database entry
 * Created by bamptonm on 7/3/17.
 */
@Keep
@Entity(tableName = "PreSetMsg")
public class PreSetMsg {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "keyID")
    private int index;

    @ColumnInfo(name = "timeMs")
    private Long timeMs;

    @ColumnInfo(name = "msg")
    private String msg;
    @ColumnInfo(name = "number")
    private String number;
    @ColumnInfo(name = "name")
    private String name;


    public static PreSetMsg[] populateData() {
        return new PreSetMsg[] {
                new PreSetMsg(1,System.currentTimeMillis(), "Add your own", ""),
                new PreSetMsg(2, System.currentTimeMillis(), "Call me… URGENT!!", "Mum"),
                new PreSetMsg(3, System.currentTimeMillis(), "Don't worry, its sorted", "Dad")
        };
    }

    @Ignore
    public PreSetMsg() { }

    @Ignore
    public PreSetMsg(int index, Long timeMs, String msg, String name) {
        this.index = index;
        this.timeMs = timeMs;
        this.msg = msg;
        this.name = name;
    }

    public PreSetMsg(int index, Long timeMs, String msg, String number, String name) {
        this.index = index;
        this.timeMs = timeMs;
        this.msg = msg;
        this.number = number;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    public Long getTimeMs() { return timeMs; }
    public void setTimeMs() { this.timeMs = System.currentTimeMillis(); }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

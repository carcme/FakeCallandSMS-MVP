package me.carc.fakecallandsms_mvp.app;

import android.app.Application;
import android.content.Context;

import me.carc.fakecallandsms_mvp.alarm.AlarmHelper;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;

/**
 * The Application
 *
 * Created by bamptonm on 7/3/17.
 */

public class App extends Application {

    private static App mInstance;
    private static Context mCtx;
    private static boolean appRunning;

    public static synchronized App getInstance() {
        return mInstance;
    }
    public static synchronized Context getCtx() {
        return mCtx;
    }
    public static boolean isFakeCallerVisible() { return appRunning; }


    @Override
    public void onCreate() {
        super.onCreate();
        if(!C.DEBUG_ENABLED)
        mInstance = this;
        mCtx = getApplicationContext();

        if(TinyDB.getTinyDB() == null)
            new TinyDB(this);

        AlarmHelper.getInstance().init(getApplicationContext());
    }
}

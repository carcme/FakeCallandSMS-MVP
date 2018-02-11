package me.carc.fakecallandsms_mvp.app;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import me.carc.fakecallandsms_mvp.alarm.AlarmHelper;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.db.AppDatabase;

/**
 * The Application
 * <p>
 * Created by bamptonm on 7/3/17.
 */

public class App extends Application {

    private static final String FAKECALL_DATABASE_NAME = "fakecallandsms_mvp.db";

    private AppDatabase mDatabase;

    public synchronized AppDatabase getDB() {
        if (mDatabase == null)
            mDatabase = initDB();
        return mDatabase;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (!C.DEBUG_ENABLED) Fabric.with(this, new Crashlytics());

        if (TinyDB.getTinyDB() == null) new TinyDB(this);

        AlarmHelper.getInstance().init(getApplicationContext());

        mDatabase = initDB();
    }

    /**
     * Init database
     */
    private AppDatabase initDB() {
        return Room.databaseBuilder(getApplicationContext(), AppDatabase.class, FAKECALL_DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }
}

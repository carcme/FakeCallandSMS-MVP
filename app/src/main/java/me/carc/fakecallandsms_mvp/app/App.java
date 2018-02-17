package me.carc.fakecallandsms_mvp.app;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.os.StrictMode;

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

        // Crashlytyics #51 - FileUriExposedException: file:///xxx.mp3 exposed beyond app through Notification.sound
        // See this to do it properly with FileProvider
        // https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(C.DEBUG_ENABLED)
                .build();
        Fabric.with(fabric);

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

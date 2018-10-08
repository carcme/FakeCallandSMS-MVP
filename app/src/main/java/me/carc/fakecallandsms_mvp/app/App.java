package me.carc.fakecallandsms_mvp.app;

import android.app.Application;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import me.carc.fakecallandsms_mvp.BuildConfig;
import me.carc.fakecallandsms_mvp.alarm.AlarmHelper;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.common.utils.NotificationUtils;

/**
 * The Application
 * <p>
 * Created by bamptonm on 7/3/17.
 */

public class App extends Application {

    public static final String DATABASE_NAME = "fakecallandsms_mvp.db";

    @Override
    public void onCreate() {
        super.onCreate();

        // Crashlytyics #51 - FileUriExposedException: file:///xxx.mp3 exposed beyond app through Notification.sound
        // See this to do it properly with FileProvider
        // https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if(BuildConfig.USE_CRASHLYTICS) {
            final Fabric fabric = new Fabric.Builder(this)
                    .kits(new Crashlytics())
                    .debuggable(C.DEBUG_ENABLED)
                    .build();
            Fabric.with(fabric);
        }

        if (TinyDB.getTinyDB() == null) new TinyDB(this);

        AlarmHelper.getInstance().init(getApplicationContext());

        if (C.HAS_O)
            new NotificationUtils(this);
    }

}

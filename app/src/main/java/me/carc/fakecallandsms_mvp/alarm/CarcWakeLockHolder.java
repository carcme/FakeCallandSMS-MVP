package me.carc.fakecallandsms_mvp.alarm;

import android.content.Context;
import android.os.PowerManager;

import me.carc.fakecallandsms_mvp.common.C;

/**
 * WakeLock Singleton
 * Created by bamptonm on 7/7/17.
 */
@Deprecated
public class CarcWakeLockHolder {

    private PowerManager.WakeLock wl = null;

    private static CarcWakeLockHolder instance;

    public CarcWakeLockHolder(Context ctx) {
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, C.WAKE_LOCK_TAG);
        }
        instance = this;
    }

    public static CarcWakeLockHolder getInstance() {
        if(instance == null)
            return null;
        return instance;
    }

    public PowerManager.WakeLock getWakeLock() {
        return wl;
    }

    public void aquireWakeLock() {
        if (wl != null)
            wl.acquire(60000);
    }

    public void releaseWakeLock() {
        if (wl != null && wl.isHeld()) {
            wl.release();
            wl = null;
        }
    }
}

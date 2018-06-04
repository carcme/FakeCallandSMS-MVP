package me.carc.fakecallandsms_mvp.common.utils;

import android.app.Activity;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;

/**
 * Android specific utils
 * Created by bamptonm on 01/06/2018.
 */

public class AndroidUtils {

/*
    public static int isGooglePlayServicesAvailable(Context appCtx) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(appCtx);
    }
*/

    /**
     * Check if device is rooted
     *
     * @return true if rooted
     */
    public static boolean isRooted() {
        String buildTags = Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys"))
            return true;

        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists())
                return true;

        } catch (Exception ignore) {
        }

        return canExecute("/system/xbin/which su") ||
                canExecute("/system/bin/which su") ||
                canExecute("which su");
    }

    private static boolean canExecute(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void hideSoftKeyboard(final Activity activity, final View input) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            if (input != null) {
                IBinder windowToken = input.getWindowToken();
                if (windowToken != null) {
                    inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
                }
            }
        }

    }


}

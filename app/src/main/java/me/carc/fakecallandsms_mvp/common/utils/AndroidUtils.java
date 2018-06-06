package me.carc.fakecallandsms_mvp.common.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;

import me.carc.fakecallandsms_mvp.BuildConfig;
import me.carc.fakecallandsms_mvp.R;

/**
 * Android specific utils
 * Created by bamptonm on 01/06/2018.
 */

public class AndroidUtils {

    public final static String playStoreAppURI = "https://play.google.com/store/apps/details?id=";
    public final static String amznStoreAppURI = "https://www.amazon.com/gp/mas/dl/android?p=";


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

    public static String getVersion() {
        String versionName = BuildConfig.VERSION_NAME.concat(" (").concat(String.valueOf(BuildConfig.VERSION_CODE)).concat(")");
        if (BuildConfig.DEBUG)
            versionName += " " + BuildConfig.BUILD_TYPE;
        return versionName;
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

    public static void getOpenFacebookIntent(Activity context, String name) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + name));
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + name));
                context.startActivity(intent);
            } catch (Exception e1) {
                Toast.makeText(context, R.string.no_link_for_app, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void startTwitter(Activity context, String name) {
        try {
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
                context.startActivity(intent);
            } catch (Exception e1) {
                Toast.makeText(context, R.string.no_link_for_app, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void openApp(Activity context, String packageName) {
        String appURI = "market://details?id=" + packageName;
        String webURI = playStoreAppURI + packageName;
        openApplication(context, appURI, webURI);
    }

    public static void openPublisher(Activity context, String publisher, String packageName) {
        String appURI = null;
        String webURI = null;
        // see:
        // https://developer.android.com/distribute/marketing-tools/linking-to-google-play.html#OpeningPublisher
        // https://stackoverflow.com/questions/32029408/how-to-open-developer-page-on-google-play-store-market
        // https://issuetracker.google.com/65244694
        if (publisher.matches("\\d+")) {
            webURI = "https://play.google.com/store/apps/dev?id=8471035566675297849" + publisher;
            appURI = webURI;
        } else {
            appURI = "market://search?q=pub:" + publisher;
            webURI = "https://play.google.com/store/search?q=pub:" + publisher;
        }
        openApplication(context, appURI, webURI);
    }

    public static void openApplication(Activity context, String appURI, String webURI) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURI)));
        } catch (ActivityNotFoundException e1) {
            try {
                openHTMLPage(context, webURI);
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(context, R.string.no_link_for_app, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void openHTMLPage(Activity context, String htmlPath) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(htmlPath)));
    }

}

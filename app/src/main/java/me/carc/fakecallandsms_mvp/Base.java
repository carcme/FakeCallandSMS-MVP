package me.carc.fakecallandsms_mvp;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;

/**
 * Created by bamptonm on 7/2/17.
 */

public class Base extends AppCompatActivity {


    protected void showLaunchIcon() {
        TinyDB.getTinyDB().putBoolean(C.PREF_SHOW_LAUNCHER_ICON, false);

        final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName("me.carc.fakecallandsms_mvp",
                "me.carc.fakecallandsms_mvp.Launcher");
        PackageManager packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    protected void showWarningDialog(String title, String msg) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setMessage(msg)
                .show();

    }

    protected void showAlertDialog(String title,
                                   String message) {
        showAlertDialog(title, message, null, null, null, null);
    }

    /**
     * This method shows dialog with given title & message.
     * Also there is an option to pass onClickListener for positive & negative button.
     *
     * @param title                         - dialog title
     * @param message                       - dialog message
     * @param onPositiveButtonClickListener - listener for positive button
     * @param positiveText                  - positive button text
     * @param onNegativeButtonClickListener - listener for negative button
     * @param negativeText                  - negative button text
     */
    protected void showAlertDialog(String title,
                                   String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @Nullable String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @Nullable String negativeText) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        if (positiveText == null)
            positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);

        if (negativeText != null)
            builder.setNegativeButton(negativeText, onNegativeButtonClickListener);

        builder.show();
    }

}

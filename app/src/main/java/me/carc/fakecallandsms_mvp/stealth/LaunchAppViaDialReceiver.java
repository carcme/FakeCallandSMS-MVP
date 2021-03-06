package me.carc.fakecallandsms_mvp.stealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import me.carc.fakecallandsms_mvp.MainTabActivity;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;

/**
 * Launch from the dial pad
 * Created by bamptonm on 7/6/17.
 */

public class LaunchAppViaDialReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        TinyDB db = new TinyDB(ctx);
        String action = intent.getAction();

        if (!TextUtils.isEmpty(action) && action.equalsIgnoreCase("android.intent.action.NEW_OUTGOING_CALL")) {

            String dialPadNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            String dialCode = db.getString(C.PREF_DIAL_LAUNCHER);

            if (TextUtils.isEmpty(dialCode))
                dialCode = C.DIAL_PAD_LAUNCH_DEF_CODE;

            if (dialPadNumber != null && dialPadNumber.equals(dialCode)) {
                setResultData(null); // Kills the outgoing call

                Intent i = new Intent(ctx, MainTabActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);

            } else
                Log.d("DEAD", "onReceive: Wrong code");
        }
    }
}
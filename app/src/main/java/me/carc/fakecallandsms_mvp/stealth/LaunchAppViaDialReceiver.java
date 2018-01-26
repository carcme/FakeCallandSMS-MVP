package me.carc.fakecallandsms_mvp.stealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import me.carc.fakecallandsms_mvp.MainTabActivity;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.common.utils.Algorithms;

/**
 * Launch from the dial pad
 * Created by bamptonm on 7/6/17.
 */

public class LaunchAppViaDialReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {

        TinyDB db = new TinyDB(ctx);

        String dialPadNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        String dialCode = db.getString(C.PREF_DIAL_LAUNCHER);

        if(Algorithms.isEmpty(dialCode))
            dialCode = C.DIAL_PAD_LAUNCH_DEF_CODE;

        if (dialPadNumber.equals(dialCode)) {

            setResultData(null);  // needed??

            Intent i = new Intent(ctx, MainTabActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            Toast.makeText(ctx, "Call Scheduled", Toast.LENGTH_SHORT).show();

        } else
            Log.d("DEAD", "onReceive: Wrong code");
    }
}
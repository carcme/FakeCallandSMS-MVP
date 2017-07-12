package me.carc.fakecallandsms_mvp.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.utils.U;

/**
 * Created by bamptonm on 7/4/17.
 */

public class MmsReceiver extends BroadcastReceiver {
    private static final String TAG = C.DEBUG + U.getTag();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive: ");
    }
}
package me.carc.fakecallandsms_mvp.common;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.CallLog;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import me.carc.fakecallandsms_mvp.R;

/**
 * Call log util - add entry to the call logs
 * Created by bamptonm on 7/11/17.
 */

public class CallLogUtil {

    @RequiresPermission(Manifest.permission.WRITE_CALL_LOG)
    public static void addCallToLog(Context ctx, String contact, String number, long duration, int type, long time) {

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            return;

        if (contact == null && number == null)
            contact = ctx.getString(R.string.unknown_caller);

        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, number != null ? number : contact);
        values.put(CallLog.Calls.DATE, time);
        values.put(CallLog.Calls.DURATION, (duration / 1000));
        values.put(CallLog.Calls.TYPE, type);
        values.put(CallLog.Calls.NEW, 1);
        values.put(CallLog.Calls.CACHED_NAME, contact);
        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
        values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");

        try {
            ctx.getApplicationContext().getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
        }catch(IllegalArgumentException e) {
            Log.w(CallLogUtil.class.getName(), "Cannot insert call log entry. Probably not a phone", e);
        }
    }
}

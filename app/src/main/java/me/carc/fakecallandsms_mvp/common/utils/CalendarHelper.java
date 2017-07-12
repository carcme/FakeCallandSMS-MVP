package me.carc.fakecallandsms_mvp.common.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import java.util.Calendar;

import me.carc.fakecallandsms_mvp.common.C;

/**
 * Picker helper functions
 * Created by bamptonm on 7/5/17.
 */

public class CalendarHelper {

    private static final String TAG = C.DEBUG + U.getTag();

    private Context ctx;

    public CalendarHelper(Context context) {
        ctx = context;
    }

    public void datePicker(DatePickerDialog.OnDateSetListener listener) {
        Calendar calendarInst = Calendar.getInstance();
        int year = calendarInst.get(Calendar.YEAR);
        int month = calendarInst.get(Calendar.MONTH);
        int day = calendarInst.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(ctx, listener, year, month, day).show();
    }

    public void timePicker(TimePickerDialog.OnTimeSetListener  listener) {
        Calendar calendarInst = Calendar.getInstance();
        int hour = calendarInst.get(Calendar.HOUR_OF_DAY);
        int minute = calendarInst.get(Calendar.MINUTE);
        new TimePickerDialog(ctx, listener, hour, minute, false).show();
    }

}

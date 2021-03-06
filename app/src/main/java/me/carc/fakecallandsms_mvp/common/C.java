package me.carc.fakecallandsms_mvp.common;

import android.os.Build;
import android.provider.CallLog;

import me.carc.fakecallandsms_mvp.BuildConfig;

/**
 * Created by Carc.me on 16.01.16.
 * <p/>
 * Define the application constants
 */
public class C {

    public static final boolean DEBUG_ENABLED = BuildConfig.DEBUG;

    public static final String DEBUG ="DEBUG";

    public static final String WAKE_LOCK_TAG ="FAKE_CALL_WAKE_LOCK_TAG";
    public static final String SMS_DEFAULT_PACKAGE_KEY = "SMS_DEFAULT_PACKAGE_KEY";
    public static final String FILE_TMP = "temp.jpg";

    public static final String DIAL_PAD_LAUNCH_DEF_CODE = "123";
    public static final int QUICK_TIME_DEFAULT = 7;
    public static final int MAX_CALL_DURATION_DEFAULT = 60;

    public static final String SP_UNSPLASH_PHOTO_LIST = "UNSPLASH_PHOTO_LIST_V2";
    public static final String SP_FAVORITE_PHOTO_LIST = "SP_FAVORITE_PHOTO_LIST";


    public static final int CALL_INCOMING = CallLog.Calls.INCOMING_TYPE;
    public static final int CALL_OUTGOING = CallLog.Calls.OUTGOING_TYPE;
    public static final int CALL_MISSED   = CallLog.Calls.MISSED_TYPE;

    public static final int TYPE_CALL = 0;
    public static final int TYPE_SMS  = 1;

    public static final String SMS_INBOX = "SMS_INBOX";
    public static final String SMS_SENT  = "SMS_SENT";
    public static final String SMS_OUTBOX = "SMS_OUTBOX";
    public static final String SMS_DRAFT  = "SMS_DRAFT";

    public static final int PICK_CONTACT = 2000;

    // CALL
    public static final String NAME     = "NAME";
    public static final String NUMBER   = "NUMBER";
    public static final String IMAGE    = "IMAGE";
    public static final String VIBRATE  = "VIBRATE";
    public static final String DURATION = "DURATION";
    public static final String CALLLOGS = "CALLLOGS";
    public static final String RINGTONE = "RINGTONE";
    public static final String TIME     = "TIME";

    // SMS
    public static final String SMS_DEFAULT_APP = "SMS_DEFAULT_APP";
    public static final String MESSAGE         = "MESSAGE";
    public static final String SMS_TYPE        = "SMS_TYPE";
    public static final String MMS_SUBJECT     = "MMS_SUBJECT";
    public static final String MMS_IMAGE_PATH  = "MMS_IMAGE_PATH";


    public static final String PREF_SHOW_LAUNCHER_ICON = "PREF_SHOW_LAUNCHER_ICON";
    public static final String PREF_QUICK_TIME = "PREF_QUICK_TIME";
    public static final String PREF_BACKGROUND_VOICE_DISPLAY = "PREF_BACKGROUND_VOICE_DISPLAY";
    public static final String PREF_BACKGROUND_VOICE = "PREF_BACKGROUND_VOICE";
    public static final String PREF_RING_TONE = "PREF_RING_TONE";
    public static final String PREF_SMS_RING_TONE_DISPLAY = "PREF_SMS_RING_TONE_DISPLAY";
    public static final String PREF_SMS_RING_TONE = "PREF_SMS_RING_TONE";
    public static final String PREF_MAX_CALL_DURATION = "PREF_MAX_CALL_DURATION";
    public static final String PREF_DIAL_LAUNCHER = "PREF_DIAL_LAUNCHER";
    public static final String PREF_RESET_SMS_ON_EXIT = "PREF_RESET_SMS_ON_EXIT";




    public static final int SECOND_MILLIS = 1000;
    public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;

    public static final long PING_TIMEOUT  = SECOND_MILLIS;

    public static final int CALL_REJECT = 0;
    public static final int CALL_ACCEPT = 1;
    public static final int CALL_END = 2;

    public static Integer SCREEN_WIDTH;
    public static Integer SCREEN_HEIGHT;

//    public static final boolean HAS_K = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    public static final boolean HAS_L = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    public static final boolean HAS_M = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    public static final boolean HAS_N = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    public static final boolean HAS_O = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

}
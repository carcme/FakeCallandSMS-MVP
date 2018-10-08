package me.carc.fakecallandsms_mvp.alarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.Objects;

import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.utils.NotificationUtils;
import me.carc.fakecallandsms_mvp.common.utils.U;
import me.carc.fakecallandsms_mvp.sms.FakeSmsReceiver;

import static android.provider.Telephony.Threads.getOrCreateThreadId;

/**
 * Add scheduled SMS to the default sms app
 * Created by bamptonm on 7/3/17.
 */

public class SmsIntentService extends IntentService {
    private static final String TAG = SmsIntentService.class.getName();
    private static final int NOTIFY_ID = 1333;

    private static final String MMS_URI = "content://mms";
    private static final String MMS_URI_PATH = MMS_URI + "/";


    private static final String ADDRESS_COLUMN_NAME = "address";
    private static final String DATE_COLUMN_NAME = "date";
    private static final String BODY_COLUMN_NAME = "body";

    public SmsIntentService() {
        super("CallIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            new NotificationUtils(this);

            Notification notification = new NotificationCompat.Builder(this, NotificationUtils.ANDROID_CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(NOTIFY_ID, notification);
        } else
            startForeground(NOTIFY_ID, new Notification());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent == null)
            return;

        if (Build.VERSION.SDK_INT >= 26)
            FakeSmsReceiver.smsNotification(getApplicationContext(), intent);

        String number = intent.hasExtra(C.NUMBER) ? intent.getStringExtra(C.NUMBER) : "Unknown";
        String message = intent.hasExtra(C.MESSAGE) ? intent.getStringExtra(C.MESSAGE) : "";
        String where = intent.hasExtra(C.SMS_TYPE) ? intent.getStringExtra(C.SMS_TYPE) : C.SMS_INBOX;
        String mmsImage = intent.hasExtra(C.MMS_IMAGE_PATH) ? intent.getStringExtra(C.MMS_IMAGE_PATH) : null;
        String mmsSubject = intent.hasExtra(C.MMS_SUBJECT) ? intent.getStringExtra(C.MMS_SUBJECT) : null;

        if (TextUtils.isEmpty(mmsImage) && TextUtils.isEmpty(mmsSubject)) {
            ContentValues values = new ContentValues();
            values.put(Telephony.Sms.ADDRESS, number);
            values.put(Telephony.Sms.BODY, message);

            //Insert the message
            if (C.HAS_L) {
                switch (where) {
                    case C.SMS_INBOX:
                        getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);
                        break;
                    case C.SMS_SENT:
                        getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, values);
                        break;
                    case C.SMS_OUTBOX:
                        getContentResolver().insert(Telephony.Sms.Outbox.CONTENT_URI, values);
                        break;
                    case C.SMS_DRAFT:
                        getContentResolver().insert(Telephony.Sms.Draft.CONTENT_URI, values);
                        break;
                }
            } else {

                getContentResolver().insert(Uri.parse("content://sms/sent"), values);
            }
        } else {
            int msgBox;
            Bitmap bitmap = null;

            if (mmsImage != null) bitmap = BitmapFactory.decodeFile(mmsImage);

            switch (where) {
                case C.SMS_SENT:
                    msgBox = Telephony.Mms.MESSAGE_BOX_SENT;
                    break;
                case C.SMS_OUTBOX:
                case C.SMS_DRAFT:
                    msgBox = Telephony.Mms.MESSAGE_BOX_OUTBOX;
                    break;
                default:
                case C.SMS_INBOX:
                    msgBox = Telephony.Mms.MESSAGE_BOX_INBOX;
                    break;
            }
            insert(this, number, message, mmsSubject, msgBox, bitmap != null ? U.bitmapToByteArray(bitmap) : null);
        }
    }

    public static Uri insert(Context context, String to, String message, String subject, int msgBox, byte[] imageBytes) {
        try {
            Uri destUri = Uri.parse(MMS_URI);

            // Get thread id
            long thread_id = getOrCreateThreadId(context, to);
            long date = SystemClock.currentThreadTimeMillis() / 1000;
            Log.e(TAG, "Thread ID is " + thread_id);

            // Create a dummy sms
            ContentValues dummyValues = new ContentValues();
            dummyValues.put(Telephony.Sms.THREAD_ID, thread_id);
            dummyValues.put(Telephony.Sms.ADDRESS, to);
            dummyValues.put(Telephony.Sms.BODY, message);
            dummyValues.put(Telephony.Sms.DATE, date);
            Uri dummySms = context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, dummyValues);

            // Create a new message entry
            long now = System.currentTimeMillis();
            ContentValues mmsValues = new ContentValues();
            mmsValues.put(Telephony.Mms.THREAD_ID, thread_id);
            mmsValues.put(Telephony.Mms.DATE, now / 1000L);
            mmsValues.put(Telephony.Mms.MESSAGE_BOX, msgBox);
            mmsValues.put(Telephony.Mms.READ, 1);
            mmsValues.put(Telephony.Mms.SUBJECT, subject);

            mmsValues.put(Telephony.Mms.SUBJECT_CHARSET, 106);
            mmsValues.put(Telephony.Mms.CONTENT_TYPE, "application/vnd.wap.multipart.related");
            mmsValues.put(Telephony.Mms.EXPIRY, imageBytes != null ? imageBytes.length : 0);
            mmsValues.put(Telephony.Mms.MESSAGE_CLASS, "personal");
            mmsValues.put(Telephony.Mms.MESSAGE_TYPE, 128); // 132 (RETRIEVE CONF) 130 (NOTIF IND) 128 (SEND REQ)
            mmsValues.put(Telephony.Mms.MMS_VERSION, 19);
            mmsValues.put(Telephony.Mms.PRIORITY, 129);
            mmsValues.put(Telephony.Mms.TRANSACTION_ID, "T" + Long.toHexString(now));
            mmsValues.put(Telephony.Mms.RESPONSE_STATUS, 128);

            // Insert message
            Uri res = context.getContentResolver().insert(destUri, mmsValues);
            String messageId = null;
            if (res != null) {
                messageId = res.getLastPathSegment().trim();
                Log.e(TAG, "Message saved as " + res);

                // Create part
                createPartText(context, messageId, message);
                if (imageBytes != null)
                    createPartImage(context, messageId, imageBytes);

                // Create addresses
                createAddr(context, messageId, to);

                // Delete dummy sms from database
                String mSelectionClause = ADDRESS_COLUMN_NAME + " = ? AND " + BODY_COLUMN_NAME + " = ? AND " + DATE_COLUMN_NAME + " = ?";
                String[] mSelectionArgs = new String[3];
                mSelectionArgs[0] = to;
                mSelectionArgs[1] = message;
                mSelectionArgs[2] = String.valueOf(date);

                if (context.getContentResolver().delete(Uri.parse("content://sms"), mSelectionClause, mSelectionArgs) > 0)
                    Log.d(TAG, "Deleted Dummy SMS");
                else
                    Log.d(TAG, "Delete Dummy SMS FAILED!");

            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void createPartText(Context context, String id, String text) throws Exception {
        ContentValues mmsPartValue = new ContentValues();
        mmsPartValue.put(Telephony.Mms.Part.MSG_ID, id);
        mmsPartValue.put(Telephony.Mms.Part.CONTENT_TYPE, "text/plain");
        mmsPartValue.put(Telephony.Mms.Part.CONTENT_ID, "<" + System.currentTimeMillis() + ">");
        mmsPartValue.put(Telephony.Mms.Part.TEXT, text);
        Uri partUri = Uri.parse(MMS_URI_PATH + id + "/part");

        context.getContentResolver().insert(partUri, mmsPartValue);
    }

    private static void createPartImage(Context context, String id, byte[] imageBytes) throws Exception {
        ContentValues mmsPartValue = new ContentValues();
        mmsPartValue.put(Telephony.Mms.Part.MSG_ID, id);
        mmsPartValue.put(Telephony.Mms.Part.CONTENT_TYPE, "image/jpeg");
        mmsPartValue.put(Telephony.Mms.Part.CONTENT_ID, "<" + System.currentTimeMillis() + ">");
        Uri partUri = Uri.parse(MMS_URI_PATH + id + "/part");
        Uri res = context.getContentResolver().insert(partUri, mmsPartValue);

        if (res != null) {
            // Add image data to part
            try {
                OutputStream os = context.getContentResolver().openOutputStream(res);
                ByteArrayInputStream is = new ByteArrayInputStream(imageBytes);
                byte[] buffer = new byte[256];
                for (int len; (len = is.read(buffer)) != -1; ) {
                    assert os != null;
                    os.write(buffer, 0, len);
                }
                assert os != null;
                os.close();
                is.close();
            } catch (NullPointerException ignored) { /* IGNORED */ }
        }
    }

    private static void createAddr(Context context, String id, String address) throws Exception {
        ContentValues addrValues = new ContentValues();
        addrValues.put(Telephony.Mms.Addr.ADDRESS, address);
        addrValues.put(Telephony.Mms.Addr.CHARSET, "106");
        addrValues.put(Telephony.Mms.Addr.TYPE, 151); // PduHeaders.TO
        Uri addrUri = Uri.parse(MMS_URI_PATH + id + "/addr");
        context.getContentResolver().insert(addrUri, addrValues);
    }
}

package me.carc.fakecallandsms_mvp.sms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.InputStream;

import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.alarm.SmsIntentService;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.common.utils.NotificationUtils;
import me.carc.fakecallandsms_mvp.common.utils.U;

/**
 * Catch the sms alarm expire
 * Created by bamptonm on 7/4/17.
 */

public class FakeSmsReceiver extends BroadcastReceiver {

    private static final String TAG = C.DEBUG + U.getTag();
    private static final int NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Insert the sms to the sms database
        Intent insertSmsIntent = new Intent(context, SmsIntentService.class);
        if (intent != null)
            insertSmsIntent.putExtras(intent);

        insertSmsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextCompat.startForegroundService(context, insertSmsIntent);

        if (Build.VERSION.SDK_INT < 26) {
            smsNotification(context, intent);
        }
    }

    public static void smsNotification(Context context, Intent intent) {
        // extract the sms parameters
        String name = intent.hasExtra(C.NAME) ? intent.getStringExtra(C.NAME) : "Unknown";
        String number = intent.hasExtra(C.NUMBER) ? intent.getStringExtra(C.NUMBER) : "Unknown";
        String message = intent.hasExtra(C.MESSAGE) ? intent.getStringExtra(C.MESSAGE) : "Received message was corrupted.";
        String contactImage = intent.hasExtra(C.IMAGE) ? intent.getStringExtra(C.IMAGE) : null;

        // SMS open intent
        Intent openSmsIntent = new Intent(Intent.ACTION_MAIN);
        openSmsIntent.addCategory(Intent.CATEGORY_APP_MESSAGING);
        PendingIntent showSMSIntent = PendingIntent.getActivity(context, 0, openSmsIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap icon = null;
        if (contactImage != null && !contactImage.equals("")) {
            Uri contactImageUri = Uri.parse(contactImage);

            try {
                ContentResolver contentResolver = context.getContentResolver();
                InputStream contactImageStream = contentResolver.openInputStream(contactImageUri);
                icon = BitmapFactory.decodeStream(contactImageStream);
                icon.setHasMipMap(true);
            } catch (Exception e) { /*EMPTY*/ }
        }

        Notification.Builder nBuilder;

        if (C.HAS_O)
            nBuilder = new Notification.Builder(context, NotificationUtils.ANDROID_CHANNEL_ID);
        else
            nBuilder = new Notification.Builder(context);

        if (icon != null) nBuilder.setLargeIcon(getCircleBitmap(icon));
        if (TextUtils.isEmpty(name))
            nBuilder.setContentTitle(number);
        else
            nBuilder.setContentTitle(name);

        nBuilder.setSmallIcon(R.drawable.ic_sms, 1);
        nBuilder.setContentText(message);
        nBuilder.setDefaults(Notification.DEFAULT_ALL);
        nBuilder.setPriority(Notification.PRIORITY_HIGH);
        nBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));//Color.rgb(255, 87, 34));

        if (C.HAS_M) {
            nBuilder.addAction(new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.ic_reply), "Reply", showSMSIntent).build());
            nBuilder.addAction(new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.ic_read), "Read", showSMSIntent).build());
            nBuilder.addAction(new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.ic_call), "Call", showSMSIntent).build());
        } else {
            nBuilder.addAction(R.drawable.ic_reply, "Reply", showSMSIntent);
            nBuilder.addAction(R.drawable.ic_read, "Read", showSMSIntent);
            nBuilder.addAction(R.drawable.ic_call, "Call", showSMSIntent);

            // get custom ringtone
            String uriTone = TinyDB.getTinyDB().getString(C.PREF_SMS_RING_TONE);
            if (!TextUtils.isEmpty(uriTone)) {
                nBuilder.setSound(Uri.parse(uriTone));
                nBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            }
        }

        nBuilder.setContentIntent(showSMSIntent);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(NOTIFICATION_ID, nBuilder.build());
//            nm.notify(2001, nBuilder.build());
        }
    }

    /**
     * Create a round bitmap
     *
     * @param bitmap the bitmap
     * @return the round bitmap
     */
    private static Bitmap getCircleBitmap(Bitmap bitmap) {

        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();

        return output;
    }
}
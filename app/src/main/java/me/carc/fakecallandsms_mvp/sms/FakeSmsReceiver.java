package me.carc.fakecallandsms_mvp.sms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;

import java.io.InputStream;

import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.alarm.SmsIntentService;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.utils.Algorithms;
import me.carc.fakecallandsms_mvp.common.utils.U;

/**
 * Created by bamptonm on 7/4/17.
 */

public class FakeSmsReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = C.DEBUG + U.getTag();

    @Override
    public void onReceive(Context context, Intent intent) {

        // Insert the sms to the sms database
        Intent insertSmsIntent = new Intent(context, SmsIntentService.class);
        insertSmsIntent.putExtras(intent.getExtras());
        insertSmsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startWakefulService(context, insertSmsIntent);

        smsNotification(context, intent);

    }


    public static void smsNotification(Context context, Intent intent) {

        // extract the sms parameters
        String name = intent.getStringExtra(C.NAME);
        String number = intent.getStringExtra(C.NUMBER);
        String message = intent.getStringExtra(C.MESSAGE);
        String contactImage = intent.getStringExtra(C.IMAGE);

        // Send sms notification
        Intent smsNotication = new Intent(Intent.ACTION_MAIN);
        smsNotication.addCategory(Intent.CATEGORY_DEFAULT);
        smsNotication.setType("vnd.android-dir/mms-sms");
        PendingIntent showSMSIntent = PendingIntent.getActivity(context, 0, smsNotication, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);

        if (contactImage != null && !contactImage.equals("")) {

            Uri contactImageUri = Uri.parse(contactImage);

            try {

                ContentResolver contentResolver = context.getContentResolver();
                InputStream contactImageStream = contentResolver.openInputStream(contactImageUri);
                Bitmap icon = BitmapFactory.decodeStream(contactImageStream);
                icon.setHasMipMap(true);
                nBuilder.setLargeIcon(FakeSmsReceiver.getCircleBitmap(icon));

            } catch (Exception e) { /*EMPTY*/ }
        }

        if (Algorithms.isEmpty(name))
            nBuilder.setContentTitle(number);
        else
            nBuilder.setContentTitle(name);

        nBuilder.setDefaults(Notification.DEFAULT_ALL);
        nBuilder.setSmallIcon(R.drawable.ic_sms, 1);
        nBuilder.setContentText(message);
        nBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        nBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));//Color.rgb(255, 87, 34));
        nBuilder.addAction(R.drawable.ic_reply, "Reply", showSMSIntent);
        nBuilder.addAction(R.drawable.ic_read, "Read", showSMSIntent);
        nBuilder.addAction(R.drawable.ic_call, "Call", showSMSIntent);
        nBuilder.setContentIntent(showSMSIntent);

        nm.notify(2001, nBuilder.build());

    }

    /**
     * Create a round bitmap
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
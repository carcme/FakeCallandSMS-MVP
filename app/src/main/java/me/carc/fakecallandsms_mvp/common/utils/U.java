package me.carc.fakecallandsms_mvp.common.utils;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.codemybrainsout.ratingdialog.FeedbackDialog;
import com.codemybrainsout.ratingdialog.RatingDialog;

import java.util.List;

import me.carc.fakecallandsms_mvp.BuildConfig;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.C;

/**
 * Random Utils to help out
 * <p>
 * Created by bamptonm on 7/2/17.
 */

public class U {

    /**
     * Build a TAG for debugging
     *
     * @return The debug tag
     */
    public static String getTag() {
        String tag = "";
        if (BuildConfig.DEBUG) {
            final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            for (int i = 0; i < ste.length; i++) {
                if (ste[i].getMethodName().equals("getTag")) {
                    tag = "(" + ste[i + 1].getFileName() + ":" + ste[i + 1].getLineNumber() + ")";
                }
            }
        }
        return tag;
    }

    public static String getPath(final Context context, final Uri uri) throws RuntimeException{

        // DocumentProvider
        if (C.HAS_K && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static String fileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1, url.length());
    }

    public static int currentTimeInteger(long time) {
        return (int) (time % Integer.MAX_VALUE);
    }


    public static void featureRequest(final Context ctx, String hint) {

        String text = String.format(ctx.getString(R.string.feature_request), hint)
                + "\n\nManufacturer: " + Build.MANUFACTURER
                + "\nModel: " + Build.MODEL
                + "\nVersion: " + Build.VERSION.SDK_INT
                + "\nRelease: " + Build.VERSION.RELEASE;

        final FeedbackDialog fbDialog = new FeedbackDialog.Builder(ctx)
                .titleTextColor(R.color.black)
                .formTitle(ctx.getString(R.string.feature_request_title))
                .formText(text)
                .formSubmitText(ctx.getString(R.string.feedback_request_send))
                .formCancelText(ctx.getString(R.string.feedback_request_cancel))

                .positiveButtonTextColor(R.color.white)
                .positiveButtonBackgroundColor(R.drawable.button_selector_positive)

                .negativeButtonTextColor(R.color.controlDisabled)
                .negativeButtonBackgroundColor(R.drawable.button_selector_negative)

                .onFeedbackFormSumbit(new FeedbackDialog.Builder.FeedbackDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.setType("message/rfc822");
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"carcmedev@gmail.com"});
                        email.putExtra(Intent.EXTRA_SUBJECT, ctx.getString(R.string.app_name));
                        email.putExtra(Intent.EXTRA_TEXT, feedback);
                        ctx.startActivity(email);
                    }

                    @Override
                    public void onFormCancel() { /* EMPTY */}
                }).build();

        fbDialog.show();
    }

    public static void emailFeedbackForm(final Context ctx) {
        final FeedbackDialog fbDialog = new FeedbackDialog.Builder(ctx)
                .titleTextColor(R.color.black)
                .formTitle(ctx.getString(R.string.feedback_request_title))
                .formHint(ctx.getString(R.string.feedback_request_hint))
                .formSubmitText(ctx.getString(R.string.feedback_request_send))
                .formCancelText(ctx.getString(R.string.feedback_request_cancel))

                .positiveButtonTextColor(R.color.white)
                .positiveButtonBackgroundColor(R.drawable.button_selector_positive)

                .negativeButtonTextColor(R.color.controlDisabled)
                .negativeButtonBackgroundColor(R.drawable.button_selector_negative)

                .onFeedbackFormSumbit(new FeedbackDialog.Builder.FeedbackDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.setType("message/rfc822");
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"carcmedev@gmail.com"});
                        email.putExtra(Intent.EXTRA_SUBJECT, ctx.getString(R.string.app_name));
                        email.putExtra(Intent.EXTRA_TEXT, feedback);
                        ctx.startActivity(email);
                    }

                    @Override
                    public void onFormCancel() { /* EMPTY */}
                }).build();

        fbDialog.show();
    }




    public static void showRatingForm(final Context ctx) {
        final RatingDialog ratingDialog = new RatingDialog.Builder(ctx)
                .icon(ContextCompat.getDrawable(ctx, R.mipmap.ic_launcher))
                .threshold(3)
                .title(ctx.getString(R.string.ratings_request_title))
                .titleTextColor(R.color.black)
                .formTitle(ctx.getString(R.string.feedback_request_title))
                .formHint(ctx.getString(R.string.feedback_request_hint))
                .formSubmitText(ctx.getString(R.string.feedback_request_send))
                .formCancelText(ctx.getString(R.string.feedback_request_cancel))
                .ratingBarColor(R.color.colorAccent)

                .positiveButtonTextColor(R.color.colorAccent)
                .positiveButtonBackgroundColor(R.drawable.button_selector_positive)

                .negativeButtonTextColor(R.color.colorPrimaryDark)
                .negativeButtonBackgroundColor(R.drawable.button_selector_negative)
                .onThresholdCleared(new RatingDialog.Builder.RatingThresholdClearedListener() {
                    @Override
                    public void onThresholdCleared(RatingDialog dlg, float rating, boolean thresholdCleared) {

                        final Uri marketUri = Uri.parse("market://details?id=" + ctx.getPackageName());
                        try {
                            ctx.startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
                        } catch (android.content.ActivityNotFoundException ex) {

                            new AlertDialog.Builder(ctx)
                                    .setTitle("Error")
                                    .setMessage("Couldn't find PlayStore on this device")
                                    .show();
                        }
                        dlg.dismiss();
                    }
                })

                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                    }
                }).build();

        ratingDialog.show();
    }
}

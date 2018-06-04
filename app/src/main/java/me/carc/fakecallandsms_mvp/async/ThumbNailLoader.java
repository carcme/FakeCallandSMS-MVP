package me.carc.fakecallandsms_mvp.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import me.carc.fakecallandsms_mvp.common.TinyDB;

/**
 * Decode the thumbnail and pop it in the supplied imageview
 * Created by bamptonm on 2/6/17.
 */

public class ThumbNailLoader extends AsyncTask<Long, Void, Bitmap> {
    private static final String SP_THUMBNAIL = "_SP_THUMBNAIL ";

    private final WeakReference<ImageView> imageViewRef;

    public ThumbNailLoader(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewRef = new WeakReference<>(imageView);
    }

    // Decode thumbnail in background.
    @Override
    protected Bitmap doInBackground(Long... params) {
        long refTime = params[0];

        final String encodedImage = TinyDB.getTinyDB().getString(refTime + SP_THUMBNAIL);
        byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            final ImageView imageView = imageViewRef.get();
            if (imageView != null) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            }
        }
    }
}
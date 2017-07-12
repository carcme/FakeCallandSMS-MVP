package me.carc.fakecallandsms_mvp.common.utils;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

/**
 * Created by Carc.me on 05.06.16.
 * <p/>
 * View helper functions stolen from across the net
 */
public class ViewUtils {
    private static final float INTERPOLATOR_FACTOR = 2.0f;

    private ViewUtils() {
        throw new AssertionError();
    }

    public static void setButtonDrawableColor(Activity activity, Button button, int colorRes, int iconIndex) {
        Drawable[] icons = button.getCompoundDrawables();
        if (icons[iconIndex] != null) {
            icons[iconIndex].mutate().setColorFilter(ContextCompat.getColor(activity, colorRes), PorterDuff.Mode.SRC_IN);
            button.setCompoundDrawables(icons[0], icons[1], icons[2], icons[3]);
        }
        button.setTextColor(ContextCompat.getColor(activity, colorRes));
    }
}
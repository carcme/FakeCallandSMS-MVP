package me.carc.fakecallandsms_mvp.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.Button;

import me.carc.fakecallandsms_mvp.common.C;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void changeFabIcon(Context ctx, android.support.design.widget.FloatingActionButton fab, int res) {
        if (C.HAS_L) {
            fab.setImageDrawable(ctx.getResources().getDrawable(res, ctx.getTheme()));
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(ctx, res));
        }
    }

    public static Drawable changeIconColour(Context ctx, int icon, int color) {
        Drawable drawable = ContextCompat.getDrawable(ctx, icon);

        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(ctx, color));
        }
        return drawable;
    }

    public static int getStatusBarHeight(Context ctx) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
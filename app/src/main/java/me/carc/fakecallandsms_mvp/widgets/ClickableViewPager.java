package me.carc.fakecallandsms_mvp.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * ClickableViewPager
 * Created by bamptonm on 2/5/17.
 */

public class ClickableViewPager extends ViewPager {

    private OnClickListener mOnClickListener;

    public ClickableViewPager(Context context) {
        super(context);
        init();
    }

    public ClickableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final GestureDetector tapGestureDetector = new GestureDetector(getContext(), new TapGestureListener());

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    public void setOnViewPagerClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onViewPagerClick(ViewPager viewPager);
        void onDoubleTap(ViewPager viewPager);
    }

    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (mOnClickListener != null) {
                mOnClickListener.onViewPagerClick(ClickableViewPager.this);
            }

            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mOnClickListener != null) {
                mOnClickListener.onDoubleTap(ClickableViewPager.this);
            }
            return true;
        }
    }
}

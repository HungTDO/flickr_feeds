package com.framgia.flickrfeeds.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * This class fix: pointerIndex out of range when multitouch on viewpager.
 * Dispatched onTouchEvent and onInterceptTouchEvent.
 *
 * Created by quannh on 1/20/15.
 */
public class ViewPagerFixed extends android.support.v4.view.ViewPager {

    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}

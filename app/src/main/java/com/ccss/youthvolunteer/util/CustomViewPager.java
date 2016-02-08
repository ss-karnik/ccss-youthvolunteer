package com.ccss.youthvolunteer.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {
    private boolean swipePageChangeEnabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipePageChangeEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.swipePageChangeEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.swipePageChangeEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean swipePageChangeEnabled) {
        this.swipePageChangeEnabled = swipePageChangeEnabled;
    }
}

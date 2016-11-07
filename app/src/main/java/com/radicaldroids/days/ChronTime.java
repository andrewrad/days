package com.radicaldroids.days;

import android.content.Context;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.RemoteViews.RemoteView;
import android.widget.TextView;


/**
 * Created by Andrew on 11/4/2016.
 */

public class ChronTime extends TextView {

    private long mBase;
    private long mNow; // the currently displayed time
    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;
    private boolean mLogged;
    private String mFormat;
    private Object[] mFormatterArgs = new Object[1];
    private StringBuilder mFormatBuilder;
    private StringBuilder mRecycle = new StringBuilder(8);
    private boolean mCountDown;

//    public ChronTime(Context context) {
//        super (context);
//        init();
//    }

    public ChronTime(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

//    public ChronTime(Context context, AttributeSet attrs, int defStyle) {
//        this(context, attrs);
//        init();
//    }

    private void init() {
        mBase = System.currentTimeMillis();
        updateText(mBase);
    }

    public void enterTime(long t) {
        updateText(t);
    }

    private synchronized void updateText(long now) {
        mCountDown = true;
        long seconds = mCountDown ? mBase - now : now - mBase;
        seconds /= 1000;
        boolean negative = false;
        if (seconds < 0) {
            seconds = -seconds;
            negative = true;
        }
        String text = DateUtils.formatElapsedTime(mRecycle, seconds);
        if (negative) {
//            text = getResources().getString(R.string.negative_duration, text);
        }

//        if (mFormat != null) {
//            Locale loc = Locale.getDefault();
//            if (mFormatter == null || !loc.equals(mFormatterLocale)) {
//                mFormatterLocale = loc;
//                mFormatter = new Formatter(mFormatBuilder, loc);
//            }
//            mFormatBuilder.setLength(0);
//            mFormatterArgs[0] = text;
//            try {
//                mFormatter.format(mFormat, mFormatterArgs);
//                text = mFormatBuilder.toString();
//            } catch (IllegalFormatException ex) {
//                if (!mLogged) {
//                    Log.w(TAG, "Illegal format string: " + mFormat);
//                    mLogged = true;
//                }
//            }
//        }
        setText(text);
    }
    public void start() {
        mStarted = true;
        updateRunning();
    }

    public void stop() {
        mStarted = false;
        updateRunning();
    }

    private void updateRunning() {
        mVisible = true;
        boolean running = mVisible && mStarted;
        if (running != mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());
//                dispatchChronometerTick();
                postDelayed(mTickRunnable, 1000);
            } else {
                removeCallbacks(mTickRunnable);
            }
            mRunning = running;
        }
    }

    private final Runnable mTickRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRunning) {
                updateText(SystemClock.elapsedRealtime());
//                dispatchChronometerTick();
                postDelayed(mTickRunnable, 1000);
            }
        }
    };
}

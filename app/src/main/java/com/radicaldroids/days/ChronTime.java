package com.radicaldroids.days;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;


/**
 * Created by Andrew on 11/4/2016.
 */

public class ChronTime extends TextView {

    private long mBase;
    private boolean mStarted;
    private boolean mRunning;
    private StringBuilder mRecycle = new StringBuilder(8);
    private long startTime;
    public long totalTime;
    public Context context;

//    public ChronTime(Context context) {
//        super (context);
//        init();
//    }

    public ChronTime(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();
    }

//    public ChronTime(Context context, AttributeSet attrs, int defStyle) {
//        this(context, attrs);
//        init();
//    }

    private void init() {
        mBase = System.currentTimeMillis();
        mBase = 0;
        updateText(mBase);
    }

    public void enterTime(long t) {
        updateText(t);
    }

    private synchronized void updateText(long now) {
        boolean mCountDown = false;  //TODO: build countDown method
        long millisecs = mCountDown ? mBase - now : now - mBase;
        Log.e("time", "mBase: " + mBase + ", now: " + now);
        boolean negative = false;
        if (millisecs < 0) {
            millisecs = -millisecs;
            negative = true;
        }
//        String text = DateUtils.formatElapsedTime(mRecycle, seconds);
        String text = formatElapsedTime(mRecycle, millisecs);
        if (negative) {
//            text = getResources().getString(R.string.negative_duration, text);
        }

        setText(text);
    }

    public String formatElapsedTime(StringBuilder recycle, long elapsedMilliseconds) {
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        long milliSecs = 0;
        if (elapsedMilliseconds >= 3600000) {
            hours = elapsedMilliseconds / 3600000;
            elapsedMilliseconds -= hours * 3600000;
        }
        if (elapsedMilliseconds >= 60000) {
            minutes = elapsedMilliseconds / 60000;
            elapsedMilliseconds -= minutes * 60000;
        }
        if (elapsedMilliseconds >= 1000) {
            seconds = elapsedMilliseconds / 1000;
            elapsedMilliseconds -= seconds * 1000;
        }

        milliSecs = elapsedMilliseconds/100;

        StringBuilder sb = recycle;
        if (sb == null) {
            sb = new StringBuilder(8);
        } else {
            sb.setLength(0);
        }

        Formatter f = new Formatter(sb, Locale.getDefault());
        if (hours > 0) {
            return f.format(context.getResources().getString(R.string.elapsed_time_short_format_h_mm_ss), hours, minutes, seconds).toString();
        } else {
            return f.format(context.getResources().getString(R.string.elapsed_time_short_format_mm_ss_ms), minutes, seconds, milliSecs).toString();
        }
    }

    public void start() {
        startTime = System.currentTimeMillis();
        mStarted = true;
        updateRunning();
    }

    public void stop() {
        long lastStopTime = System.currentTimeMillis();
        totalTime = totalTime + lastStopTime - startTime;
        mStarted = false;
        updateRunning();
    }

    private void updateRunning() {
        boolean mVisible = true;
        boolean running = mVisible && mStarted;
        if (running != mRunning) {
            if (running) {
                updateText(totalTime);
//                dispatchChronometerTick();
                postDelayed(mTickRunnable, 100);
            } else {
                removeCallbacks(mTickRunnable);
            }
            mRunning = running;
        }
    }

    private final Runnable mTickRunnable = new Runnable() {
        int counter;
        @Override
        public void run() {
            if (mRunning) {
                updateText(System.currentTimeMillis() - startTime + totalTime);
//                dispatchChronometerTick();
                counter++;
                postDelayed(mTickRunnable, 100);
            }
        }
    };
}

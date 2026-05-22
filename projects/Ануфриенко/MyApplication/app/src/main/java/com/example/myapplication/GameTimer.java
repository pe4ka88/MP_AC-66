package com.example.myapplication;

import android.os.Handler;
import android.os.Looper;

public class GameTimer {

    public interface TimerListener {
        void onTick(int secondsLeft);
        void onFinish();
    }

    private static final long TICK_INTERVAL_MS = 250L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final TimerListener listener;

    private long endTimeMillis;
    private long millisLeft;
    private boolean isRunning = false;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) return;

            long millisLeft = endTimeMillis - System.currentTimeMillis();
            int secondsLeft = (int) Math.ceil(millisLeft / 1000.0);

            if (secondsLeft <= 0) {
                isRunning = false;
                listener.onTick(0);
                listener.onFinish();
                return;
            }

            listener.onTick(secondsLeft);
            handler.postDelayed(this, TICK_INTERVAL_MS);
        }
    };

    public GameTimer(TimerListener listener) {
        this.listener = listener;
    }

    public void start(int seconds) {
        stop();
        endTimeMillis = System.currentTimeMillis() + seconds * 1000L;
        isRunning = true;
        handler.post(timerRunnable);
    }
    public void pause() {
        if (!isRunning) return;
        millisLeft = endTimeMillis - System.currentTimeMillis();
        stop();
    }
    public void resume() {
        if (isRunning) return;
        endTimeMillis = System.currentTimeMillis() + millisLeft;
        isRunning = true;
        handler.post(timerRunnable);
    }
    public void addTimeMillis(long millis) {
        endTimeMillis += millis;
    }

    public void stop() {
        isRunning = false;
        handler.removeCallbacks(timerRunnable);
    }

    public int getTimeLeft() {
        if (!isRunning) {
            return (int) Math.ceil(millisLeft / 1000.0);
        } else {
            long left = endTimeMillis - System.currentTimeMillis();
            return (int) Math.max(Math.ceil(left / 1000.0), 0);
        }
    }
}
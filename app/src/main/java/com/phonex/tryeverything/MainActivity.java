package com.phonex.tryeverything;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

    private static final long DELAY_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 主线程setContentView，子线程修改颜色或大小
//        testUiThread();

        // 子线程setContentView，子线程修改颜色或大小
//        testNoUiThread();

        // 子线程创建window并addView，子线程修改颜色或大小
        testWindowNoUiThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void testUiThread() {
        log("setContentView in " + Thread.currentThread().getName());
        setContentView(R.layout.activity_main);
        final View v = findViewById(R.id.color_rect);
        HandlerThread ht = new HandlerThread("no_ui_thread_window");
        ht.start();
        Handler h = new Handler(ht.getLooper()) {
            private boolean isBlue = false;
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        updateUI(v, true);
                        sendEmptyMessageDelayed(0, DELAY_TIME);
                        break;
                    case 1:
                        updateUI(v, false);
                        sendEmptyMessageDelayed(1, DELAY_TIME);
                        break;
                }
            }
        };
        h.sendEmptyMessageDelayed(0, DELAY_TIME);
    }

    private void testWindowNoUiThread() {
        setContentView(R.layout.activity_main);
        findViewById(R.id.color_rect).setVisibility(View.GONE);

       final FrameLayout fl = new FrameLayout(MainActivity.this);
        final WindowManager.LayoutParams fllp = new WindowManager.LayoutParams();
        fl.setBackgroundResource(android.R.color.darker_gray);
        fllp.width = 800;
        fllp.height = 800;

        final View v = new View(MainActivity.this);
        FrameLayout.LayoutParams vlp = new FrameLayout.LayoutParams(400, 400);
        v.setBackgroundResource(android.R.color.holo_blue_dark);

        fl.addView(v, vlp);
        final WindowManager wm = getWindowManager();

        HandlerThread ht = new HandlerThread("no_ui_thread_window");
        ht.start();
        Handler h = new Handler(ht.getLooper()) {
            private boolean isBlue = false;
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        updateUI(v, true);
                        sendEmptyMessageDelayed(0, DELAY_TIME);
                        break;
                    case 1:
                        updateUI(v, false);
                        sendEmptyMessageDelayed(1, DELAY_TIME);
                        break;
                }
            }
        };

        h.post(new Runnable() {
            @Override
            public void run() {
                wm.addView(fl, fllp);
            }
        });

        h.sendEmptyMessageDelayed(0, DELAY_TIME);
    }

    private void testNoUiThread() {
        HandlerThread ht = new HandlerThread("no_ui_thread_window");
        ht.start();
        Handler handler = new Handler(ht.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        View v = findViewById(R.id.color_rect);
                        updateUI(v, true);
                        sendEmptyMessageDelayed(0, DELAY_TIME);
                        break;
                    case 1:
                        v = findViewById(R.id.color_rect);
                        updateUI(v, false);
                        sendEmptyMessageDelayed(1, DELAY_TIME);
                        break;
                }
            }
        };
        handler.post(new Runnable() {
            @Override
            public void run() {
                log("setcontentView in " + Thread.currentThread().getName());
                setContentView(R.layout.activity_main);
            }
        });

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handler.sendEmptyMessageDelayed(0, DELAY_TIME);
    }

    private boolean mState = false;
    private void updateUI(View v, boolean updateColor) {
        log("updateUI in " + Thread.currentThread().getName());
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (updateColor) {
            if (mState) {
                v.setBackgroundResource(android.R.color.holo_red_dark);
            } else {
                v.setBackgroundResource(android.R.color.holo_blue_dark);
            }
            mState = !mState;
        } else if (lp != null) {
            if (mState) {
                lp.width = lp.height =400;
            } else {
                lp.width = lp.height = 800;
            }
            v.setLayoutParams(lp);
            mState = !mState;
        }
    }

    private void log(String msg) {
        Log.d("NoUiThread", msg);
    }
}

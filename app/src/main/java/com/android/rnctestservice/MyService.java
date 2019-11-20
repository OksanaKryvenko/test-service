package com.android.rnctestservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {

    private ExecutorService mExecutor;
    private boolean alive;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("logtag", "onCreate");
        mExecutor = Executors.newFixedThreadPool(1);
        alive = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("logtag", "onStartCommand");

        int time = intent.getIntExtra(MainActivity.PARAM_TIME, 1);

        mExecutor.execute(new MyRun(time));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("logtag", "onDestroy");
        alive = false;
    }

    class MyRun implements Runnable {

       private int mTime;

        public MyRun(int time) {
            mTime = time;
        }

        @Override
        public void run() {
            Log.d("logtag", "run");
            try {
                while (alive ) {
                    Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                    intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_START);
                    sendBroadcast(intent);
                    TimeUnit.SECONDS.sleep(mTime);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}

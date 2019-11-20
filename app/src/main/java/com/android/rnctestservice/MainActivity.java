package com.android.rnctestservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvTimer;
    private TextView mTvCounter;
    private Button mBtnOn;
    private Button mBtnOff;
    private SharedPreferences mPrefs;

    public final static int STATUS_START = 100;

    public static final String PARAM_TIME = "time";
    public static final String PARAM_STATUS = "status";
    public static final String BROADCAST_ACTION = "com.android.rollncodetestservice.BROADCAST_ACTION";

    private BroadcastReceiver mReceiver;

    private String mTime;
    private int mCount;

    public static boolean alive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvTimer = findViewById(R.id.tvTimer);
        mTvCounter = findViewById(R.id.tvCounter);
        mBtnOn = findViewById(R.id.btnOn);
        mBtnOff = findViewById(R.id.btnOff);

        mPrefs = getPreferences(MODE_PRIVATE);
        mBtnOn.setOnClickListener(this);
        mBtnOff.setOnClickListener(this);

        if (savedInstanceState != null) {
            alive = true;
            mCount = Integer.parseInt(mPrefs.getString("counter", ""));
            mTime = mPrefs.getString("timer", "");
        }

        mTvTimer.setText(mTime);
        mTvCounter.setText(String.valueOf(mCount));

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS, 0);

                Log.d("logtag", "onReceive" + status);

                if (status == STATUS_START) {
                    mTvCounter.setText(Integer.toString(mCount));
                    mCount += 1;
                    mPrefs.edit().putString("timer", mTvTimer.getText().toString())
                            .putString("counter", mTvCounter.getText().toString())
                            .apply();
                    Log.d("logtag", "mCount = " + mCount);
                }
            }
        };
        registerReceiver(mReceiver, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        intent = new Intent(MainActivity.this, MyService.class)
                .putExtra(PARAM_TIME, 5);
        switch (v.getId()) {
            case R.id.btnOn:
                alive = true;
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyy   hh:mm:ss");
                mTime = formatter.format(date);
                mTvTimer.setText(mTime);
                startService(intent);
                break;

            case R.id.btnOff:
                alive = false;
                stopService(intent);
                Log.d("logtag", "mCount = " + mCount);
                Log.d("logtag", "mTime = " + mTime);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alive = false;
        stopService(new Intent(MainActivity.this, MyService.class));
        unregisterReceiver(mReceiver);
    }
}

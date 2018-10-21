package com.jingwei.cai.luke;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class DouYinActivity extends AppCompatActivity {
    private TextListView mListView;
    private EditText mEditTextMinute;
    private BroadcastReceiver mDynamicReceiver;
    private PendingIntent mPendingIntent = null;
    private long mTimeStart = 0;

    private static long sAlarmInterval = 15*1000;

    private static String DOUYIN_PKG_NAME = "com.ss.android.ugc.aweme";
    public static final String CUSTOM_INTENT_ACTION_STATIC = "douyin_custom_intent_static";
    public static final String CUSTOM_INTENT_ACTION_DYNAMIC = "douyin_custom_intent_dynamic";
    public static final String CUSTOM_INTENT_ACTION_EXTRA_NAME = "douyin_extra";
    private static final IntentFilter sDynamicIntentFilter;
    static {
        sDynamicIntentFilter = new IntentFilter();
        sDynamicIntentFilter.addAction(CUSTOM_INTENT_ACTION_DYNAMIC);
    }

    private class DynamicReceiver extends android.content.BroadcastReceiver {
        boolean mCanStopDouyin = false;
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case CUSTOM_INTENT_ACTION_DYNAMIC:
                    final Calendar cal = Calendar.getInstance();
                    String timeStr = String.format("%02d:%02d:%02d",
                            cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
                    long timeAllowMin = Long.parseLong(mEditTextMinute.getText().toString());
                    // compare with start time
                    double timeElapseMin = (System.currentTimeMillis() - mTimeStart) / 1000 / 60.0; // minutes
                    long timeLeftMin = timeAllowMin - new Double(timeElapseMin).longValue();
                    String toastStr = String.format("%s: %d minutes left", timeStr, timeLeftMin);
                    mListView.addMessage(toastStr);

                    int second = cal.get(Calendar.SECOND);
                    if ((56 <= second) || (second <= 4)) {
                        Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_SHORT).show();
                    }

                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    boolean stopped = false;
                    if (timeLeftMin <= 0) {
                        if (mCanStopDouyin) {
                            mCanStopDouyin = false;
                            mListView.addMessage("Then: kill DouYin onl when it is at background");
                            stopDouYin();
                        } else {
                            mCanStopDouyin = true;
                            mListView.addMessage("First: bring myself to foreground");
                            // we have to wait for next timer to really kill douyin; otherwise douyin is still at foreground
                            bringToForeground();
                            // set next alarm
                            am.set(AlarmManager.RTC, sAlarmInterval, mPendingIntent);
                        }
                    } else {
                        // set next alarm
                        //  Android 5.1, value is forced up to 60 seconds
                        am.set(AlarmManager.RTC, 3 * 1000, mPendingIntent);
                    }
                    break;
            }
        }
    }

    private final void startReceiver() {
        if (mDynamicReceiver != null) {
            return;
        }
        mDynamicReceiver = new DynamicReceiver();
        mListView.addMessage("start receiver: start");
        registerReceiver(mDynamicReceiver, sDynamicIntentFilter);
        mListView.addMessage("start receiver: done");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douyin);
        mListView = (TextListView) findViewById(R.id.listview);
        mEditTextMinute = (EditText)findViewById(R.id.editTextMinute);

        findViewById(R.id.buttonStartDouYin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDouYin();
            }
        });
        ((Button)findViewById(R.id.buttonStopDouYin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            stopDouYin();
            }
        });

        mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, StaticReceiver.class).setAction(CUSTOM_INTENT_ACTION_STATIC), PendingIntent.FLAG_UPDATE_CURRENT);
        // start receiver for alarm manager
        startReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopReceiver();
    }

    private final void stopReceiver() {
        if (mDynamicReceiver == null) {
            return;
        }
        mListView.addMessage("stop receiver: start");
        unregisterReceiver(mDynamicReceiver);
        mDynamicReceiver = null;
        mListView.addMessage("stop receiver: done");
    }

    private void startDouYin() {
        mListView.addMessage("start DouYin");

        mTimeStart = System.currentTimeMillis();

        Intent intent = null;
        intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(DOUYIN_PKG_NAME);
        if (intent != null) {
            getApplicationContext().startActivity(intent);
        }

        // start a timer to monitor DouYin

//        Intent intentAlarm = new Intent(this, AlarmService.class);
//        intentAlarm.setAction(AlarmService.ACTION_ALARM);
//        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Android 5.1, value is forced up to 60 seconds
        am.set(AlarmManager.RTC, sAlarmInterval, mPendingIntent);
    }

    // NOT CORRECT
    private static boolean isApplicationBroughtToForeground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = "";
        if(Build.VERSION.SDK_INT > 20) {
            packageName = am.getRunningAppProcesses().get(0).processName;
        } else {
            packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
        }
        return packageName.equals(context.getPackageName());
    }


    private void bringToForeground() {
        mListView.addMessage("stop DouYin");

        // bring myself to foreground
        Intent intent = null;
        intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.jingwei.cai.luke");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(getApplicationContext(), this.getClass()));
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        getApplicationContext().startActivity(intent);
    }

    private void stopDouYin()  {
        mListView.addMessage("stop DouYin");
        // and then we can kill douyin as background process
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(DOUYIN_PKG_NAME);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(mPendingIntent);
        // forceStopPackage can not be used by user app
//        <uses-permission android:name="android.permission.FORCE_STOP_PACKAGES"/>
//        try {
//            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
//            if (method != null) {
//                method.invoke(am, DOUYIN_PKG_NAME);  //packageName是需要强制停止的应用程序包名
//            }
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }
}

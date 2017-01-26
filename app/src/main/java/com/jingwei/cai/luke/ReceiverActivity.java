package com.jingwei.cai.luke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ReceiverActivity extends AppCompatActivity {

    private class DynamicReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_SCREEN_ON:
                case Intent.ACTION_SCREEN_OFF:
                    mListView.addMessage("onReceive(): " + action);
                    break;
                case CUSTOM_INTENT_ACTION_DYNAMIC:
                    String extra = intent.getStringExtra(CUSTOM_INTENT_ACTION_EXTRA_NAME);
                    mListView.addMessage("onReceive(): " + action + "; " + extra);
                    break;
            }
        }
    }

    private TextListView mListView;
    private BroadcastReceiver mDynamicReceiver;

    private static final Intent sCustomStaticIntent;
    private static final Intent sCustomDynamicIntent;
    public static final String CUSTOM_INTENT_ACTION_STATIC = "luke_custom_intent_static";
    public static final String CUSTOM_INTENT_ACTION_DYNAMIC = "luke_custom_intent_dynamic";
    public static final String CUSTOM_INTENT_ACTION_EXTRA_NAME = "luke_extra";
    private static final String CUSTOM_INTENT_ACTION_EXTRA_VALUE = "from activity";
    private static final IntentFilter sDynamicIntentFilter;
    static {
        sDynamicIntentFilter = new IntentFilter();
        sDynamicIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        sDynamicIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        sDynamicIntentFilter.addAction(CUSTOM_INTENT_ACTION_DYNAMIC);

        sCustomDynamicIntent = new Intent();
        sCustomDynamicIntent.setAction(CUSTOM_INTENT_ACTION_DYNAMIC);
        sCustomDynamicIntent.putExtra(CUSTOM_INTENT_ACTION_EXTRA_NAME, CUSTOM_INTENT_ACTION_EXTRA_VALUE);

        sCustomStaticIntent = new Intent();
        sCustomStaticIntent.setAction(CUSTOM_INTENT_ACTION_STATIC);
        sCustomStaticIntent.putExtra(CUSTOM_INTENT_ACTION_EXTRA_NAME, CUSTOM_INTENT_ACTION_EXTRA_VALUE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        mListView = (TextListView) findViewById(R.id.listview);
        findViewById(R.id.buttonStartReceiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReceiver();
            }
        });
        findViewById(R.id.buttonStopReceiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopReceiver();
            }
        });
        findViewById(R.id.buttonSendDynamic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(sCustomDynamicIntent);
            }
        });
        findViewById(R.id.buttonSendStatic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(sCustomStaticIntent);
            }
        });

        startReceiver();
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
}

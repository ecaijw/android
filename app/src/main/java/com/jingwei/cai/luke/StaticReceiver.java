package com.jingwei.cai.luke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StaticReceiver extends BroadcastReceiver {
    public StaticReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if  (intent.getAction() == ReceiverActivity.CUSTOM_INTENT_ACTION_STATIC) {
            Intent intentToSend = new Intent(ReceiverActivity.CUSTOM_INTENT_ACTION_DYNAMIC);
            intentToSend.putExtra(ReceiverActivity.CUSTOM_INTENT_ACTION_EXTRA_NAME, "from " + this.getClass().getName());
            context.sendBroadcast(intentToSend);
        }

        if  (intent.getAction() == DouYinActivity.CUSTOM_INTENT_ACTION_STATIC) {
            Intent intentToSend = new Intent(DouYinActivity.CUSTOM_INTENT_ACTION_DYNAMIC);
            intentToSend.putExtra(DouYinActivity.CUSTOM_INTENT_ACTION_EXTRA_NAME, "from " + this.getClass().getName());
            context.sendBroadcast(intentToSend);
        }
    }
}

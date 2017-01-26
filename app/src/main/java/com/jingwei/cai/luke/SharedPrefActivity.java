package com.jingwei.cai.luke;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Random;

public class SharedPrefActivity extends AppCompatActivity {

    private static TextListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_pref);

        mListView = (TextListView) findViewById(R.id.listview);
        findViewById(R.id.buttonWritePref).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePref();
            }
        });
        findViewById(R.id.buttonReadPref).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readPref();
            }
        });
    }

    private static final String PREF_NAME = "luke";
    private static final String KEY_LONG = "long";
    private static final String KEY_STRING = "string";
    private void writePref() {
        mListView.addMessage("write pref: start");
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(KEY_LONG, new Random().nextInt(100));
        editor.putString(KEY_STRING, String.valueOf(System.currentTimeMillis()));
        editor.apply();
        mListView.addMessage("write pref: done");
    }

    private void readPref() {
        mListView.addMessage("read pref: start");
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        mListView.addMessage(String.valueOf(sp.getLong(KEY_LONG, 0)));
        mListView.addMessage(sp.getString(KEY_STRING, ""));
        mListView.addMessage("read pref: done");

    }
}

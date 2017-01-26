package com.jingwei.cai.luke;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class ActivityHandlerThread extends AppCompatActivity {
    private TextListView mListView;
    private ArrayList<HandlerThread> mThreads = new ArrayList<>();
    private ArrayList<Handler> mThreadHandlers = new ArrayList<>();
    private Handler mUIHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            mListView.addMessage(message.what + ", " + message.getData().getString("name"));
            if (!mThreadHandlers.isEmpty()) {
                mThreadHandlers.get(new Random().nextInt(mThreadHandlers.size())).sendEmptyMessage(0);
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_thread);
        mListView = (TextListView) findViewById(R.id.listview);
        mListView.addMessage("start");
        ((Button)findViewById(R.id.buttonStart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HandlerThread thread = new HandlerThread("Thread#" + mThreads.size());
                mThreads.add(thread);
                thread.start();
                Handler threadHandler = new Handler(thread.getLooper(), new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        try {
                            Thread.sleep(new Random().nextInt(10) * 100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message newMsg = Message.obtain();
                        newMsg.what = (int)Thread.currentThread().getId();
                        Bundle bundle = new Bundle();
                        bundle.putString("name", Thread.currentThread().getName());
                        newMsg.setData(bundle);
                        mUIHandler.sendMessage(newMsg);
                        return false;
                    }
                });
                threadHandler.sendEmptyMessage(0);
                mThreadHandlers.add(threadHandler);
                mListView.addMessage("new thread: " + thread.getName() + " id:" + thread.getThreadId());
            }
        });
        ((Button)findViewById(R.id.buttonStop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mThreads.isEmpty()) {
                    mListView.addMessage("No more threads.");
                    return;
                }
                mThreadHandlers.remove(mThreadHandlers.size() - 1);
                HandlerThread thread = mThreads.remove(mThreads.size() - 1);
                mListView.addMessage("stop thread: " + thread.getName() + " id:" + thread.getThreadId());
                thread.quit();
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThreadHandlers.clear();
        for (HandlerThread thread : mThreads) {
            thread.quit();
        }
        mThreads.clear();
    }
}

package com.jingwei.cai.luke;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.Random;

import static android.R.attr.data;

abstract class DecodeTaskBase extends AsyncTask {
    String mPath;
    String mResultString;
    Bitmap mBitmap;
    private WeakReference<Handler> mUIHandler;

    public DecodeTaskBase(String path, Handler UIHandler) {
        mPath = path;
        mUIHandler = new WeakReference<Handler>(UIHandler);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Handler handler = mUIHandler.get();
        if (handler != null) {
            Bundle bundle = new Bundle();
            bundle.putString(ImageActivity.HANDLER_KEY_RESULT_STRING, mResultString);
            Message msg = new Message();
            msg.obj = mBitmap;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }
}

class DecodeWebAsyncTask extends DecodeTaskBase {
    public DecodeWebAsyncTask(String path, Handler UIHandler) {
        super(path, UIHandler);
    }

    @SuppressWarnings("WrongThread")
    @Override
    protected Object doInBackground(Object[] params) {
        long startTime = System.currentTimeMillis();
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);
        long duration = System.currentTimeMillis() - startTime;
        mResultString = String.format("decode done(%d) - %s", duration, (bitmap == null ? "fail" : "succeed"));
        if (bitmap == null) {
            bitmap = Helper.getRedBitmap();
        }
        mBitmap = bitmap;
        return null;
    }
}

class DecodeLocalAsyncTask extends DecodeTaskBase {
    public DecodeLocalAsyncTask(String path, Handler UIHandler) {
        super(path, UIHandler);
    }

    @SuppressWarnings("WrongThread")
    @Override
    protected Object doInBackground(Object[] params) {
        long startTime = System.currentTimeMillis();
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);
        long duration = System.currentTimeMillis() - startTime;
        mResultString = String.format("decode done(%d) - %s", duration, (bitmap == null ? "fail" : "succeed"));
        if (bitmap == null) {
            bitmap = Helper.getRedBitmap();
        }
        mBitmap = bitmap;
        return null;
    }
}

public class ImageActivity extends AppCompatActivity {

    private TextListView mListView;
    private ImageView mImageView;
    private EditText mEditLocal;
    private EditText mEditWeb;

    public static final String HANDLER_KEY_RESULT_STRING = "result";
    public static final String HANDLER_KEY_RESULT_BITMAP = "bitmap";

    private Handler mUIHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            Bundle bundle = message.getData();
            mListView.addMessage(bundle.getString(HANDLER_KEY_RESULT_STRING));
            mImageView.setImageBitmap((Bitmap)message.obj);
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mListView = (TextListView) findViewById(R.id.listview);
        mEditLocal = (EditText) findViewById(R.id.editTextLocal);
        mEditLocal.setText(Environment.getExternalStorageDirectory() + "/test/2.jpg");
        mEditWeb = (EditText) findViewById(R.id.editTextWeb);
        mEditWeb.setText("http://img4.duitang.com/uploads/item/201603/26/20160326193535_dj8cx.jpeg");
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.green));
        ((Button) findViewById(R.id.buttonLocal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = mEditLocal.getText().toString();
                mListView.addMessage("decode local: " + path);
                new DecodeLocalAsyncTask(path, mUIHandler).execute();
            }
        });
        ((Button)findViewById(R.id.buttonWeb)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = mEditWeb.getText().toString();
                mListView.addMessage("decode web: " + path);
//                http://img4.duitang.com/uploads/item/201603/26/20160326193535_dj8cx.jpeg
//                http://pic24.nipic.com/20121023/5692504_110554234175_2.jpg
//                http://pic24.nipic.com/20121023/5692504_113455646192_2.jpg
                // new DecodeLocalAsyncTask(path).execute();
            }
        });
    }
}

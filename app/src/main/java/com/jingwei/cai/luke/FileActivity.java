package com.jingwei.cai.luke;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

public class FileActivity extends AppCompatActivity {
    private TextListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        mListView = (TextListView) findViewById(R.id.listview);

        findViewById(R.id.buttonWriteStream).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeStream();
            }
        });
        ((Button)findViewById(R.id.buttonReadStream)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readStream();
            }
        });

        ((Button)findViewById(R.id.buttonWriteFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeFile();
            }
        });
        ((Button)findViewById(R.id.buttonReadFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFile();
            }
        });
    }

    private final String getFileStreamPath() {
        return getFilesDir() + "/test_stream.data";
    }

    private final String getFilePath() {
//        return Environment.getExternalStorageDirectory() + "/luke/test/test_file.data";
        return getFilesDir() + "/test_file.data";
    }

    private void createDir(String filePath) {
        File file = new File(filePath);
        mListView.addMessage("check: " + file.getParent());
        if (new File(file.getParent()).exists()) {
            return;
        }
        new File(file.getParent()).mkdir();
    }

    private void writeFile() {
        mListView.addMessage("write file: start");

        RandomAccessFile file = null;
        try {
            createDir(getFilePath());
            file = new RandomAccessFile(getFilePath(), "rw");

            // write int
            file.writeInt(new Random().nextInt());

            // write date
            Calendar calendar = Calendar.getInstance();
            file.writeBytes(String.format("%s-%s", calendar.YEAR, calendar.MONTH));
            file.writeBytes("\r\n");

            // write date
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.US);
            file.writeBytes(df.format(new Date()));
            file.writeBytes("\r\n");

            // write time
            GregorianCalendar time = new GregorianCalendar();
            SimpleDateFormat timeFormat = new SimpleDateFormat("H:m:s");
            file.writeBytes(timeFormat.format(time.getTime()));
            file.writeBytes("\r\n");

            // write double
            file.writeDouble(System.currentTimeMillis() * 1.0);

            file.close();
            mListView.addMessage("write file: DONE");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readFile() {
        mListView.addMessage("read file: start");
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(getFilePath(), "r");
            mListView.addMessage(String.valueOf(file.readInt()));
            mListView.addMessage(file.readLine());
            mListView.addMessage(file.readLine());
            mListView.addMessage(file.readLine());
            mListView.addMessage(String.valueOf(file.readDouble()));
            mListView.addMessage("read file: DONE");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void writeStream() {
        mListView.addMessage("write stream: start");
        try {
            createDir(getFileStreamPath());
            File file = new File(getFileStreamPath());
            if (!file.exists()) {
                file.createNewFile();
            }
            // openFileOutput(): 创建的文件保存在/data/data/<package name>/files目录, can not contain a path separator
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write((new Random()).nextInt());
                byte[] bytes = "jingwei".getBytes("UTF-8");
                stream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stream.close();
            mListView.addMessage("write stream: DONE");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mListView.addMessage("create file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readStream() {
        mListView.addMessage("read stream: start");
        FileInputStream stream = null;
        try {
            File file = new File(getFileStreamPath());
            if (!file.isFile()) {
                mListView.addMessage("Error: file is NOT a file");
                return;
            }
            stream = new FileInputStream(file);

            byte[] intBuf = new byte[1];
            stream.read(intBuf);
            byte[] buf = new byte[(int) file.length() - intBuf.length];
            stream.read(buf);
            mListView.addMessage(Integer.valueOf(intBuf[0]).toString());
            //mListView.addMessage(String.valueOf(buf));  // wrong
            mListView.addMessage(new String(buf, "UTF-8"));

            stream.close();
            mListView.addMessage("read stream: DONE");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

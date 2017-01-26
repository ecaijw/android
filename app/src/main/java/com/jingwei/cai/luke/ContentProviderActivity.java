package com.jingwei.cai.luke;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.w3c.dom.Text;

public class ContentProviderActivity extends AppCompatActivity {

    private TextListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);

        mListView = (TextListView) findViewById(R.id.listview);
        findViewById(R.id.buttonWriteContent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeContent();
            }
        });

        findViewById(R.id.buttonReadContent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readContent();
            }
        });
    }

    private void writeContent() {
        //
    }

    private void readContent() {

    }
}

package com.jingwei.cai.luke;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.jingwei.cai.luke.LukeContentProvider.MyContentProviderMetaData;
import com.jingwei.cai.luke.LukeContentProvider.MyContentProviderMetaData.UserTableMetaData;

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
        mListView.addMessage("add 5 users; delete user2; update user3 to user333");

        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        int i = 0;
        while (i < 5) {
            values.put(UserTableMetaData.USER_NAME, "user" + i);
            resolver.insert(UserTableMetaData.CONTENT_URI, values);
            i += 1;
        }

        resolver.delete(UserTableMetaData.CONTENT_URI, "name = ?", new String[] {"user2"});

        values.put(UserTableMetaData.USER_NAME, "user333");
        resolver.update(UserTableMetaData.CONTENT_URI, values, "name = ?", new String[] {"user3"});
    }

    private void readContent() {
        mListView.addMessage("query");
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(UserTableMetaData.CONTENT_URI, UserTableMetaData.PROJECTION_ALL, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            mListView.addMessage(cursor.getString(0));
        }
        cursor.close();
    }
}

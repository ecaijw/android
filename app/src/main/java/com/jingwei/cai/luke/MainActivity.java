package com.jingwei.cai.luke;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("email", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.material_blue_grey_800)).show();
            }
        });

        // listview's adapter
        class ListViewData {
            String mText;
            Class<?> mActivity;
            public ListViewData(String text) {
                this(text, null);
            }

            public ListViewData(String text, Class<?> activity) {
                mText = text;
                if (activity == null) {
                    mText = "TODO: " + mText;
                }
                mActivity = activity;
            }
        }
        final ArrayList<ListViewData> lvData = new ArrayList<>();
        lvData.add(new ListViewData("Start DouYin", DouYinActivity.class));
        lvData.add(new ListViewData("content provider", ContentProviderActivity.class));
        lvData.add(new ListViewData("receiver", ReceiverActivity.class));
        lvData.add(new ListViewData("service (not done)", ServiceActivity.class));
        lvData.add(new ListViewData("handler thread", ActivityHandlerThread.class));
        lvData.add(new ListViewData("File", FileActivity.class));
        lvData.add(new ListViewData("image decoding", ImageActivity.class));
        lvData.add(new ListViewData("shared preference", SharedPrefActivity.class));
        lvData.add(new ListViewData("settings"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        for (ListViewData data : lvData) {
            adapter.add(data.mText);
        }
        ListView lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(adapter);
        lv.setSelector(R.drawable.green);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (lvData.get(i).mActivity != null) {
                    startActivity(new Intent(getApplicationContext(), lvData.get(i).mActivity));
                } else {
                    Toast.makeText(getApplicationContext(), "TODO feature", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

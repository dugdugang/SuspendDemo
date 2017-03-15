package com.jpyl.suspenddemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/23.
 */

public class EntryActivity extends AppCompatActivity {
    PullZoomListview listview;
    RelativeLayout topBar;
    View fixed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        listview = (PullZoomListview) findViewById(R.id.pullListview);
        topBar = (RelativeLayout) findViewById(R.id.topBar);
        fixed = findViewById(R.id.fixed);
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            list.add("第" + i + "项");
        }
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(aa);
        listview.setTopBar(topBar);
        listview.setFixed(fixed);
        listview.setScroll();
    }
}

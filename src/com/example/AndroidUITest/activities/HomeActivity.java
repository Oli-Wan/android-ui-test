package com.example.AndroidUITest.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.AndroidUITest.ActivityUtils;
import com.example.AndroidUITest.R;
import com.example.AndroidUITest.adapters.MissionAdapter;
import com.example.AndroidUITest.models.Mission;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        List<Mission> list = new ArrayList<Mission>();
        for(int i = 0; i < 1000; i++) {
            list.add(new Mission(i, "Observation "+i));
        }

        ListView listView = (ListView) findViewById(R.id.listView);
        final Activity currentActivity = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ActivityUtils.startNewActivity(currentActivity, MissionDescriptionActivity.class);
            }
        });
        listView.setClickable(true);
        listView.setAdapter(new MissionAdapter(getBaseContext(), list));
    }
}

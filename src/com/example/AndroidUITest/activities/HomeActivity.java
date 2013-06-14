package com.example.AndroidUITest.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.AndroidUITest.ActivityUtils;
import com.example.AndroidUITest.R;
import com.example.AndroidUITest.adapters.MissionAdapter;
import com.example.AndroidUITest.models.Command;
import com.example.AndroidUITest.models.Mission;
import com.example.AndroidUITest.network.tasks.GetCommandsTask;
import com.example.AndroidUITest.storage.CommandOpenHelper;
import com.example.AndroidUITest.storage.MissionOpenHelper;

import java.util.List;

public class HomeActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getMissions(null);
    }

    public void getMissions(View view) {
        List<Mission> missions = new MissionOpenHelper(getBaseContext()).getAll();

        ListView listView = (ListView) findViewById(R.id.listView);
        final Activity currentActivity = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ActivityUtils.startNewActivity(currentActivity, MissionDescriptionActivity.class);
            }
        });
        listView.setClickable(true);
        listView.setAdapter(new MissionAdapter(getBaseContext(), missions));
    }

    public void getDistantCommands(View view) {
        System.out.println("Get commands");
        GetCommandsTask getCommandsTask = new GetCommandsTask(getBaseContext());
        getCommandsTask.execute();
    }

    public void getLocalCommands(View view) {
        List<Command> commands = new CommandOpenHelper(getBaseContext()).getAll();
        for (Command cmd : commands) {
            System.out.println(cmd.getData());
        }
    }
}

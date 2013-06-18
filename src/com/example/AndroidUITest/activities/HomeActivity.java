package com.example.AndroidUITest.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.AndroidUITest.R;
import com.example.AndroidUITest.adapters.MissionAdapter;
import com.example.AndroidUITest.messaging.MissionMessagingService;
import com.example.AndroidUITest.models.Mission;
import com.example.AndroidUITest.network.CommandListener;
import com.example.AndroidUITest.network.CommandSender;
import com.example.AndroidUITest.storage.MissionDataSource;
import com.example.AndroidUITest.utils.ActivityUtils;

import java.util.List;

public class HomeActivity extends Activity {
    private boolean serviceBound = false;
    private Messenger messenger;
    private List<Mission> missions;
    private NetworkServiceConnection networkServiceConnection;
    private Messenger networkService;

    @Override
    protected void onStart() {
        super.onStart();
        if (!CommandListener.getInstance().isStarted())
            CommandListener.getInstance().start(getApplicationContext());

        if (CommandSender.getInstance().isStarted())
            CommandSender.getInstance().start(getApplicationContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.messenger = new Messenger(new IncomingHandler());
        bindService();
        setContentView(R.layout.main);
        loadCommands();
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d("HomeActivity", "Got message");
            switch (msg.what) {
                case MissionMessagingService.MISSION_UPDATED:
                    loadCommands();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private class NetworkServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            networkService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, MissionMessagingService.REGISTER);
                msg.replyTo = messenger;
                networkService.send(msg);
                Log.d("HomeActivity", "Connected to service");
            } catch (RemoteException e) {
                Log.e("HomeActivity", "Error in registering", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
        CommandSender.getInstance().stop();
        CommandListener.getInstance().stop();
    }

    private void loadCommands() {
        Log.d("HomeActivity", "Loading commands");
        missions = new MissionDataSource().getAll();
        ListView listView = (ListView) findViewById(R.id.listView);
        final Activity currentActivity = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putLong("MISSION_ID", missions.get(i).getId());
                ActivityUtils.startNewActivity(currentActivity, MissionDescriptionActivity.class, bundle);
            }
        });
        listView.setClickable(true);
        listView.setAdapter(new MissionAdapter(getBaseContext(), missions));
    }

    private void unbindService() {
        if(!serviceBound)
            return;

        Message message = Message.obtain(null, MissionMessagingService.UNREGISTER);
        message.replyTo = messenger;
        try {
            networkService.send(message);
        } catch (RemoteException e) {
            Log.e("HomeActivity", "Error", e);
        }
        unbindService(networkServiceConnection);
        serviceBound = false;
    }

    private void bindService() {
        if(serviceBound)
            return;

        Intent intent = new Intent(this, MissionMessagingService.class);
        startService(intent);
        networkServiceConnection = new NetworkServiceConnection();
        bindService(intent, networkServiceConnection, Context.BIND_AUTO_CREATE);
        serviceBound = true;
    }
}

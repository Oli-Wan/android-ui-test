package com.example.AndroidUITest.messaging;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MissionMessagingService extends Service {

    public static final int MISSION_UPDATED = 0;
    public static final int REGISTER = 1;
    public static final int UNREGISTER = 2;

    private final Messenger messenger;

    private final List<Messenger> clients = new ArrayList<Messenger>();

    public MissionMessagingService() {
        this.messenger = new Messenger(new IncomingHandler());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER:
                    Log.d("MissionMessagingService", "Adding client: " + msg.replyTo);
                    clients.add(msg.replyTo);
                    break;
                case MISSION_UPDATED:
                    // forward message
                    for (Messenger client : clients) {
                        try {
                            client.send(Message.obtain(null, MISSION_UPDATED));
                        } catch (RemoteException e) {
                            Log.e("MissionMessagingService", "Couldn't dispatch message" + msg.replyTo);
                        }
                    }
                    break;
                case UNREGISTER:
                    Log.d("MissionMessagingService", "Removing client: " + msg.replyTo);
                    clients.remove(msg.replyTo);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}

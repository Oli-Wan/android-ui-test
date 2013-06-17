package com.example.AndroidUITest.network;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;
import com.codebutler.android_websockets.SocketIOClient;
import com.example.AndroidUITest.messaging.MissionMessagingService;
import com.example.AndroidUITest.models.Command;
import com.example.AndroidUITest.models.Mission;
import com.example.AndroidUITest.network.utils.NetworkUtils;
import com.example.AndroidUITest.storage.CommandOpenHelper;
import com.example.AndroidUITest.storage.MissionOpenHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class CommandListener {

    private static final String BACKEND_URL = "http://192.168.100.51:2403";
    private CommandOpenHelper commands;
    private MissionOpenHelper missions;
    private SocketIOClient client;
    private Messenger messenger;
    private boolean started = false;

    private CommandListener() {
    }

    private static class CommandListenerHolder {
        public static final CommandListener INSTANCE = new CommandListener();
    }

    public static CommandListener getInstance() {
        return CommandListenerHolder.INSTANCE;
    }

    public void start(Context context) {
        if(started)
            return;

        Intent intent = new Intent(context, MissionMessagingService.class);
        context.startService(intent);
        context.bindService(intent, new NetworkServiceConnection(), Context.BIND_AUTO_CREATE);

        this.commands = new CommandOpenHelper(context);
        this.missions = new MissionOpenHelper(context);
        this.client = new SocketIOClient(URI.create(BACKEND_URL), new CommandClient());
        parseCommands();
        started = true;
    }

    public boolean getStarted() {
        return started;
    }


    private class NetworkServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            messenger = new Messenger(service);
            Log.d("CommandListener", "Connected to service");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    }


    private class CommandClient implements SocketIOClient.Handler {
        @Override
        public void onConnect() {
        }

        @Override
        public void on(String event, JSONArray arguments) {
            if (!event.equals("commands:new"))
                return;

            try {
                createCommandFromJSON(arguments.getJSONObject(0));
                messenger.send(Message.obtain(null, MissionMessagingService.MISSION_UPDATED));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnect(int code, String reason) {
            Log.d("CommandListener", String.format("Disconnected! Code: %d Reason: %s", code, reason));
        }

        @Override
        public void onError(Exception error) {
            Log.e("CommandListener", "Error", error);
        }

        @Override
        public void onConnectToEndpoint(String endpoint) {
        }

        @Override
        public void onJSON(JSONObject json) {
        }

        @Override
        public void onMessage(String message) {
        }

    }

    private void parseCommands() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Command lastCmd = commands.getLastReceivedCommand();
                String params = "";

                if (lastCmd != null && lastCmd.getDate() > 0) {
                    params = "{\"date\":{\"$gt\":" + lastCmd.getDate() + "},\"$sort\":{\"date\":1}}";
                }

                System.out.println(params);
                String getUrl = BACKEND_URL + "/commands" + "?" + NetworkUtils.encodeParams(params);
                System.out.println(getUrl);

                HttpGet get = new HttpGet(getUrl);

                HttpResponse response = NetworkUtils.sendRequest(get);
                JSONArray jsonArray = NetworkUtils.parseAsJsonArray(response);

                if (jsonArray == null)
                    return null;

                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        createCommandFromJSON(obj);
                    }
                } catch (JSONException e) {
                    Log.d("CommandListener.parseCommands", "Couldn't read command from Array", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                client.connect();
            }
        }.execute();
    }

    private Command createCommandFromJSON(JSONObject obj) {
        Command command = null;
        try {
            command = new Command();
            command.setDate(obj.getLong("date"));
            command.setOrigin(obj.getString("origin"));
            command.setData(obj.getString("data"));
            handleCommand(command.getData());
            commands.create(command);
        } catch (JSONException e) {
            Log.d("CommandListener.createCommandFromJson", "Couldn't read json", e);
        }
        return command;
    }

    private void handleCommand(String data) throws JSONException {
        JSONObject json = new JSONObject(data);
        if (json.getString("entity").equalsIgnoreCase("mission")) {
            if (!json.has("changes"))
                return;

            JSONArray changes = json.getJSONArray("changes");
            Mission mission = new Mission();
            mission.setId(json.getLong("id"));
            for (int i = 0; i < changes.length(); i++) {
                JSONObject change = changes.getJSONObject(i);
                String attribute = change.getString("attribute");
                if (attribute.equalsIgnoreCase("vehicle")) {
                    mission.setVehicle(change.getString("new_val"));
                } else if (attribute.equalsIgnoreCase("observation")) {
                    mission.setObservation(change.getString("new_val"));
                } else if (attribute.equalsIgnoreCase("type")) {
                    mission.setType(change.getString("new_val"));
                } else if (attribute.equalsIgnoreCase("responsible")) {
                    mission.setResponsible(change.getString("new_val"));
                }
            }
            missions.create(mission);
        }
    }
}

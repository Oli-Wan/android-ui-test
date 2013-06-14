package com.example.AndroidUITest.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.codebutler.android_websockets.SocketIOClient;
import com.example.AndroidUITest.messaging.BroadcastMessages;
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
import java.util.ArrayList;
import java.util.List;

public class CommandListener {

    private static final URI backendUri = URI.create("http://192.168.100.51:2403");
    private List<BroadcastMessages.onNewCommandListener> listeners;
    private CommandOpenHelper commands;
    private MissionOpenHelper missions;
    private SocketIOClient client;

    private CommandListener() {
    }

    private static class CommandListenerHolder {
        public static final CommandListener INSTANCE = new CommandListener();
    }

    public static CommandListener getInstance() {
        return CommandListenerHolder.INSTANCE;
    }

    public void register(BroadcastMessages.onNewCommandListener listener) {
        this.listeners.add(listener);
    }

    public void init(Context context) {
        this.listeners = new ArrayList<BroadcastMessages.onNewCommandListener>();
        this.commands = new CommandOpenHelper(context);
        this.missions = new MissionOpenHelper(context);
        client = new SocketIOClient(backendUri, new CommandClient());
        parseCommands();
    }

    private class CommandClient implements SocketIOClient.Handler {
        @Override
        public void onConnect() {
        }

        @Override
        public void on(String event, JSONArray arguments) {
            try {
                Command command = createCommandFromJSON(arguments.getJSONObject(0));
                for (BroadcastMessages.onNewCommandListener listener : listeners) {
                    listener.onNewCommand(new Command());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnect(int code, String reason) {
            System.out.println(String.format("Disconnected! Code: %d Reason: %s", code, reason));
        }

        @Override
        public void onError(Exception error) {
            System.out.println("Error!");
            error.printStackTrace();
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

                if (lastCmd != null) {
                    params = "?{\"date\": {\"$gt\":" + lastCmd.getDate() + "},\"$sort\": {\"date\": 1}}";
                }

                HttpGet get = new HttpGet(backendUri.toString() + "/commands" + NetworkUtils.encodeParams(params));
                HttpResponse response = NetworkUtils.sendRequest(get);
                JSONArray jsonArray = NetworkUtils.parseAsJsonArray(response);

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
        };
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

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
import com.example.AndroidUITest.utils.JSONUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommandListener {
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
        if (started)
            return;

        Intent intent = new Intent(context, MissionMessagingService.class);
        context.startService(intent);
        context.bindService(intent, new NetworkServiceConnection(), Context.BIND_AUTO_CREATE);

        this.commands = new CommandOpenHelper(context);
        this.missions = new MissionOpenHelper(context);
        this.client = new SocketIOClient(URI.create(NetworkUtils.BACKEND_URL), new CommandClient());
        parseCommands();
        started = true;
    }

    public boolean isStarted() {
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

            String argumentsString = arguments.toString();
            try {
                List<Map<String, Object>> list = JSONUtils.decode(argumentsString, List.class);
                Command command = extractCommandFromMap(list.get(0));
                command.setStatus("read");
                handleCommand(command.getData());
                commands.create(command);
                messenger.send(Message.obtain(null, MissionMessagingService.MISSION_UPDATED));
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
            Log.d("CommandListener", message);
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
                String getUrl = NetworkUtils.BACKEND_URL + "/commands" + "?" + NetworkUtils.encodeParams(params);
                System.out.println(getUrl);

                HttpGet get = new HttpGet(getUrl);

                HttpResponse response = NetworkUtils.sendRequest(get);
                String responseAsString = NetworkUtils.getResponseAsString(response);
                List<Map<String, Object>> commandList = JSONUtils.decode(responseAsString, List.class);

                if (commandList == null)
                    return null;

                for (Map<String, Object> cmdMap : commandList) {
                    Command command = extractCommandFromMap(cmdMap);
                    handleCommand(command.getData());
                    commands.create(command);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                client.connect();
            }
        }.execute();
    }

    private Command extractCommandFromMap(Map<String, Object> cmdMap) {
        Command command = new Command();
        command.setStatus("read");
        command.setDate((Long) cmdMap.get("date"));
        command.setOrigin((String) cmdMap.get("origin"));
        command.setData((Map<String, Object>) cmdMap.get("data"));
        return command;
    }

    private void handleCommand(Map<String, Object> data) {
        if (!data.containsKey("entity") || !data.containsKey("changes") || !data.containsKey("id")) {
            Log.e("CommandListener", "Badly formatted data : " + JSONUtils.encode(data));
            return;
        }

        String entity = (String) data.get("entity");
        if (entity.equalsIgnoreCase("mission")) {
            Mission mission = new Mission();
            mission.setId((Long) data.get("id"));
            List<Map<String, String>> changes = (List<Map<String, String>>) data.get("changes");
            for (Map<String, String> change : changes) {
                String attribute = change.get("attribute");
                if (attribute.equalsIgnoreCase("vehicle")) {
                    mission.setVehicle(change.get("new_val"));
                } else if (attribute.equalsIgnoreCase("observation")) {
                    mission.setObservation(change.get("new_val"));
                } else if (attribute.equalsIgnoreCase("type")) {
                    mission.setType(change.get("new_val"));
                } else if (attribute.equalsIgnoreCase("responsible")) {
                    mission.setResponsible(change.get("new_val"));
                }
            }
            missions.incomingChanges(mission);
        }
    }
}

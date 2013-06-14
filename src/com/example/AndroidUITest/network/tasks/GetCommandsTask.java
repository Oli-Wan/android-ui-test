package com.example.AndroidUITest.network.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import com.example.AndroidUITest.models.Command;
import com.example.AndroidUITest.models.Mission;
import com.example.AndroidUITest.storage.CommandOpenHelper;
import com.example.AndroidUITest.storage.MissionOpenHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetCommandsTask extends AsyncTask<Void, Void, JSONArray> {

    private final CommandOpenHelper commandDb;
    private final Context context;
    private MissionOpenHelper missionDb;

    public GetCommandsTask(Context context) {
        this.context = context;
        this.missionDb = new MissionOpenHelper(context);
        this.commandDb = new CommandOpenHelper(context);
    }

    @Override
    protected JSONArray doInBackground(Void... voids) {
        System.out.println("Task");
        JSONArray json = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://192.168.100.51:2403/commands");
            HttpResponse response = client.execute(get);
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String l;
            while ((l = in.readLine()) != null) {
                result.append(l);
            }
            in.close();
            json = new JSONArray(result.toString());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        commandDb.clear();
        missionDb.clear();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Command command = new Command();
                command.setDate(obj.getString("date"));
                command.setOrigin(obj.getString("origin"));
                command.setData(obj.getString("data"));
                handleCommand(command.getData());
                commandDb.create(command);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(String data) throws JSONException {
        JSONObject json = new JSONObject(data);
        System.out.println(data);
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

            missionDb.create(mission);
        }
    }
}

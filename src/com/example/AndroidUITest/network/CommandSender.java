package com.example.AndroidUITest.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.example.AndroidUITest.models.Command;
import com.example.AndroidUITest.network.utils.NetworkUtils;
import com.example.AndroidUITest.storage.CommandOpenHelper;
import com.example.AndroidUITest.utils.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class CommandSender extends AsyncTask<Void, Void, Void> {
    private CommandOpenHelper commandOpenHelper;
    private boolean started;

    private CommandSender() {

    }

    private static class CommandSenderHolder {
        public static final CommandSender INSTANCE = new CommandSender();
    }

    public static CommandSender getInstance() {
        return CommandSenderHolder.INSTANCE;
    }

    public void start(Context context) {
        if (started)
            return;

        commandOpenHelper = new CommandOpenHelper(context);
        started = true;
        this.execute();
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            while (true) {
                List<Command> nonSentCommands = commandOpenHelper.getNonSentCommands();
                for (Command command : nonSentCommands) {
                    Log.d("CommandSender", "Got non sent commands");
                    String json = JSONUtils.encode(command);
                    Log.d("CommandSender", command.toString());
                    Log.d("CommandSender", json);
                    HttpPost post = new HttpPost(NetworkUtils.BACKEND_URL + "/commands");
                    post.setEntity(new ByteArrayEntity(json.getBytes("UTF8")));
                    HttpResponse httpResponse = NetworkUtils.sendRequest(post);
                    StatusLine statusLine = httpResponse.getStatusLine();
                    if (statusLine.getStatusCode() == 200) {
                        command.setStatus("sent");
                        commandOpenHelper.update(command);
                    } else {
                        Log.e("CommandSender", "Could not send command, status : " + statusLine.getStatusCode());
                        Log.e("CommandSender", NetworkUtils.getResponseAsString(httpResponse));
                    }
                }
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.example.AndroidUITest.network;

import android.os.AsyncTask;
import com.example.AndroidUITest.storage.CommandOpenHelper;

public class CommandSender extends AsyncTask<Void, Void, Void> {
    private CommandSender() {

    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class CommandSenderHolder {
        public static final CommandSender INSTANCE = new CommandSender();
    }

    public static CommandSender getInstance() {
        return CommandSenderHolder.INSTANCE;
    }


}

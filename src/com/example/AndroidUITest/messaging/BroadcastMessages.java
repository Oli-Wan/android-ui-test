package com.example.AndroidUITest.messaging;

import com.example.AndroidUITest.models.Command;

public class BroadcastMessages {
    public static interface onNewCommandListener {
        public void onNewCommand(Command command);
    }
}

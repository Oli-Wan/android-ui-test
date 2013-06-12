package com.example.AndroidUITest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.example.AndroidUITest.activities.MissionDescriptionActivity;

public class ActivityUtils {
    public static void startNewActivity(Activity origin, Class<? extends Activity> newActivity) {
        Intent intent = new Intent(origin.getBaseContext(), newActivity);
        origin.startActivity(intent);
    }
}

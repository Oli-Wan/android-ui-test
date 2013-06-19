package com.example.AndroidUITest.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ActivityUtils {
    public static void startNewActivity(Activity origin, Class<? extends Activity> newActivity, Bundle bundle) {
        Intent intent = new Intent(origin.getBaseContext(), newActivity);
        if(bundle != null)
            intent.putExtras(bundle);
        origin.startActivity(intent);
    }
}

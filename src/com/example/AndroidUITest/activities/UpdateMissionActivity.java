package com.example.AndroidUITest.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import com.example.AndroidUITest.R;
import com.example.AndroidUITest.adapters.MenuAdapter;
import com.example.AndroidUITest.models.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import java.util.ArrayList;
import java.util.List;

public class UpdateMissionActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.update_mision);

        // Sliding menu
        SlidingMenu slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setBehindWidth(300);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setMenu(R.layout.hidden_menu);
        ListView list = (ListView) slidingMenu.getMenu().findViewById(R.id.hiddenMenu);
        List<MenuItem> menu = new ArrayList<MenuItem>();
        menu.add(new MenuItem("Mission"));
        menu.add(new MenuItem("Personnel"));
        menu.add(new MenuItem("Evènements"));
        menu.add(new MenuItem("Victimes"));
        menu.add(new MenuItem("Véhicules"));
        menu.add(new MenuItem("Retour à la liste"));
        list.setAdapter(new MenuAdapter(getBaseContext(), menu));

        Spinner spinner = (Spinner) findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mission_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}

package com.example.AndroidUITest.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.example.AndroidUITest.utils.ActivityUtils;
import com.example.AndroidUITest.R;
import com.example.AndroidUITest.activities.fragments.*;
import com.example.AndroidUITest.adapters.MenuAdapter;
import com.example.AndroidUITest.listeners.MenuClickListener;
import com.example.AndroidUITest.models.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

public class MissionDescriptionActivity extends Activity {

    private SlidingMenu slidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Creation");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mission_description);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment updateMission = new UpdateMissionFragment();
        fragmentTransaction.add(R.id.fragmentContainer, updateMission);
        fragmentTransaction.commit();

        // Sliding menu
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setBehindWidth(300);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setMenu(R.layout.hidden_menu);
        ListView list = (ListView) slidingMenu.getMenu().findViewById(R.id.hiddenMenu);
        List<MenuItem> menu = new ArrayList<MenuItem>();
        menu.add(new MenuItem("Mission", UpdateMissionFragment.class));
        menu.add(new MenuItem("Personnel", StaffListFragment.class));
        menu.add(new MenuItem("Evènements", EventListFragment.class));
        menu.add(new MenuItem("Victimes", VictimListFragment.class));
        menu.add(new MenuItem("Véhicules", VehicleListFragment.class));
        menu.add(new MenuItem("Retour à la liste", null));
        list.setAdapter(new MenuAdapter(getBaseContext(), menu));
        list.setOnItemClickListener(new MenuClickListener(this, menu, R.id.fragmentContainer));
    }

    public void onViewChange() {
        slidingMenu.showContent(true);
    }


    public void goToNewEvent(View view) {
        ActivityUtils.startNewActivity(this, AddEventActivity.class);
    }
}

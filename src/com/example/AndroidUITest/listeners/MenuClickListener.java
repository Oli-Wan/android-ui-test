package com.example.AndroidUITest.listeners;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import com.example.AndroidUITest.utils.ActivityUtils;
import com.example.AndroidUITest.activities.HomeActivity;
import com.example.AndroidUITest.activities.MissionDescriptionActivity;
import com.example.AndroidUITest.models.MenuItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MenuClickListener implements AdapterView.OnItemClickListener {

    private final List<MenuItem> menu;
    private final int container;
    private final MissionDescriptionActivity activity;

    public MenuClickListener(MissionDescriptionActivity activity, List<MenuItem> menu, int container) {
        this.menu = menu;
        this.activity = activity;
        this.container = container;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        MenuItem menuItem = menu.get(position);
        Class<? extends Fragment> fragment = menuItem.getFragment();

        // go back to home
        if(fragment == null) {
            ActivityUtils.startNewActivity(activity, HomeActivity.class, null);
            return;
        }

        FragmentManager fragmentManager = activity.getFragmentManager();
        try {
            Constructor<? extends Fragment> constructor = fragment.getConstructor();
            Fragment fragmentInstance = constructor.newInstance();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragmentInstance);
            fragmentTransaction.addToBackStack(null);
            activity.onViewChange();
            fragmentTransaction.commit();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

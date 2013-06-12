package com.example.AndroidUITest.listeners;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import com.example.AndroidUITest.models.MenuItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MenuClickListener implements AdapterView.OnItemClickListener {

    private final List<MenuItem> menu;
    private final FragmentManager fragmentManager;
    private final int container;

    public MenuClickListener(FragmentManager manager, List<MenuItem> menu, int container) {
        this.menu = menu;
        this.fragmentManager = manager;
        this.container = container;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        MenuItem menuItem = menu.get(position);
        Class<? extends Fragment> fragment = menuItem.getFragment();
        try {
            Constructor<? extends Fragment> constructor = fragment.getConstructor();
            Fragment fragmentInstance = constructor.newInstance();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(container, fragmentInstance);
            fragmentTransaction.addToBackStack(null);
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

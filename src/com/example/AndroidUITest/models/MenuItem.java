package com.example.AndroidUITest.models;

import android.app.Fragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MenuItem {
    private final String name;
    private final Class<? extends Fragment> fragment;

    public MenuItem(String name, Class<? extends Fragment> fragment) {
        this.name = name;
        this.fragment = fragment;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Fragment> getFragment() {
        return fragment;
    }
}

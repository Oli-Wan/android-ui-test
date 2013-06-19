package com.example.AndroidUITest.utils;

import com.example.AndroidUITest.models.Identifiable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandUtils {
    public static Map<String, Object> getCommandData(Identifiable oldObject, Identifiable newObject) throws NoSuchFieldException, IllegalAccessException {
        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> changes = getChanges(oldObject, newObject);
        String entity = getEntity(oldObject);
        data.put("entity", entity.toLowerCase());
        data.put("type", "update");
        data.put("id", oldObject.getId());
        data.put("changes", changes);
        return data;
    }

    private static String getEntity(Identifiable oldObject) {
        String[] fqdn = oldObject.getClass().getName().split("\\.");
        String entity;
        if (fqdn.length > 0) {
            entity = fqdn[fqdn.length - 1];
        } else {
            entity = oldObject.getClass().getName();
        }
        return entity;
    }

    private static <T> List<Map<String, Object>> getChanges(T oldObject, T newObject) throws IllegalAccessException {
        List<Map<String, Object>> changes = new ArrayList<Map<String, Object>>();
        Class<?> klazz = oldObject.getClass();
        Field[] fields = klazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object oldValue = field.get(oldObject);
            Object newValue = field.get(newObject);

            if (oldValue == null && newValue != null) {
                changes.add(getChange(field, "", newValue));
            } else if (oldValue != null && !(oldValue.equals(newValue))) {
                changes.add(getChange(field, oldValue, newValue));
            }
        }
        return changes;
    }

    private static Map<String, Object> getChange(Field field, Object oldValue, Object newValue) {
        Map<String, Object> change = new HashMap<String, Object>();
        change.put("attribute", field.getName());
        change.put("old_val", oldValue);
        change.put("new_val", newValue);
        return change;
    }
}

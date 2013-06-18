package com.example.AndroidUITest.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.AndroidUITest.models.Command;
import com.example.AndroidUITest.models.Identifiable;
import com.example.AndroidUITest.utils.JSONUtils;

import java.lang.reflect.Field;
import java.util.*;

public class CommandOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 13;
    private static final String TABLE_NAME = "command";

    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_ORIGIN = "origin";
    private static final String COL_DATA = "data";
    private static final String COL_STATUS = "status";

    public CommandOpenHelper(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_NAME +
                "(" + COL_ID + " INTEGER PRIMARY KEY, " +
                COL_DATE + " INTEGER, " +
                COL_ORIGIN + " TEXT, " +
                COL_DATA + " TEXT, " +
                COL_STATUS + " TEXT );";
        Log.d("CommandOpenHelper", createQuery);
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        Log.d("CommandOpenHelper", "upgrade");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<Command> getAll() {
        ArrayList<Command> commands = new ArrayList<Command>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM command", null);
        if (cursor.moveToFirst()) {
            do {
                Command command = getCommandFromCursor(cursor);
                commands.add(command);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return commands;
    }

    public void create(Command command) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues values = getContentValuesFromCommand(command);
        writableDatabase.insert("command", null, values);
        writableDatabase.close();
    }

    public void update(Command command) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getContentValuesFromCommand(command);
        db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(command.getId())});
        db.close();
    }

    public void clear() {
        onUpgrade(this.getWritableDatabase(), 0, 0);
    }

    public Command getLastReceivedCommand() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_ID, "MAX(" + COL_DATE + ")", COL_ORIGIN, COL_DATA, COL_STATUS}, null, null, null, null, null);
        Command command = null;
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            command = getCommandFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return command;
    }

    public List<Command> getNonSentCommands() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_ID, COL_DATE,
                COL_ORIGIN, COL_DATA, COL_STATUS}, COL_STATUS + "=?",
                new String[]{"waiting"}, null, null, null, null);

        List<Command> commands = new ArrayList<Command>();
        if (cursor.moveToFirst()) {
            do {
                Command command = getCommandFromCursor(cursor);
                commands.add(command);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return commands;
    }

    public void createCommandIfNeeded(Identifiable oldObject, Identifiable newObject) {

        try {
            List<Map<String, Object>> changes = getChanges(oldObject, newObject);
            Map<String, Object> data = new HashMap<String, Object>();
            String[] fqdn = oldObject.getClass().getName().split("\\.");
            String entity;
            Log.d("CommandOpenHelper", String.valueOf(fqdn.length));
            if (fqdn.length > 0) {
                entity = fqdn[fqdn.length - 1];
            } else {
                entity = oldObject.getClass().getName();
            }
            Log.d("CommandOpenHelper", entity);
            data.put("entity", entity.toLowerCase());
            data.put("type", "update");
            data.put("id", oldObject.getId());
            data.put("changes", changes);
            Command command = new Command();
            command.setOrigin("android");
            command.setData(data);
            command.setDate(new Date().getTime());
            command.setStatus("waiting");
            Log.d("CommandOpenHelper", command.toString());
            this.create(command);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private <T> List<Map<String, Object>> getChanges(T oldObject, T newObject) throws NoSuchFieldException, IllegalAccessException {
        List<Map<String, Object>> changes = new ArrayList<Map<String, Object>>();
        Class<?> klazz = oldObject.getClass();
        Field[] fields = klazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object oldValue = field.get(oldObject);
            Object newValue = field.get(newObject);

            if (oldValue == null && newValue != null) {
                Map<String, Object> change = new HashMap<String, Object>();
                change.put("attribute", field.getName());
                change.put("old_val", "");
                change.put("new_val", newValue);
                changes.add(change);
            } else if (oldValue != null && !(oldValue.equals(newValue))) {
                Map<String, Object> change = new HashMap<String, Object>();
                change.put("attribute", field.getName());
                change.put("old_val", oldValue);
                change.put("new_val", newValue);
                changes.add(change);
            }
        }
        return changes;
    }

    private Command getCommandFromCursor(Cursor cursor) {
        Command command = new Command();
        Map data = JSONUtils.decode(cursor.getString(3), Map.class);
        command.setId(cursor.getLong(0));
        command.setDate(cursor.getLong(1));
        command.setOrigin(cursor.getString(2));
        command.setData(data);
        command.setStatus(cursor.getString(4));
        return command;
    }

    private ContentValues getContentValuesFromCommand(Command command) {
        ContentValues values = new ContentValues();
        String data = JSONUtils.encode(command.getData());
        values.put("id", command.getId());
        values.put("date", command.getDate());
        values.put("origin", command.getOrigin());
        values.put("data", data);
        values.put("status", command.getStatus());
        return values;
    }
}

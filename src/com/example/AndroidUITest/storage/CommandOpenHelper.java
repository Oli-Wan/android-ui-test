package com.example.AndroidUITest.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.AndroidUITest.models.Command;
import com.example.AndroidUITest.models.Identifiable;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.*;

public class CommandOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String TABLE_NAME = "command";

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
                "(" + COL_DATE + " INTEGER, " +
                COL_ORIGIN + " TEXT, " +
                COL_DATA + " TEXT, " +
                COL_STATUS + "TEXT );";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<Command> getAll() {
        ArrayList<Command> commands = new ArrayList<Command>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM command", null);
        if (cursor.moveToFirst()) {
            do {
                Command command = extractCommandFromCursor(cursor);
                commands.add(command);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return commands;
    }

    public void create(Command command) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", command.getDate());
        values.put("origin", command.getOrigin());
        values.put("data", command.getData());
        values.put("status", command.getStatus());
        writableDatabase.insert("command", null, values);
        writableDatabase.close();
    }

    public void clear() {
        onUpgrade(this.getWritableDatabase(), 0, 0);
    }

    public Command getLastReceivedCommand() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"MAX(" + COL_DATE + ")", COL_ORIGIN, COL_DATA, COL_STATUS}, null, null, null, null, null);
        Command command = null;
        if (cursor.moveToFirst()) {
            command = extractCommandFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return command;
    }

    public List<Command> getNonSentCommands() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_DATE,
                COL_ORIGIN, COL_DATA, COL_DATA}, COL_STATUS+ "=?",
                new String[]{"waiting"}, null, null, null, null);

        List<Command> commands = new ArrayList<Command>();
        if (cursor.moveToFirst()) {
            do {
                Command command = extractCommandFromCursor(cursor);
                commands.add(command);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return commands;
    }

    public void createCommandIfNeeded(Identifiable oldObject, Identifiable newObject) {
        try {
            List<Map<String, String>> changes = getChanges(oldObject, newObject);
            Map<String, String> data = new HashMap<String, String>();
            data.put("entity", oldObject.getClass().getName().toLowerCase());
            data.put("type", "update");
            data.put("id", String.valueOf(oldObject.getId()));
            JSONObject dataObject = new JSONObject(data);
            dataObject.put("changes", changes);
            Command command = new Command();
            command.setOrigin("android");
            command.setData(dataObject.toString());
            command.setDate(new Date().getTime());
            command.setStatus("waiting");
            this.create(command);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private <T> List<Map<String, String>> getChanges(T oldObject, T newObject) throws NoSuchFieldException, IllegalAccessException {
        List<Map<String, String>> changes = new ArrayList<Map<String, String>>();
        Class<?> klazz = oldObject.getClass();
        Field[] fields = klazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object oldValue = field.get(newObject);
            Object newValue = field.get(oldObject);

            if (oldValue == null && newValue != null) {
                Map<String, String> change = new HashMap<String, String>();
                change.put("attribute", field.getName());
                change.put("old_val", "");
                change.put("new_value", newValue.toString());
                changes.add(change);
            } else if (oldValue != null && !(oldValue.equals(newValue))) {
                Map<String, String> change = new HashMap<String, String>();
                change.put("attribute", field.getName());
                change.put("old_val", oldValue.toString());
                String stringValue2 = newValue == null ? "" : newValue.toString();
                change.put("new_value", stringValue2);
                changes.add(change);
            }
        }
        return changes;
    }

    private Command extractCommandFromCursor(Cursor cursor) {
        Command command = new Command();
        command.setDate(cursor.getLong(0));
        command.setOrigin(cursor.getString(1));
        command.setData(cursor.getString(2));
        command.setStatus(cursor.getString(3));
        return command;
    }
}

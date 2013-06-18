package com.example.AndroidUITest.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.AndroidUITest.models.Command;
import com.example.AndroidUITest.models.Identifiable;
import com.example.AndroidUITest.utils.CommandUtils;
import com.example.AndroidUITest.utils.JSONUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CommandDataSource {
    private final SQLiteDatabase db;

    public CommandDataSource() {
        this.db = DatabaseOpenHelper.getInstance().getDb();
    }

    public void create(Command command) {
        ContentValues values = getContentValuesFromCommand(command);
        db.insert(DatabaseOpenHelper.CommandTable.TABLE_NAME, null, values);
    }

    public void update(Command command) {
        ContentValues values = getContentValuesFromCommand(command);
        db.update(DatabaseOpenHelper.CommandTable.TABLE_NAME, values, "id = ?", new String[]{String.valueOf(command.getId())});
    }

    public Command getLastReceivedCommand() {
        Cursor cursor = db.query(DatabaseOpenHelper.CommandTable.TABLE_NAME, new String[]{DatabaseOpenHelper.CommandTable.COL_ID,
                "MAX(" + DatabaseOpenHelper.CommandTable.COL_DATE + ")", DatabaseOpenHelper.CommandTable.COL_ORIGIN, DatabaseOpenHelper.CommandTable.COL_DATA,
                DatabaseOpenHelper.CommandTable.COL_STATUS}, null, null, null, null, null);
        Command command = null;
        if (cursor.moveToFirst()) {
            command = getCommandFromCursor(cursor);
        }
        cursor.close();
        return command;
    }

    public List<Command> getNonSentCommands() {
        Cursor cursor = db.query(DatabaseOpenHelper.CommandTable.TABLE_NAME, new String[]{DatabaseOpenHelper.CommandTable.COL_ID, DatabaseOpenHelper.CommandTable.COL_DATE,
                DatabaseOpenHelper.CommandTable.COL_ORIGIN, DatabaseOpenHelper.CommandTable.COL_DATA, DatabaseOpenHelper.CommandTable.COL_STATUS}, DatabaseOpenHelper.CommandTable.COL_STATUS + "=?",
                new String[]{"waiting"}, null, null, null, null);

        List<Command> commands = new ArrayList<Command>();
        if (cursor.moveToFirst()) {
            do {
                Command command = getCommandFromCursor(cursor);
                commands.add(command);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return commands;
    }

    public void createLocalCommand(Identifiable oldObject, Identifiable newObject) {
        try {
            Map<String, Object> data = CommandUtils.getCommandData(oldObject, newObject);

            Command command = new Command();
            command.setOrigin("android");
            command.setData(data);
            command.setDate(new Date().getTime());
            command.setStatus("waiting");
            this.create(command);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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

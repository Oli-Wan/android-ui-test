package com.example.AndroidUITest.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.AndroidUITest.models.Command;

import java.util.ArrayList;
import java.util.List;

public class CommandOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_NAME = "command";

    private static final String COL_DATE = "date";
    private static final String COL_ORIGIN = "origin";
    private static final String COL_DATA = "data";

    public CommandOpenHelper(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_NAME +
                "(" + COL_DATE + " TEXT," +
                COL_ORIGIN + " TEXT, " +
                COL_DATA + " TEXT);";
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
                Command command = new Command();
                command.setDate(cursor.getString(1));
                command.setOrigin(cursor.getString(2));
                command.setData(cursor.getString(3));
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
        writableDatabase.insert("command", null, values);
        writableDatabase.close();
    }

    public void clear() {
        onUpgrade(this.getWritableDatabase(), 0, 0);
    }
}

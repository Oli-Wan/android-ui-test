package com.example.AndroidUITest.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.AndroidUITest.models.Mission;

import java.util.ArrayList;
import java.util.List;

public class MissionOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;


    private static final String TABLE_NAME = "mission";

    private static final String COL_ID = "id";
    private static final String COL_OBSERVATION = "observation";
    private static final String COL_VEHICLE = "vehicle";
    private static final String COL_RESPONSIBLE = "responsible";
    private static final String COL_TYPE = "type";

    public MissionOpenHelper(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_NAME +
                "(" + COL_ID + " INTEGER UNIQUE NOT NULL," +
                COL_OBSERVATION + " TEXT," +
                COL_TYPE + " TEXT, " +
                COL_VEHICLE + " TEXT, " +
                COL_RESPONSIBLE + " TEXT);";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public List<Mission> getAll() {
        ArrayList<Mission> missions = new ArrayList<Mission>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM mission", null);
        if (cursor.moveToFirst()) {
            do {
                Mission mission = new Mission();
                mission.setId(cursor.getLong(0));
                mission.setObservation(cursor.getString(1));
                mission.setType(cursor.getString(2));
                mission.setVehicle(cursor.getString(3));
                mission.setResponsible(cursor.getString(4));
                missions.add(mission);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return missions;
    }

    public Mission get(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_ID,
                COL_OBSERVATION, COL_TYPE, COL_VEHICLE, COL_RESPONSIBLE}, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        Mission mission = null;
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            mission = new Mission();
            mission.setId(cursor.getLong(0));
            mission.setObservation(cursor.getString(1));
            mission.setType(cursor.getString(2));
            mission.setVehicle(cursor.getString(3));
            mission.setResponsible(cursor.getString(4));
        }
        cursor.close();
        db.close();
        return mission;
    }

    public void create(Mission mission) {
        ContentValues values = new ContentValues();
        values.put("id", mission.getId());
        values.put("observation", mission.getObservation());
        values.put("type", mission.getType());
        values.put("vehicle", mission.getVehicle());
        values.put("responsible", mission.getResponsible());
        System.out.println(mission.getId());
        boolean missionExists = get(mission.getId()) == null;
        SQLiteDatabase db = this.getWritableDatabase();
        if (missionExists ) {
            System.out.println("insert");
            db.insert("mission", null, values);
        } else {
            System.out.println("update");
            db.update("mission", values, "id = ?", new String[]{String.valueOf(mission.getId())});
        }

        db.close();
    }

    public void clear() {
        onUpgrade(this.getWritableDatabase(), 0, 0);
    }
}

package com.example.AndroidUITest.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.AndroidUITest.models.Mission;

import java.util.ArrayList;
import java.util.List;

public class MissionDataSource {
    private final SQLiteDatabase db;

    public MissionDataSource() {
        this.db = DatabaseOpenHelper.getInstance().getDb();
    }

    public List<Mission> getAll() {
        ArrayList<Mission> missions = new ArrayList<Mission>();
        Cursor cursor = db.rawQuery("SELECT * FROM mission", null);
        if (cursor.moveToFirst()) {
            do {
                missions.add(getMissionFromContentValues(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return missions;
    }

    public Mission get(long id) {
        Cursor cursor = db.query(DatabaseOpenHelper.MissionTable.TABLE_NAME, new String[]{DatabaseOpenHelper.MissionTable.COL_ID,
                DatabaseOpenHelper.MissionTable.COL_OBSERVATION, DatabaseOpenHelper.MissionTable.COL_TYPE, DatabaseOpenHelper.MissionTable.COL_VEHICLE,
                DatabaseOpenHelper.MissionTable.COL_RESPONSIBLE}, DatabaseOpenHelper.MissionTable.COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        Mission mission = null;
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            mission = getMissionFromContentValues(cursor);
        }
        cursor.close();
        return mission;
    }

    public void create(Mission mission) {
        Log.d("DatabaseOpenHelper.MissionTable", "Create");
        ContentValues values = getContentValuesFromMission(mission);
        db.insert(DatabaseOpenHelper.MissionTable.TABLE_NAME, null, values);
    }

    public void update(Mission mission) {
        Log.d("DatabaseOpenHelper.MissionTable", "Update");
        ContentValues values = getContentValuesFromMission(mission);
        db.update(DatabaseOpenHelper.MissionTable.TABLE_NAME, values,
                DatabaseOpenHelper.MissionTable.COL_ID + " = ?",
                new String[]{String.valueOf(mission.getId())});
    }

    public void incomingChanges(Mission mission) {
        boolean missionExists = get(mission.getId()) != null;
        if (missionExists) {
            this.update(mission);
        } else {
            this.create(mission);
        }
    }

    private ContentValues getContentValuesFromMission(Mission mission) {
        ContentValues values = new ContentValues();
        values.put("id", mission.getId());
        values.put("observation", mission.getObservation());
        values.put("type", mission.getType());
        values.put("vehicle", mission.getVehicle());
        values.put("responsible", mission.getResponsible());
        return values;
    }

    private Mission getMissionFromContentValues(Cursor cursor) {
        Mission mission = new Mission();
        mission.setId(cursor.getLong(0));
        mission.setObservation(cursor.getString(1));
        mission.setType(cursor.getString(2));
        mission.setVehicle(cursor.getString(3));
        mission.setResponsible(cursor.getString(4));
        return mission;
    }
}

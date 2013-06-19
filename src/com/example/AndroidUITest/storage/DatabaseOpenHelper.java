package com.example.AndroidUITest.storage;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.AndroidUITest.MyApplication;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABSE_NAME = "AndroidUIDB";
    private final SQLiteDatabase db;

    public class MissionTable {
        public static final String TABLE_NAME = "mission";
        public static final String COL_ID = "id";
        public static final String COL_OBSERVATION = "observation";
        public static final String COL_VEHICLE = "vehicle";
        public static final String COL_RESPONSIBLE = "responsible";
        public static final String COL_TYPE = "type";
    }

    public class CommandTable {
        public static final String TABLE_NAME = "command";
        public static final String COL_ID = "id";
        public static final String COL_DATE = "date";
        public static final String COL_ORIGIN = "origin";
        public static final String COL_DATA = "data";
        public static final String COL_STATUS = "status";
    }

    private DatabaseOpenHelper() {
        super(MyApplication.getAppContext(), DATABSE_NAME, null, DATABASE_VERSION);
        this.db = getWritableDatabase();
    }

    private static class DatabaseOpenHelperHolder {
        public static final DatabaseOpenHelper INSTANCE = new DatabaseOpenHelper();
    }

    public static DatabaseOpenHelper getInstance() {
        return DatabaseOpenHelperHolder.INSTANCE;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void close() {
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMissionTable = "CREATE TABLE " + MissionTable.TABLE_NAME +
                "(" + MissionTable.COL_ID + " INTEGER UNIQUE NOT NULL," +
                MissionTable.COL_OBSERVATION + " TEXT," +
                MissionTable.COL_TYPE + " TEXT, " +
                MissionTable.COL_VEHICLE + " TEXT, " +
                MissionTable.COL_RESPONSIBLE + " TEXT);";

        String createCommandTable = "CREATE TABLE " + CommandTable.TABLE_NAME +
                "(" + CommandTable.COL_ID + " INTEGER PRIMARY KEY, " +
                CommandTable.COL_DATE + " INTEGER, " +
                CommandTable.COL_ORIGIN + " TEXT, " +
                CommandTable.COL_DATA + " TEXT, " +
                CommandTable.COL_STATUS + " TEXT );";

        db.execSQL(createCommandTable);
        db.execSQL(createMissionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MissionTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CommandTable.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


}

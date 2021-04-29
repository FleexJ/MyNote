package com.example.mynote.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "widgets.db";
    public static final int SCHEMA = 2;

    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_NOTES_ID = "id";
    public static final String COLUMN_NOTES_NAME = "name";
    public static final String COLUMN_NOTES_DESCRIPTION = "descr";
    public static final String COLUMN_NOTES_STATE = "state";
    public static final String COLUMN_NOTES_DELAY = "delay";
    public static final String COLUMN_NOTES_REPEAT = "repeat";

    public static final String TABLE_TIMERS = "timers";
    public static final String COLUMN_TIMERS_ID = "id";
    public static final String COLUMN_TIMERS_NAME = "name";
    public static final String COLUMN_TIMERS_STATE = "state";
    public static final String COLUMN_TIMERS_MINUTE = "minute";

    public static final String TABLE_TRASH = "trash";
    public static final String COLUMN_TRASH_ID = "id";
    public static final String COLUMN_TRASH_NAME = "name";
    public static final String COLUMN_TRASH_DESCRIPTION = "description";
    public static final String COLUMN_TRASH_DELAY = "delay";
    public static final String COLUMN_TRASH_TYPE = "type";

    public static final String TABLE_ID_COUNT = "id_count";
    public static final String COLUMN_ID_COUNT_ID = "id";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + " (" +
                COLUMN_NOTES_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NOTES_NAME + " TEXT, " +
                COLUMN_NOTES_DESCRIPTION + " TEXT, " +
                COLUMN_NOTES_STATE + " INTEGER, " +
                COLUMN_NOTES_DELAY + " TEXT, " +
                COLUMN_NOTES_REPEAT + " TEXT)"
        );
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TIMERS + " (" +
                COLUMN_TIMERS_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TIMERS_NAME + " TEXT, " +
                COLUMN_TIMERS_STATE + " INTEGER, " +
                COLUMN_TIMERS_MINUTE + " INTEGER)"
        );
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRASH + " (" +
                COLUMN_TRASH_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TRASH_NAME + " TEXT, " +
                COLUMN_TRASH_DESCRIPTION + " TEXT, " +
                COLUMN_TRASH_DELAY + " TEXT, " +
                COLUMN_TRASH_TYPE + " INTEGER)"
        );
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ID_COUNT + " (" +
                COLUMN_ID_COUNT_ID + " INTEGER PRIMARY KEY)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRASH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ID_COUNT);
        onCreate(db);
    }
}

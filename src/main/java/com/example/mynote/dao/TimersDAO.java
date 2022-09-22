package com.example.mynote.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.mynote.entity.Timer;

import java.util.ArrayList;
import java.util.List;

public class TimersDAO {

    private final Context context;

    public TimersDAO(Context context) {
        this.context = context;
    }

    public void edit(Timer timer) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        SQLiteStatement sqLiteStatement = db.compileStatement("UPDATE " + DatabaseHelper.TABLE_TIMERS +
                " SET " +
                DatabaseHelper.COLUMN_TIMERS_NAME + "=?, " +
                DatabaseHelper.COLUMN_TIMERS_STATE + "=?, " +
                DatabaseHelper.COLUMN_TIMERS_MINUTE + "=? " +
                "WHERE " + DatabaseHelper.COLUMN_TIMERS_ID + "=?"
        );
        sqLiteStatement.bindString(1, timer.getName());
        sqLiteStatement.bindLong(2, timer.getState());
        sqLiteStatement.bindLong(3, timer.getMinute());
        sqLiteStatement.bindLong(4, timer.getId());
        sqLiteStatement.executeUpdateDelete();
        db.close();
    }

    public void delete(Timer timer) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        db.execSQL("DELETE FROM timers WHERE " + DatabaseHelper.COLUMN_TIMERS_ID + "=" + timer.getId());
        db.close();
    }

    public void insert(Timer timer) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        SQLiteStatement sqLiteStatement = db.compileStatement(
                "INSERT INTO " + DatabaseHelper.TABLE_TIMERS + " VALUES(?, ?, ?, ?)"
        );
        sqLiteStatement.bindLong(1, timer.getId());
        sqLiteStatement.bindString(2, timer.getName());
        sqLiteStatement.bindLong(3, timer.getState());
        sqLiteStatement.bindLong(4, timer.getMinute());
        sqLiteStatement.executeInsert();
        db.close();
    }

    public Timer getById(int id) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_TIMERS + " WHERE " + DatabaseHelper.COLUMN_TIMERS_ID + "=" + id,
                null);
        Timer timer = null;
        if (!(cursor == null)) {
            cursor.moveToFirst();
            timer = new Timer(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3)
            );
            cursor.close();
        }
        db.close();
        return timer;
    }

    public List<Timer> getAll() {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        List<Timer> timerList = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_TIMERS + " ORDER BY " + DatabaseHelper.COLUMN_TIMERS_ID + " ASC",
                null);
        while (cursor.moveToNext())
            timerList.add(
                    new Timer(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                    )
            );
        cursor.close();
        db.close();
        return timerList;
    }

    public List<Timer> getActiveAll() {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        List<Timer> timers = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_TIMERS + " WHERE " + DatabaseHelper.COLUMN_TIMERS_STATE + "=" + Timer.ACTIVE_STATE,
                null);
        while (cursor.moveToNext())
            timers.add(
                    new Timer(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                    )
            );
        cursor.close();
        db.close();
        return timers;
    }

    public void setStateNotActiveAll() {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        db.execSQL("UPDATE " + DatabaseHelper.TABLE_TIMERS +
                " SET " + DatabaseHelper.COLUMN_TIMERS_STATE + "=" + Timer.NOT_ACTIVE_STATE +
                " WHERE " + DatabaseHelper.COLUMN_TIMERS_STATE + "=" + Timer.ACTIVE_STATE);
        db.close();
    }
}

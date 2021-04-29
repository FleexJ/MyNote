package com.example.mynote.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.mynote.entity.Timer;

import java.util.ArrayList;
import java.util.List;

public class TimersDAO {

    private SQLiteDatabase db;

    public TimersDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public List<Timer> getAllTimers() {
        List<Timer> timerList = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_TIMERS + " ORDER BY " + DatabaseHelper.COLUMN_TIMERS_ID + " ASC",
                null);
        while (cursor.moveToNext()) {
            timerList.add(new Timer(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3)
            ));
        }
        cursor.close();
        return timerList;
    }

    public void editTimer(Timer timer) {
        SQLiteStatement sqLiteStatement = db.compileStatement("UPDATE " + DatabaseHelper.TABLE_TIMERS + " SET " +
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
    }

    public void deleteTimer(Timer timer) {
        db.execSQL("DELETE FROM timers WHERE " + DatabaseHelper.COLUMN_TIMERS_ID + "=" + timer.getId());
    }

    public void insertTimer(Timer timer) {
        SQLiteStatement sqLiteStatement = db.compileStatement(
                "INSERT INTO " + DatabaseHelper.TABLE_TIMERS + " VALUES(?, ?, ?, ?)"
        );
        sqLiteStatement.bindLong(1, timer.getId());
        sqLiteStatement.bindString(2, timer.getName());
        sqLiteStatement.bindLong(3, timer.getState());
        sqLiteStatement.bindLong(4, timer.getMinute());
        sqLiteStatement.executeInsert();
    }

    public Timer getTimersById(int id) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_TIMERS + " WHERE " + DatabaseHelper.COLUMN_TIMERS_ID + "=" + id,
                null);
        if (!(cursor == null)) {
            cursor.moveToFirst();
            Timer timer = new Timer(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3)
            );
            cursor.close();
            return timer;
        }
        return null;
    }

    public void setStateNotActiveAll() {
        db.execSQL("UPDATE " + DatabaseHelper.TABLE_TIMERS +
                " SET " + DatabaseHelper.COLUMN_TIMERS_STATE + "=" + Timer.NOT_ACTIVE_STATE +
                " WHERE " + DatabaseHelper.COLUMN_TIMERS_STATE + "=" + Timer.ACTIVE_STATE);
    }
}

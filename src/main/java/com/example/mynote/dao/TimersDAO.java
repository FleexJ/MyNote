package com.example.mynote.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.mynote.entity.Timer;

import java.util.ArrayList;
import java.util.List;

public class TimersDAO {
    private final String table = "timers";

    private SQLiteDatabase DB;

    public TimersDAO(SQLiteDatabase DB) {
        this.DB = DB;
        DB.execSQL("CREATE TABLE IF NOT EXISTS " + table + " (id INTEGER PRIMARY KEY, name TEXT, state INTEGER, minute INTEGER);");
    }

    public List<Timer> getAllTimers() {
        List<Timer> timerList = new ArrayList<>();
        Cursor cursor = DB.rawQuery("SELECT * FROM " + table + " ORDER BY id ASC", null);
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
        SQLiteStatement sqLiteStatement = DB.compileStatement("UPDATE " + table + " SET name=?, state=?, minute=? WHERE id=?");
        sqLiteStatement.bindString(1, timer.getName());
        sqLiteStatement.bindLong(2, timer.getState());
        sqLiteStatement.bindLong(3, timer.getMinute());
        sqLiteStatement.bindLong(4, timer.getId());
        sqLiteStatement.executeUpdateDelete();
    }

    public void deleteTimer(Timer timer) {
        DB.execSQL("DELETE FROM timers WHERE id=" + timer.getId());
    }

    public void insertTimer(Timer timer) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("INSERT INTO " + table + " VALUES(?, ?, ?, ?)");
        sqLiteStatement.bindLong(1, timer.getId());
        sqLiteStatement.bindString(2, timer.getName());
        sqLiteStatement.bindLong(3, timer.getState());
        sqLiteStatement.bindLong(4, timer.getMinute());
        sqLiteStatement.executeInsert();
    }

    public Timer getTimersById(int id) {
        Cursor cursor = DB.rawQuery("SELECT * FROM " + table + " WHERE id = " + id + ";", null);
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

    public void setStateNullAll() {
        DB.execSQL("UPDATE " + table + " SET state=0 WHERE state=1;");
    }
}

package com.example.mynote.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.mynote.entity.Timers;

import java.util.ArrayList;
import java.util.List;

public class TimersDAO {
    private SQLiteDatabase DB;

    public TimersDAO(SQLiteDatabase DB) {
        this.DB = DB;
        DB.execSQL("CREATE TABLE IF NOT EXISTS timers (id INTEGER PRIMARY KEY, name TEXT, state INTEGER, minute INTEGER);");
    }

    public List<Timers> getAllTimers() {
        List<Timers> timersList = new ArrayList<>();
        Cursor cursor = DB.rawQuery("SELECT * FROM timers ORDER BY id ASC", null);
        while (cursor.moveToNext()) {
            timersList.add(new Timers(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3)
            ));
        }
        return timersList;
    }

    public void editTimers(Timers timers) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("UPDATE timers SET name=?, state=?, minute=? WHERE id=?");
        sqLiteStatement.bindString(1, timers.getName());
        sqLiteStatement.bindLong(2, timers.getState());
        sqLiteStatement.bindLong(3, timers.getMinute());
        sqLiteStatement.bindLong(4, timers.getId());
        sqLiteStatement.executeUpdateDelete();
    }

    public void deleteTimers(Timers timers) {
        DB.execSQL("DELETE FROM timers WHERE id=" + timers.getId());
    }

    public void insertTimers(Timers timers) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("INSERT INTO timers VALUES(?, ?, ?, ?)");
        sqLiteStatement.bindLong(1, timers.getId());
        sqLiteStatement.bindString(2, timers.getName());
        sqLiteStatement.bindLong(3, timers.getState());
        sqLiteStatement.bindLong(4, timers.getMinute());
        sqLiteStatement.executeInsert();
    }

    public Timers getTimersById(int id) {
        Cursor query = DB.rawQuery("SELECT * FROM timers WHERE id = " + id + ";", null);
        if (!(query == null)) {
            query.moveToFirst();
            return new Timers(
                    query.getInt(0),
                    query.getString(1),
                    query.getInt(2),
                    query.getInt(3)
            );
        }
        return null;
    }

    public void setStateNullAll() {
        DB.execSQL("UPDATE timers SET state=0 WHERE state=1;");
    }
}

package com.example.mynote.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.mynote.entity.TrashNote;

import java.util.ArrayList;
import java.util.List;

public class TrashDAO {
    private SQLiteDatabase DB;

    public TrashDAO(SQLiteDatabase DB) {
        this.DB = DB;
        DB.execSQL("CREATE TABLE IF NOT EXISTS trash (id INTEGER PRIMARY KEY, name TEXT, descr TEXT, delay TEXT, type INTEGER);");
    }

    public void insertTrash(TrashNote trashNote) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("INSERT INTO trash VALUES(?, ?, ?, ?, ?)");
        sqLiteStatement.bindLong(1, trashNote.getId());
        sqLiteStatement.bindString(2, trashNote.getName());
        sqLiteStatement.bindString(3, trashNote.getDescription());
        sqLiteStatement.bindLong(4, trashNote.getDelay());
        sqLiteStatement.bindLong(5, trashNote.getType());
        sqLiteStatement.executeInsert();
    }

    public void deleteTrash(TrashNote trashNote) {
        DB.execSQL("DELETE FROM trash WHERE id=" + trashNote.getId());
    }

    public void deleteAllTrash() {
        DB.execSQL("DELETE FROM trash");
    }

    public List<TrashNote> getAllTrash() {
        List<TrashNote> trashNoteList = new ArrayList<>();
        Cursor cursor = DB.rawQuery("SELECT * FROM trash ORDER BY id ASC;", null);
        while (cursor.moveToNext()) {
            trashNoteList.add(new TrashNote(
                    cursor.getInt(0), //id
                    cursor.getString(1), //name
                    cursor.getString(2), //descr
                    cursor.getLong(3), //delay
                    cursor.getInt(4) //type
            ));
        }
        return trashNoteList;
    }
}

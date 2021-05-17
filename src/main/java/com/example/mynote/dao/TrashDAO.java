package com.example.mynote.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.mynote.entity.TrashNote;
import com.example.mynote.entity.TypeRepeat;

import java.util.ArrayList;
import java.util.List;

public class TrashDAO {

    private final SQLiteDatabase db;

    public TrashDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public void insert(TrashNote trashNote) {
        SQLiteStatement sqLiteStatement = db.compileStatement(
                "INSERT INTO " + DatabaseHelper.TABLE_TRASH + " VALUES(?, ?, ?, ?, ?, ?)"
        );
        sqLiteStatement.bindLong(1, trashNote.getId());
        sqLiteStatement.bindString(2, trashNote.getName());
        sqLiteStatement.bindString(3, trashNote.getDescription());
        sqLiteStatement.bindLong(4, trashNote.getDelay());
        sqLiteStatement.bindString(5, trashNote.getRepeat().name());
        sqLiteStatement.bindLong(6, trashNote.getType());
        sqLiteStatement.executeInsert();
    }

    public void delete(TrashNote trashNote) {
        db.execSQL(
                "DELETE FROM " + DatabaseHelper.TABLE_TRASH + " WHERE " + DatabaseHelper.COLUMN_TRASH_ID + "=" + trashNote.getId()
        );
    }

    public void deleteAll() {
        db.execSQL("DELETE FROM " + DatabaseHelper.TABLE_TRASH);
    }

    public List<TrashNote> getAll() {
        List<TrashNote> trashNoteList = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_TRASH + " ORDER BY id ASC",
                null
        );
        while (cursor.moveToNext())
            trashNoteList.add(
                    new TrashNote(
                        cursor.getInt(0), //id
                        cursor.getString(1), //name
                        cursor.getString(2), //descr
                        cursor.getLong(3), //delay
                        TypeRepeat.valueOf(cursor.getString(4)), //repeat
                        cursor.getInt(5) //type
                    )
            );
        cursor.close();
        return trashNoteList;
    }
}

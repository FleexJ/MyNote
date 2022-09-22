package com.example.mynote.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.mynote.entity.Note;
import com.example.mynote.entity.TypeRepeat;

import java.util.ArrayList;
import java.util.List;

public class NotesDAO {

    private final Context context;

    public NotesDAO(Context context) {
        this.context = context;
    }

    public void edit(Note note) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        SQLiteStatement sqLiteStatement = db.compileStatement("UPDATE " + DatabaseHelper.TABLE_NOTES +
                " SET " +
                DatabaseHelper.COLUMN_NOTES_NAME +"=?, " +
                DatabaseHelper.COLUMN_NOTES_DESCRIPTION + "=?, " +
                DatabaseHelper.COLUMN_NOTES_STATE + "=?, " +
                DatabaseHelper.COLUMN_NOTES_DELAY + "=?, " +
                DatabaseHelper.COLUMN_NOTES_REPEAT + "=? " +
                "WHERE " + DatabaseHelper.COLUMN_NOTES_ID + "=?"
        );
        sqLiteStatement.bindString(1, note.getName());
        sqLiteStatement.bindString(2, note.getDescription());
        sqLiteStatement.bindLong(3, note.getState());
        sqLiteStatement.bindLong(4, note.getDelay());
        sqLiteStatement.bindString(5, note.getRepeat().name());
        sqLiteStatement.bindLong(6, note.getId());
        sqLiteStatement.executeUpdateDelete();
        db.close();
    }

    public void delete(Note note) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        SQLiteStatement sqLiteStatement = db.compileStatement(
                "DELETE FROM " + DatabaseHelper.TABLE_NOTES + " WHERE " + DatabaseHelper.COLUMN_NOTES_ID + "=?");
        sqLiteStatement.bindLong(1, note.getId());
        sqLiteStatement.executeUpdateDelete();
        db.close();
    }

    public void insert(Note note) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        SQLiteStatement sqLiteStatement = db.compileStatement(
                "INSERT INTO " + DatabaseHelper.TABLE_NOTES + " VALUES(?, ?, ?, " + Note.NOT_ACTIVE_STATE + ", ?, ?)"
        );
        sqLiteStatement.bindLong(1, note.getId());
        sqLiteStatement.bindString(2, note.getName());
        sqLiteStatement.bindString(3, note.getDescription());
        sqLiteStatement.bindLong(4, note.getDelay());
        sqLiteStatement.bindString(5, note.getRepeat().name());
        sqLiteStatement.executeInsert();
        db.close();
    }

    public Note getById(int id) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_NOTES + " WHERE " + DatabaseHelper.COLUMN_NOTES_ID + "=" + id,
                null);
        Note note = null;
        if (!(cursor == null)) {
            cursor.moveToFirst();
            note = new Note(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getLong(4),
                    TypeRepeat.valueOf(cursor.getString(5))
            );
            cursor.close();
        }
        db.close();
        return note;
    }

    public List<Note> getAll() {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_NOTES,
                null);
        while (cursor.moveToNext())
            noteList.add(
                    new Note(
                            cursor.getInt(0), //id
                            cursor.getString(1),//name
                            cursor.getString(2),//descr
                            cursor.getInt(3),//state
                            cursor.getLong(4),//delay
                            TypeRepeat.valueOf(cursor.getString(5))//repeat
                    )
            );
        cursor.close();
        db.close();
        return noteList;
    }

    public List<Note> getActiveAll() {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_NOTES + " WHERE " + DatabaseHelper.COLUMN_NOTES_STATE + "=" + Note.ACTIVE_STATE,
                null);
        while (cursor.moveToNext())
            noteList.add(
                    new Note(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getLong(4),
                        TypeRepeat.valueOf(cursor.getString(5))
                    )
            );
        cursor.close();
        db.close();
        return noteList;
    }
 }

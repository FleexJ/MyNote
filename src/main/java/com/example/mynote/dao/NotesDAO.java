package com.example.mynote.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.mynote.entity.Note;
import com.example.mynote.entity.TypeRepeat;

import java.util.ArrayList;
import java.util.List;

public class NotesDAO {

    private final SQLiteDatabase db;

    public NotesDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_NOTES,
                null);
        while (cursor.moveToNext())
            noteList.add(new Note(
                    cursor.getInt(0), //id
                    cursor.getString(1),//name
                    cursor.getString(2),//descr
                    cursor.getInt(3),//state
                    cursor.getLong(4),//delay
                    TypeRepeat.valueOf(cursor.getString(5))//repeat
            ));
        cursor.close();
        return noteList;
    }

    public Note[] getArrayNotes() {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_NOTES,
                null);
        Note[] notes = new Note[cursor.getCount()];

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            notes[i] = new Note(
                    cursor.getInt(0), //id
                    cursor.getString(1),//name
                    cursor.getString(2),//descr
                    cursor.getInt(3),//state
                    cursor.getLong(4),//delay
                    TypeRepeat.valueOf(cursor.getString(5))//repeat
            );
        }
        cursor.close();
        return notes;
    }

    public void editNote(Note note) {
        SQLiteStatement sqLiteStatement = db.compileStatement("UPDATE " + DatabaseHelper.TABLE_NOTES + " SET " +
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
    }

    public void deleteNote(Note note) {
        SQLiteStatement sqLiteStatement = db.compileStatement(
                "DELETE FROM " + DatabaseHelper.TABLE_NOTES +
                        " WHERE " + DatabaseHelper.COLUMN_NOTES_ID + "=?");
        sqLiteStatement.bindLong(1, note.getId());
        sqLiteStatement.executeUpdateDelete();
    }

    public List<Note> getActiveNotes() {
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_NOTES +
                        " WHERE " + DatabaseHelper.COLUMN_NOTES_STATE + "=" + Note.ACTIVE_STATE,
                null);
        while (cursor.moveToNext())
            noteList.add(new Note(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getLong(4),
                    TypeRepeat.valueOf(cursor.getString(5))
            ));
        cursor.close();
        return noteList;
    }

    public void insertNote(Note note) {
        SQLiteStatement sqLiteStatement = db.compileStatement(
                "INSERT INTO " + DatabaseHelper.TABLE_NOTES + " VALUES(?, ?, ?, " + Note.NOT_ACTIVE_STATE + ", ?, ?)"
        );
        sqLiteStatement.bindLong(1, note.getId());
        sqLiteStatement.bindString(2, note.getName());
        sqLiteStatement.bindString(3, note.getDescription());
        sqLiteStatement.bindLong(4, note.getDelay());
        sqLiteStatement.bindString(5, note.getRepeat().name());
        sqLiteStatement.executeInsert();
    }

    public Note getNoteById(int id) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_NOTES + " WHERE " + DatabaseHelper.COLUMN_NOTES_ID + "=" + id,
                null);
        if (!(cursor == null)) {
            cursor.moveToFirst();
            Note note = new Note(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getLong(4),
                    TypeRepeat.valueOf(cursor.getString(5))
            );
            cursor.close();
            return note;
        }
        return null;
    }
 }

package com.example.mynote.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.mynote.entity.Note;
import com.example.mynote.entity.TypeRepeat;

import java.util.ArrayList;
import java.util.List;

public class NotesDAO {
    private final String table = "notes";

    private SQLiteDatabase DB;

    public NotesDAO(SQLiteDatabase DB) {
        this.DB = DB;
        DB.execSQL("CREATE TABLE IF NOT EXISTS " + table + " (id INTEGER PRIMARY KEY, name TEXT, descr TEXT, state INTEGER, delay TEXT, repeat TEXT);");
    }

    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = DB.rawQuery("SELECT * FROM " + table + ";", null);
        while (cursor.moveToNext()) {
            noteList.add(new Note(
                    cursor.getInt(0), //id
                    cursor.getString(1),//name
                    cursor.getString(2),//descr
                    cursor.getInt(3),//state
                    cursor.getLong(4),//delay
                    TypeRepeat.valueOf(cursor.getString(5))//repeat
            ));
        }
        cursor.close();
        return noteList;
    }

    public void editNote(Note note) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("UPDATE " + table + " SET name=?, descr=?, state=?, delay=?, repeat=? WHERE id=?");
        sqLiteStatement.bindString(1, note.getName());
        sqLiteStatement.bindString(2, note.getDescription());
        sqLiteStatement.bindLong(3, note.getState());
        sqLiteStatement.bindLong(4, note.getDelay());
        sqLiteStatement.bindString(5, note.getRepeat().name());
        sqLiteStatement.bindLong(6, note.getId());
        sqLiteStatement.executeUpdateDelete();
    }

    public void deleteNote(Note note) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("DELETE FROM " + table + " WHERE id=?;");
        sqLiteStatement.bindLong(1, note.getId());
        sqLiteStatement.executeUpdateDelete();
    }

    public List<Note> getActiveNotes() {
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = DB.rawQuery("SELECT * FROM " + table + " WHERE state=1;", null);
        while (cursor.moveToNext()) {
            noteList.add(new Note(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getLong(4),
                    TypeRepeat.valueOf(cursor.getString(5))
            ));
        }
        cursor.close();
        return noteList;
    }

    public void insertNote(Note note) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("INSERT INTO " + table + " VALUES(?, ?, ?, 0, ?, ?);");
        sqLiteStatement.bindLong(1, note.getId());
        sqLiteStatement.bindString(2, note.getName());
        sqLiteStatement.bindString(3, note.getDescription());
        sqLiteStatement.bindLong(4, note.getDelay());
        sqLiteStatement.bindString(5, note.getRepeat().name());
        sqLiteStatement.executeInsert();
    }

    public Note getNoteById(int id) {
        Cursor cursor = DB.rawQuery("SELECT * FROM " + table + " WHERE id = " + id + ";", null);
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

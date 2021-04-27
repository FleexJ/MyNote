package com.example.mynote.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.TypeRepeat;

import java.util.ArrayList;
import java.util.List;

public class NotesDAO {
    private SQLiteDatabase DB;

    public NotesDAO(SQLiteDatabase DB) {
        this.DB = DB;
        DB.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, name TEXT, descr TEXT, state INTEGER, delay TEXT, repeat TEXT);");
    }

    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = DB.rawQuery("SELECT * FROM notes;", null);
        while (cursor.moveToNext()) {
            noteList.add(new Note(
                    cursor.getInt(0), //id
                    cursor.getString(1),//name
                    cursor.getString(2),//descr
                    cursor.getInt(3),//state
                    cursor.getString(4),//delay
                    TypeRepeat.valueOf(cursor.getString(5))//repeat
            ));
        }
        return noteList;
    }

    public void editNote(Note note) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("UPDATE notes SET name=?, descr=?, state=?, delay=?, repeat=? WHERE id=?");
        sqLiteStatement.bindString(1, note.getName());
        sqLiteStatement.bindString(2, note.getDescription());
        sqLiteStatement.bindLong(3, note.getState());
        sqLiteStatement.bindString(4, note.getDelay());
        sqLiteStatement.bindString(5, note.getRepeat().name());
        sqLiteStatement.bindLong(6, note.getId());
        sqLiteStatement.executeUpdateDelete();
    }

    public void deleteNote(Note note) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("DELETE FROM notes WHERE id=?");
        sqLiteStatement.bindLong(1, note.getId());
        sqLiteStatement.executeUpdateDelete();
    }

    public List<Note> getActiveNotes() {
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = DB.rawQuery("SELECT * FROM notes WHERE state=1;", null);
        while (cursor.moveToNext()) {
            noteList.add(new Note(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    TypeRepeat.valueOf(cursor.getString(5))
            ));
        }
        return noteList;
    }

    public void insertNote(Note note) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("INSERT INTO notes VALUES(?, ?, ?, 0, ?, ?)");
        sqLiteStatement.bindLong(1, note.getId());
        sqLiteStatement.bindString(2, note.getName());
        sqLiteStatement.bindString(3, note.getDescription());
        sqLiteStatement.bindString(4, note.getDelay());
        sqLiteStatement.bindString(5, note.getRepeat().name());
        sqLiteStatement.executeInsert();
    }

    public Note getNoteById(int id) {
        Cursor query = DB.rawQuery("SELECT * FROM notes WHERE id = " + id + ";", null);
        if (!(query == null)) {
            query.moveToFirst();
            return new Note(
                    query.getInt(0),
                    query.getString(1),
                    query.getString(2),
                    query.getInt(3),
                    query.getString(4),
                    TypeRepeat.valueOf(query.getString(5))
            );
        }
        return null;
    }
 }

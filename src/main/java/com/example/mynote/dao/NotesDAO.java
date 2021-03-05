package com.example.mynote.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.example.mynote.entity.Notes;

import java.util.ArrayList;
import java.util.List;

public class NotesDAO {
    private SQLiteDatabase DB;

    public NotesDAO(SQLiteDatabase DB) {
        this.DB = DB;
        DB.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, name TEXT, descr TEXT, state INTEGER, delay TEXT, repeat TEXT);");
    }

    public List<Notes> getAllNotes() {
        List<Notes> notesList = new ArrayList<>();
        Cursor cursor = DB.rawQuery("SELECT * FROM notes;", null);
        while (cursor.moveToNext()) {
            notesList.add(new Notes(
                    cursor.getInt(0), //id
                    cursor.getString(1),//name
                    cursor.getString(2),//descr
                    cursor.getInt(3),//state
                    cursor.getString(4),//delay
                    cursor.getString(5)//repeat
            ));
        }
        return notesList;
    }

    public void editNote(Notes notes) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("UPDATE notes SET name=?, descr=?, state=?, delay=?, repeat=? WHERE id=?");
        sqLiteStatement.bindString(1, notes.getName());
        sqLiteStatement.bindString(2, notes.getDescription());
        sqLiteStatement.bindLong(3, notes.getState());
        sqLiteStatement.bindString(4, notes.getDelay());
        sqLiteStatement.bindString(5, notes.getRepeat());
        sqLiteStatement.bindLong(6, notes.getId());
        sqLiteStatement.executeUpdateDelete();
    }

    public void deleteNote(Notes notes) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("DELETE FROM notes WHERE id=?");
        sqLiteStatement.bindLong(1, notes.getId());
        sqLiteStatement.executeUpdateDelete();
    }

    public List<Notes> getActiveNotes() {
        List<Notes> notesList = new ArrayList<>();
        Cursor cursor = DB.rawQuery("SELECT * FROM notes WHERE state=1;", null);
        while (cursor.moveToNext()) {
            notesList.add(new Notes(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5)
            ));
        }
        return notesList;
    }

    public void insertNote(Notes notes) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("INSERT INTO notes VALUES(?, ?, ?, 0, ?, ?)");
        sqLiteStatement.bindLong(1, notes.getId());
        sqLiteStatement.bindString(2, notes.getName());
        sqLiteStatement.bindString(3, notes.getDescription());
        sqLiteStatement.bindString(4, notes.getDelay());
        sqLiteStatement.bindString(5, notes.getRepeat());
        sqLiteStatement.executeInsert();
    }

    public Notes getNoteById(int id) {
        Cursor query = DB.rawQuery("SELECT * FROM notes WHERE id = " + id + ";", null);
        if (!(query == null)) {
            query.moveToFirst();
            return new Notes(
                    query.getInt(0),
                    query.getString(1),
                    query.getString(2),
                    query.getInt(3),
                    query.getString(4),
                    query.getString(5)
            );
        }
        return null;
    }
 }

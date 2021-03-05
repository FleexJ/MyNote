package com.example.mynote.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class IdCountDAO {
    private SQLiteDatabase DB;

    public IdCountDAO(SQLiteDatabase DB) {
        this.DB = DB;
        DB.execSQL("CREATE TABLE IF NOT EXISTS id_count (id INTEGER PRIMARY KEY);");
    }

    public void insertIdCount(int id) {
        DB.execSQL("INSERT INTO id_count VALUES(" + id + ");");
    }

    public int getNewId() {
        Cursor cursor = DB.rawQuery("SELECT * FROM id_count ORDER BY id ASC;", null);
        //Если это первая запись, то возвращаем нулевой идентификатор
        if (cursor.getCount() == 0) {
            cursor.close();
            return 0;
        } else {
            cursor.moveToLast();
            int newId = cursor.getInt(0) + 1;
            cursor.close();
            return newId;
        }
    }

    public void deleteId(int id) {
        SQLiteStatement sqLiteStatement = DB.compileStatement("DELETE FROM id_count WHERE id=?");
        sqLiteStatement.bindLong(1, id);
        sqLiteStatement.executeUpdateDelete();
    }
}

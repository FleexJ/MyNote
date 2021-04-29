package com.example.mynote.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class IdCountDAO {

    private final SQLiteDatabase db;

    public IdCountDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public void insertIdCount(int id) {
        db.execSQL(
                "INSERT INTO " + DatabaseHelper.TABLE_ID_COUNT +
                        " VALUES(" + id + ")"
        );
    }

    public int getNewId() {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_ID_COUNT + " ORDER BY " + DatabaseHelper.COLUMN_ID_COUNT_ID + " DESC",
                null
        );
        //Если это первая запись, то возвращаем нулевой идентификатор
        if (cursor.getCount() == 0) {
            cursor.close();
            return 0;
        } else {
            cursor.moveToFirst();
            int newId = cursor.getInt(0) + 1;
            cursor.close();
            return newId;
        }
    }

    public void deleteId(int id) {
        SQLiteStatement sqLiteStatement = db.compileStatement(
                "DELETE FROM " + DatabaseHelper.TABLE_ID_COUNT +
                        " WHERE " + DatabaseHelper.COLUMN_ID_COUNT_ID + "=?"
        );
        sqLiteStatement.bindLong(1, id);
        sqLiteStatement.executeUpdateDelete();
    }
}

package com.example.mynote.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class IdCountDAO {

    private final Context context;

    public IdCountDAO(Context context) {
        this.context = context;
    }

    public void insert(int id) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        db.execSQL(
                "INSERT INTO " + DatabaseHelper.TABLE_ID_COUNT + " VALUES(" +
                        id +
                        ")"
        );
        db.close();
    }

    public void delete(int id) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        SQLiteStatement sqLiteStatement = db.compileStatement(
                "DELETE FROM " + DatabaseHelper.TABLE_ID_COUNT + " WHERE " + DatabaseHelper.COLUMN_ID_COUNT_ID + "=?"
        );
        sqLiteStatement.bindLong(1, id);
        sqLiteStatement.executeUpdateDelete();
        db.close();
    }

    public int getNewId() {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_ID_COUNT + " ORDER BY " + DatabaseHelper.COLUMN_ID_COUNT_ID + " DESC",
                null
        );
        int id;
        //Если это первая запись, то возвращаем нулевой идентификатор
        if (cursor.getCount() == 0) {
            cursor.close();
            id = 0;
        } else {
            cursor.moveToFirst();
            int newId = cursor.getInt(0) + 1;
            cursor.close();
            id = newId;
        }
        db.close();
        return id;
    }
}

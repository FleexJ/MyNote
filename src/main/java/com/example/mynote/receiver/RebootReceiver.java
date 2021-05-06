package com.example.mynote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.example.mynote.dao.DatabaseHelper;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.Timer;
import com.example.mynote.globalVar.MyGlobal;

//Класс для активации напоминаний при перезагрузке устройства
public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent_rec) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent_rec.getAction())) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context.getApplicationContext());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            NotesDAO notesDAO = new NotesDAO(db);
            TimersDAO timersDAO = new TimersDAO(db);

            //Переактивация всех активных записей
            for (Note note : notesDAO.getActiveAll())
                NoteReceiver.startAlarmNote(context, note);
            for (Timer timer : timersDAO.getActiveAll())
                TimerReceiver.startAlarmTimer(context, timer);

            db.close();
        }
    }
}
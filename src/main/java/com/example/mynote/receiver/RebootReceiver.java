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

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private TimersDAO timersDAO;
    private NotesDAO notesDAO;

    @Override
    public void onReceive(Context context, Intent intent_rec) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent_rec.getAction())) {
            databaseHelper = new DatabaseHelper(context.getApplicationContext());
            db = databaseHelper.getWritableDatabase();
            notesDAO = new NotesDAO(db);
            timersDAO = new TimersDAO(db);

            //Переактивация всех активных записей
            for (Note note : notesDAO.getActiveNotes())
                NoteReceiver.startAlarmNote(context, note);
            for (Timer timer : timersDAO.getActiveTimers())
                TimerReceiver.startAlarmTimer(context, timer);

            //Сброс состояний таймера до неактивного
            timersDAO.setStateNotActiveAll();
            db.close();
        }
    }
}
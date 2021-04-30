package com.example.mynote.receiver;

import android.app.AlarmManager;
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

import static android.content.Context.MODE_PRIVATE;

//Класс для активации напоминаний при перезагрузке устройства
public class RebootReceiver extends BroadcastReceiver {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private TimersDAO timersDAO;
    private NotesDAO notesDAO;

    private final MyGlobal myGlobal = new MyGlobal();

    @Override
    public void onReceive(Context context, Intent intent_rec) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent_rec.getAction())) {
            databaseHelper = new DatabaseHelper(context.getApplicationContext());
            db = databaseHelper.getWritableDatabase();
            notesDAO = new NotesDAO(db);
            timersDAO = new TimersDAO(db);

            //Переактивация всех активных записей
            for (Note note : notesDAO.getActiveNotes())
                myGlobal.startAlarmNote(context, note);
            for (Timer timer : timersDAO.getActiveTimers())
                myGlobal.startAlarmTimers(context, timer);

            //Сброс состояний таймера до неактивного
            timersDAO.setStateNotActiveAll();
        }
    }
}
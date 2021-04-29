package com.example.mynote.receiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.globalVar.MyGlobal;

import static android.content.Context.MODE_PRIVATE;

//Класс для активации напоминаний при перезагрузке устройства
public class RebootReceiver extends BroadcastReceiver {
    private AlarmManager am;
    private TimersDAO timersDAO;
    private NotesDAO notesDAO;
    private SQLiteDatabase DB;
    private final MyGlobal myGlobal = new MyGlobal();

    @Override
    public void onReceive(Context context, Intent intent_rec) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent_rec.getAction())) {
            DB = context.getApplicationContext().openOrCreateDatabase("vidgets.db", MODE_PRIVATE, null);
            notesDAO = new NotesDAO(DB);
            timersDAO = new TimersDAO(DB);

            //Активация всех записей
            for (Note note : notesDAO.getActiveNotes())
                myGlobal.startAlarmNote(
                        context,
                        note);

            //Сброс состояний таймера до неактивного
            timersDAO.setStateNullAll();
        }
    }
}
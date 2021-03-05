package com.example.mynote.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.entity.Notes;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

//Класс для активации напоминаний при перезагрузке устройства
public class RebootReceiver extends BroadcastReceiver {

    AlarmManager am;
    TimersDAO timersDAO;
    NotesDAO notesDAO;
    SQLiteDatabase DB;

    @Override
    public void onReceive(Context context, Intent intent_rec) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent_rec.getAction())) {
            DB = context.getApplicationContext().openOrCreateDatabase("vidgets.db", MODE_PRIVATE, null);
            notesDAO = new NotesDAO(DB);
            timersDAO = new TimersDAO(DB);
            List<Notes> notesList = notesDAO.getActiveNotes();
            for(Notes notes : notesList) {
                Intent intent = new Intent(context, MyReceiver.class);
                intent.putExtra("id", notes.getId());
                //Добавление флага для точного срабатывания
                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notes.getId() , intent, PendingIntent.FLAG_CANCEL_CURRENT);
                am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                //Обнуляем секунды и милисекунды, чтобы аларм сработал с точностью до минуты
                am.setExact(AlarmManager.RTC_WAKEUP, notes.getDelayCalendar().getTimeInMillis(), pendingIntent);
            }
            //Сброс состояний таймера до неактивного
            timersDAO.setStateNullAll();
        }
    }
}
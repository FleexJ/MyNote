package com.example.mynote.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mynote.dao.NotesDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.globalVar.MyGlobal;

import java.util.Calendar;


public class NoteReceiver extends BroadcastReceiver {

    private NotesDAO notesDAO;

    @Override
    public void onReceive(Context context, Intent intent) {
        notesDAO = new NotesDAO(context);

        int id = intent.getIntExtra("id",0);
        Note note = notesDAO.getById(id);

        MyGlobal.showNotification(context, note);

        Calendar calendar = note.getDelayCalendar();
        switch (note.getRepeat()) {
            case HOUR :
                calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
                note.setDelay(calendar.getTimeInMillis());
                startAlarmNote(context, note);
                break;

            case DAY :
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
                note.setDelay(calendar.getTimeInMillis());
                startAlarmNote(context, note);
                break;

            case WEEK :
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 7);
                note.setDelay(calendar.getTimeInMillis());
                startAlarmNote(context, note);
                break;

            case MONTH:
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                note.setDelay(calendar.getTimeInMillis());
                startAlarmNote(context, note);
                break;

            case YEAR:
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                note.setDelay(calendar.getTimeInMillis());
                startAlarmNote(context, note);
                break;

            default:
                note.setState(Note.NOT_ACTIVE_STATE);
                break;
        }
        notesDAO.edit(note);
    }

    //функция старта аларма для записей
    public static void startAlarmNote(Context context, Note note) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Заготовки для уведомлений
        Intent intent = new Intent(context, NoteReceiver.class);
        intent.putExtra("id", note.getId());
        //Добавление флага для точного срабатывания
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                note.getId(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pendingIntent);
        //Активация аларма
        am.setExact(
                AlarmManager.RTC_WAKEUP,
                note.getDelayCalendar().getTimeInMillis(),
                pendingIntent);
    }

    //функция остановки аларма по id
    public static void cancelAlarmNote(Context context, int id) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NoteReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pendingIntent);
    }
}
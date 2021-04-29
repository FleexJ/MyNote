package com.example.mynote.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mynote.R;
import com.example.mynote.activity.NotifActivity;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.globalVar.MyGlobal;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;


public class MyReceiver extends BroadcastReceiver {

    private NotesDAO notesDAO;
    private SQLiteDatabase DB;
    //Объект общих функций
    private final MyGlobal myGlobal = new MyGlobal();

    @Override
    public void onReceive(Context context, Intent intent) {
        DB = context.getApplicationContext().openOrCreateDatabase("vidgets.db", MODE_PRIVATE, null);
        notesDAO = new NotesDAO(DB);

        int id = intent.getIntExtra("id",0);
        //Данные для уведомления берутся из бд, в случае если пользователь их отредактирует
        Note note = notesDAO.getNoteById(id);

        myGlobal.showNotification(context, note);

        Calendar calendar = note.getDelayCalendar();
        switch (note.getRepeat()) {
            case HOUR :
                calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
                note.setDelay(calendar.getTimeInMillis());
                myGlobal.startAlarmNote(context, note);
                break;
            case DAY :
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
                note.setDelay(calendar.getTimeInMillis());
                myGlobal.startAlarmNote(context, note);
                break;
            case WEEK :
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 7);
                note.setDelay(calendar.getTimeInMillis());
                myGlobal.startAlarmNote(context, note);
                break;
            case MONTH:
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                note.setDelay(calendar.getTimeInMillis());
                myGlobal.startAlarmNote(context, note);
                break;
            case YEAR:
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                note.setDelay(calendar.getTimeInMillis());
                myGlobal.startAlarmNote(context, note);
                break;
            default:
                note.setState(0);
                break;
        }
        notesDAO.editNote(note);
        DB.close();

//        if(Build.VERSION.SDK_INT >= 24 && isForeground(context.getApplicationContext()))
//            context.getApplicationContext().startActivity(intent_new);
    }
}
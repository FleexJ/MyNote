package com.example.mynote.receiver;

import android.app.ActivityManager;
import android.app.AlarmManager;
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

import com.example.mynote.activity.NotifActivity;
import com.example.mynote.R;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.entity.Note;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MyReceiver extends BroadcastReceiver {

    private NotesDAO notesDAO;
    private SQLiteDatabase DB;

    @Override
    public void onReceive(Context context, Intent intent) {
        DB = context.getApplicationContext().openOrCreateDatabase("vidgets.db", MODE_PRIVATE, null);
        notesDAO = new NotesDAO(DB);

        int id = intent.getIntExtra("id",0);
        //Данные для уведомления берутся из бд, в случае если пользователь их отредактирует
        Note note = notesDAO.getNoteById(id);

        startNotification(context, note);

        Calendar calendar = note.getDelayCalendar();
        switch (note.getRepeat()) {
            case HOUR :
                calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
                note.setDelay(calendar.getTimeInMillis());
                startAlarmNote(note, context);
                break;
            case DAY :
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
                note.setDelay(calendar.getTimeInMillis());
                startAlarmNote(note, context);
                break;
            case WEEK :
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 7);
                note.setDelay(calendar.getTimeInMillis());
                startAlarmNote(note, context);
                break;
            case MONTH:
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                note.setDelay(calendar.getTimeInMillis());
                startAlarmNote(note, context);
                break;
            case YEAR:
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                note.setDelay(calendar.getTimeInMillis());
                startAlarmNote(note, context);
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

    //функция старта аларма для записей
    private void startAlarmNote(Note note, Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Заготовки для уведомлений
        Intent intent = new Intent(context, MyReceiver.class);
        intent.putExtra("id", note.getId());
        //Добавление флага для точного срабатывания
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, note.getId() , intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pendingIntent);
        //Активация аларма
        am.setExact(AlarmManager.RTC_WAKEUP, note.getDelayCalendar().getTimeInMillis(), pendingIntent);
    }

    //Показ уведомления
    private void startNotification(Context context, Note note) {
        Intent intent_new = new Intent(context, NotifActivity.class);
        intent_new.putExtra("id", note.getId());
        intent_new.putExtra("type", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), note.getId() , intent_new, PendingIntent.FLAG_CANCEL_CURRENT);

        if(Build.VERSION.SDK_INT >= 26 ){
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context.getApplicationContext(), "channel_id_mynote");
            NotificationChannel notificationChannel = new NotificationChannel("channel_id_mynote", "channel_name_mynote", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(1);
            notificationChannel.enableLights(true);

            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.icon_notif)
                    .setContentTitle(
                            context.getString(R.string.notifNoteTitle)
                    )
                    .setContentText(
                            note.getName() + ": " + note.getDescription()
                    )
                    .setPriority(Notification.PRIORITY_MAX)
                    .setShowWhen(true)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            notification.defaults = NotificationCompat.DEFAULT_ALL;
            nm.createNotificationChannel(notificationChannel);
            nm.cancel(note.getId());
            nm.notify(note.getId(), notification);
        }
        else
        if(Build.VERSION.SDK_INT >=21 ){
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context.getApplicationContext(), "channel_id_mynote")
                            .setContentTitle(
                                    context.getString(R.string.notifNoteTitle)
                            )
                            .setContentText(
                                    note.getName() + ": " + note.getDescription()
                            )
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.icon_notif);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context.getApplicationContext());
            notificationManager.cancel(note.getId());
            notificationManager.notify(note.getId(), builder.build());
        }
    }

    //Проверяет, запущено ли окно приложения
    private static boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
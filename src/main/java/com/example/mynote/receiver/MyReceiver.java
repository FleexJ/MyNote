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
import com.example.mynote.entity.Notes;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MyReceiver extends BroadcastReceiver
{
    NotesDAO notesDAO;
    SQLiteDatabase DB;

    @Override
    public void onReceive(Context context, Intent intent) {
        DB = context.getApplicationContext().openOrCreateDatabase("vidgets.db", MODE_PRIVATE, null);
        notesDAO = new NotesDAO(DB);

        int id = intent.getIntExtra("id",0);
        //Данные для уведомления берутся из бд, в случае если пользователь их отредактирует
        Notes notes = notesDAO.getNoteById(id);

        startNotification(context, notes);

        Calendar calendar = notes.getDelayCalendar();
        switch (notes.getRepeat()) {
            case "Час" :
                calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
                notes.setDelay(calendar.getTimeInMillis() + "");
                startAlarmNote(notes, context);
                break;
            case "День" :
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
                notes.setDelay(calendar.getTimeInMillis() + "");
                startAlarmNote(notes, context);
                break;
            case "Неделя" :
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 7);
                notes.setDelay(calendar.getTimeInMillis() + "");
                startAlarmNote(notes, context);
                break;
            case "Месяц" :
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                notes.setDelay(calendar.getTimeInMillis() + "");
                startAlarmNote(notes, context);
                break;
            case "Год" :
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                notes.setDelay(calendar.getTimeInMillis() + "");
                startAlarmNote(notes, context);
                break;
            default:
                notes.setState(0);
                break;
        }
        notesDAO.editNote(notes);
        DB.close();

//        if(Build.VERSION.SDK_INT >= 24 && isForeground(context.getApplicationContext()))
//            context.getApplicationContext().startActivity(intent_new);
    }

    //функция старта аларма для записей
    private void startAlarmNote(Notes notes, Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Заготовки для уведомлений
        Intent intent = new Intent(context, MyReceiver.class);
        intent.putExtra("id", notes.getId());
        //Добавление флага для точного срабатывания
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notes.getId() , intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pendingIntent);
        //Активация аларма
        am.setExact(AlarmManager.RTC_WAKEUP, notes.getDelayCalendar().getTimeInMillis(), pendingIntent);
    }

    //Показ уведомления
    private void startNotification(Context context, Notes notes) {
        Intent intent_new = new Intent(context, NotifActivity.class);
        intent_new.putExtra("id", notes.getId());
        intent_new.putExtra("type", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), notes.getId() , intent_new, PendingIntent.FLAG_CANCEL_CURRENT);

        if(Build.VERSION.SDK_INT >= 26 ){
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context.getApplicationContext(), "channel_id_mynote");
            NotificationChannel notificationChannel = new NotificationChannel("channel_id_mynote", "channel_name_mynote", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(1);
            notificationChannel.enableLights(true);

            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.icon_notif)
                    //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notepad_icon))
                    .setContentTitle("Напоминание о задаче!")
                    .setContentText(notes.getName() + ": " + notes.getDescription())
                    .setPriority(Notification.PRIORITY_MAX)
                    .setShowWhen(true)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            notification.defaults = NotificationCompat.DEFAULT_ALL;
            nm.createNotificationChannel(notificationChannel);
            nm.cancel(notes.getId());
            nm.notify(notes.getId(), notification);
        }
        else
        if(Build.VERSION.SDK_INT >=21 ){
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context.getApplicationContext(), "channel_id_mynote")
                            .setContentTitle("Напоминание о задаче!")
                            .setContentText(notes.getName() + ": " + notes.getDescription())
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.icon_notif);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context.getApplicationContext());
            notificationManager.cancel(notes.getId());
            notificationManager.notify(notes.getId(), builder.build());
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
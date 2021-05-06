package com.example.mynote.receiver;

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

import com.example.mynote.R;
import com.example.mynote.activity.MainActivity;
import com.example.mynote.dao.DatabaseHelper;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.entity.Timer;
import com.example.mynote.globalVar.MyGlobal;

import java.util.Calendar;


public class TimerReceiver extends BroadcastReceiver {

    public static String CHANNEL_ID_PROGRESS_TIMER = "channel_id_mynote_progress";
    public static String CHANNEL_NAME_PROGRESS_TIMER = "channel_name_mynote_progress";

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private TimersDAO timersDAO;


    @Override
    public void onReceive(Context context, Intent intent) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
        db = databaseHelper.getWritableDatabase();
        timersDAO = new TimersDAO(db);

        int id = intent.getIntExtra("id",0);
        Timer timer = timersDAO.getById(id);
        //Если время не вышло, то убавляем его и создаем новый аларм
        if((timer.getMinute() - 1) >= 1) {
            timer.setMinute(timer.getMinute() - 1);
            timersDAO.edit(timer);

            startAlarmTimer(context, timer);
            showNotifProgressTimer(context, timer);
        } else {//Если время вышло, то уведомление
            timer.setState(Timer.NOT_ACTIVE_STATE);
            timer.setMinute(1);
            timersDAO.edit(timer);

            MyGlobal.showNotification(context, timer);
        }

        db.close();
    }

    //функция старта аларма для таймеров
    public static void startAlarmTimer(Context context, Timer timer) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Заготовки для уведомлений
        Intent intent = new Intent(context, TimerReceiver.class);
        intent.putExtra("id", timer.getId());
        Calendar delay_minute = Calendar.getInstance();
        delay_minute.add(Calendar.MINUTE, 1);
        //Добавление флага для точного срабатывания
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                timer.getId(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pendingIntent);
        am.setExact(AlarmManager.RTC_WAKEUP, delay_minute.getTimeInMillis(), pendingIntent);
    }

    //функция остановки аларма по id
    public static void cancelAlarmTimer(Context context, int id) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pendingIntent);
    }


    //Показ уведомления
    public static void showNotifProgressTimer(Context context, Timer timer) {
        Intent intent_new = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, timer.getId() , intent_new, PendingIntent.FLAG_CANCEL_CURRENT);

        if(Build.VERSION.SDK_INT >= 26 ){
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID_PROGRESS_TIMER);
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID_PROGRESS_TIMER,
                    CHANNEL_NAME_PROGRESS_TIMER,
                    NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setLockscreenVisibility(1);

            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.icon_notif)
                    .setContentTitle(
                            context.getString(R.string.notifTimerProgress, timer.getName()))
                    .setContentText(
                            context.getString(R.string.timerProgress, timer.getMinute()))
                    .setShowWhen(true)
                    .setOngoing(true)
                    .setAutoCancel(false);
            Notification notification = builder.build();
            nm.createNotificationChannel(notificationChannel);
            nm.cancel(timer.getId());
            nm.notify(timer.getId(), notification);
        }
        else if(Build.VERSION.SDK_INT >= 21 ){
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, CHANNEL_ID_PROGRESS_TIMER)
                            .setContentTitle(
                                    context.getString(R.string.notifTimerProgress, timer.getName()))
                            .setContentText(
                                    context.getString(R.string.timerProgress, timer.getMinute()))
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_MIN)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.icon_notif);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(timer.getId());
            notificationManager.notify(timer.getId(), builder.build());
        }
    }
    public static void cancelNotifProgressTimer(Context context, Timer timer) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(timer.getId());
    }
}
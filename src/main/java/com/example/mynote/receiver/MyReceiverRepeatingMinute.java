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
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.entity.Timer;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class MyReceiverRepeatingMinute extends BroadcastReceiver {

    private TimersDAO timersDAO;
    private SQLiteDatabase DB;

    @Override
    public void onReceive(Context context, Intent intent) {
        DB = context.getApplicationContext().openOrCreateDatabase("vidgets.db", MODE_PRIVATE, null);
        timersDAO = new TimersDAO(DB);

        int id = intent.getIntExtra("id",0);
        Timer timer = timersDAO.getTimersById(id);
        //Если время не вышло, то убавляем его и создаем новый аларм
        if((timer.getMinute() - 1) >= 1) {
            timer.setMinute(timer.getMinute() - 1);
            timersDAO.editTimers(timer);

            Intent intent_alarm = new Intent(context.getApplicationContext(), MyReceiverRepeatingMinute.class);
            intent_alarm.putExtra("id", id);
            //Добавление флага для точного срабатывания
            intent_alarm.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id , intent_alarm, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            //am.cancel(pendingIntent);
            Calendar delay_minute = Calendar.getInstance();
            delay_minute.add(Calendar.MINUTE, 1);
            am.setExact(AlarmManager.RTC_WAKEUP, delay_minute.getTimeInMillis(), pendingIntent);
            startNotifProgress(context, timer);
        }
        //Если время вышло, то создаем аларм на уведомление
        else {
            timer.setMinute(1);
            timersDAO.editTimers(timer);
            //Отправляем аларм на текущий момент, чтобы вызвать уведомление
            Intent intent_alarm = new Intent(context.getApplicationContext(), MyReceiverMinute.class);
            intent_alarm.putExtra("id", id);
            //Добавление флага для точного срабатывания
            intent_alarm.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id , intent_alarm, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            //am.cancel(pendingIntent);
            am.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pendingIntent);
        }
        DB.close();
    }

    private void startNotifProgress(Context context, Timer timer) {
        Intent intent_new = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), timer.getId() , intent_new, PendingIntent.FLAG_CANCEL_CURRENT);

        if(Build.VERSION.SDK_INT >= 26 ){
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context.getApplicationContext(), "channel_id_mynote_progress");
            NotificationChannel notificationChannel = new NotificationChannel("channel_id_mynote_progress", "channel_name_mynote_progress", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(false);
            notificationChannel.setLockscreenVisibility(1);
            notificationChannel.enableLights(false);

            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.icon_notif)
                    .setContentTitle(
                            context.getString(R.string.notifTimerWorkTitle, timer.getName())
                    )
                    .setContentText(
                            context.getString(R.string.timerProgress, timer.getMinute())
                    )
                    .setShowWhen(true)
                    .setOngoing(true)
                    .setAutoCancel(false);
            Notification notification = builder.build();
            nm.createNotificationChannel(notificationChannel);
            nm.cancel(timer.getId());
            nm.notify(timer.getId(), notification);
        }
        else
        if(Build.VERSION.SDK_INT >= 21 ){
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context.getApplicationContext(), "channel_id_mynote_progress")
                            .setContentTitle(
                                    context.getString(R.string.notifTimerWorkTitle, timer.getName())
                            )
                            .setContentText(
                                    context.getString(R.string.timerProgress, timer.getMinute())
                            )
                            .setContentIntent(pendingIntent)
//                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(false)
                            .setOngoing(true)
                            .setSmallIcon(R.drawable.icon_notif);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context.getApplicationContext());
            notificationManager.cancel(timer.getId());
            notificationManager.notify(timer.getId(), builder.build());
        }
    }
}
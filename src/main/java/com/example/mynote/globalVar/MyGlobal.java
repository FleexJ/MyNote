package com.example.mynote.globalVar;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mynote.R;
import com.example.mynote.activity.MainActivity;
import com.example.mynote.activity.NotifActivity;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.Timer;
import com.example.mynote.receiver.TimerReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MyGlobal {

    //Типа записей
    public static final int TYPE_NOTE = 1;
    public static final int TYPE_TIMER = 2;

    //ОБъекты для работы с форматом даты
    public static Locale locale = Locale.getDefault();
    public static SimpleDateFormat sdfCal = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", locale);
    public static SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy  HH:mm", locale);

    public static String CHANNEL_ID = "channel_id_mynote";
    public static String CHANNEL_NAME = "channel_name_mynote";


    public static void showToastShort(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    //Показ уведомления
    public static void showNotification(Context context, Object objectToNotif) {
        String title;
        String content;
        int type;
        int id;

        if (objectToNotif instanceof Note) {
            Note note = (Note) objectToNotif;
            title =context.getString(R.string.notifNoteTitle);
            content = note.getName() + ": " + note.getDescription();
            type = TYPE_NOTE;
            id = note.getId();
        }
        else if (objectToNotif instanceof Timer) {
            Timer timer = (Timer) objectToNotif;
            title = context.getString(R.string.notifTimerDoneTitle);
            content = timer.getName();
            type = TYPE_TIMER;
            id = timer.getId();
        } else
            return;

        //Показ уведомления
        Intent intent_new = new Intent(context, NotifActivity.class);
        intent_new.putExtra("id", id);
        intent_new.putExtra("type", type);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context.getApplicationContext(),
                id,
                intent_new,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        if(Build.VERSION.SDK_INT >= 26){
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(
                    context.getApplicationContext(),
                    CHANNEL_ID);
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(1);
            notificationChannel.enableLights(true);

            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.icon_notif)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setShowWhen(true)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            nm.createNotificationChannel(notificationChannel);
            nm.cancel(id);
            nm.notify(id, notification);
        }
        else if(Build.VERSION.SDK_INT >= 21){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                            .setContentTitle(title)
                            .setContentText(content)
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.icon_notif);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
            notificationManager.cancel(id);
            notificationManager.notify(id, builder.build());
        }
    }

    public static void cancelNotificaton(Context context, int id) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(id);
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

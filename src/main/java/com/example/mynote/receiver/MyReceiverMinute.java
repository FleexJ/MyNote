package com.example.mynote.receiver;

import android.app.ActivityManager;
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
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.entity.Timer;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MyReceiverMinute extends BroadcastReceiver {

    private TimersDAO timersDAO;
    private SQLiteDatabase DB;

    @Override
    public void onReceive(Context context, Intent intent) {
        DB = context.getApplicationContext().openOrCreateDatabase("vidgets.db", MODE_PRIVATE, null);
        timersDAO = new TimersDAO(DB);

        int id = intent.getIntExtra("id",0);
        Timer timer = timersDAO.getTimersById(id);

        startNotification(context, timer);

        timer.setState(0);
        timersDAO.editTimers(timer);
        DB.close();

//        if(Build.VERSION.SDK_INT >= 24 && isForeground(context.getApplicationContext()))
//            context.getApplicationContext().startActivity(intent_new);
    }

    //Показ уведомления
    private void startNotification(Context context, Timer timer) {
        Intent intent_new = new Intent(context, NotifActivity.class);
        intent_new.putExtra("id", timer.getId());
        intent_new.putExtra("type", 2);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), timer.getId() , intent_new, PendingIntent.FLAG_CANCEL_CURRENT);

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
                    .setContentTitle(
                            context.getString(R.string.notifTimerWorkedTitle)
                    )
                    .setContentText(timer.getName())
                    .setPriority(Notification.PRIORITY_MAX)
                    .setShowWhen(true)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            notification.defaults = NotificationCompat.DEFAULT_ALL;
            nm.createNotificationChannel(notificationChannel);
            nm.cancel(timer.getId());
            nm.notify(timer.getId(), notification);
        }
        else
        if(Build.VERSION.SDK_INT >= 21 ){
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context.getApplicationContext(), "channel_id_mynote")
                            .setContentTitle(
                                    context.getString(R.string.notifTimerWorkedTitle)
                            )
                            .setContentText(timer.getName())
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.icon_notif);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context.getApplicationContext());
            notificationManager.cancel(timer.getId());
            notificationManager.notify(timer.getId(), builder.build());
        }
    }

    //Проверяет, запущено ли окно приложения
    public static boolean isForeground(Context context) {
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
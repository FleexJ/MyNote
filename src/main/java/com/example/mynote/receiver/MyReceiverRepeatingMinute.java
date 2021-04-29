package com.example.mynote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.example.mynote.dao.TimersDAO;
import com.example.mynote.entity.Timer;
import com.example.mynote.globalVar.MyGlobal;

import static android.content.Context.MODE_PRIVATE;

public class MyReceiverRepeatingMinute extends BroadcastReceiver {

    private TimersDAO timersDAO;
    private SQLiteDatabase DB;
    //Объект общих функций
    private final MyGlobal myGlobal = new MyGlobal();

    @Override
    public void onReceive(Context context, Intent intent) {
        DB = context.getApplicationContext().openOrCreateDatabase(MyGlobal.DB_NAME, MODE_PRIVATE, null);
        timersDAO = new TimersDAO(DB);

        int id = intent.getIntExtra("id",0);
        Timer timer = timersDAO.getTimersById(id);
        //Если время не вышло, то убавляем его и создаем новый аларм
        if((timer.getMinute() - 1) >= 1) {
            timer.setMinute(timer.getMinute() - 1);
            timersDAO.editTimer(timer);

            myGlobal.startAlarmTimers(context, timer);
            myGlobal.showNotifProgressTimers(context, timer);
        } else //Если время вышло, то уведомление
            myGlobal.showNotification(context, timer);

        DB.close();
    }

}
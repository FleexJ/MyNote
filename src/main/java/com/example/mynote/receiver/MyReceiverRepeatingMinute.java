package com.example.mynote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.example.mynote.dao.DatabaseHelper;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.entity.Timer;
import com.example.mynote.globalVar.MyGlobal;

import static android.content.Context.MODE_PRIVATE;

public class MyReceiverRepeatingMinute extends BroadcastReceiver {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private TimersDAO timersDAO;
    //Объект общих функций
    private final MyGlobal myGlobal = new MyGlobal();

    @Override
    public void onReceive(Context context, Intent intent) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
        db = databaseHelper.getWritableDatabase();
        timersDAO = new TimersDAO(db);

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

        db.close();
    }

}
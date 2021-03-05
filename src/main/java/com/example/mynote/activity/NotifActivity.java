package com.example.mynote.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mynote.R;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.entity.Notes;
import com.example.mynote.entity.Timers;

public class NotifActivity extends Activity {

    int id;
    NotesDAO notesDAO;
    TimersDAO timersDAO;
    SQLiteDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);

        DB = getBaseContext().openOrCreateDatabase("vidgets.db", MODE_PRIVATE, null);
        notesDAO = new NotesDAO(DB);
        timersDAO = new TimersDAO(DB);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_notif);
        TextView textView_title = findViewById(R.id.textView_notifTitle);
        TextView textView_name = findViewById(R.id.textView_notifName);
        TextView textView_desc = findViewById(R.id.textView_notifDesc);
        id = getIntent().getIntExtra("id", 0);
        int type = getIntent().getIntExtra("type",0);
        if(type == 1) {
            Notes notes = notesDAO.getNoteById(id);
             textView_title.setText("Напоминаем вам о задаче!");
             textView_name.setText(notes.getName());
             textView_desc.setText(notes.getDescription());
             if(notes.getDescription().isEmpty())
                 linearLayout.removeView(textView_desc);
             if(notes.getName().isEmpty())
                 linearLayout.removeView(textView_name);
        }
        else
            if(type == 2) {
                Timers timers = timersDAO.getTimersById(id);
                textView_title.setText("Cработал таймер!");
                textView_name.setText(timers.getName());
                linearLayout.removeView(textView_desc);
            }
        DB.close();
    }

    public void clickApply(View view) {
        ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(id);
        finish();
    }

}

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
import com.example.mynote.dao.DatabaseHelper;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.Timer;
import com.example.mynote.globalVar.MyGlobal;

public class NotifActivity extends Activity {

    private int id;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private NotesDAO notesDAO;
    private TimersDAO timersDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getWritableDatabase();
        notesDAO = new NotesDAO(db);
        timersDAO = new TimersDAO(db);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_notif);
        TextView textView_title = findViewById(R.id.textView_notifTitle);
        TextView textView_name = findViewById(R.id.textView_notifName);
        TextView textView_desc = findViewById(R.id.textView_notifDesc);
        id = getIntent().getIntExtra("id", 0);
        int type = getIntent().getIntExtra("type",0);
        if(type == MyGlobal.TYPE_NOTE) {
            Note note = notesDAO.getNoteById(id);
             textView_title.setText(
                     getString(R.string.notifNoteTitle)
             );
             textView_name.setText(note.getName());
             textView_desc.setText(note.getDescription());
             if(note.getDescription().isEmpty())
                 linearLayout.removeView(textView_desc);
             if(note.getName().isEmpty())
                 linearLayout.removeView(textView_name);
        }
        else if(type == MyGlobal.TYPE_TIMER) {
                Timer timer = timersDAO.getTimersById(id);
                textView_title.setText(
                        getString(R.string.notifTimerWorkedTitle)
                );
                textView_name.setText(timer.getName());
                linearLayout.removeView(textView_desc);
            }
        db.close();
    }

    public void clickApply(View view) {
        ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(id);
        finish();
    }

}

package com.example.mynote.activity;


import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TabHost;

import com.example.mynote.R;
import com.example.mynote.adapter.NoteAdapter;
import com.example.mynote.adapter.TimerAdapter;
import com.example.mynote.dao.DatabaseHelper;
import com.example.mynote.dao.IdCountDAO;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.dao.TrashDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.Timer;
import com.example.mynote.globalVar.MyGlobal;
import com.example.mynote.receiver.NoteReceiver;
import com.example.mynote.receiver.TimerReceiver;
import com.example.mynote.swipeListener.MainSwipeListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

//    private long time = 3600000;
//    private CountDownTimer countDownTimer_timers, countDownTimer_notes;
    //Объект работы с бд
    private SQLiteDatabase db;
    private NotesDAO notesDAO;
    private TimersDAO timersDAO;
    private TrashDAO trashDAO;
    private IdCountDAO idCountDAO;

    private List<Note> notes;
    private NoteAdapter noteAdapter;
    private ListView listView_note;

    private List<Timer> timers;
    private TimerAdapter timerAdapter;
    private ListView listView_timer;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getWritableDatabase();
        notesDAO = new NotesDAO(db);
        timersDAO = new TimersDAO(db);
        trashDAO = new TrashDAO(db);
        idCountDAO = new IdCountDAO(db);

        listView_note = findViewById(R.id.listView_notes);
        View emptyNotes = findViewById(R.id.layout_emptyNotes);
        listView_note.setEmptyView(emptyNotes);

        listView_timer = findViewById(R.id.listView_timers);
        View emptyTimers = findViewById(R.id.layout_emptyTimers);
        listView_timer.setEmptyView(emptyTimers);

        emptyNotes.setOnTouchListener(new MainSwipeListener(this));
        emptyTimers.setOnTouchListener(new MainSwipeListener(this));
        listView_note.setOnTouchListener(new MainSwipeListener(this));
        listView_timer.setOnTouchListener(new MainSwipeListener(this));

        //Переактивация всех активных записей
        for (Note note : notesDAO.getActiveAll())
            NoteReceiver.startAlarmNote(getApplicationContext(), note);

        //Настройка tabHost
        TabHost tabHost = findViewById(R.id.tab_menu);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator(getString(R.string.simpleNote));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator(getString(R.string.timer));
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
    }

    @Override
    public void onResume(){
//        setCountDownTimer_timers();
//        setCountDownTimer_notes();
        initNotes();
        initTimers();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }

    public void initNotes(){
        noteAdapter = new NoteAdapter(
                this,
                notesDAO.getAll(),
                db
        );
        listView_note.setAdapter(noteAdapter);
    }

     public void initTimers(){
        timerAdapter = new TimerAdapter(
                this,
                timersDAO.getAll(),
                db
        );
        listView_timer.setAdapter(timerAdapter);
    }

    public void addTimer(View view){
        int newId = idCountDAO.getNewId();
        Timer timer = new Timer(
                        newId,
                        getString(R.string.timerDefaultName),
                        Timer.NOT_ACTIVE_STATE,
                        1);
        timersDAO.insert(timer);
        idCountDAO.insert(newId);

        initTimers();
    }

    public void addNote(View view){
        Intent intentAdd = new Intent(".AddNoteActivity");
        startActivity(intentAdd);
    }

    public void clickPopupMenu(View view){
        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
        popup.getMenu().add(Menu.NONE, 0, Menu.NONE,
                getString(R.string.trash)
        );
        popup.getMenu().add(Menu.NONE, 1, Menu.NONE,
                getString(R.string.popupUpdateScreen)
        );
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case 0:
                        Intent intent = new Intent(".TrashActivity");
                        startActivity(intent);
                        break;
                    case 1:
                        initNotes();
                        initTimers();
                        MyGlobal.showToastShort(
                                getApplicationContext(),
                                getString(R.string.updated)
                        );
                        break;
                }
                return true;
            }
        });
    }

//    //запускает таймер обратного отсчета, который обновляет экран таймеров
//    public void setCountDownTimer_timers() {
//        if (countDownTimer_timers != null)
//            countDownTimer_timers.cancel();
//        countDownTimer_timers = new CountDownTimer(time, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                doMinute();
//            }
//            @Override
//            public void onFinish() {
//                setCountDownTimer_timers();
//            }
//        };
//        countDownTimer_timers.start();
//    }
//    public void cancelCountDownTimer_timers() {
//        if (countDownTimer_timers != null)
//            countDownTimer_timers.cancel();
//    }
//
//    //запускает таймер обратного отсчета, который обновляет экран записей
//    public void setCountDownTimer_notes() {
//        if (countDownTimer_notes != null)
//            countDownTimer_notes.cancel();
//        countDownTimer_notes = new CountDownTimer(time, 3000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                doSomething();
//            }
//            @Override
//            public void onFinish() {
//                setCountDownTimer_notes();
//            }
//        };
//        countDownTimer_notes.start();
//    }
//    public void cancelCountDownTimer_notes() {
//        if (countDownTimer_notes != null)
//            countDownTimer_notes.cancel();
//    }
}



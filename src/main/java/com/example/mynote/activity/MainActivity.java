package com.example.mynote.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.mynote.R;
import com.example.mynote.dao.IdCountDAO;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.dao.TrashDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.Timer;
import com.example.mynote.entity.TrashNote;
import com.example.mynote.globalVar.MyGlobal;
import com.example.mynote.swipeListener.MainSwipeListener;

import java.util.List;


public class MainActivity extends Activity {

    private long time = 3600000;
    private CountDownTimer countDownTimer_timers, countDownTimer_notes;
    //Объект работы с бд
    private SQLiteDatabase DB;
    private NotesDAO notesDAO;
    private TrashDAO trashDAO;
    private TimersDAO timersDAO;
    private IdCountDAO idCountDAO;
    //Объект общих функций
    private final MyGlobal myGlobal = new MyGlobal();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DB = getBaseContext().openOrCreateDatabase("vidgets.db", MODE_PRIVATE, null);
        notesDAO = new NotesDAO(DB);
        trashDAO = new TrashDAO(DB);
        timersDAO = new TimersDAO(DB);
        idCountDAO = new IdCountDAO(DB);

        ScrollView scrollView = findViewById(R.id.ScrollView_note);
        scrollView.setOnTouchListener(new MainSwipeListener(this));
        ScrollView scrollView_minute = findViewById(R.id.ScrollView_minute);
        scrollView_minute.setOnTouchListener(new MainSwipeListener(this));
        myGlobal.restartAlarmNotes(
                getApplicationContext(),
                notesDAO
        );

        //Настройка tabHost
        TabHost tabHost = findViewById(R.id.tab_menu);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator(
                getString(R.string.tab_simpleNoteTitle)
        );
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator(
                getString(R.string.tab_timerTitle)
        );
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(0);

        if( getIntent().getIntExtra("tab_nom",-1) == 1)
            tabHost.setCurrentTab(1);
        else
            tabHost.setCurrentTab(0);
    }

    @Override
    public void onResume(){
        setCountDownTimer_timers();
        setCountDownTimer_notes();
        doSomething();
        doMinute();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        DB.close();
        super.onDestroy();
    }

    //Главная процедура обычных записей
    public void doSomething(){
        LinearLayout linear_note = findViewById(R.id.LinearLayout);
        linear_note.removeAllViews();

        final List<Note> noteList = notesDAO.getAllNotes();

        final int note_k = noteList.size();
        //Форма для вывода информации из бд
        if (note_k == 0) {//Если в таблице нет ни одной записи, то выводим сообщение об этом, путем добавления на слой textView и imageView
            TextView textView_null_note = new TextView(this, null, 0, R.style.BDout_null);
            textView_null_note.setText(
                    getString(R.string.emptyNotesYet)
            );
            linear_note.addView(textView_null_note);
            ImageView imageView_null_note = new ImageView(this, null, 0, R.style.BDout_null);
            imageView_null_note.setImageResource(R.drawable.icon_null);
            //imageView_null.setLayoutParams(new RelativeLayout.LayoutParams(1000, 1200));
            linear_note.addView(imageView_null_note);
        } else {
            //Если в таблице есть какие-то записи
            //Поле для хранения описания
            final TextView[] textView_desc_note = new TextView[note_k];
            //Поле для хранения имени
            final TextView[] textView_name_note = new TextView[note_k];
            //Лэйаут для вывода имени и чекбокса
            LinearLayout[] linear_name_checkbox = new LinearLayout[note_k];
            //Чекбокс для вывода состояния задания
            final CheckBox[] checkBox_note = new CheckBox[note_k];
            //Поле задержки
            final TextView[] textView_delay_note = new TextView[note_k];
            //Общий лэйаут для хранения всех данных одной записи
            LinearLayout[] linear_bd_note = new LinearLayout[note_k];

            for (int i = 0 ; i < note_k; i++) {
                final int final_i = i;
                linear_name_checkbox[final_i] = new LinearLayout(this);
                checkBox_note[final_i] = new CheckBox(this);
                //Инициализация массивов данных с присвоением стиля
                textView_name_note[final_i] = new TextView(this, null, 0, R.style.BDout_name_note);
                //textView_name_note[final_i].setTypeface(Typeface.createFromAsset(getAssets(), "fonts/main_font.ttf"));
                textView_desc_note[final_i] = new TextView(this, null, 0, R.style.BDout_desc);
                textView_delay_note[final_i] = new TextView(this , null, 0, R.style.BDout_delay);

                //Внесении данных результата запроса в массивы
                if(noteList.get(final_i).getState() == 1)
                    checkBox_note[final_i].setChecked(true);
                else
                    checkBox_note[final_i].setChecked(false);

                textView_name_note[final_i].setText(noteList.get(final_i).getName());
                textView_desc_note[final_i].setText(noteList.get(final_i).getDescription());

                //Выводим задержку в удобном формате
                textView_delay_note[final_i].setText(
                        getString(R.string.viewSimpleNoteBottom,
                                MyGlobal.sdfDate.format(noteList.get(final_i).getDelayCalendar().getTime()),
                                noteList.get(final_i).getRepeat().getString(this)
                                )
                );

                //Добавление представлений на экран
                linear_bd_note[final_i] = new LinearLayout(this, null, 0, R.style.BDout_layout);
                linear_bd_note[final_i].setBackgroundResource(R.drawable.linear_round);
//                linear_bd_note[final_i].addView(textView_name_note[final_i]);
                linear_name_checkbox[final_i].addView(checkBox_note[final_i]);
                linear_name_checkbox[final_i].addView(textView_name_note[final_i]);
                linear_bd_note[final_i].addView(linear_name_checkbox[final_i]);
                linear_bd_note[final_i].addView(new View(this, null, 0, R.style.BDout_line));

                //Если поле описания не пусто, то добавляем его на экран
                if(!textView_desc_note[final_i].getText().toString().isEmpty())
                    linear_bd_note[final_i].addView(textView_desc_note[final_i]);

                linear_bd_note[final_i].addView(textView_delay_note[final_i]);
                linear_note.addView(linear_bd_note[final_i]);

                //Слушатель для клика по состоянию записи
                checkBox_note[final_i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        CheckBox checkBox = (CheckBox) buttonView;
                        if(checkBox.isChecked()) {
                            if(System.currentTimeMillis() >= noteList.get(final_i).getDelayCalendar().getTimeInMillis()) {
                                myGlobal.makeToastShort(
                                        getApplicationContext(),
                                        getString(R.string.toastIncorrectTimeForStart)
                                        );
                                checkBox.setChecked(false);
                            } else {
                                //Запуск аларма для записи
                                myGlobal.startAlarmNote(getApplicationContext(), noteList.get(final_i));
                                noteList.get(final_i).setState(1);
                                notesDAO.editNote(noteList.get(final_i));
//                                AlertDialog.Builder alert_builder = new AlertDialog.Builder(MainActivity.this);
//                                alert_builder.setMessage("Для корректной работы приложения, нужно разрешить автозапуск в системных настройках приложения.")
//                                        .setCancelable(true)
//                                        .setTitle("Напоминание активировано")
//                                        .setPositiveButton("Ок",
//                                                new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialog, int which) {
//                                                    }
//                                                    });
//                                alert_builder.show();
                                myGlobal.makeToastShort(
                                        getApplicationContext(),
                                        getString(R.string.toastNoteStarted)
                                );
                                checkBox_note[final_i].setChecked(false);
                            }
                        } else {
                            noteList.get(final_i).setState(0);
                            notesDAO.editNote(noteList.get(final_i));
                            //Удаление аларма для записи
                            myGlobal.cancelAlarm(
                                    getApplicationContext(),
                                    noteList.get(final_i).getId());
                        }
                        setCountDownTimer_notes();
                    }
                });
                checkBox_note[final_i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (countDownTimer_notes != null)
                            countDownTimer_notes.cancel();
                        return false;
                    }
                });

                //Диалог предлагающий удалить запись при долгом клике
                linear_bd_note[final_i].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder alert_builder = new AlertDialog.Builder(MainActivity.this);
                        alert_builder
                                .setMessage(
                                        getString(R.string.dialogueTitleDeleteNote)
                                )
                                .setCancelable(true)
                                .setPositiveButton(
                                        getString(R.string.dialogueOk) ,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Удаление аларм менеджера, в случае удаления записи из бд
                                                myGlobal.cancelAlarm(
                                                        getApplicationContext(),
                                                        noteList.get(final_i).getId()
                                                );

                                                notesDAO.deleteNote(noteList.get(final_i));
                                                trashDAO.insertTrash(new TrashNote(
                                                        noteList.get(final_i).getId(),
                                                        noteList.get(final_i).getName(),
                                                        noteList.get(final_i).getDescription(),
                                                        noteList.get(final_i).getDelay(),
                                                        MyGlobal.TYPE_NOTE
                                                ));
                                                myGlobal.makeToastShort(
                                                        getApplicationContext(),
                                                        getString(R.string.toastNoteDeleted)
                                                );
                                                dialog.cancel();
                                                doSomething();
                                            }
                                        })
                                .setNegativeButton(
                                        getString(R.string.dialogueCancel) ,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                        alert_builder.show();
                        return true;
                    }
                });

                //Редактирование записи по одному клику
                linear_bd_note[final_i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentEdit = new Intent(".EditActivity");
                        //Передаем в другое активити индекс нажатой записи
                        intentEdit.putExtra("idEdit", noteList.get(final_i).getId());
                        startActivity(intentEdit);
                        //Удаление слушателя, чтобы не было двойного вызова при двойном клике
                        v.setOnClickListener(null);
                    }
                });

                //Добавление пустого элемента для отступа между записями
                linear_note.addView(new TextView(this));
                linear_note.addView(new TextView(this));
            }
            //Отступ у последнего элемента, чтобы кнопка не преграждала запись
            linear_note.addView(new TextView(this));
            linear_note.addView(new TextView(this));
        }
    }

    //Главная процедура записей по минутам
     public void doMinute(){
        final LinearLayout linear_timer = findViewById(R.id.LinearLayout_minute);
        linear_timer.removeAllViews();

        final List<Timer> timers = timersDAO.getAllTimers();

        int note_k = timers.size();
        //Форма для вывода информации из бд
        if (note_k == 0) {//Если в таблице нет ни одной записи, то выводим сообщение об этом, путем добавления на слой textView и imageView
            TextView textView_null_timer = new TextView(this, null, 0, R.style.BDout_null);
            textView_null_timer.setText(
                    getString(R.string.emptyTimersYet)
            );
            linear_timer.addView(textView_null_timer);
            ImageView imageView_null_timer = new ImageView(this, null, 0, R.style.BDout_null);
            imageView_null_timer.setImageResource(R.drawable.icon_null);
            //imageView_null.setLayoutParams(new RelativeLayout.LayoutParams(1000, 1200));
            linear_timer.addView(imageView_null_timer);
        }
        else {
            //Если в таблице есть какие-то записи
            final LinearLayout[] linear_name_state = new LinearLayout[note_k];
            final CheckBox[] checkBox_state_timer = new CheckBox[note_k];
            //Поле для хранения имени
            final TextView[] textView_name_timer = new TextView[note_k];
            //Текстовое поле для прогресса
            final TextView[] textView_minute_timer = new TextView[note_k];
            //Поле задержки
            final SeekBar[] seekBar_minute_timer = new SeekBar[note_k];
            //Общий лэйаут для хранения всех данных одной записи
            final LinearLayout[] linear_bd_timer = new LinearLayout[note_k];

            for (int i = 0; i < note_k; i++) {
                final int final_i = i;
                linear_name_state[final_i] = new LinearLayout(this, null, 0, R.style.BDout_name);
                checkBox_state_timer[final_i] = new CheckBox(this);
                textView_name_timer[final_i] = new TextView(this, null, 0, R.style.BDout_name);
                textView_minute_timer[final_i] = new TextView(this, null, 0, R.style.BDout_minute);
                seekBar_minute_timer[final_i] = new SeekBar(this, null, 0, R.style.BDout_seekBar);
                linear_bd_timer[final_i] = new LinearLayout(this, null, 0, R.style.BDout_layout);

                //Внесении данных результата запроса в массивы
                textView_name_timer[final_i].setText(timers.get(final_i).getName());
                //Инициализация переключателя состояния
                if(timers.get(final_i).getState() == 1)
                    checkBox_state_timer[final_i].setChecked(true);
                else
                    checkBox_state_timer[final_i].setChecked(false);

                //Инициализация прогресса минут
                textView_minute_timer[final_i].setText(
                        getString(R.string.timerProgress, timers.get(final_i).getMinute())
                );
                seekBar_minute_timer[final_i].setMax(61);
                seekBar_minute_timer[final_i].setProgress(timers.get(final_i).getMinute());
                //Задаем кастомный вид seekBar
                //seekBar_minute_timer[final_i].setProgressDrawable(getDrawable(R.drawable.seekbar_custom));
                //seekBar_minute_timer[final_i].setThumb(getDrawable(R.drawable.seekbar_thumb));

                //Слушатель одного клика по записи, здесь отображается диалоговое окно, которое предлагает ввести новое название таймера
                linear_bd_timer[final_i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater li = LayoutInflater.from(MainActivity.this);
                        View promptsView = li.inflate(R.layout.prompt_minute_name, null);
                        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        //Настраиваем prompt.xml для нашего AlertDialog:
                        mDialogBuilder.setView(promptsView);
                        //Настраиваем отображение поля для ввода текста в открытом диалоге:
                        final EditText userInput = promptsView.findViewById(R.id.editName_minute);
                        userInput.setText(timers.get(final_i).getName());
                        //Настраиваем сообщение в диалоговом окне:
                        mDialogBuilder
                                .setCancelable(true)
                                .setPositiveButton(
                                        getString(R.string.dialogueOk) ,
                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(!userInput.getText().toString().isEmpty()) {
                                                    timers.get(final_i).setName(userInput.getText().toString());
                                                    timersDAO.editTimer(timers.get(final_i));
                                                    textView_name_timer[final_i].setText(userInput.getText().toString());
                                                }
                                            }
                                        })
                                .setNegativeButton(
                                        getString(R.string.dialogueCancel) ,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alertDialog = mDialogBuilder.create();
                        userInput.setSelection(userInput.getText().length());
                        alertDialog.show();
                    }
                });

                //Диалог предлагающий удалить таймер при долгом клике
                linear_bd_timer[final_i].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder alert_builder = new AlertDialog.Builder(MainActivity.this);
                        alert_builder
                                .setMessage(
                                        getString(R.string.dialogueTitleDeleteTimer))
                                .setCancelable(true)
                                .setPositiveButton(
                                        getString(R.string.dialogueOk) ,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Удаление аларм менеджера, в случае удаления записи из бд
                                                myGlobal.cancelAlarm(
                                                        getApplicationContext(),
                                                        timers.get(final_i).getId()
                                                );
                                                //Удаление уведомления прогресса
                                                myGlobal.cancelNotifProgressTimers(
                                                        getApplicationContext(),
                                                        timers.get(final_i)
                                                );

                                                timersDAO.deleteTimer(timers.get(final_i));
                                                trashDAO.insertTrash(new TrashNote(
                                                        timers.get(final_i).getId(),
                                                        timers.get(final_i).getName(),
                                                        "",
                                                        timers.get(final_i).getMinute(),
                                                        MyGlobal.TYPE_TIMER
                                                ));
                                                myGlobal.makeToastShort(
                                                        getApplicationContext(),
                                                        getString(R.string.toastTimerDeleted)
                                                );
                                                dialog.cancel();
                                                doMinute();
                                            }
                                        })
                                .setNegativeButton(
                                        getString(R.string.dialogueCancel) ,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                        alert_builder.show();
                        return true;
                    }
                });

                //Слушатель переключателя состояния
                checkBox_state_timer[final_i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        CheckBox checkBox = (CheckBox) buttonView;
                        if(checkBox.isChecked()) {
                            //Запуск аларма для таймера
                            myGlobal.startAlarmTimers(
                                    getApplicationContext(),
                                    timers.get(final_i)
                            );
                            //Показ уведомления прогресса
                            myGlobal.showNotifProgressTimers(
                                    getApplicationContext(),
                                    timers.get(final_i)
                            );

                            timers.get(final_i).setState(1);
                            timersDAO.editTimer(timers.get(final_i));
                            myGlobal.makeToastShort(
                                    getApplicationContext(),
                                    getString(R.string.toastTimerStarted, timers.get(final_i).getMinute())
                            );
                        }
                        else {
                            timers.get(final_i).setState(0);
                            timersDAO.editTimer(timers.get(final_i));
                            //Удаление аларма для таймера
                            myGlobal.startAlarmTimers(
                                    getApplicationContext(),
                                    timers.get(final_i)
                            );
                            //Удаление уведомления прогресса
                            myGlobal.cancelNotifProgressTimers(
                                    getApplicationContext(),
                                    timers.get(final_i)
                            );
                        }
                        setCountDownTimer_timers();
                    }
                });
                checkBox_state_timer[final_i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (countDownTimer_timers != null)
                            countDownTimer_timers.cancel();
                        return false;
                    }
                });

                //Слушатель смены прогресса SeekBar
                seekBar_minute_timer[final_i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textView_minute_timer[final_i].setText(
                                getString(R.string.timerProgress, progress)
                        );
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //Останавливаем обновление экрана, пока пользователь выбирает время таймера
                        countDownTimer_timers.cancel();
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if(seekBar.getProgress() == 0)
                            seekBar.setProgress(1);
                        timers.get(final_i).setMinute(seekBar.getProgress());
                        timers.get(final_i).setState(0);
                        timersDAO.editTimer(timers.get(final_i));
                        //Отменяем напоминание
                        myGlobal.cancelAlarm(
                                getApplicationContext(),
                                timers.get(final_i).getId()
                        );

                        checkBox_state_timer[final_i].setChecked(false);

                        //Запускаем таймер обновления экрана заново
                        setCountDownTimer_timers();
                    }
                });

                //Добавление всех представлений на экран
                linear_name_state[i].addView(checkBox_state_timer[i]);
                linear_name_state[i].addView(textView_name_timer[i]);
                linear_bd_timer[i].addView(linear_name_state[i]);
                linear_bd_timer[i].addView(textView_minute_timer[i]);
                linear_bd_timer[i].addView(seekBar_minute_timer[i]);

                linear_timer.addView(linear_bd_timer[i]);
                linear_timer.addView(new TextView(this));
            }
            //Добавление пустых полей, чтобы кнопка не преграждала запись
            linear_timer.addView(new TextView(this));
            linear_timer.addView(new TextView(this));
            linear_timer.addView(new TextView(this));
        }
    }

    //Добавление новых записей таймера
    public void addMinute(View view){
        int newId = idCountDAO.getNewId();
        timersDAO.insertTimer(new Timer(
                newId,
                getString(R.string.timerDefaultName),
                0,
                1
        ));
        idCountDAO.insertIdCount(newId);
        doMinute();
    }

    //Добавление новых записей
    public void addNote(View view){
        Intent intentAdd = new Intent(".AddNoteActivity");
        startActivity(intentAdd);
    }

    //Клик по значку меню
    public void clickPopupMenu(View view){
        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
        popup.getMenu().add(Menu.NONE, 0, Menu.NONE,
                getString(R.string.popupTrash)
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
                        doSomething();
                        doMinute();
                        myGlobal.makeToastShort(
                                getApplicationContext(),
                                getString(R.string.popupUpdated)
                        );
                        break;
                }
                return true;
            }
        });
    }

    //запускает таймер обратного отсчета, который обновляет экран таймеров
    public void setCountDownTimer_timers() {
        if (countDownTimer_timers != null)
            countDownTimer_timers.cancel();
        countDownTimer_timers = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                doMinute();
            }
            @Override
            public void onFinish() {
                setCountDownTimer_timers();
            }
        };
        countDownTimer_timers.start();
    }

    //запускает таймер обратного отсчета, который обновляет экран записей
    public void setCountDownTimer_notes() {
        if (countDownTimer_notes != null)
            countDownTimer_notes.cancel();
        countDownTimer_notes = new CountDownTimer(time, 3000) {
            @Override
            public void onTick(long millisUntilFinished) {
                doSomething();
            }
            @Override
            public void onFinish() {
                setCountDownTimer_notes();
            }
        };
        countDownTimer_notes.start();
    }
}



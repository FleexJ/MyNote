package com.example.mynote.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mynote.R;
import com.example.mynote.dao.IdCountDAO;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.dao.TrashDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.Timer;
import com.example.mynote.entity.TrashNote;
import com.example.mynote.entity.TypeRepeat;
import com.example.mynote.globalVar.MyGlobal;
import com.example.mynote.swipeListener.TrashSwipeListener;

import java.util.List;

public class TrashActivity extends Activity {

    private SQLiteDatabase DB;
    private TrashDAO trashDAO;
    private NotesDAO notesDAO;
    private TimersDAO timersDAO;
    private IdCountDAO idCountDAO;
    //Объект общих функций
    private final MyGlobal myGlobal = new MyGlobal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        DB = getBaseContext().openOrCreateDatabase(MyGlobal.DB_NAME, MODE_PRIVATE, null);
        trashDAO = new TrashDAO(DB);
        notesDAO = new NotesDAO(DB);
        timersDAO = new TimersDAO(DB);
        idCountDAO = new IdCountDAO(DB);

        doSomething();
        ConstraintLayout trash_constraint = findViewById(R.id.trash_constraint);
        trash_constraint.setOnTouchListener(new TrashSwipeListener(this));
        ScrollView scrollView = findViewById(R.id.ScrollViewTrash);
        scrollView.setOnTouchListener(new TrashSwipeListener(this));
    }

    @Override
    public void onDestroy() {
        DB.close();
        super.onDestroy();
    }

    //Закрытие активити корзины
    public void click_back(View view) {
        finish();
    }

    //Диалог удаления всех записей из корзины
    public void click_deleteAll(View view) {
        List<TrashNote> trashNoteList = trashDAO.getAllTrash();
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(TrashActivity.this);
        if(trashNoteList.size() != 0) {
            alert_builder
                    .setMessage(getString(R.string.dialogueClearTrash))
                    .setCancelable(true)
                    .setPositiveButton(
                            getString(R.string.dialogueClearTrashPositive),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    trashDAO.deleteAllTrash();
                                    myGlobal.makeToastShort(
                                            getApplicationContext(),
                                            getString(R.string.toastClearedTrash));
                                    dialog.cancel();
                                    doSomething();
                                }
                            })
                    .setNegativeButton(
                            getString(R.string.dialogueCancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            alert_builder.show();
        }
        else myGlobal.makeToastShort(
                getApplicationContext(),
                getString(R.string.emptyTrashYet)
        );
    }

    public void doSomething() {
        LinearLayout linear_trash = findViewById(R.id.linear_trash);
        linear_trash.removeAllViews();
        final List<TrashNote> trashNoteList = trashDAO.getAllTrash();
        int trash_k = trashNoteList.size(), i;

        if (trash_k == 0) {//Если в таблице нет ни одной записи, то выводим сообщение об этом, путем добавления на слой textView и imageView
            TextView textView_null = new TextView(this, null, 0, R.style.BDout_null);
            textView_null.setText(getString(R.string.emptyTrashYet));
            linear_trash.addView(textView_null);
            ImageView imageView_null = new ImageView(this, null, 0, R.style.BDout_null);
            imageView_null.setImageResource(R.drawable.icon_null);
            //imageView_null.setLayoutParams(new RelativeLayout.LayoutParams(1000, 1200));
            linear_trash.addView(imageView_null);
        } else {
            //Если в таблице есть какие-то записи
            //Поле для хранения заголовка
            TextView[] textView_name = new TextView[trash_k];
            //Поле для хранения описания
            TextView[] textView_desc = new TextView[trash_k];
            //Поле повторов
            TextView[] textView_delay = new TextView[trash_k];
            //Общий лэйаут для хранения всех данных одной записи
            LinearLayout[] linear_bd = new LinearLayout[trash_k];

            for (i=0; i<trash_k; i++) {
//                final int final_i = i;
                final TrashNote trashNote = trashNoteList.get(i);

                //Инициализация массивов данных с присвоением стиля
                textView_name[i] = new TextView(this, null, 0, R.style.BDout_name);
                textView_desc[i] = new TextView(this, null, 0, R.style.BDout_desc);
                textView_delay[i] = new TextView(this , null, 0, R.style.BDout_delay);

                //Внесении данных результата запроса в массивы
                textView_name[i].setText(trashNoteList.get(i).getName());
                textView_desc[i].setText(trashNoteList.get(i).getDescription());
                //Выводим задержку в удобном формате
                if(trashNoteList.get(i).getType() == MyGlobal.TYPE_NOTE)
                    textView_delay[i].setText(
                            MyGlobal.sdfDate.format(trashNoteList.get(i).getDelayCalendar().getTime())
                    );
                else if (trashNoteList.get(i).getType() == MyGlobal.TYPE_TIMER)
                    textView_delay[i].setText(
                            getString(R.string.timerProgress,  trashNoteList.get(i).getDelay())
                    );

                //Добавление представлений на экран
                linear_bd[i] = new LinearLayout(this, null, 0, R.style.BDout_layout);
                linear_bd[i].setBackgroundResource(R.drawable.linear_round);
                if(!textView_name[i].getText().toString().isEmpty())
                    linear_bd[i].addView(textView_name[i]);
//                linear_bd[i].addView(new TextView(this, null, 0, R.style.BDout_line));
                //Если поле описания пусто, то не добавляем его на лэйаут
                if(!textView_desc[i].getText().toString().isEmpty())
                    linear_bd[i].addView(textView_desc[i]);
                linear_bd[i].addView(textView_delay[i]);
                linear_trash.addView(linear_bd[i]);
                //Добавление пустого элемента для отступа между записями
                linear_trash.addView(new TextView(this));
                linear_trash.addView(new TextView(this));

                linear_bd[i].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder alert_builder = new AlertDialog.Builder(TrashActivity.this);
                        alert_builder
                                .setMessage(getString(R.string.dialogueDeleteFromTrash))
                                .setCancelable(true)
                                .setPositiveButton(
                                        getString(R.string.dialogueOk),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                trashDAO.deleteTrash(trashNote);
                                                idCountDAO.deleteId(trashNote.getId());
                                                myGlobal.makeToastShort(
                                                        getApplicationContext(),
                                                        getString(R.string.toastDeletedFromTrash)
                                                );
                                                dialog.cancel();
                                                doSomething();
                                            }
                                        })
                                .setNegativeButton(
                                        getString(R.string.dialogueCancel),
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

                linear_bd[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert_builder = new AlertDialog.Builder(TrashActivity.this);
                        alert_builder.setMessage(getString(R.string.backNoteFromTrashTitle))
                                .setCancelable(true)
                                .setPositiveButton(
                                        getString(R.string.dialogueOk),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                trashDAO.deleteTrash(trashNote);
                                                if (trashNote.getType() == MyGlobal.TYPE_NOTE) {
                                                    notesDAO.insertNote(new Note(
                                                            trashNote.getId(),
                                                            trashNote.getName(),
                                                            trashNote.getDescription(),
                                                            0,
                                                            trashNote.getDelay(),
                                                            TypeRepeat.NO
                                                    ));
                                                }
                                                else if (trashNote.getType() == MyGlobal.TYPE_TIMER) {
                                                    timersDAO.insertTimer(new Timer(
                                                            trashNote.getId(),
                                                            trashNote.getName(),
                                                            0,
                                                            (int) trashNote.getDelay()
                                                    ));
                                                }
                                                myGlobal.makeToastShort(
                                                        getApplicationContext(),
                                                        getString(R.string.toastBackedNoteFromTrash)
                                                );
                                                dialog.cancel();
                                                doSomething();
                                            }
                                        })
                                .setNegativeButton(
                                        getString(R.string.dialogueCancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                        alert_builder.show();
                    }
                });
            }
        }
    }
}

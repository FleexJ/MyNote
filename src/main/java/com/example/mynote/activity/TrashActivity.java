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
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mynote.R;
import com.example.mynote.dao.IdCountDAO;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.dao.TrashDAO;
import com.example.mynote.entity.Notes;
import com.example.mynote.entity.Timers;
import com.example.mynote.entity.TrashNote;
import com.example.mynote.swipeListener.TrashSwipeListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TrashActivity extends Activity {

    Locale locale = new Locale("ru", "RU");
    SimpleDateFormat sdfCal = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", locale);
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy  HH:mm", locale);
    TrashDAO trashDAO;
    NotesDAO notesDAO;
    TimersDAO timersDAO;
    IdCountDAO idCountDAO;
    SQLiteDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        DB = getBaseContext().openOrCreateDatabase("vidgets.db", MODE_PRIVATE, null);
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

    public void makeToast(String mes){
        Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
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
            alert_builder.setMessage("Очистить корзину?")
                    .setCancelable(true)
                    .setPositiveButton("Очистить",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    trashDAO.deleteAllTrash();
                                    makeToast("Корзина очищена");
                                    dialog.cancel();
                                    doSomething();
                                }
                            })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            alert_builder.show();
        }
        else makeToast("Корзина уже пуста");
    }

    public void doSomething() {
        LinearLayout linear_trash = findViewById(R.id.linear_trash);
        linear_trash.removeAllViews();
        final List<TrashNote> trashNoteList = trashDAO.getAllTrash();

        //Подсчет количества записей корзины
        int trash_k = trashNoteList.size(), i;

        if (trash_k == 0) {//Если в таблице нет ни одной записи, то выводим сообщение об этом, путем добавления на слой textView и imageView
            TextView textView_null = new TextView(this, null, 0, R.style.BDout_null);
            textView_null.setText("Корзина пуста");
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
                final int final_i = i;

                //Инициализация массивов данных с присвоением стиля
                textView_name[i] = new TextView(this, null, 0, R.style.BDout_name);
                textView_desc[i] = new TextView(this, null, 0, R.style.BDout_desc);
                textView_delay[i] = new TextView(this , null, 0, R.style.BDout_delay);

                //Внесении данных результата запроса в массивы
                textView_name[i].setText(trashNoteList.get(final_i).getName() + "");
                textView_desc[i].setText(trashNoteList.get(final_i).getDescription() + "");
                //Выводим задержку в удобном формате
                if(trashNoteList.get(final_i).getType() == 1)
                    textView_delay[i].setText(sdfDate.format(trashNoteList.get(final_i).getDelayCalendar().getTime()) + "");
                else
                    textView_delay[i].setText(trashNoteList.get(final_i).getDelay() + " мин.");

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
                        alert_builder.setMessage("Удалить запись навсегда?")
                                .setCancelable(true)
                                .setPositiveButton("Удалить",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                trashDAO.deleteTrash(trashNoteList.get(final_i));
                                                idCountDAO.deleteId(trashNoteList.get(final_i).getId());
                                                makeToast("Удалено");
                                                dialog.cancel();
                                                doSomething();
                                            }
                                        })
                                .setNegativeButton("Отмена",
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
                        alert_builder.setMessage("Вернуть запись из корзины?")
                                .setCancelable(true)
                                .setPositiveButton("Вернуть",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                trashDAO.deleteTrash(trashNoteList.get(final_i));
                                                if(trashNoteList.get(final_i).getType() == 1) {
                                                    notesDAO.insertNote(new Notes(
                                                            trashNoteList.get(final_i).getId(),
                                                            trashNoteList.get(final_i).getName(),
                                                            trashNoteList.get(final_i).getDescription(),
                                                            0,
                                                            trashNoteList.get(final_i).getDelay(),
                                                            "Нет"
                                                    ));
                                                }
                                                else {
                                                    timersDAO.insertTimers(new Timers(
                                                            trashNoteList.get(final_i).getId(),
                                                            trashNoteList.get(final_i).getName(),
                                                            0,
                                                            Integer.parseInt(trashNoteList.get(final_i).getDelay())
                                                    ));
                                                }
                                                makeToast("Возвращено");
                                                dialog.cancel();
                                                doSomething();
                                            }
                                        })
                                .setNegativeButton("Отмена",
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

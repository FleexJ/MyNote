package com.example.mynote.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.mynote.R;
import com.example.mynote.dao.DatabaseHelper;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.TypeRepeat;
import com.example.mynote.globalVar.MyGlobal;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private NotesDAO notesDAO;

    private EditText editText_desc, editText_name;
    private Button button_apply;
    private TextView textView_delay, textView_label;
    private Spinner spinner_repeat;
    private Calendar calendar = GregorianCalendar.getInstance();
    private Boolean isSave = false;
    //Объект в котором хранятся общие методы и переменные
    private final MyGlobal myGlobal = new MyGlobal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getWritableDatabase();
        notesDAO = new NotesDAO(db);

        //Заголовок активити
        editText_name = findViewById(R.id.editText_name);
        //Поле примечания
        editText_desc =  findViewById(R.id.editText_desc);
        //Поле с временем напоминания
        textView_delay =  findViewById(R.id.textView_delay);
        textView_label = findViewById(R.id.textView_label);
        //Поле выбора повтора
        spinner_repeat = findViewById(R.id.spinner_repeat);
        //Получение id нажатой на главной активити записи
        int id = getIntent().getIntExtra("idEdit",-1);
        Note note = notesDAO.getNoteById(id);
        //Заполнение заголовка
        editText_name.setText(note.getName());
        editText_name.setSelection(editText_name.getText().length());
        //Заполнения поля примечания
        editText_desc.setText(note.getDescription());
        //Переводим строку в calendar
        calendar.setTimeInMillis(note.getDelayCalendar().getTimeInMillis());
        //Выводим строку на экран в удобном формате
        textView_delay.setText(
                MyGlobal.sdfDate.format(calendar.getTime())
        );

        //Кнопка принятия
        button_apply = findViewById(R.id.button_apply);
        button_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSave = true;
                finish();
            }
        });

        spinner_repeat.setSelection(
                note.getRepeat().getId()
        );
        spinner_repeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onPause() {
        if(isSave) {
            int id = getIntent().getIntExtra("idEdit",-1);
            //Отменяем напоминание, если изменения были приняты
            myGlobal.cancelAlarm(
                    getApplicationContext(),
                    id
            );

            String desc = editText_desc.getText().toString();
            String name = editText_name.getText().toString();
            //Если поле заголовка не пусто, то обновляем запись с новым заголовком
            if(!name.isEmpty() || !desc.isEmpty()) {
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                TypeRepeat repeat = TypeRepeat.values()[
                                                         (int) spinner_repeat.getSelectedItemId()
                                                       ];
                Note note = new Note(id, name, desc,0,calendar.getTimeInMillis(), repeat);
                notesDAO.editNote(note);
            }
        }
        super.onPause();
    }

    //Вызов диалогов выбора даты и времени
    public void initDate(View view){
        final int mHour=calendar.get(Calendar.HOUR_OF_DAY),
                mMinute=calendar.get(Calendar.MINUTE),
                mYear=calendar.get(Calendar.YEAR),
                mMonth=calendar.get(Calendar.MONTH),
                mDay=calendar.get(Calendar.DAY_OF_MONTH);
        TimePickerDialog timePickerDialog = new TimePickerDialog(EditActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        textView_delay.setText(
                                MyGlobal.sdfDate.format(calendar.getTime())
                        );
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(EditActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        textView_delay.setText(
                                MyGlobal.sdfDate.format(calendar.getTime())
                        );
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}


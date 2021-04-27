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
import android.widget.Toast;

import com.example.mynote.R;
import com.example.mynote.dao.IdCountDAO;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.TypeRepeat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddNoteActivity extends Activity {

    private NotesDAO notesDAO;
    private IdCountDAO idCountDAO;
    private SQLiteDatabase DB;
    private EditText editText_desc, editText_name;
    private Button button_apply;
    private TextView textView_delay;
    private Spinner spinner_repeat;
    private Calendar calendar = GregorianCalendar.getInstance();
    private Locale locale = new Locale("ru", "RU");
    private SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy  HH:mm", locale);
    private SimpleDateFormat sdfCal = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", locale);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        DB = getBaseContext().openOrCreateDatabase("vidgets.db", MODE_PRIVATE, null);
        notesDAO = new NotesDAO(DB);
        idCountDAO = new IdCountDAO(DB);

        //Заголовок активити
        editText_name = findViewById(R.id.editText_name);
        //Поле примечания
        editText_desc =  findViewById(R.id.editText_desc);
        //Поле с временем напоминания
        textView_delay =  findViewById(R.id.textView_delay);
        //Выводим строку на экран в удобном формате
        textView_delay.setText(sdfDate.format(calendar.getTime()));
        //Выбор повтора
        spinner_repeat = findViewById(R.id.spinner_repeat);
        spinner_repeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Кнопка принятия
        button_apply = findViewById(R.id.button_apply);
        button_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText_name.getText().toString();
                String desc = editText_desc.getText().toString();
                if(!name.isEmpty() || !desc.isEmpty()) {
                    int newId = idCountDAO.getNewId();
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    TypeRepeat repeat = TypeRepeat.values()[(int) spinner_repeat.getSelectedItemId()];
                    Note note = new Note(newId, name, desc, 0, calendar.getTimeInMillis(), repeat);
                    notesDAO.insertNote(note);
                    idCountDAO.insertIdCount(newId);
                }
                finish();
            }
        });
    }

    public void makeToast(String mes){
        Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
    }

    //Вызов диалогов выбора даты и времени
    public void initDate(View view){
        final int     mHour=calendar.get(Calendar.HOUR_OF_DAY),
                mMinute=calendar.get(Calendar.MINUTE),
                mYear=calendar.get(Calendar.YEAR),
                mMonth=calendar.get(Calendar.MONTH),
                mDay=calendar.get(Calendar.DAY_OF_MONTH);
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddNoteActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        textView_delay.setText(sdfDate.format(calendar.getTime()));
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddNoteActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        textView_delay.setText(sdfDate.format(calendar.getTime()));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
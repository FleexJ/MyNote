package com.example.mynote.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
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
import com.example.mynote.receiver.NoteReceiver;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditNoteActivity extends Activity {

    private SQLiteDatabase db;
    private NotesDAO notesDAO;

    int id;

    private EditText editText_name, editText_description;
    private TextView textView_delay;
    private Spinner spinner_repeat;
    private Calendar calendar = GregorianCalendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getWritableDatabase();
        notesDAO = new NotesDAO(db);

        editText_name = findViewById(R.id.editText_name);
        editText_description =  findViewById(R.id.editText_description);
        textView_delay =  findViewById(R.id.textView_delay);
        spinner_repeat = findViewById(R.id.spinner_repeat);

        id = getIntent().getIntExtra("idEdit",-1);
        Note note = notesDAO.getById(id);

        editText_name.setText(note.getName());
        editText_name.setSelection(editText_name.getText().length());

        editText_description.setText(note.getDescription());

        calendar.setTimeInMillis(note.getDelayCalendar().getTimeInMillis());
        textView_delay.setText(
                MyGlobal.sdfDate.format(calendar.getTime())
        );

        spinner_repeat.setSelection(
                note.getRepeat().getId()
        );
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    public void saveChanges(View view) {
        NoteReceiver.cancelAlarmNote(this, id);

        String desc = editText_description.getText().toString();
        String name = editText_name.getText().toString();
        if(!name.isEmpty() || !desc.isEmpty()) {
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            TypeRepeat repeat = TypeRepeat.values()[
                                                    (int) spinner_repeat.getSelectedItemId()
                                                   ];
            Note note = new Note(id, name, desc,0,calendar.getTimeInMillis(), repeat);
            notesDAO.edit(note);
        }
        finish();
    }

    //Вызов диалогов выбора даты и времени
    public void initDate(View view){
        final int mHour = calendar.get(Calendar.HOUR_OF_DAY),
                mMinute = calendar.get(Calendar.MINUTE),
                mYear = calendar.get(Calendar.YEAR),
                mMonth = calendar.get(Calendar.MONTH),
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
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


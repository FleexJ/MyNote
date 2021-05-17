package com.example.mynote.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.mynote.R;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TrashDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.TrashNote;
import com.example.mynote.globalVar.MyGlobal;
import com.example.mynote.receiver.NoteReceiver;
import com.example.mynote.receiver.TimerReceiver;

import java.util.List;

public class NoteAdapter extends BaseAdapter {

    private final Activity activity;
    private final LayoutInflater inflater;
    private final List<Note> notes;

    private final NotesDAO notesDAO;
    private final TrashDAO trashDAO;

    public NoteAdapter(Activity activity, List<Note> notes, SQLiteDatabase db) {
        notesDAO = new NotesDAO(db);
        trashDAO = new TrashDAO(db);

        this.activity = activity;
        this.notes = notes;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Note note = notes.get(position);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.element_list_note, parent, false);

        final TextView textView_title = convertView.findViewById(R.id.textView_noteTitle);
        textView_title.setText(note.getName());

        final TextView textView_description = convertView.findViewById(R.id.textView_noteDescription);
        if (!note.getDescription().isEmpty()) {
            textView_description.setText(note.getDescription());
        }

        final Switch switch_state = convertView.findViewById(R.id.switch_noteState);
        switch_state.setChecked(
                note.getState() == Note.ACTIVE_STATE
        );

        final TextView textView_bottom = convertView.findViewById(R.id.textView_noteBottom);
        textView_bottom.setText(
                activity.getString(R.string.noteBottom,
                        MyGlobal.sdfDate.format(
                                note.getDelayCalendar().getTime()),
                        note.getRepeat().getString(activity))
        );

        // onClick - activity edit note
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEdit = new Intent(".EditActivity");
                //Передаем в другое активити индекс нажатой записи
                intentEdit.putExtra("idEdit", note.getId());
                activity.startActivity(intentEdit);
                //Удаление слушателя, чтобы не было двойного вызова при двойном клике
                v.setOnClickListener(null);
            }
        });

        // onLongClick - dialogue delete note
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert_builder = new AlertDialog.Builder(activity);
                alert_builder
                        .setMessage(activity.getString(R.string.dialogueTitleDeleteNote))
                        .setCancelable(true)
                        .setPositiveButton(
                                activity.getString(R.string.ok) ,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Удаление аларм менеджера, в случае удаления записи из бд
                                        NoteReceiver.cancelAlarmNote(activity, note.getId());

                                        notesDAO.delete(note);
                                        trashDAO.insert(new TrashNote(
                                                note.getId(),
                                                note.getName(),
                                                note.getDescription(),
                                                note.getDelay(),
                                                note.getRepeat(),
                                                MyGlobal.TYPE_NOTE)
                                        );
                                        notes.remove(note);
                                        notifyDataSetChanged();
                                        MyGlobal.cancelNotificaton(activity, note.getId());

                                        MyGlobal.showToastShort(
                                                activity,
                                                activity.getString(R.string.noteDeleted));
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton(
                                activity.getString(R.string.cancel),
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

        // switch state - change state note
        switch_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()) {
                    if(System.currentTimeMillis() >= note.getDelayCalendar().getTimeInMillis()) {
                        MyGlobal.showToastShort(
                                activity,
                                activity.getString(R.string.incorrectTimeForStart));
                        buttonView.setChecked(false);
                    } else {
                        //Запуск аларма для записи
                        NoteReceiver.startAlarmNote(activity, note);
                        note.setState(Note.ACTIVE_STATE);
                        notesDAO.edit(note);
                        MyGlobal.showToastShort(
                                activity,
                                activity.getString(R.string.noteStarted));
                    }
                } else {
                    note.setState(Note.NOT_ACTIVE_STATE);
                    notesDAO.edit(note);
                    //Удаление аларма для записи
                    NoteReceiver.cancelAlarmNote(activity, note.getId());
                }
            }
        });

        return convertView;
    }
}

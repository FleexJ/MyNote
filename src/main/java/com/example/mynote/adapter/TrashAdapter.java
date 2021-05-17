package com.example.mynote.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.mynote.R;
import com.example.mynote.dao.IdCountDAO;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.dao.TrashDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.Timer;
import com.example.mynote.entity.TrashNote;
import com.example.mynote.globalVar.MyGlobal;

import java.util.List;

public class TrashAdapter extends BaseAdapter {

    private final Activity activity;
    private final LayoutInflater inflater;
    private final List<TrashNote> trashNotes;

    private final TrashDAO trashDAO;
    private final IdCountDAO idCountDAO;
    private final NotesDAO notesDAO;
    private final TimersDAO timersDAO;

    public TrashAdapter(Activity activity, List<TrashNote> trashNotes, SQLiteDatabase db) {
        trashDAO = new TrashDAO(db);
        idCountDAO = new IdCountDAO(db);
        notesDAO = new NotesDAO(db);
        timersDAO = new TimersDAO(db);

        this.activity = activity;
        this.trashNotes = trashNotes;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return trashNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return trashNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TrashNote trashNote = trashNotes.get(position);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.element_list_trash, parent, false);

        final TextView textView_name = convertView.findViewById(R.id.textView_trashName);
        textView_name.setText(trashNote.getName());

        final TextView textView_description = convertView.findViewById(R.id.textView_trashDescription);
        textView_description.setText(trashNote.getDescription());

        final TextView textView_delay = convertView.findViewById(R.id.textView_trashDelay);
        switch (trashNote.getType()) {
            case MyGlobal.TYPE_NOTE:
                textView_delay.setText(
                        activity.getString(R.string.noteBottom,
                                MyGlobal.sdfDate.format(
                                        trashNote.getDelayCalendar().getTime()),
                                trashNote.getRepeat().getString(activity))
                );
                break;

            case MyGlobal.TYPE_TIMER:
                textView_delay.setText(
                        activity.getString(R.string.timerProgress, trashNote.getDelay())
                );
                break;
        }

        //onClick - dialogue return note from trash
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert_builder = new AlertDialog.Builder(activity);
                alert_builder.setMessage(activity.getString(R.string.returnFromTrash))
                        .setCancelable(true)
                        .setPositiveButton(
                                activity.getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        trashDAO.delete(trashNote);
                                        trashNotes.remove(trashNote);

                                        switch (trashNote.getType()) {
                                            case MyGlobal.TYPE_NOTE:
                                                notesDAO.insert(new Note(
                                                        trashNote.getId(),
                                                        trashNote.getName(),
                                                        trashNote.getDescription(),
                                                        Note.NOT_ACTIVE_STATE,
                                                        trashNote.getDelay(),
                                                        trashNote.getRepeat())
                                                );
                                                break;

                                            case MyGlobal.TYPE_TIMER:
                                                timersDAO.insert(new Timer(
                                                        trashNote.getId(),
                                                        trashNote.getName(),
                                                        Timer.NOT_ACTIVE_STATE,
                                                        (int) trashNote.getDelay())
                                                );
                                                break;
                                        }

                                        notifyDataSetChanged();
                                        MyGlobal.showToastShort(
                                                activity,
                                                activity.getString(R.string.returned));
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
            }
        });

        // onLongClick - delete from trash permanently
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert_builder = new AlertDialog.Builder(activity);
                alert_builder
                        .setMessage(activity.getString(R.string.deleteFromTrash))
                        .setCancelable(true)
                        .setPositiveButton(
                                activity.getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        trashDAO.delete(trashNote);
                                        idCountDAO.delete(trashNote.getId());
                                        trashNotes.remove(trashNote);

                                        notifyDataSetChanged();
                                        MyGlobal.showToastShort(
                                                activity,
                                                activity.getString(R.string.deleted));
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

        return convertView;
    }
}

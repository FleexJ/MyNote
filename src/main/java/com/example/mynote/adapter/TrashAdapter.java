package com.example.mynote.adapter;

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
import com.example.mynote.activity.TrashActivity;
import com.example.mynote.dao.IdCountDAO;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.dao.TrashDAO;
import com.example.mynote.entity.Note;
import com.example.mynote.entity.Timer;
import com.example.mynote.entity.TrashNote;
import com.example.mynote.entity.TypeRepeat;
import com.example.mynote.globalVar.MyGlobal;

import org.w3c.dom.Text;

import java.util.List;

public class TrashAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<TrashNote> trashNotes;

    TrashDAO trashDAO;
    IdCountDAO idCountDAO;
    NotesDAO notesDAO;
    TimersDAO timersDAO;

    public TrashAdapter(Context context, List<TrashNote> trashNotes, SQLiteDatabase db) {
        trashDAO = new TrashDAO(db);
        idCountDAO = new IdCountDAO(db);
        notesDAO = new NotesDAO(db);
        timersDAO = new TimersDAO(db);

        this.context = context;
        this.trashNotes = trashNotes;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                        context.getString(R.string.noteBottom,
                                MyGlobal.sdfDate.format(
                                        trashNote.getDelayCalendar().getTime()),
                                trashNote.getRepeat().getString(context))
                );
                break;

            case MyGlobal.TYPE_TIMER:
                textView_delay.setText(
                        context.getString(R.string.timerProgress, trashNote.getDelay())
                );
                break;
        }


        convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);
                        alert_builder.setMessage(context.getString(R.string.returnFromTrash))
                                .setCancelable(true)
                                .setPositiveButton(
                                        context.getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                trashDAO.deleteTrash(trashNote);
                                                trashNotes.remove(trashNote);

                                                switch (trashNote.getType()) {
                                                    case MyGlobal.TYPE_NOTE:
                                                        notesDAO.insertNote(new Note(
                                                                trashNote.getId(),
                                                                trashNote.getName(),
                                                                trashNote.getDescription(),
                                                                Note.NOT_ACTIVE_STATE,
                                                                trashNote.getDelay(),
                                                                trashNote.getRepeat())
                                                        );
                                                        break;

                                                    case MyGlobal.TYPE_TIMER:
                                                        timersDAO.insertTimer(new Timer(
                                                                trashNote.getId(),
                                                                trashNote.getName(),
                                                                Timer.NOT_ACTIVE_STATE,
                                                                (int) trashNote.getDelay())
                                                        );
                                                        break;
                                                }

                                                notifyDataSetChanged();
                                                MyGlobal.showToastShort(
                                                        context,
                                                        context.getString(R.string.returned));
                                                dialog.cancel();
                                            }
                                        })
                                .setNegativeButton(
                                        context.getString(R.string.cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                        alert_builder.show();
                    }
                });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);
                        alert_builder
                                .setMessage(context.getString(R.string.deleteFromTrash))
                                .setCancelable(true)
                                .setPositiveButton(
                                        context.getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                trashDAO.deleteTrash(trashNote);
                                                idCountDAO.deleteId(trashNote.getId());
                                                trashNotes.remove(trashNote);

                                                notifyDataSetChanged();
                                                MyGlobal.showToastShort(
                                                        context,
                                                        context.getString(R.string.deleted));
                                                dialog.cancel();
                                            }
                                        })
                                .setNegativeButton(
                                        context.getString(R.string.cancel),
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

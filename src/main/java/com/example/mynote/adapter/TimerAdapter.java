package com.example.mynote.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.mynote.R;
import com.example.mynote.activity.MainActivity;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.dao.TrashDAO;
import com.example.mynote.entity.Timer;
import com.example.mynote.entity.TrashNote;
import com.example.mynote.entity.TypeRepeat;
import com.example.mynote.globalVar.MyGlobal;
import com.example.mynote.receiver.TimerReceiver;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TimerAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<Timer> timers;

    TimersDAO timersDAO;
    TrashDAO trashDAO;

    public TimerAdapter(Context context, List<Timer> timers, SQLiteDatabase db) {
        timersDAO = new TimersDAO(db);
        trashDAO = new TrashDAO(db);

        this.context = context;
        this.timers = timers;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return timers.size();
    }

    @Override
    public Object getItem(int position) {
        return timers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Timer timer = timers.get(position);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.element_list_timer , parent, false);

        final TextView textView_name = convertView.findViewById(R.id.textView_timerName);
        textView_name.setText(timer.getName());

        final Switch switch_state = convertView.findViewById(R.id.switch_timerState);
        if (timer.getState() == Timer.ACTIVE_STATE)
            switch_state.setChecked(true);
        else
            switch_state.setChecked(false);

        final TextView textView_progress = convertView.findViewById(R.id.textView_progress);
        textView_progress.setText(
                context.getString(R.string.timerProgress, timer.getMinute())
        );

        final SeekBar seekBar_progress = convertView.findViewById(R.id.seekBar_progress);
        seekBar_progress.setProgress(timer.getMinute());

        // onClick - edit timer
        convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater li = LayoutInflater.from(context);
                        View promptsView = li.inflate(R.layout.prompt_timer_name, null);

                        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                        mDialogBuilder.setView(promptsView);

                        final EditText userInput = promptsView.findViewById(R.id.editText_name);
                        userInput.setText(timer.getName());

                        mDialogBuilder.setCancelable(true)
                                .setPositiveButton(
                                        context.getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(!userInput.getText().toString().isEmpty()) {
                                                    timer.setName(userInput.getText().toString());
                                                    timersDAO.edit(timer);
                                                    textView_name.setText(userInput.getText().toString());
                                                }
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
                        AlertDialog alertDialog = mDialogBuilder.create();
                        userInput.setSelection(userInput.getText().length());
                        alertDialog.show();
                    }
                });

        // onLongClick - delete timer
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);
                        alert_builder
                                .setMessage(context.getString(R.string.dialogueTitleDeleteTimer))
                                .setCancelable(true)
                                .setPositiveButton(
                                        context.getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                TimerReceiver.cancelAlarmTimer(context, timer.getId());
                                                TimerReceiver.cancelNotifProgressTimer(context, timer);

                                                timersDAO.delete(timer);
                                                timers.remove(timer);
                                                trashDAO.insert(
                                                        new TrashNote(
                                                                timer.getId(),
                                                                timer.getName(),
                                                                "",
                                                                timer.getMinute(),
                                                                TypeRepeat.NO,
                                                                MyGlobal.TYPE_TIMER)
                                                );
                                                notifyDataSetChanged();
                                                MyGlobal.showToastShort(
                                                        context,
                                                        context.getString(R.string.timerDeleted));
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

        // switch state - change state timer
        switch_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(buttonView.isChecked()) {
                            TimerReceiver.startAlarmTimer(context, timer);
                            TimerReceiver.showNotifProgressTimer(context, timer);

                            timer.setState(Timer.ACTIVE_STATE);
                            timersDAO.edit(timer);
                            MyGlobal.showToastShort(
                                    context,
                                    context.getString(R.string.timerStarted, timer.getMinute())
                            );
                        }
                        else {
                            timer.setState(Timer.NOT_ACTIVE_STATE);
                            timersDAO.edit(timer);
                            TimerReceiver.cancelAlarmTimer(context, timer.getId());
                            TimerReceiver.cancelNotifProgressTimer(context, timer);
                        }
                    }
                });

        // change seekBar progress - edit progress timer
        seekBar_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textView_progress.setText(
                                context.getString(R.string.timerProgress, progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if(seekBar.getProgress() <= 0)
                            seekBar.setProgress(1);
                        timer.setMinute(seekBar.getProgress());
                        timersDAO.edit(timer);

                        if (timer.getState() == Timer.ACTIVE_STATE) {
                            TimerReceiver.startAlarmTimer(context, timer);
                            TimerReceiver.showNotifProgressTimer(context, timer);
                        }
                    }
                });

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}

package com.example.mynote.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mynote.R;
import com.example.mynote.adapter.TrashAdapter;
import com.example.mynote.dao.DatabaseHelper;
import com.example.mynote.dao.IdCountDAO;
import com.example.mynote.dao.NotesDAO;
import com.example.mynote.dao.TimersDAO;
import com.example.mynote.dao.TrashDAO;
import com.example.mynote.entity.TrashNote;
import com.example.mynote.globalVar.MyGlobal;
import com.example.mynote.swipeListener.TrashSwipeListener;

import java.util.List;

public class TrashActivity extends Activity {

    private SQLiteDatabase db;
    private TrashDAO trashDAO;
    private NotesDAO notesDAO;
    private TimersDAO timersDAO;
    private IdCountDAO idCountDAO;

    private List<TrashNote> trashNotes;
    private TrashAdapter trashAdapter;
    private ListView listView_trash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getWritableDatabase();
        trashDAO = new TrashDAO(db);
        notesDAO = new NotesDAO(db);
        timersDAO = new TimersDAO(db);
        idCountDAO = new IdCountDAO(db);

        listView_trash = findViewById(R.id.listView_trash);
        View emptyTrash = findViewById(R.id.layout_emptyTrash);
        listView_trash.setEmptyView(emptyTrash);

        emptyTrash.setOnTouchListener(new TrashSwipeListener(this));
        listView_trash.setOnTouchListener(new TrashSwipeListener(this));

        initTrash();
    }

    @Override
    public void onDestroy() {
        db.close();
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
                            getString(R.string.clear),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    trashDAO.deleteAllTrash();
                                    MyGlobal.showToastShort(
                                            getApplicationContext(),
                                            getString(R.string.cleared));
                                    dialog.cancel();
                                    initTrash();
                                }
                            })
                    .setNegativeButton(
                            getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            alert_builder.show();
        }
        else MyGlobal.showToastShort(
                getApplicationContext(),
                getString(R.string.emptyTrashYet));
    }

    public void initTrash() {
        trashAdapter = new TrashAdapter(
                this,
                trashDAO.getAllTrash(),
                db
        );
        listView_trash.setAdapter(trashAdapter);
    }
}

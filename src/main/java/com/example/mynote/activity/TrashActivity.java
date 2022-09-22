package com.example.mynote.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.mynote.R;
import com.example.mynote.adapter.TrashAdapter;
import com.example.mynote.dao.TrashDAO;
import com.example.mynote.entity.TrashNote;
import com.example.mynote.globalVar.MyGlobal;
import com.example.mynote.swipeListener.TrashSwipeListener;

import java.util.List;

public class TrashActivity extends Activity {

    private TrashDAO trashDAO;

    private TrashAdapter trashAdapter;
    private ListView listView_trash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        trashDAO = new TrashDAO(getApplicationContext());

        listView_trash = findViewById(R.id.listView_trash);
        View emptyTrash = findViewById(R.id.layout_emptyTrash);
        listView_trash.setEmptyView(emptyTrash);

        emptyTrash.setOnTouchListener(new TrashSwipeListener(this));
        listView_trash.setOnTouchListener(new TrashSwipeListener(this));

        initTrash();
    }

    private void initTrash() {
        trashAdapter = new TrashAdapter(this, trashDAO.getAll());
        listView_trash.setAdapter(trashAdapter);
    }

    //Закрытие активити корзины
    public void click_back(View view) {
        finish();
    }

    //Диалог удаления всех записей из корзины
    public void click_deleteAll(View view) {
        List<TrashNote> trashNoteList = trashDAO.getAll();
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);

        if (trashNoteList.size() == 0) {
            MyGlobal.showToastShort(
                    getApplicationContext(),
                    getString(R.string.emptyTrashYet)
            );
            return;
        }

        alert_builder
                .setMessage(getString(R.string.dialogueClearTrash))
                .setCancelable(true)
                .setPositiveButton(
                        getString(R.string.clear),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                trashDAO.deleteAll();
                                MyGlobal.showToastShort(
                                        TrashActivity.this,
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
}

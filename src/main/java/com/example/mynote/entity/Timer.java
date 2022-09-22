package com.example.mynote.entity;

import android.content.Context;

import com.example.mynote.R;
import com.example.mynote.globalVar.MyGlobal;

public class Timer implements GetInfoForNotifInterface {
    public static final int NOT_ACTIVE_STATE = 0;
    public static final int ACTIVE_STATE = 1;


    private int id;
    private String name;
    private int state;
    private int minute;

    public Timer(int id, String name, int state, int minute) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.minute = minute;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.notifTimerDoneTitle);
    }

    @Override
    public String getContent() {
        return name;
    }

    @Override
    public int getType() {
        return MyGlobal.TYPE_TIMER;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}

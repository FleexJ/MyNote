package com.example.mynote.entity;

public class Timer {
    public static final int NOT_ACTIVE_STATE = 0;
    public static final int ACTIVE_STATE = 1;

    public static final int MAX_PROGRESS = 60;

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

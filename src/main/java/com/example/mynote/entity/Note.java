package com.example.mynote.entity;

import java.util.Calendar;

public class Note {
    private int id;
    private String name;
    private String description;
    private int state;
    private long delay;
    private TypeRepeat repeat;

    public Note(int id, String name, String description, int state, long delay, TypeRepeat repeat) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.state = state;
        this.delay = delay;
        this.repeat = repeat;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getState() {
        return state;
    }


    public long getDelay() {
        return delay;
    }

    public TypeRepeat getRepeat() {
        return repeat;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setRepeat(TypeRepeat repeat) {
        this.repeat = repeat;
    }

    public Calendar getDelayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(delay);
        return calendar;
    }
}

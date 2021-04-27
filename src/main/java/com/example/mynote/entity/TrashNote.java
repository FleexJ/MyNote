package com.example.mynote.entity;

import java.util.Calendar;

public class TrashNote {
    private int id;
    private String name;
    private String description;
    private long delay;
    private int type;

    public TrashNote(int id, String name, String description, long delay, int type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.delay = delay;
        this.type = type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Calendar getDelayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(delay);
        return calendar;
    }
}

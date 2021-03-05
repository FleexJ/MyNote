package com.example.mynote.entity;

import java.util.Calendar;

public class Notes {
    private int id;
    private String name;
    private String description;
    private int state;
    private String delay;
    private String repeat;

    public Notes(int id, String name, String description, int state, String delay, String repeat) {
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


    public String getDelay() {
        return delay;
    }

    public String getRepeat() {
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

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public Calendar getDelayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(delay));
        return calendar;
    }
}

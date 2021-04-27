package com.example.mynote.globalVar;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyGlobal {
    //Типа записей
    public static final int TYPE_NOTE = 1;
    public static final int TYPE_TIMER = 2;

    //ОБъекты для работы с форматом даты
    public static Locale locale = Locale.getDefault();
    public static SimpleDateFormat sdfCal = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", locale);
    public static SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy  HH:mm", locale);
}

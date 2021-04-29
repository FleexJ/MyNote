package com.example.mynote.entity;

import android.content.Context;

import com.example.mynote.R;

//Количество вариантов и порядок должны СТРОГО совпадать с массивом строк repeat_array
public enum TypeRepeat {
    NO,
    HOUR,
    DAY,
    WEEK,
    MONTH,
    YEAR;

    //Возвращает локализированную строку типа в соответствии с массивом строковых ресурсов repeat_array
    public String getString(Context context) {
        String[] noteRepeat = context.getResources().getStringArray(R.array.repeat_array);
        if (TypeRepeat.values().length == noteRepeat.length) {
            return noteRepeat[this.getId()];
        }
        return this.name();
    }

    //Возвращает id текущего элемента в TypeRepeat
    public int getId() {
        TypeRepeat[] repeats = TypeRepeat.values();

        for (int i = 0; i < repeats.length; i++)
            if (repeats[i].equals(this))
                return i;

        return 0;
    }
}

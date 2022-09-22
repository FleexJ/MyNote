package com.example.mynote.entity;

import android.content.Context;

public interface GetInfoForNotifInterface {
    int getId();
    String getTitle(Context context);
    String getContent();
    int getType();
}

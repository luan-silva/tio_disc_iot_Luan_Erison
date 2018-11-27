package com.example.erisonmiller.opengeo2;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;

public interface Sensor {

    public boolean getState(DatabaseReference ctx);
    public void setState();
    public String getType();
    public String toJson() throws JSONException;

    public String toStringSensor();
}

package com.example.erisonmiller.opengeo2;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class SensorTime implements Sensor {

    int hora, minutos;
    SensorTime(int h, int m){
        hora = h;
        minutos = m;
    }


    @Override
    public boolean getState(DatabaseReference mDatabase) {
        Calendar mcurrentTime = Calendar.getInstance();
        if(mcurrentTime.get(Calendar.HOUR_OF_DAY)==hora &&
                mcurrentTime.get(Calendar.MINUTE)==minutos){
            return true;
        }
        return false;
    }

    @Override
    public void setState() {

    }

    @Override
    public String getType() {
        return "time";
    }

    @Override
    public String toJson() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("type", getType());
        js.put("hora", hora);
        js.put("minutos", minutos);
        return js.toString();
    }

    @Override
    public String toStringSensor(){
        return hora + ":" + minutos;
    }
}

package com.example.erisonmiller.opengeo2;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

public class SensorDias implements Sensor {
    boolean[] dias = new boolean[7];

    SensorDias(boolean[] dias){
        this.dias = dias;
    }



    @Override
    public boolean getState(DatabaseReference mDatabase) {
        return false;
    }

    @Override
    public void setState() {

    }

    public void setState(boolean state, int dia) {
        dias[dia] = state;
    }

    @Override
    public String getType() {
        return "dias_semana";
    }

    @Override
    public String toJson() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("type", getType());
        js.put("d0", dias[0]);
        js.put("d1", dias[1]);
        js.put("d2", dias[2]);
        js.put("d3", dias[3]);
        js.put("d4", dias[4]);
        js.put("d5", dias[5]);
        js.put("d6", dias[6]);
        return js.toString();
    }

    @Override
    public String toStringSensor(){
        return "sla";
    }
}

package com.example.erisonmiller.opengeo2;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class SensorData implements Sensor {

    int dia, mes, ano;
    SensorData(){
        dia = 1;
        mes = 0;
        ano = 2019;
    }
    SensorData(int dia, int mes, int ano){
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
    }


    @Override
    public boolean getState(DatabaseReference mDatabase) {
        Calendar mcurrentTime = Calendar.getInstance();
        if(mcurrentTime.get(Calendar.DAY_OF_MONTH) == dia &&
                mcurrentTime.get(Calendar.MONTH) == mes &&
                mcurrentTime.get(Calendar.YEAR) == ano){
            return true;
        }
        return false;
    }

    @Override
    public void setState() {

    }

    @Override
    public String getType() {
        return "data";
    }

    @Override
    public String toJson() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("type", getType());
        js.put("dia", dia);
        js.put("mes", mes);
        js.put("ano", ano);
        return js.toString();
    }

    @Override
    public String toStringSensor(){
        return dia + "/" + (mes+1) + "/" + ano;
    }
}

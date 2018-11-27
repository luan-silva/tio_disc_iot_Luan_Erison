package com.example.erisonmiller.opengeo2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlarmList {
    private static AlarmList ourInstance;

    private ArrayList<Alarm> alarmes = new ArrayList<>();

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    AlarmList(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("OPENGEOSENSOR", Context.MODE_PRIVATE);
        editor = prefs.edit();

        String alarms = prefs.getString("alarmes", "erro");
        alarmes = StartList(alarms);
    }

    public static AlarmList getInstance(Context ctx) {
        if(ourInstance==null){
            ourInstance = new AlarmList(ctx);
        }
        return ourInstance;
    }

    private void PutList(){
        String json = "";
        for(int i=0, size=alarmes.size(); i<size; i++){
            try {
                if(alarmes.get(i)!= null) {
                    json += alarmes.get(i).toJson();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putString("alarmes",json);
        editor.commit();
    }

    private ArrayList<Alarm> StartList(String s){

        try {

            int i, f;
            JSONObject jo;
            ArrayList<Alarm> arrayList = new ArrayList<>();
            Log.d("MyApp","I am here "+s);
            int lat, lon;
            String name, place;
            boolean state;
            while (s.length() > 5){
                String json = "", type="";
                //pega o alarme
                i = s.indexOf("[");
                f = s.indexOf("]")+1;
                json = s.substring(i,f);
                //pega o nome
                i = json.indexOf("{");
                f = json.indexOf("}")+1;
                jo = new JSONObject(json.substring(i,f));
                lat = jo.getInt("lat");
                lon = jo.getInt("lon");
                state = jo.getBoolean("activated");
                name = jo.getString("name");
                place = jo.getString("place");
                //name = json.substring(i+1,f-1);

                //pega a lista de sensores
                json = json.substring(f);

                ArrayList<Sensor> sensores = new ArrayList<>();
                while (json.length() > 8) {
                    i = json.indexOf("{");
                    f = json.indexOf("}") + 1;

                    jo = new JSONObject(json.substring(i, f));
                    type = jo.getString("type");
                    if(type.equals("time")){
                        sensores.add(new SensorTime(
                                jo.getInt("hora"),
                                jo.getInt("minutos")));
                    }
                    if(type.equals("temperatura")){
                        sensores.add(new SensorTemperatura(
                                (float)jo.getDouble("temp"),
                                jo.getInt("comp")));
                    }
                    if(type.equals("luz")){
                        sensores.add(new SensorLuz(
                                jo.getBoolean("state")));
                    }
                    if(type.equals("dias_semana")){
                        SensorDias sd = new SensorDias(new boolean[7]);
                        for(int dia=0;dia<7;dia++){
                            sd.setState(jo.getBoolean("d"+Integer.toString(dia)),dia);
                        }
                        sensores.add(sd);
                    }
                    if(f>json.length())f=json.length();
                    json = json.substring(f);
                }
                arrayList.add(new Alarm(name, sensores, lat, lon, place, state));
                f = s.indexOf("]")+1;
                s = s.substring(f);
            }
            return arrayList;

            //Toast.makeText(, jsonObject.getString("reposta"), Toast.LENGTH_LONG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Alarm> getAlarmes(){
        return alarmes;
    }
    public void setAlarmes(ArrayList<Alarm> al){
        alarmes = al;
        PutList();
    }

}

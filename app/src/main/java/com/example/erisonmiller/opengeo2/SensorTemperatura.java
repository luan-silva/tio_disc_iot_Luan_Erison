package com.example.erisonmiller.opengeo2;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class SensorTemperatura implements Sensor{

    String type;
    float temperatura;
    int comparator;
    boolean state;
    SensorTemperatura(String type){
        this.type = type;
    }
    SensorTemperatura(float temp){
        temperatura = temp;
    }
    SensorTemperatura(float temp, int comp){
        temperatura = temp;comparator = comp;
    }
    @Override
    public boolean getState(DatabaseReference mDatabase) {

        ///DatabaseReference mDatabase;

        mDatabase.child(getType()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MyApp","recebi " +dataSnapshot.toString());
                float temp = dataSnapshot.getValue(float.class);

                Log.d("MyApp","recebi " + temp +" " + state);
                if(comparator==0)state = temperatura<temp;
                if(comparator==1)state = temperatura>temp;
                if(comparator==2)state = temperatura==temp;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MyApp","desconectei sla men ");
            }
        });


        try {
            Thread.sleep(500);
        } catch (Exception e) {

        }


        return state;
    }

    public boolean getState(float temp) {
        if(comparator==0)return temperatura>temp;
        if(comparator==1)return temperatura<temp;
        if(comparator==2)return temperatura==temp;
        return false;
    }

    @Override
    public void setState() {

    }

    public void setTemperatura(float temp) {
        temperatura = temp;
    }
    public void setComparator(int comp) {
        comparator = comp;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String toJson() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("type", getType());
        js.put("temp", temperatura);
        js.put("comp", comparator);
        return js.toString();
    }

    @Override
    public String toStringSensor(){
        if(comparator == 0){return "Temp > "+temperatura;}
        else {
            if (comparator == 1){return "Temp < "+temperatura;}
            else{return "Temp = "+temperatura;}
        }
    }
}

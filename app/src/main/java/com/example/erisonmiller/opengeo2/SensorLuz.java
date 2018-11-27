package com.example.erisonmiller.opengeo2;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class SensorLuz implements Sensor {
    boolean state, isSet;
    boolean ligado = false;
    SensorLuz(boolean state){
        this.state = state;
    }


    @Override
    public boolean getState(DatabaseReference mDatabase) {
        if(isSet)return true;
        /*try {
            //float sensTemp;
            state = false;
            String clientId = MqttClient.generateClientId();
            MqttAndroidClient client =
                    new MqttAndroidClient(ctx, "tcp://demo.thingsboard.io:1883",
                            clientId);



            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("CbfSzKY8kikCQFjvl1qt");
            //options.setPassword("".toCharArray());
            Log.d("MyApp","I am here num sei");

            //client.connect(options);
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("MyApp", "onSuccess");
                    state = true;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("MyApp", "onFailure");
                    state = false;
                }
            });
            Log.d("MyApp","foi aqui ");
            String topic = "v1/devices/me/rpc/request/+";

            try {
                Thread.sleep(2000);
            } catch (Exception e) {

            }
            if(!state)return state;
            state = false;


            IMqttToken subToken = client.subscribe(topic,0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MyApp","inscritoooooooooooooo carai");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MyApp","ñ inscrito");
                }
            });

            //client.publish('v1/devices/me/rpc/request/1', JSONObject(request));


            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable throwable) {
                    Log.d("MyApp","ñ inscrito");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d("MyApp","recebi "+ message.toString());
                    JSONObject obj = new JSONObject(message.toString());

                    state = obj.getBoolean("params");
                    Log.d("MyApp","recebi "+ message.toString() +" " + state);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MyApp","carai ");
                }
            });

            try {
                Thread.sleep(6000);
            } catch (Exception e) {

            }
            //client.unsubscribe(topic);

            Log.d("MyApp","I am here num sei 2");
            client.disconnect();

            Log.d("MyApp","I am here num sei 3");
        } catch (MqttException e) {
            e.printStackTrace();
        }
*/
        mDatabase.child(getType()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MyApp","recebi " +dataSnapshot.toString());
                boolean bool = dataSnapshot.getValue(boolean.class);

                //Log.d("MyApp","recebi " + temp +" " + state);
                ligado = (state==bool);

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


        return ligado;
    }

    @Override
    public void setState() {

    }

    public void setState(DatabaseReference mDatabase){
        mDatabase.child(getType()).setValue(state);
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public String getType() {
        return "luz";
    }

    @Override
    public String toJson() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("type", getType());
        js.put("state", state);
        return js.toString();
    }


    @Override
    public String toStringSensor(){
        if(state)return "If light is ON";
        return "If light is OFF";
    }
}

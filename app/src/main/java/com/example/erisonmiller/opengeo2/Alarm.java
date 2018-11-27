package com.example.erisonmiller.opengeo2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Alarm {

    ArrayList<Sensor> data = new ArrayList<>();
    String name;
    boolean activated = true;

    int lat=0, lon=0;
    String place="myPlace";

    Alarm(String name, ArrayList<Sensor> data){
        this.name = name;
        this.data = data;
    }

    Alarm(String name, ArrayList<Sensor> data, int lat, int lon, String place, boolean activated){
        this.name = name;
        this.data = data;
        this.lat = lat;
        this.lon = lon;
        this.place = place;
        this.activated = activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
        Log.d("MyApp","mudou " + activated);
    }


    public boolean checkState(Context ctx){
        if(!activated)return false;

        DatabaseReference mDatabase;
        if(place.equals("") || place.isEmpty())return false;
        if(place.equals("myPlace")){
            Location location = myPosition(ctx);

            mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://opengeoiot.firebaseio.com/sensors/" +
                    (int)(location.getLatitude()*1000) + "/" + (int)(location.getLongitude()*1000) + "/public");
        }else {
            mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://opengeoiot.firebaseio.com/sensors/" +
                    lat + "/" + lon + "/" + place);
        }

        for(int i=0, size=data.size(); i<size; i++){
            Log.d("MyApp","testando "+i);
            if(!data.get(i).getState(mDatabase))return false;
        }
        for(int i=0, size=data.size(); i<size; i++){
            Log.d("MyApp","testando "+i);
            if(data.get(i).getType()=="luz"){
                SensorLuz sl = (SensorLuz)data.get(i);
                if(sl.isSet){
                    sl.setState(mDatabase);
                }
            }
        }

        return true;
    }

    public String getName(){
        return name;
    }

    /**
     * Usa o gps para saber a posição atual do usuario
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public Location myPosition(Context ctx){
        if (ctx.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ctx.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Location location = new Location("dummyprovider");
            location.setLatitude(0);
            location.setLongitude(0);
            return location;
            //requestPermissions(new String[]{
            //        "Manifest.permission.ACCESS_COARSE_LOCATION",
            //        "Manifest.permission.ACCESS_FINE_LOCATION"
            //}, 1);
        } else {

            final LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            final android.location.LocationListener locationListener;

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                System.err.println("É não tão ligados não");
            }

            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if (location == null) {

                System.out.println("Ta nulo aqui");
                locationListener = new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(final Location location) {

                        // getting location of user
                        final double latitude = location.getLatitude();
                        final double longitude = location.getLongitude();
                        //do something with Lat and Lng
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                System.out.println("Hmmmm");

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 500, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 500, locationListener);
                location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

                if (locationManager == null) {System.err.println("pqp tá o manager ta null tbm");}
                if(location==null){
                    System.err.println("pqp tá null dnv");
                }else{return location;}
            }else {
                System.out.println("Deu");
                return location;
            }
        }

        Location location = new Location("dummyprovider");
        location.setLatitude(0);
        location.setLongitude(0);
        return location;
    }

    public String toJson() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("name", name);
        js.put("lat", lat);
        js.put("lon", lon);
        js.put("place", place);
        js.put("activated", activated);
        String json = "["+js.toString()+",";
        for(int i=0, size=data.size(); i<size; i++){
            json+= data.get(i).toJson()+",";
        }
        json+="]";
        return json;
    }
}

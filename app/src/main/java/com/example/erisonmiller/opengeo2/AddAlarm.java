package com.example.erisonmiller.opengeo2;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class AddAlarm extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "AddAlarm";

    static GoogleMap mMap;
    MapView mapView;
    static Location myLocationStatic;


    private RecyclerView recycler;
    private static SensorsViewAdapter adapter;
    ArrayList<Sensor> data = new ArrayList<>();
    double Lat, Lon, alarmLat, alarmLon;
    String local;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alarm);

        data.add(new SensorTime(10,20));

        recycler = (RecyclerView) findViewById(R.id.sensor_recycler);
        recycler.setNestedScrollingEnabled(false);
        //past the data to the adapter
        adapter = new SensorsViewAdapter(this, data);
        //set the recycler adapter
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        ListView lv = (ListView)findViewById(R.id.sensor_list);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type_sensor = (String) parent.getItemAtPosition(position);


                if(type_sensor.equals("Hora")){
                    data.add(new SensorTime(0,0));
                    adapter.notifyItemInserted(data.size()-1);
                }
                if(type_sensor.equals("Dias da semana")){
                    data.add(new SensorDias(new boolean[7]));
                    adapter.notifyItemInserted(data.size()-1);
                }
                if(type_sensor.equals("Data")){
                    data.add(new SensorData());
                    adapter.notifyItemInserted(data.size()-1);
                }
                if(type_sensor.equals("Luz") || type_sensor.equals("Porta") || type_sensor.equals("Set luz")){
                    data.add(new SensorLuz(false));
                    if(type_sensor.equals("Set luz")){((SensorLuz)data.get(data.size()-1)).isSet = true;}
                    adapter.notifyItemInserted(data.size()-1);
                }
                if(type_sensor.equals("Temperatura") || type_sensor.equals("Humidade")){
                    if(type_sensor.equals("Temperatura")){
                        data.add(new SensorTemperatura("temperatura"));
                    }
                    if(type_sensor.equals("Humidade")){
                        data.add(new SensorTemperatura("humidade"));
                    }
                    adapter.notifyItemInserted(data.size()-1);
                }
                findViewById(R.id.sensors_card).setVisibility(View.GONE);
            }
        });

        adapter.setOnItemClickListener(new SensorsViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                data.remove(position);
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onItemUpdate(int position, Sensor s) {
                data.set(position, s);
                adapter.notifyItemChanged(position);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                findViewById(R.id.sensors_card).setVisibility(View.VISIBLE);

            }
        });

        findViewById(R.id.sensors_card).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                findViewById(R.id.sensors_card).setVisibility(View.GONE);

            }
        });

        findViewById(R.id.save_alarm).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ArrayList<Alarm> alarms = AlarmList.getInstance(AddAlarm.this).getAlarmes();
                Log.d("MyApp2","I am here "+alarms.size());

                TextView txt = (TextView) findViewById(R.id.alarm_title);

                CheckBox check = (CheckBox)findViewById(R.id.useMyPosition);
                if(check.isChecked())local = "myPlace";

                alarms.add(new Alarm(txt.getText().toString(),data, (int)(alarmLat*1000), (int)(alarmLon*1000), local, true));
                AlarmList.getInstance(AddAlarm.this).setAlarmes(alarms);

                Intent intent = new Intent(AddAlarm.this, MainActivity.class);
                startActivity(intent);

            }
        });

        findViewById(R.id.SelectPlace).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Lon = alarmLon;
                Lat = alarmLat;
                findViewById(R.id.place).setVisibility(View.VISIBLE);

            }
        });


        findViewById(R.id.save_place).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alarmLat = Lat;
                alarmLon = Lon;
                mMap.clear();
                MarkerOptions mOptions = new MarkerOptions()
                        .title("Lat : " + String.format("%.3f", alarmLat) + " , "
                                + "Long : " + String.format("%.3f", alarmLon))
                        .position(new LatLng(alarmLat,alarmLon));
                mMap.addMarker(mOptions);
                local = ((TextView)findViewById(R.id.txtLocal)).getText().toString() +
                        ((TextView)findViewById(R.id.txtSenha)).getText().toString();
                findViewById(R.id.place).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.place).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                findViewById(R.id.place).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.cancel_place).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                findViewById(R.id.place).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.cancel_place).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                findViewById(R.id.place).setVisibility(View.GONE);
            }
        });

        ((CheckBox)findViewById(R.id.useMyPosition)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked){
                    findViewById(R.id.getLocal).setVisibility(View.INVISIBLE);
                }else{
                    findViewById(R.id.getLocal).setVisibility(View.VISIBLE);
                }
            }
        });


        // ______________________ map
        mapView = (MapView) findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    /**
     * Função para após ter acesso ao servidor de mapas do google
     * @param googleMap
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this);

        mMap = googleMap;

        Log.d("MyApp","teve acesso eu acho");
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        Log.d("MyApp","foi mapa ");

        GoToLocationZoom(-3.741625, -38.564147, 12);

        myLocation();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                MarkerOptions mOptions = new MarkerOptions()
                        .title("Lat : " + String.format("%.3f", latLng.latitude) + " , "
                                + "Long : " + String.format("%.3f", latLng.longitude))
                        .position(new LatLng(latLng.latitude,latLng.longitude));
                mMap.addMarker(mOptions);
                ((TextView)findViewById(R.id.local)).setText("Local: " + String.format("%.3f", latLng.latitude)
                        + ", " + String.format("%.3f", latLng.longitude));
                Lat = latLng.latitude;
                Lon = latLng.longitude;
            }
        });
    }


    /**
     * Aredonda a posição atual do usuario
     * @return
     */
    public Location myRoundPosition(){
        Location location = myPosition();
        double val = (Math.round(location.getLatitude()*1000))/1000D;
        location.setLatitude(val);
        location.setLongitude((Math.round(location.getLongitude()*1000))/1000D);
        return location;
    }

    /**
     * Usa o gps para saber a posição atual do usuario
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public Location myPosition(){
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    "Manifest.permission.ACCESS_COARSE_LOCATION",
                    "Manifest.permission.ACCESS_FINE_LOCATION"
            }, 1);
        } else {

            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

    /**
     * Vai para um determinado local no mapa
     * @param lat
     * @param lng
     * @param zoom
     */
    public static void GoToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));
    }

    /**
     * Vai para a localização do usuário
     *
     */
    public void myLocation() {

        Location location = myRoundPosition();

        try {
            GoToLocationZoom(location.getLatitude(), location.getLongitude(), 14);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}


class SensorsViewAdapter extends RecyclerView.Adapter<SensorsViewCard> {

    private LayoutInflater inflater;
    ArrayList<Sensor> data = new ArrayList<>();
    private Context context;
    OnItemClickListener mListener;

    interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onItemUpdate(int position, Sensor s);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public SensorsViewAdapter(Context context, ArrayList<Sensor> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public SensorsViewCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.add_sensor, parent, false);

        SensorsViewCard holder = new SensorsViewCard(view, mListener);

        //mListener.se();

        return holder;
    }

    @Override
    public void onBindViewHolder(SensorsViewCard holder, int position) {
        Sensor actual = data.get(position);

        holder.initi(actual);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}


/**
 * this class is to control the Vote card, he just start the voto with the corrects params, that he gets from the data
 */
class SensorsViewCard extends RecyclerView.ViewHolder {

    private View view;
    SensorsViewAdapter.OnItemClickListener ls;
    Sensor mySensor;

    public SensorsViewCard(@NonNull View itemView, final SensorsViewAdapter.OnItemClickListener listner) {
        super(itemView);
        view = itemView;
        ls = listner;
        view.findViewById(R.id.delete_sensor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listner!= null){
                    int position = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION){
                        listner.onDeleteClick(position);
                    }
                }
            }
        });
    }

    public void initi(Sensor s){
        mySensor = s;
        view.findViewById(R.id.time).setVisibility(View.GONE);
        view.findViewById(R.id.luz).setVisibility(View.GONE);
        view.findViewById(R.id.temperatura).setVisibility(View.GONE);
        view.findViewById(R.id.dias_semana).setVisibility(View.GONE);

        if(s.getType() == "luz"){
            SensorLuz sl = (SensorLuz)s;
            if(sl.isSet){
                ((TextView)view.findViewById(R.id.luzTxt)).setText("Set luz");
            }
            view.findViewById(R.id.luz).setVisibility(View.VISIBLE);
            ((CheckBox)view.findViewById(R.id.luz_check)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    ((SensorLuz)mySensor).setState(isChecked);
                }
            });
        }
        if(s.getType() == "temperatura" || s.getType() == "humidade"){
            if(s.getType() == "temperatura"){
                ((TextView)view.findViewById(R.id.typeText)).setText("Temperatura");
            }
            if(s.getType() == "humidade"){
                ((TextView)view.findViewById(R.id.typeText)).setText("Humidade");
            }

            view.findViewById(R.id.temperatura).setVisibility(View.VISIBLE);

            ((Spinner)view.findViewById(R.id.temp_type)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    ((SensorTemperatura)mySensor).setComparator(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            ((TextView)view.findViewById(R.id.temp_set)).addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    String ss = ((TextView)view.findViewById(R.id.temp_set)).getText().toString();
                    if(!ss.isEmpty()){
                    float temp = Float.valueOf(ss);
                    ((SensorTemperatura)mySensor).setTemperatura(temp);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }
            });

        }
        if(s.getType() == "time"){
            view.findViewById(R.id.time).setVisibility(View.VISIBLE);
            SensorTime st = (SensorTime) s;
            String time = "";
            if(st.hora<10)time+="0";
            time+=st.hora+":";
            if(st.minutos<10)time+="0";
            time+=st.minutos;
            ((TextView)view.findViewById(R.id.time_text)).setText(time);

            view.findViewById(R.id.time_text).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(view.getContext(),  AlertDialog.THEME_DEVICE_DEFAULT_DARK, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            mySensor = new SensorTime(selectedHour, selectedMinute);
                            if(ls!= null){
                                int position = getAdapterPosition();
                                if(position!= RecyclerView.NO_POSITION){
                                    ls.onItemUpdate(position, mySensor);
                                }
                            }
                        }
                    }, 0, 0, true);//Yes 24 hour time
                    //mTimePicker.setTitle("Select Time");
                    //mTimePicker.set

                    mTimePicker.show();
                }
            });

        }
        if(s.getType() == "dias_semana"){
            view.findViewById(R.id.dias_semana).setVisibility(View.VISIBLE);

            for(int i=0; i<7; i++) {
                final int dia = i;
                ((CheckBox) view.findViewById(R.id.d0+i)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ((SensorDias) mySensor).setState(isChecked, dia);
                    }
                });
            }
        }
        if(s.getType() == "data"){
            view.findViewById(R.id.time).setVisibility(View.VISIBLE);
            final TextView txt = (TextView)view.findViewById(R.id.time_text);
            txt.setText(s.toStringSensor());

            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    final Calendar calendar = Calendar.getInstance();
                    int yy = calendar.get(Calendar.YEAR);
                    int mm = calendar.get(Calendar.MONTH);
                    int dd = calendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePicker = new DatePickerDialog(view.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            txt.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                        }
                    }, yy, mm, dd);
                    //datePicker.context
                    datePicker.show();
                }
            });

        }
    }
}
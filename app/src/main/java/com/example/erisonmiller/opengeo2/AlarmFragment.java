package com.example.erisonmiller.opengeo2;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

public class AlarmFragment extends Fragment {
    private static final String TAG = "AlarmFragment";


    private static RecyclerView recycler;
    static private AlarmViewAdapter adapter;
    private static Context ctx;
    ArrayList<Alarm> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = (View) inflater.inflate(R.layout.alarm_fragment, container,false);
        ctx = getContext();

        recycler = (RecyclerView) view.findViewById(R.id.alarm_recycler);
        recycler.setNestedScrollingEnabled(false);
        //past the data to the adapter
        data = AlarmList.getInstance(ctx).getAlarmes();
        adapter = new AlarmViewAdapter(ctx, data);
        //set the recycler adapter
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(ctx));

        return view;
    }
}










class AlarmViewAdapter extends RecyclerView.Adapter<AlarmViewCard> {

    private LayoutInflater inflater;
    ArrayList<Alarm> data = new ArrayList<>();
    private Context context;

    public AlarmViewAdapter(Context context, ArrayList<Alarm> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public AlarmViewCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.alarm_item, parent, false);

        final AlarmViewCard holder = new AlarmViewCard(view);

        //mListener.se();


        view.findViewById(R.id.delete_alarm).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int posi = holder.getAdapterPosition();
                ArrayList<Alarm> alarms = AlarmList.getInstance(view.getContext()).getAlarmes();
                alarms.remove(posi);
                AlarmList.getInstance(view.getContext()).setAlarmes(alarms);
                notifyItemRemoved(posi);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(AlarmViewCard holder, int position) {
        Alarm actual = data.get(position);

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
class AlarmViewCard extends RecyclerView.ViewHolder {

    private View view;
    Alarm myAlarm;
    ImageButton ib;

    public AlarmViewCard(@NonNull View itemView) {
        super(itemView);
        view = itemView;

        ib = (ImageButton) view.findViewById(R.id.delete_alarm);
    }

    public void initi(Alarm a){
        myAlarm = a;
        TextView txt = (TextView) view.findViewById(R.id.alarm_titl);
        txt.setText(a.name);
        txt = (TextView) view.findViewById(R.id.alarm_time);
        txt.setText(a.data.get(0).toStringSensor());

        Switch sw = (Switch) view.findViewById(R.id.activated);
        sw.setChecked(a.activated);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myAlarm.setActivated(isChecked);
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });

    }
}
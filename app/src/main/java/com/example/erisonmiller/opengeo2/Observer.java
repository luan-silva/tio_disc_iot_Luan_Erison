package com.example.erisonmiller.opengeo2;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

public class Observer extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public Observer(String name) {
        super(name);
    }
    public Observer() {
        super("observer_sensor");
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        while(true) {
            ArrayList<Alarm> alarmes = AlarmList.getInstance(getBaseContext()).getAlarmes();
            //Log.d("MyApp","I am here "+alarmes.size());
            for (int i = 0, size = alarmes.size(); i < size; i++) {

                if (alarmes.get(i).checkState(this)) {
                    NotificationCompat.Builder nb = new NotificationCompat.Builder(this);


                    nb.setContentText(alarmes.get(i).data.get(0).toStringSensor());

                    nb.setContentTitle(alarmes.get(i).getName());
                    nb.setSmallIcon(R.mipmap.ic_launcher_round);

                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    nm.notify(i, nb.build());

                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                    mp.start();

                    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(1000);
                }
            }

            try {
                Thread.sleep(5000);
            } catch (Exception e) {

            }
        }
    }
}

package com.cs160.joleary.catnip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class localPolitianWelcomePage extends WearableActivity  implements SensorEventListener {
    private static Context context;

    private SensorManager sensorManager;
    double ax,ay,az;   // these are the acceleration in x,y and z axis

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_politian_welcome_page);

        setAmbientEnabled();

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        Intent thisIntent = getIntent();
        int messageSenderBoolean = thisIntent.getIntExtra("SendMessageBoo", 0);

        Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
        sendIntent.putExtra("Message", "Start serives");
        startService(sendIntent);


        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Called when sensor values have changed.
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     * <p/>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            if (ax!=event.values[0] || ay!=event.values[1]){
                ax=event.values[0];
                ay=event.values[1];
                az=event.values[2];

                Log.d("myTag", "acce changed");

                Intent messageIntent = new Intent("custom-event-name");
                // You can also include some extra data.
                messageIntent.putExtra("ChannelWW", 1);
                messageIntent.putExtra("Message", 99);
                LocalBroadcastManager.getInstance(context).sendBroadcast(messageIntent);
            }
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                Bundle data = intent.getBundleExtra("datamap");
                // Display received data in UI
                Log.d("myTag", "Checking data transfer " + data.getString("Can1name"));
                Intent myIntent = new Intent(localPolitianWelcomePage.this, my2dPicker.class);
                myIntent.putExtra("name1", data.getString("Can1name"));
                myIntent.putExtra("party1", data.getString("Can1party"));
                myIntent.putExtra("index1", data.getInt("Index1"));
                myIntent.putExtra("name2", data.getString("Can2name"));
                myIntent.putExtra("party2", data.getString("Can2party"));
                myIntent.putExtra("index2", data.getInt("Index2"));
                myIntent.putExtra("name3", data.getString("Can3name"));
                myIntent.putExtra("party3", data.getString("Can3party"));
                myIntent.putExtra("index3", data.getInt("Index3"));
                localPolitianWelcomePage.this.startActivity(myIntent);
            } catch (NullPointerException nu) {
                Log.d("myTag", "I am at watch reciever. ");

                Bundle data = intent.getBundleExtra("datamap2");

                Intent myIntent = new Intent(localPolitianWelcomePage.this, election2012.class);
                myIntent.putExtra("obama", data.getString("obama"));
                myIntent.putExtra("rommy", data.getString("rommy"));
                myIntent.putExtra("zipCode", data.getString("zipCode"));

                localPolitianWelcomePage.this.startActivity(myIntent);
            }

        }
    }
}

package com.cs160.joleary.catnip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class localPolitianWelcomePage extends WearableActivity {
    private static Context context;

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

    }




    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
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
        }
    }
}

package com.cs160.joleary.catnip;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

public class election2012 extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private ImageButton returnBtn;
    private ImageButton returnlistBtn;
    private String thisZipCode;
    String oba;
    String rom;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    GoogleApiClient googleClient;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election2012);

        context = this.getApplicationContext();

        Intent intent = getIntent();
        thisZipCode = (intent.getStringExtra("zipCode"));

        oba = intent.getStringExtra("obamaPerc");
        rom = intent.getStringExtra("rommyPerc");

        returnBtn = (ImageButton) findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(election2012.this, welcomeMain.class);
                election2012.this.startActivity(myIntent);
            }
        });

        TextView obaView = (TextView)findViewById(R.id.obaper);
        obaView.setText(oba + " %");
        TextView romView = (TextView)findViewById(R.id.romper);
        romView.setText(rom + " %");

        // Build a new GoogleApiClient for the the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStart(){
        googleClient.connect();
        super.onStart();
    }

    @Override
    public void onConnected(Bundle bundle) {
        String WEARABLE_DATA_PATH = "/elect";

        // Create a DataMap object and send it to the data layer
        DataMap dataMap = new DataMap();
        dataMap.putLong("Time", System.currentTimeMillis());

        dataMap.putString("obama", oba);
        dataMap.putString("rommy", rom);
        dataMap.putString("zipCode", thisZipCode);

        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
    }

    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {


//            File photoFile = new File("sdcard/politianImg/" + poTweetIds[0] + ".jpg");
//            Bitmap photo1 = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//            File photoFile2 = new File("sdcard/politianImg/" + poTweetIds[1] + ".jpg");
//            Bitmap photo2 = BitmapFactory.decodeFile(photoFile2.getAbsolutePath());
//            File photoFile3 = new File("sdcard/politianImg/" + poTweetIds[2] + ".jpg");
//            Bitmap photo3 = BitmapFactory.decodeFile(photoFile3.getAbsolutePath());
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            photo1.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
//            photo2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
//            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
//            photo3.compress(Bitmap.CompressFormat.PNG, 100, stream3);
//
//            byte[] byteArray1 = stream.toByteArray();
//            byte[] byteArray2 = stream2.toByteArray();
//            byte[] byteArray3 = stream3.toByteArray();
//
//            dataMap.putByteArray("photo1", byteArray1);
//            dataMap.putByteArray("photo2", byteArray2);
//            dataMap.putByteArray("photo3", byteArray3);

            // Construct a DataRequest and send over the data layer
            PutDataMapRequest putDMR = PutDataMapRequest.create(path);
            putDMR.getDataMap().putAll(dataMap);
            PutDataRequest request = putDMR.asPutDataRequest();


            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();
            if (result.getStatus().isSuccess()) {
                Log.v("myTag", "DataMap: " + dataMap + " sent successfully to data layer ");
            } else {
                // Log an error
                Log.v("myTag", "ERROR: failed to send DataMap to data layer");
            }

        }
    }

    @Override
    public void onStop(){
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleClient.connect();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("myTag", "connection suspened: " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("myTag", "connection failed: " + connectionResult);
    }
}

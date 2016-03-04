package com.cs160.joleary.catnip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class localPolitiansList extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private ImageButton returnBtn;
    private ImageButton elect_btn;
    private int myZipCode;
    private String[] politianNames = {"Hillary Clinton", "Berney Sanders", "Donald Trumph", "Obama", "Rommy", "Collin"};
    private String[] partiesNames = {"D", "I", "R", "D", "R", "R"};
    private int[] politionPhotos = {R.drawable.hillary, R.drawable.berney, R.drawable.trump, R.drawable.obama, R.drawable.rommy, R.drawable.collin};
    private int[] taking = new int[3];

    GoogleApiClient googleClient;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_politian_list);

        Intent intent = getIntent();
        myZipCode = Integer.parseInt(intent.getStringExtra("zipCode"));

        if (myZipCode == 94806) {
            taking[0] = 0;
            taking[1] = 1;
            taking[2] = 2;
        }else{
            taking[0] = 3;
            taking[1] = 4;
            taking[2] = 5;
        }

        TextView welcomeMessage = (TextView) findViewById(R.id.welcome_text);
        welcomeMessage.setText("Meet your leaders near " + Integer.toString(myZipCode));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(localPolitiansList.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("HighLevelZipCode", myZipCode);
        editor.apply();

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

        // Build a new GoogleApiClient for the the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        final ListView pList = (ListView) findViewById(R.id.secondListMain);

        String[] disN = new String[3];
        String[] disP = new String[3];
        int[] disPh= new int[3];

        for (int i=0; i< 3 ; i++){
            disN[i] = politianNames[taking[i]];
            disP[i] = partiesNames[taking[i]];
            disPh[i] = politionPhotos[taking[i]];
        }

        final myPolitianAdapter pAdapter = new myPolitianAdapter(this, disN, disP, disPh, taking);

        pList.setAdapter(pAdapter);

        returnBtn = (ImageButton) findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(localPolitiansList.this, welcomeMain.class);
                localPolitiansList.this.startActivity(myIntent);
            }
        });


    }

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        String WEARABLE_DATA_PATH = "/myData";

        // Create a DataMap object and send it to the data layer
        DataMap dataMap = new DataMap();
        dataMap.putLong("Time", System.currentTimeMillis());

        for(int l=0; l<3; l++){
            int position = taking[l];

//            Bitmap photo1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.hillary);
//        //Convert to byte array
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        photo1.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray1 = stream.toByteArray();
//
//        dataMap.putByteArray("photo1", byteArray1);
            dataMap.putString("Can"+Integer.toString(l+1)+"name", politianNames[position]);
            dataMap.putString("Can"+Integer.toString(l+1)+"party", partiesNames[position]);
            dataMap.putInt("Index" + Integer.toString(l + 1), position);
        }

        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            int channel = intent.getIntExtra("ChannelPP", 0);
            if (channel == 1){
                String message = intent.getStringExtra("message");
                Log.d("myTag", "Got message: " + message);


                to_DetailView(Integer.parseInt(message));
            }
        }
    };

    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
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

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    public class myPolitianAdapter extends BaseAdapter {
        private final Context context;
        private final String[] politianNames;
        private final String[] partiesNames;
        private final int[] photos;
        private final int[] taking;


        public myPolitianAdapter(Context context, String[] names, String[] parties, int[] photos, int[] takingW){
            this.context = context;
            this.politianNames = names;
            this.partiesNames = parties;
            this.photos = photos;
                this.taking = takingW;

        }

        @Override
        public int getCount() {
            return photos.length;
        }

        @Override
        public Object getItem(int position) {
            return politianNames[position];
        }

        @Override
        public long getItemId(int position) {
            return taking[position];
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View oneRow = inflater.inflate(R.layout.politician_inner_list, parent, false);

            ImageView photoView = (ImageView) oneRow.findViewById(R.id.photoView);
            TextView nameView = (TextView) oneRow.findViewById(R.id.nameView);
            TextView partyView = (TextView) oneRow.findViewById(R.id.partyView);

            nameView.setText(politianNames[position] +", "+ partiesNames[position]);
            partyView.setText("Tweet: The last one was good.");
            photoView.setImageResource(photos[position]);

            oneRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    to_DetailView(taking[position]);
                }
            });

            return oneRow;
        }
    }

    private void to_DetailView(int position){
        Intent myIntent = new Intent(localPolitiansList.this, localPolitianDetail.class);
        myIntent.putExtra("intSelected", position);
        myIntent.putExtra("intPhoto", politionPhotos[position]);
        myIntent.putExtra("politianName", politianNames[position]);
        myIntent.putExtra("partiesName", partiesNames[position]);
        myIntent.putExtra("zipCode", myZipCode);

        localPolitiansList.this.startActivity(myIntent);
    }


}



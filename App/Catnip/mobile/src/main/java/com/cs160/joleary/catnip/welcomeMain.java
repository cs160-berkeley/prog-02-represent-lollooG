package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.internal.widget.AppCompatPopupWindow;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.TwitterSession;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.fabric.sdk.android.Fabric;

public class welcomeMain extends FragmentActivity implements LocationListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "GXKYLZfa83h5BA05CfZtqTyhv";
    private static final String TWITTER_SECRET = "HUmHIq21vMEU6cJWM9xgkc482qhwVx0LL7Hpy3V7iGrGhBpIhF";

    private TwitterApiClient twitterApiClient;

    private Button submitButton;
    private ImageButton locationButton;
    private boolean logined;
    private LocationManager locationManager;

    Context context;

    TwitterLoginButton loginButton;
    GoogleMap googleMap;
    Marker googleMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.welcom_main);
        context = this.getApplicationContext();


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
//        TwitterCore core = Twitter.getInstance().core;
//        TweetUi tweetUi = Twitter.getInstance().tweetUi;

        submitButton = (Button) findViewById(R.id.submitCodeButton);
        locationButton = (ImageButton) findViewById(R.id.location_button);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                String Username = result.data.getUserName();
                Toast.makeText(welcomeMain.this, Username, Toast.LENGTH_SHORT).show();
                Log.d("myTag", "authcomplelet with " + Username);
            }

            @Override
            public void failure(TwitterException e) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edittheZip = (EditText) findViewById(R.id.zipCodeNum);
                String theZip = edittheZip.getText().toString();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edittheZip, InputMethodManager.SHOW_IMPLICIT);

                if (theZip.matches("\\d+") && theZip.length() == 5) {
                    gotoNextPage(theZip);

                } else {
                    Toast.makeText(welcomeMain.this, "you can only enter 5 numbers",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
                LatLng laln = googleMaker.getPosition();
                try {
                    List<Address> adds = mGeocoder.getFromLocation(laln.latitude, laln.longitude, 1);
                    Address theAddress = adds.get(0);

                    Log.d("myTag", "got the address: " + theAddress.getAddressLine(1) + " | " + theAddress.getAdminArea());

                    if (theAddress.getPostalCode() == null){
                        Toast.makeText(welcomeMain.this, "Please adjust a little bit",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        gotoNextPage(theAddress.getPostalCode().toString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

//                gotoNextPage("Use the google Luke");
            }
        });

        setUpGoogleMapIfNeeded();

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng newLatLon) {
                googleMaker.setPosition(newLatLon);
            }
        });
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

                if (Integer.parseInt(message) == 99){
                    Log.d("myTag", "back to shake phone");

//                    Intent intent2 = getIntent();
//                    intent2.putExtra("zipCode", "89032");
//                    finish();
//                    startActivity(intent2);

                    Random rand = new Random();
                    int n = rand.nextInt(2);
                    if (n==0){
                        gotoNextPage("89032");
                    }else{
                        gotoNextPage("83338");
                    }

                }
            }
        }
    };

    private void setUpGoogleMapIfNeeded(){
        if (googleMap==null){
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (googleMap != null){
                setUpMap();
            }
        }
    }

    private void setUpMap(){
        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings mapUi = googleMap.getUiSettings();
//        mapUi.setRotateGesturesEnabled(true);
        mapUi.setZoomControlsEnabled(true);

        if(location!=null){
            onLocationChanged(location);
        }

        double lati = location.getLatitude();
        double longt = location.getLongitude();
        LatLng latlng = new LatLng(lati, longt);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));
        googleMaker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lati, longt))
                .title("Use this location")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.tinymerica)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    private void gotoNextPage (String zip){
//        Toast.makeText(welcomeMain.this, zip,
//                Toast.LENGTH_LONG).show();

        Log.d("myTag", "Welcome main onClick: " + zip);

        Intent myIntent = new Intent(welcomeMain.this, localPolitiansList.class);
        myIntent.putExtra("zipCode", zip);
        welcomeMain.this.startActivity(myIntent);
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.d("myTag", "location indeed changed");

        // Getting latitude of the current location
        double latitude = location.getLatitude();
        // Getting longitude of the current location
        double longitude = location.getLongitude();
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }
}

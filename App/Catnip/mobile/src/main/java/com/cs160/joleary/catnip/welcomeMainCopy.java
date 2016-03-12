package com.cs160.joleary.catnip;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class welcomeMainCopy extends FragmentActivity implements LocationListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "GXKYLZfa83h5BA05CfZtqTyhv";
    private static final String TWITTER_SECRET = "HUmHIq21vMEU6cJWM9xgkc482qhwVx0LL7Hpy3V7iGrGhBpIhF";

    private TwitterApiClient twitterApiClient;

    private Button submitButton;
    private ImageButton locationButton;
    private boolean logined;
    private LocationManager locationManager;

    TwitterLoginButton loginButton;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.welcom_main);


//        TwitterCore core = Twitter.getInstance().core;
//        TweetUi tweetUi = Twitter.getInstance().tweetUi;

        submitButton = (Button) findViewById(R.id.submitCodeButton);
        locationButton = (ImageButton) findViewById(R.id.location_button);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                String Username = result.data.getUserName();
                Toast.makeText(welcomeMainCopy.this, Username, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(welcomeMainCopy.this, "you can only enter 5 numbers",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextPage("Use the google Luke");
            }
        });

        setUpGoogleMapIfNeeded();
    }

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

        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        UiSettings mapUi = googleMap.getUiSettings();
        mapUi.setRotateGesturesEnabled(true);
        if(location!=null){
            onLocationChanged(location);
        }

        double lati = location.getLatitude();
        double longt = location.getLongitude();
        LatLng latlng = new LatLng(lati, longt);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lati, longt)).title("Current Location"));
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

        Log.d("myTag", "Welcome main onClick: "+ zip);

        Intent myIntent = new Intent(welcomeMainCopy.this, localPolitiansList.class);
        myIntent.putExtra("zipCode", zip);
        welcomeMainCopy.this.startActivity(myIntent);
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

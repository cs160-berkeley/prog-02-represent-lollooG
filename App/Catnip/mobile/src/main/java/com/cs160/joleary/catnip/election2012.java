package com.cs160.joleary.catnip;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

public class election2012 extends Activity {

    private ImageButton returnBtn;
    private ImageButton returnlistBtn;
    private int myZipCode;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election2012);

        returnBtn = (ImageButton) findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(election2012.this, welcomeMain.class);
                election2012.this.startActivity(myIntent);
            }
        });

        returnlistBtn = (ImageButton) findViewById(R.id.returnListButton);
        returnlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(election2012.this, localPolitiansList.class);
                myIntent.putExtra("zipCode", Integer.toString(myZipCode));
                election2012.this.startActivity(myIntent);
            }
        });
    }
}

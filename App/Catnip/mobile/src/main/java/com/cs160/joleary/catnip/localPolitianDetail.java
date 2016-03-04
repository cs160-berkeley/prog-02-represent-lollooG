package com.cs160.joleary.catnip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class localPolitianDetail extends Activity {

    private ImageButton returnBtn;
    private ImageButton returnlistBtn;
    private ImageButton elect_btn;
    private int myZipCode;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_politian_detail);

        Intent intent = getIntent();

        int position = intent.getIntExtra("intSelected", 0);
        int photoId = intent.getIntExtra("intPhoto", 0);
        String name = intent.getStringExtra("politianName");
        String party = intent.getStringExtra("partiesName");

        myZipCode = intent.getIntExtra("zipCode", 0);

        TextView welcomeMessage = (TextView) findViewById(R.id.welcome_text);
        welcomeMessage.setText("Meet your leaders near " + Integer.toString(myZipCode));

//        Toast.makeText(localPolitianDetail.this, photoId, Toast.LENGTH_LONG).show();
//
        ImageView photoView = (ImageView) findViewById(R.id.localPicture);
        TextView nameView = (TextView) findViewById(R.id.localPolitianNameView);
        TextView partyView = (TextView) findViewById(R.id.localPolitianPartyView);
//
        photoView.setImageResource(photoId);
        partyView.setText(party);
        nameView.setText(name);

        returnBtn = (ImageButton) findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(localPolitianDetail.this, welcomeMain.class);
                localPolitianDetail.this.startActivity(myIntent);
            }
        });

        returnlistBtn = (ImageButton) findViewById(R.id.returnListButton);
        returnlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(localPolitianDetail.this, localPolitiansList.class);
                myIntent.putExtra("zipCode", Integer.toString(myZipCode));
                localPolitianDetail.this.startActivity(myIntent);
            }
        });

        elect_btn = (ImageButton) findViewById(R.id.elecbutton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(localPolitianDetail.this, election2012.class);
                localPolitianDetail.this.startActivity(myIntent);
            }
        });
    }
}

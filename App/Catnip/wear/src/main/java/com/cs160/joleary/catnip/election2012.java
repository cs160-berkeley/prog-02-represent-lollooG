package com.cs160.joleary.catnip;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

/**
 * Created by lolloo on 3/11/2016.
 */

public class election2012 extends WearableActivity {

    String obamaPer;
    String rommyPer;
    String zip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        obamaPer = intent.getStringExtra("obama");
        rommyPer = intent.getStringExtra("rommy");
        zip = intent.getStringExtra("zipCode");

        setContentView(R.layout.election_view);

        TextView locText = (TextView)findViewById(R.id.locationview);
        locText.setText("The result in " + zip);

        TextView romT = (TextView)findViewById(R.id.rommypercentage);
        romT.setText(rommyPer + "%");

        TextView obamaT = (TextView)findViewById(R.id.obamapercentage);
        obamaT.setText(obamaPer + "%");


    }
}

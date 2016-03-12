package com.cs160.joleary.catnip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages
    private static final String FRED_FEED = "/Fred";
    private static final String LEXY_FEED = "/Lexy";

    private static final String WEARABLE_DATA_PATH = "/myData";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        DataMap dataMap;
        for (DataEvent event : dataEvents) {
            Log.v("myTag", "DataMap received on watch: " + DataMapItem.fromDataItem(event.getDataItem()).getDataMap());
            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_DATA_PATH)) {
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                    // Broadcast DataMap contents to wearable activity for display
                    // The content has the golf hole number and distances to the front,
                    // middle and back pin placements.

                    Intent messageIntent = new Intent();
                    messageIntent.setAction(Intent.ACTION_SEND);
                    messageIntent.putExtra("datamap", dataMap.toBundle());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
                } else if (path.equals("/elect")){
//                    Log.d("myTag", "----------on watch side data recieve-------");
//                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
//                    Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");
//                    Bitmap bitmap = loadBitmapFromAsset(profileAsset);
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                    Intent messageIntent = new Intent();
                    messageIntent.setAction(Intent.ACTION_SEND);
                    messageIntent.putExtra("datamap2", dataMap.toBundle());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

                    Log.d("myTag", "I am in election mode now.");
                }

            }
        }
    }

//    public Bitmap loadBitmapFromAsset(Asset asset) {
//        if (asset == null) {
//            throw new IllegalArgumentException("Asset must be non-null");
//        }
//        ConnectionResult result =
//                mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
//        if (!result.isSuccess()) {
//            return null;
//        }
//        // convert asset into a file descriptor and block until it's ready
//        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
//                mGoogleApiClient, asset).await().getInputStream();
//        mGoogleApiClient.disconnect();
//
//        if (assetInputStream == null) {
//            Log.w(TAG, "Requested an unknown Asset.");
//            return null;
//        }
//        // decode the stream into a bitmap
//        return BitmapFactory.decodeStream(assetInputStream);
//    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("watchListener", "in WatchListenerService, got: " + messageEvent.getPath() + " " + messageEvent.getData());
        //use the 'path' field in sendmessage to differentiate use cases
        //(here, fred vs lexy)

//        if (photo1 == null){
//            Log.d("watchListener", "dataTransferFailed");
//        }


//        if( messageEvent.getPath().equalsIgnoreCase( FRED_FEED ) ) {
//            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
//            Intent intent = new Intent(this, MainActivity.class );
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //you need to add this flag since you're starting a new activity from a service
//            intent.putExtra("CAT_NAME", "Fred");
//            Log.d("T", "about to start watch MainActivity with CAT_NAME: Fred");
//            startActivity(intent);
//        } else if (messageEvent.getPath().equalsIgnoreCase( LEXY_FEED )) {
//            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
//            Intent intent = new Intent(this, MainActivity.class );
//            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//            //you need to add this flag since you're starting a new activity from a service
//            intent.putExtra("CAT_NAME", "Lexy");
//            Log.d("T", "about to start watch MainActivity with CAT_NAME: Lexy");
//            startActivity(intent);
//        } else {
//            super.onMessageReceived( messageEvent );
//        }

    }
}
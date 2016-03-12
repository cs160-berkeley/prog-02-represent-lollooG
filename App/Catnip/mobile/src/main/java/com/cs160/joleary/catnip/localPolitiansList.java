package com.cs160.joleary.catnip;

import android.app.ListActivity;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.*;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.services.SearchService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import io.fabric.sdk.android.Fabric;
import retrofit.http.GET;
import retrofit.http.Query;


public class localPolitiansList extends Activity implements GoogleApiClient.ConnectionCallbacks,
                                                            GoogleApiClient.OnConnectionFailedListener {

    private ImageButton returnBtn;
    private ImageButton elect_btn;
    private String thisZipCode;
    private String[] politianNames = new String[3];
    private String[] partiesNames = new String[3];
    private String[] emails = new String[3];
    private String[] websites = new String[3];
    private String[] bioId = new String[3];
    private String[] poTweetIds = new String[3];

    private int[] politionPhotos = {R.drawable.hillary, R.drawable.berney, R.drawable.trump};
    private int usefulprofile = 0;

    private boolean flagloading;
    private boolean endofSeach;
    private static String Seach_query = "#KristiNoem";
    private static final String Search_result_type = "recent";
    private static final int Seach_count = 2;
    private long maxId;

    private TweetUtils mytweetUtils;
    int downloadfinished;

    GoogleApiClient googleClient;
    Context context;

    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_politian_list);

        context = this.getApplicationContext();

        downloadfinished = 0;

//        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//        Fabric.with(this, new Twitter(authConfig));
//
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

        // Build a new GoogleApiClient for the the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Intent intent = getIntent();
        thisZipCode = (intent.getStringExtra("zipCode"));

        TextView welcomeMessage = (TextView) findViewById(R.id.welcome_text);
        welcomeMessage.setText("Meet your leaders near " + thisZipCode);

        myTask neoMyT = new myTask();
        try {
            boolean finished = neoMyT.execute(thisZipCode).get();
        } catch (ExecutionException e1){
            Log.d("myTag", "execution Error: " + e1);
        } catch (InterruptedException e2) {
            Log.d("myTag", "Interrupt Error: " + e2);
        }

        for (int i=0; i<3; i++){
            Log.d("myTag", "politian party: " + partiesNames[i]);
        }

        fillActivity(politianNames, partiesNames);

        returnBtn = (ImageButton) findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(localPolitiansList.this, welcomeMain.class);
                localPolitiansList.this.startActivity(myIntent);

                localPolitiansList.this.onDestroy();
            }
        });

        elect_btn = (ImageButton)findViewById(R.id.elec2012Btn);
        elect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String [] elecResult = geteleNums(getCountyFromZip(thisZipCode));

                    googleClient.reconnect();

                    Intent myIntent = new Intent(localPolitiansList.this, election2012.class);
                    myIntent.putExtra("obamaPerc", elecResult[1]);
                    myIntent.putExtra("rommyPerc", elecResult[0]);
                    myIntent.putExtra("zipCode", thisZipCode);
                    localPolitiansList.this.startActivity(myIntent);

                    localPolitiansList.this.onDestroy();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


//
//        Asset asset1 = createAssetFromBitmap(photo1);
//        Asset asset2 = createAssetFromBitmap(photo2);
//        Asset asset3 = createAssetFromBitmap(photo3);
//        PutDataRequest request = PutDataRequest.create("/image");
//        request.putAsset("photo1", asset1);
//        request.putAsset("photo2", asset2);
//        request.putAsset("photo3", asset3);
//        Wearable.DataApi.putDataItem(googleClient, request);
    }

    class myTask extends AsyncTask<String, Void, Boolean> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(String... params) {
            String zipcode = params[0];

            String apikey = "6dc0cad4195d461590c2c71dd008dc6f";
            String baseURL = "https://congress.api.sunlightfoundation.com/legislators/locate?zip=";
            String zipCodeAddition = zipcode + "&apikey=" + apikey;
            String url = baseURL + zipCodeAddition;

            Log.d("myTag", "the intented url: " + url);

            try{
                URL apiurl = new URL(url);
                HttpsURLConnection urlConnection = (HttpsURLConnection) apiurl.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();

                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                    JSONObject root = new JSONObject(responseStrBuilder.toString());
                    Log.d("myTag", root.toString());

                    JSONArray polArray = root.getJSONArray("results");

                    usefulprofile = Math.min(3, polArray.length());

                    for (int i=0; i< Math.min(3, polArray.length()); i++) {
                        JSONObject onePolitian = polArray.getJSONObject(i);
                        String fname = onePolitian.getString("first_name");
                        String mname = onePolitian.getString("middle_name");
                        if (mname.equals("null")){
                            mname = "";
                        }
                        String party = onePolitian.getString("party");
                        String lname = onePolitian.getString("last_name");
                        String email = onePolitian.getString("oc_email");
                        String web = onePolitian.getString("website");
                        String twe = onePolitian.getString("twitter_id");
                        String bio = onePolitian.getString("bioguide_id");

                        String fullname = fname + " " + mname + " " + lname;

                        politianNames[i] = fullname;
                        partiesNames[i] = party;
                        emails[i] = email;
                        websites[i] = web;
                        bioId[i] = bio;
                        poTweetIds[i] = twe;

                        Log.d("myTag", fullname + " party: " + party + " email: " + email + " website: " + web + " tweet id: " + twe);
                    }

                } catch(JSONException e3) {
                    Log.d("myTag", "Json Error: " + e3);
                } finally {
                    urlConnection.disconnect();
                }

            } catch (MalformedURLException e) {
                Log.d("myTag", "url not good.");
            } catch (IOException f) {
                Log.d("myTag", "IO reading not good.");
            }

            return true;
        }
    }

    class myDownloadTask extends AsyncTask<String, Void, Boolean> {
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(String... params) {
            String path = params[0];
            String name = params[1];
            try {
                URL url = new URL(path);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                File newFile = new File("sdcard/politianImg");
                if (!newFile.exists()){
                    newFile.mkdir();
                }
                File polimg = new File(newFile, name+".jpg");
                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);

                byte[] data = new byte[1024];
                int total =0;
                int count =0;
                OutputStream outputStream = new FileOutputStream(polimg);
                while((count=inputStream.read(data)) != -1){
                    total += count;
                    outputStream.write(data, 0, count);
                }
                inputStream.close();
                outputStream.close();

                downloadfinished ++;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    class mySentInfomation extends AsyncTask<Void, Void, Boolean>{

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            String WEARABLE_DATA_PATH = "/myData";

            // Create a DataMap object and send it to the data layer
            DataMap dataMap = new DataMap();
            dataMap.putLong("Time", System.currentTimeMillis());

            for(int l=0; l<3; l++){
//            Bitmap photo1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.hillary);
//        //Convert to byte array
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        photo1.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray1 = stream.toByteArray();
//        dataMap.putByteArray("photo1", byteArray1);

//                File photoFile = new File("sdcard/politianImg/" + poTweetIds[l] + ".jpg");
//
//                while (!photoFile.exists()){
//                    try {
//                        Thread.sleep(500);
//                        photoFile = new File("sdcard/politianImg/" + poTweetIds[l] + ".jpg");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                Bitmap photo1 = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                photo1.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray1 = stream.toByteArray();
//                dataMap.putByteArray("photo"+Integer.toString(l+1), byteArray1);

                dataMap.putString("Can"+Integer.toString(l+1)+"name", politianNames[l]);
                dataMap.putString("Can"+Integer.toString(l+1)+"party", partiesNames[l]);
                dataMap.putInt("Index" + Integer.toString(l + 1), l);
            }
            //Requires a new thread to avoid blocking the UI
            new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();

            return true;
        }
    }


    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    protected void fillActivity(String[] names, String[] party){
        TextView nameView1 = (TextView) findViewById(R.id.nameText1);
        TextView nameView2 = (TextView) findViewById(R.id.nameText2);
        TextView nameView3 = (TextView) findViewById(R.id.nameText3);

        TextView emailView1 = (TextView) findViewById(R.id.emailText1);
        TextView emailView2 = (TextView) findViewById(R.id.emailText2);
        TextView emailView3 = (TextView) findViewById(R.id.emailText3);

        TextView websiteView1 = (TextView) findViewById(R.id.websiteText1);
        TextView websiteView2 = (TextView) findViewById(R.id.websiteText2);
        TextView websiteView3 = (TextView) findViewById(R.id.websiteText3);

        final ScrollView llayout1 = (ScrollView) findViewById(R.id.toShowTweet1);
        final ScrollView llayout2 = (ScrollView) findViewById(R.id.toShowTweet2);
        final ScrollView llayout3 = (ScrollView) findViewById(R.id.toShowTweet3);

        ImageView politView1 = (ImageView)findViewById(R.id.poliphoto1);
        ImageView politView2 = (ImageView)findViewById(R.id.poliphoto2);
        ImageView politView3 = (ImageView)findViewById(R.id.poliphoto3);

        final SearchService service = Twitter.getApiClient().getSearchService();

        Log.d("myTag", "the tweets: " + poTweetIds[0] + " " + poTweetIds[1] + " " + poTweetIds[2]);

        politicalListFiller(nameView1, names[0] + " (" + party[0] + ")", websiteView1, websites[0], emailView1, emails[0], llayout1, service, poTweetIds[0], politView1);
        politicalListFiller(nameView2, names[1] + " (" + party[1] + ")", websiteView2, websites[1], emailView2, emails[1], llayout2, service, poTweetIds[1], politView2);
        politicalListFiller(nameView3, names[2] + " (" + party[2] + ")", websiteView3, websites[2], emailView3, emails[2], llayout3, service, poTweetIds[2], politView3);

        FrameLayout ll1 = (FrameLayout)findViewById(R.id.candi1);
        ImageView llb1 = (ImageView)findViewById(R.id.realbackground1);
        setLinBackground(llb1, party[0]);
        FrameLayout ll2 = (FrameLayout)findViewById(R.id.candi2);
        ImageView llb2 = (ImageView)findViewById(R.id.realbackground2);
        setLinBackground(llb2, party[1]);
        FrameLayout ll3 = (FrameLayout)findViewById(R.id.candi3);
        ImageView llb3 = (ImageView)findViewById(R.id.realbackground3);
        setLinBackground(llb3, party[2]);

        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_DetailView(0);
            }
        });
        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_DetailView(1);
            }
        });
        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_DetailView(2);
            }
        });
    }

    private  void setLinBackground (ImageView ll, String party){
        if (party.equals("D")){
            ll.setImageResource(R.drawable.dbackground);
        }else if (party.equals("R")){
            ll.setImageResource(R.drawable.rbackground);
        }else {
            ll.setImageResource(R.drawable.ibackground);
        }
    }

    private void politicalListFiller (TextView tvx, String nameField, TextView webSite, String websiteField, TextView email,
                                      String emailField, final ScrollView sView, SearchService service,final String tweetId,
                                      final ImageView politiView){
        tvx.setText(nameField);
        webSite.setText(websiteField);
        email.setText(emailField);

        service.tweets("from:" + tweetId, null, null, null, Search_result_type, 1, null, null, maxId, false, new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {
                int i = 0;
                for (Tweet tweet : result.data.tweets) {
                    i++;
                    if (i == 1) {
                        String imageAdd = tweet.user.profileImageUrlHttps;
                        Log.d("myTag", "image link: " + imageAdd + " Content: " + tweet.text);

                        String imageAddbig = imageAdd.replace("_normal", "");
                        myDownloadTask dTask = new myDownloadTask();
                        try {
                            boolean aA = dTask.execute(imageAdd, tweetId).get();
                            File poliPic1 = new File("sdcard/politianImg/" +tweetId+".jpg");
                            Bitmap polibmp1 = BitmapFactory.decodeFile(poliPic1.getAbsolutePath());

                            if (polibmp1 == null){
                                Log.d("myTag", "Image failed to download | " + "sdcard/politianImg/" +tweetId+".jpg" + " | " + aA);
                            }
                            politiView.setImageBitmap(polibmp1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        try {
                            myDownloadTask dTask2 = new myDownloadTask();
                            dTask2.execute(imageAddbig, "fullsize"+tweetId).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        TweetUtils.loadTweet(tweet.id, new LoadCallback<Tweet>() {
                            @Override
                            public void success(Tweet tweet) {
                                sView.addView(new TweetView(localPolitiansList.this, tweet));
                            }

                            @Override
                            public void failure(TwitterException exception) {
                                // Toast.makeText(...).show();
                                Log.d("myTag", "load tweet failed: " + exception);
                            }
                        });
                    }
                }
            }

            @Override
            public void failure(TwitterException e) {
                Log.d("myTag", "the tweets failed to load");
            }
        });
    }

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        googleClient.connect();
        super.onStart();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
//        mySentInfomation delaySentData = new mySentInfomation();
//        delaySentData.execute();

        String WEARABLE_DATA_PATH = "/myData";

        // Create a DataMap object and send it to the data layer
        DataMap dataMap = new DataMap();
        dataMap.putLong("Time", System.currentTimeMillis());

        for(int l=0; l<3; l++){
//            Bitmap photo1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.hillary);
//        //Convert to byte array
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        photo1.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray1 = stream.toByteArray();
//        dataMap.putByteArray("photo1", byteArray1);

//                File photoFile = new File("sdcard/politianImg/" + poTweetIds[l] + ".jpg");
//
//                while (!photoFile.exists()){
//                    try {
//                        Thread.sleep(500);
//                        photoFile = new File("sdcard/politianImg/" + poTweetIds[l] + ".jpg");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                Bitmap photo1 = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                photo1.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray1 = stream.toByteArray();
//                dataMap.putByteArray("photo"+Integer.toString(l+1), byteArray1);

            dataMap.putString("Can"+Integer.toString(l+1)+"name", politianNames[l]);
            dataMap.putString("Can"+Integer.toString(l+1)+"party", partiesNames[l]);
            dataMap.putInt("Index" + Integer.toString(l + 1), l);
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

                if (Integer.parseInt(message) < 10){
                    Log.d("myTag", "Got message: " + message);

                    to_DetailView(Integer.parseInt(message));
                }

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
//            while (downloadfinished < 3){
//                try {
//                    Thread.sleep(3000);
//                    Log.d("myTag", "I am waiting here: " + System.currentTimeMillis());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }

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

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onDestroy();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("myTag", "connection suspened: " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("myTag", "connection failed: " + connectionResult);
    }


    private void to_DetailView(int position) {
        Intent myIntent = new Intent(localPolitiansList.this, localPolitianDetail.class);
        myIntent.putExtra("intSelected", position);
        myIntent.putExtra("photoPath", ("sdcard/politianImg/fullsize" + poTweetIds[position]+".jpg"));
        myIntent.putExtra("bioId", bioId[position]);
        myIntent.putExtra("politianName", politianNames[position]);
        myIntent.putExtra("partiesName", partiesNames[position]);
        myIntent.putExtra("zipCode", thisZipCode);

        localPolitiansList.this.startActivity(myIntent);
    }

    @Override
    protected void onNewIntent(Intent intent){
        setIntent(intent);
        handleIntent(intent);
        super.onNewIntent(intent);
    }

    private void handleIntent(Intent intent){
        Log.d("myTag", "renewing the incoming intent");
    }

    private String getCountyFromZip (String zip) throws IOException {

        Log.d("myTag", "using the this zip 1------------------ " + zip);

        Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());

        Log.d("myTag", "using the this zip 2------------------ " + zip);

        List<Address> adds = mGeocoder.getFromLocationName(zip, 1);
        Address oneAddres = adds.get(0);
        Log.d("myTag", "sent county: " + oneAddres);
        String addwithZip = oneAddres.getAddressLine(0);
        String counte = addwithZip.substring(0, addwithZip.length()-6);

        String[] separated = counte.split(", ");

        counte = separated[0] + " County, " + separated[1];

        Log.d("myTag", "sent county: " + counte);
        return counte;
    }

    private String[] geteleNums (String cunte) throws IOException {
        File file = new File("sdcard/politianInfo/ele2012.json");

        String jsonStr = null;
        FileInputStream stream = null;
        String[] results = new String[2];

        try {
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

            jsonStr = Charset.defaultCharset().decode(bb).toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONObject cuntResults = jsonObj.getJSONObject(cunte);
            results[0] = cuntResults.getString("romney");
            results[1] = cuntResults.getString("obama");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
    }
}




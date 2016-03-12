package com.cs160.joleary.catnip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class localPolitianDetail extends Activity {

    private ImageButton returnBtn;
    private ImageButton elect_btn;
    private String myZipCode;

    private String endTermText;
    private String OfficeAdd;
    private String PhoneNum;
    private String[] servedComi;
    private String[] billspons;

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

        String photoPath = intent.getStringExtra("photoPath");

        String name = intent.getStringExtra("politianName");
        String party = intent.getStringExtra("partiesName");
        String bioId = intent.getStringExtra("bioId");

//        Log.d("myTag", "bioIdTest: Value----------" + bioId);

        myZipCode = intent.getStringExtra("zipCode");

        TextView welcomeMessage = (TextView) findViewById(R.id.welcome_text);
        welcomeMessage.setText("Meet your leaders near " + myZipCode);

//        Toast.makeText(localPolitianDetail.this, photoId, Toast.LENGTH_LONG).show();
//
        ImageView photoView = (ImageView) findViewById(R.id.localPicture);
        TextView nameView = (TextView) findViewById(R.id.localPolitianNameView);
        TextView endtermView = (TextView) findViewById(R.id.localPolitianEndofDateView);
        ImageView headviewpic = (ImageView) findViewById(R.id.headbg);

        setLinBackground (headviewpic, party);

        File poliPic1 = new File(photoPath);
        Bitmap polibmp1 = BitmapFactory.decodeFile(poliPic1.getAbsolutePath());
        photoView.setImageBitmap(polibmp1);

        nameView.setText(name + " (" + party + ") ");

        getDetailInfo neoMyT = new getDetailInfo();
        try {
            boolean finished = neoMyT.execute(bioId).get();
        } catch (ExecutionException e1){
            Log.d("myTag", "execution Error: " + e1);
        } catch (InterruptedException e2) {
            Log.d("myTag", "Interrupt Error: " + e2);
        }

        Log.d("myTag", OfficeAdd + " | " + servedComi + " | " + billspons);

        String committeelist = "";
        for (int i=0; i<servedComi.length; i++){
            committeelist += servedComi[i] + "\n";
        }
        TextView comiScoll = (TextView)findViewById(R.id.committeeText);
        comiScoll.setText(committeelist);

        String billslist = "";
        for (int i=0; i<billspons.length; i++){
            billslist += billspons[i] + "\n";
        }
        TextView bilScoll = (TextView)findViewById(R.id.billsText);
        bilScoll.setText(billslist);

        TextView endTerm = (TextView)findViewById(R.id.localPolitianEndofDateView);
        endTerm.setText(endTermText);

        TextView addView = (TextView)findViewById(R.id.localPolitianAdd);
        addView.setText(OfficeAdd);

        TextView phoneView = (TextView)findViewById(R.id.localPolitianPhone);
        phoneView.setText("Tel: "+PhoneNum);


        returnBtn = (ImageButton) findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(localPolitianDetail.this, welcomeMain.class);
                localPolitianDetail.this.startActivity(myIntent);
            }
        });

        elect_btn = (ImageButton)findViewById(R.id.elec2012detailBtn);
        elect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(localPolitianDetail.this, election2012.class);
                myIntent.putExtra("zipCode", myZipCode);
                localPolitianDetail.this.startActivity(myIntent);
            }
        });

//        elect_btn = (ImageButton) findViewById(R.id.elecbutton);
//        returnBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(localPolitianDetail.this, election2012.class);
//                localPolitianDetail.this.startActivity(myIntent);
//            }
//        });
    }

    private  void setLinBackground (ImageView ll, String party){
        if (party.equals("D")){
            ll.setImageResource(R.drawable.dbackgroundinv);
        }else if (party.equals("R")){
            ll.setImageResource(R.drawable.rbackgroundinv);
        }else {
            ll.setImageResource(R.drawable.ibackgroundinv);
        }
    }

    class getDetailInfo extends AsyncTask<String, Void, Boolean> {

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
            String bio = params[0];

            String apikey = "6dc0cad4195d461590c2c71dd008dc6f";
            String baseurl = "https://congress.api.sunlightfoundation.com/legislators?bioguide_id=" + bio + "&apikey=" + apikey;
            String committeeURL = "https://congress.api.sunlightfoundation.com/committees?member_ids=" + bio + "&apikey=" + apikey;
            String billsURL = "https://congress.api.sunlightfoundation.com/bills?sponsor_id=" + bio + "&apikey=" + apikey;

            Log.d("myTag", "the intented url: " + baseurl);

            try{
                URL baseURLi = new URL(baseurl);
                HttpsURLConnection baseurlConnection = (HttpsURLConnection) baseURLi.openConnection();
                try {
                    InputStream in = new BufferedInputStream(baseurlConnection.getInputStream());
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();

                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                    JSONObject root = new JSONObject(responseStrBuilder.toString());
                    Log.d("myTag", root.toString());

                    JSONArray polArray = root.getJSONArray("results");
                    JSONObject onePolitian = polArray.getJSONObject(0);

                    String termStart = onePolitian.getString("term_start");
                    String termEnd = onePolitian.getString("term_end");
                    String localAdd = onePolitian.getString("office");
                    String StateInn = onePolitian.getString("state");

                    PhoneNum = onePolitian.getString("phone");
                    endTermText = "Start: "+termStart + " /End: " + termEnd;
                    OfficeAdd = localAdd + ", " + StateInn;

                } catch(JSONException e3) {
                    Log.d("myTag", "Json Error: " + e3);
                } finally {
                    baseurlConnection.disconnect();
                }

                URL commitURLi = new URL(committeeURL);
                HttpsURLConnection commiturlConnection = (HttpsURLConnection) commitURLi.openConnection();
                try {
                    InputStream in = new BufferedInputStream(commiturlConnection.getInputStream());
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();

                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                    JSONObject root = new JSONObject(responseStrBuilder.toString());
                    Log.d("myTag", root.toString());

                    JSONObject pageInfo = root.getJSONObject("page");
                    int count = Integer.parseInt(pageInfo.getString("count"));

                    servedComi = new String[count];

                    JSONArray polArray = root.getJSONArray("results");

                    for (int i=0; i < count; i++){
                        JSONObject oneCommittee = polArray.getJSONObject(i);

                        servedComi[i] = oneCommittee.getString("name") + ", (" + oneCommittee.getString("committee_id") + ")";
                    }
                } catch(JSONException e3) {
                    Log.d("myTag", "Json Error: " + e3);
                } finally {
                    commiturlConnection.disconnect();
                }

                URL billsURLi = new URL(billsURL);
                HttpsURLConnection billsurlConnection = (HttpsURLConnection) billsURLi.openConnection();
                try {
                    InputStream in = new BufferedInputStream(billsurlConnection.getInputStream());
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();

                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                    JSONObject root = new JSONObject(responseStrBuilder.toString());
                    Log.d("myTag", root.toString());

                    JSONObject pageInfo = root.getJSONObject("page");
                    int count = Integer.parseInt(pageInfo.getString("count"));

                    billspons = new String[count];

                    JSONArray polArray = root.getJSONArray("results");

                    for (int i=0; i < count; i++){
                        JSONObject oneBill = polArray.getJSONObject(i);

                        String billname = oneBill.getString("short_title");

                        if (billname.equals("null")){
                            billname = oneBill.getString("official_title");
                        }

                        billspons[i] = oneBill.getString("introduced_on") + ", " + billname;
                    }
                } catch(JSONException e3) {
                    Log.d("myTag", "Json Error: " + e3);
                } finally {
                    commiturlConnection.disconnect();
                }
            } catch (MalformedURLException e) {
                Log.d("myTag", "url not good." + e);
            } catch (IOException f) {
                Log.d("myTag", "IO reading not good.");
            }

            return true;
        }
    }
}

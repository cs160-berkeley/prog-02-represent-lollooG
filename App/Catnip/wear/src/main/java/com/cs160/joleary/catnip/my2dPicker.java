package com.cs160.joleary.catnip;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class my2dPicker extends Activity {
    private static Context context;

    public class data{
        String title;
        String text;
        int index;

        public data(String title, String text, int index) {
            this.title = title;
            this.text = text;
            this.index = index;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my2d_picker);
        my2dPicker.context = getApplicationContext();
//
//        byte[] byteArray1 = getIntent().getByteArrayExtra("photo1");
//        Bitmap bmp1 = BitmapFactory.decodeByteArray(byteArray1, 0, byteArray1.length);

        data[][] myData = {
                {new data(getIntent().getStringExtra("name1"), getIntent().getStringExtra("party1"), getIntent().getIntExtra("index1", 0))},
                {new data(getIntent().getStringExtra("name2"), getIntent().getStringExtra("party2"), getIntent().getIntExtra("index1", 0))},
                {new data(getIntent().getStringExtra("name3"), getIntent().getStringExtra("party3"), getIntent().getIntExtra("index1", 0))}};

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new my2dGrid(myData, getFragmentManager()));

    }

    public static class myFragment extends Fragment implements SensorEventListener {
        private static final float SHAKE_THRESHOLD = 1.1f;
        private static final int SHAKE_WAIT_TIME_MS = 250;
        private static final float ROTATION_THRESHOLD = 2.0f;
        private static final int ROTATION_WAIT_TIME_MS = 100;

        private View mView;
        private TextView mTextTitle;
        private TextView mTextValues;
        private SensorManager mSensorManager;
        private Sensor mSensor;
        private int mSensorType;
        private long mShakeTime = 0;
        private long mRotationTime = 0;

        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle args = getArguments();
            if(args != null) {
                mSensorType = args.getInt("sensorType");
            }

            mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(mSensorType);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
            String title = getArguments().getString("title");
            String text = getArguments().getString("text", "N/A");

            int iconId = 0;
            if (text.equals("D")){
                iconId = R.drawable.d_icon;
            }else if (text.equals("R")){
                iconId = R.drawable.r_icon;
            }else{
                iconId = R.drawable.i_icon;
            }

            final int index = getArguments().getInt("index");

            View mView = inflater.inflate(R.layout.my_fragment, container, false);
            TextView startTitle = (TextView) mView.findViewById(R.id.politic_name);
            startTitle.setText(title + " ," + text);

            ImageView iView = (ImageView) mView.findViewById(R.id.good_photoView);
            iView.setImageDrawable(ContextCompat.getDrawable(context, iconId));

            ImageButton ibtn = (ImageButton) mView.findViewById(R.id.detailButton);
            ibtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.detailbutton));

            ibtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("myTag", "Broadcasting message");
                    Intent messageIntent = new Intent("custom-event-name");
                    // You can also include some extra data.
                    messageIntent.putExtra("ChannelWW", 1);
                    messageIntent.putExtra("Message", index);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(messageIntent);

                    Log.d("myTage", "W->W 2p -> watch2phoneSend: " +Integer.toString(index));
                }
            });

            return mView;
        }

        @Override
        public void onResume() {
            super.onResume();
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        @Override
        public void onPause() {
            super.onPause();
            mSensorManager.unregisterListener(this);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // If sensor is unreliable, then just return
            if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
            {
                return;
            }

            mTextValues.setText(
                    "x = " + Float.toString(event.values[0]) + "\n" +
                            "y = " + Float.toString(event.values[1]) + "\n" +
                            "z = " + Float.toString(event.values[2]) + "\n"
            );

            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                detectShake(event);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }


        // References:
        //  - http://jasonmcreynolds.com/?p=388
        //  - http://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
        private void detectShake(SensorEvent event) {
            long now = System.currentTimeMillis();

            if((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
                mShakeTime = now;

                float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
                float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
                float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

                // gForce will be close to 1 when there is no movement
                double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

                // Change background color if gForce exceeds threshold;
                // otherwise, reset the color
                if(gForce > SHAKE_THRESHOLD) {
                    Log.d("myTag", "Shake senser is working: ");
                }
            }
        }
    }

    public class my2dGrid extends FragmentGridPagerAdapter {

        private final data[][] mData;
        private List mRows;

        public my2dGrid(data[][] ctx, FragmentManager fm) {
            super(fm);
            mData = ctx;
        }

        static final int BG_IMAGE = R.drawable.background;

        @Override
        public Fragment getFragment(int row, int col) {

//            return CardFragment.create(mData[row][col].title, mData[row][col].text, mData[row][col].iconId);


            myFragment tempFragment = new myFragment();
            Bundle mbun = new Bundle();
            mbun.putString("title", mData[row][col].title);
            mbun.putString("text", mData[row][col].text);
            mbun.putInt("index", mData[row][col].index);
            tempFragment.setArguments(mbun);
            return tempFragment;
        }

        @Override
        public Drawable getBackgroundForPage (int row, int colum){
            return ContextCompat.getDrawable(my2dPicker.this, BG_IMAGE);
        }

        @Override
        public int getRowCount() {
            return mData.length;
        }

        @Override
        public int getColumnCount(int i) {
            return mData[i].length;
        }
    }

}


package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class welcomeMain extends Activity {

    private Button submitButton;
    private ImageButton locationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcom_main);

        submitButton = (Button) findViewById(R.id.submitCodeButton);
        locationButton = (ImageButton) findViewById(R.id.location_button);


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
                gotoNextPage(Integer.toString(94806));
            }
        });
    }

    private void gotoNextPage (String zip){
        Toast.makeText(welcomeMain.this, zip,
                Toast.LENGTH_LONG).show();

        Log.d("myTag", "Welcome main onClick: "+ zip);

        Intent myIntent = new Intent(welcomeMain.this, localPolitiansList.class);
        myIntent.putExtra("zipCode", zip);
        welcomeMain.this.startActivity(myIntent);
    }


    public class myPolitianAdapter extends BaseAdapter {
        private final Context context;
        private final String[] politianNames;
        private final String[] partiesNames;
        private final int[] photos;
        private final int[] taking = new int[3];

        public myPolitianAdapter(Context context, String[] names, String[] parties, int[] photos, int[] takingW){
            this.context = context;
            this.politianNames = names;
            this.partiesNames = parties;
            this.photos = photos;
            for (int i =0; i<3; i++) {
                this.taking[i] = takingW[i];
            }
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
            return position;
        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup parent){
            final int position = taking[pos];
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View oneRow = inflater.inflate(R.layout.politician_inner_list, parent, false);

            ImageView photoView = (ImageView) oneRow.findViewById(R.id.photoView);
            TextView nameView = (TextView) oneRow.findViewById(R.id.nameView);
            TextView partyView = (TextView) oneRow.findViewById(R.id.partyView);

            nameView.setText(politianNames[position]);
            partyView.setText(partiesNames[position]);
            photoView.setImageResource(photos[position]);

            oneRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, politianNames[position], Toast.LENGTH_LONG).show();
                }
            });

            return oneRow;
        }
    }

}

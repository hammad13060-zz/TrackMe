package com.iiitd.hammad13060.trackme.SourceDestinationClasses;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.iiitd.hammad13060.trackme.R;

public class ForTesting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_testing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ForTesting.this,LoadingScreen.class);
                startActivityForResult(i, 10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        TextView t = (TextView)findViewById(R.id.Text);
        if (requestCode == 10) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Double Destination_lat = data.getDoubleExtra("DestLat",0);
                Double Destination_longi = data.getDoubleExtra("DestLongi", 0);
                Double Source_lat = data.getDoubleExtra("SrcLat",0);
                Double Source_longi = data.getDoubleExtra("SrcLongi", 0);
                t.setText(Destination_lat.toString());

                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

}

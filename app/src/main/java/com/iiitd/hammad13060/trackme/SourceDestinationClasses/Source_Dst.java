package com.iiitd.hammad13060.trackme.SourceDestinationClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iiitd.hammad13060.trackme.R;

import java.math.BigDecimal;

public class Source_Dst extends AppCompatActivity {

    ProgressBar mProgessBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source__dst);

        Intent i = new Intent(this, Source.class);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView tv1 = (TextView)findViewById(R.id.second_text_processing);
        TextView tv2 = (TextView)findViewById(R.id.LatLong_state_text_view);
        mProgessBar = (ProgressBar)findViewById(R.id.progress_bar);
        Intent send_MainFragment = new Intent();
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){

                Double Destination_lat = data.getDoubleExtra("resultDestLat1",0);
                Double Destination_longi = data.getDoubleExtra("resultDestLon2", 0);
                Double Source_lat = data.getDoubleExtra("resultSrcLat1",0);
                Double Source_longi = data.getDoubleExtra("resultSrcLon2", 0);

                String l1 = "Destination Lat and Lon : " + Double.toString(Destination_lat)+"\n" + Double.toString((Destination_longi)) + "\nSource "+ Double.toString(Source_lat)+"\n"+Double.toString(Source_longi);
                tv1.setText(l1);
                mProgessBar.setVisibility(View.GONE);
                tv2.setText(" ");

                //TO SEND TO MAIN ACTIVITY
                send_MainFragment.putExtra("SrcLat",Source_lat);
                send_MainFragment.putExtra("SrcLongi",Source_longi);
                send_MainFragment.putExtra("DestLat",Destination_lat);
                send_MainFragment.putExtra("DestLongi", Destination_longi);
                setResult(Activity.RESULT_OK, send_MainFragment);
                finish();


            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                String result=data.getStringExtra("result");
                tv1.setText(result);
            }
        }
    }

}

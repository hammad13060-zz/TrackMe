package com.iiitd.hammad13060.trackme.SourceDestinationClasses;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.Contact;

import java.util.ArrayList;
import java.util.List;


public class LoadingScreen extends AppCompatActivity {

    ProgressBar mProgessBar;
    public static List<Parcelable> finalSelectedContactList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source__dst);
        final TextView t1 = (TextView)findViewById(R.id.LatLong_state_text_view);
        final TextView t2 = (TextView)findViewById(R.id.second_text_processing);
        final ProgressBar p1 = (ProgressBar)findViewById(R.id.progress_bar);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ninfo = cm.getActiveNetworkInfo();

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoadingScreen.this);
        if (!enabled || !isOnline() ) {
            if(!enabled)
            {
                alertDialog.setTitle("!!ERROR!");
                alertDialog.setMessage("Please Turn On GPS ");
                t1.setText(" ");
                t2.setText(" ");
                // alertDialog.setIcon(R.drawable.delete);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        t1.setText("Please wait...");
                        p1.setVisibility(View.GONE);
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        finish();
                    }
                });
                alertDialog.show();
            }
            else
            {
                alertDialog.setTitle("!!ERROR!");
                alertDialog.setMessage("Please Turn On Wifi/Internet ");
                t1.setText(" ");
                t2.setText(" ");
                // alertDialog.setIcon(R.drawable.delete);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        t1.setText("Please wait...");
                        p1.setVisibility(View.GONE);
                        finish();
                    }
                });
                alertDialog.show();
            }


        }
        else
        {
            Intent i = new Intent(this, Source.class);
            startActivityForResult(i, 1);
        }



    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
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
                finalSelectedContactList = data.getParcelableArrayListExtra("contList");

                String l1 = "Destination Lat and Lon : " + Double.toString(Destination_lat)+"\n" + Double.toString((Destination_longi)) + "\nSource "+ Double.toString(Source_lat)+"\n"+Double.toString(Source_longi);
                tv1.setText(l1);
                mProgessBar.setVisibility(View.GONE);
                tv2.setText(" ");

                //TO SEND TO MAIN ACTIVITY
                send_MainFragment.putExtra("SrcLat",Source_lat);
                send_MainFragment.putExtra("SrcLongi",Source_longi);
                send_MainFragment.putExtra("DestLat",Destination_lat);
                send_MainFragment.putExtra("DestLongi", Destination_longi);
                send_MainFragment.putParcelableArrayListExtra("contList", (ArrayList<? extends Parcelable>) finalSelectedContactList);
                setResult(Activity.RESULT_OK, send_MainFragment);
                finish();


            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
//                String result=data.getStringExtra("result");
  //              tv1.setText(result);
                finish();
            }
        }
    }

}

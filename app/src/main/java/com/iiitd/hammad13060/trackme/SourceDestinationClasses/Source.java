package com.iiitd.hammad13060.trackme.SourceDestinationClasses;

import com.iiitd.hammad13060.trackme.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.iiitd.hammad13060.trackme.helpers.Constants;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class Source extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener {
    // LogCat tag
    private static final String TAG = "-- Source Class --";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    double Source_latitude,Source_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
        displayLocation();
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            Source_latitude = mLastLocation.getLatitude();
            Source_longitude = mLastLocation.getLongitude();
            //String lat = Double.toString(latitude);
            //String longi = Double.toString(longitude);
            //String result = "Latitude : "+lat + "\nLongitude: " + longi;
            /*Intent returnIntent = new Intent();
            //returnIntent.putExtra("result",result);
            returnIntent.putExtra("result1",latitude);
            returnIntent.putExtra("result2",longitude);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();*/
            ReverseGeocoding locationAddress = new ReverseGeocoding();
            locationAddress.getAddressFromLocation(Source_latitude, Source_longitude,
                    getApplicationContext(), new GeocoderHandler());

        } else {

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.i(TAG, "Connection failed:");
        Constants.showLongToast(getApplicationContext(), "ConnectionFailed");
    }

    @Override
    public void onConnected(Bundle arg0) {
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    private class GeocoderHandler extends Handler {
        //TextView tvAddress = (TextView)findViewById(R.id.second_text_processing);

        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            //tvAddress.setText(locationAddress);
            if(locationAddress!=null)
            {
                sendToDestination(locationAddress);
            }
            else
            {
                displayLocation();
            }

        }
    }

    void sendToDestination(String la)
    {
        Intent i = new Intent(this, SourceDestinationUI.class);
        i.putExtra("sourceText", la);
        startActivityForResult(i, 3);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent returnIntent_source = new Intent();
        if (requestCode == 3) {
            if(resultCode == Activity.RESULT_OK){
                //String result=data.getStringExtra("result");
                Double Destination_latitude = data.getDoubleExtra("resultDestLat1",0);
                Double Destination_longitude = data.getDoubleExtra("resultDestLon2", 0);
                //returnIntent.putExtra("result",result);
                returnIntent_source.putExtra("resultSrcLat1",Source_latitude);
                returnIntent_source.putExtra("resultSrcLon2",Source_longitude);
                returnIntent_source.putExtra("resultDestLat1",Destination_latitude);
                returnIntent_source.putExtra("resultDestLon2", Destination_longitude);
                setResult(Activity.RESULT_OK, returnIntent_source);
                finish();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }
}
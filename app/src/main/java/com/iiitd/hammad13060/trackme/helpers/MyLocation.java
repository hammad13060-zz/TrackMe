package com.iiitd.hammad13060.trackme.helpers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.iiitd.hammad13060.trackme.BroadCastReceivers.CurrentLocationReceiver;

/**
 * Created by hammad on 4/2/16.
 */

public class MyLocation implements LocationListener {

    public static final String EXTRA_MY_LAT = "LATITUDE";
    public static final String EXTRA_MY_LONG = "LONGITUDE";

    private static final String TAG = "MyLocation";

    private Context context = null;

    private LocationManager locationManager = null;
    private Location currentLocation = null;


    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;

    public MyLocation(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGPSEnabled) {
            Log.d(TAG, "gps provider enabled");
            if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "yes we have proper permissions");
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        } else {
            Log.d(TAG, "gps provider not enabled");
        }
    }

    public Location getMyCurrentLocation() {
        //gps and wifi network are the two network providers

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        //isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled) {
            Log.d(TAG, "gps enabled");
            if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getting last know location");
                if (currentLocation == null)
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

        } else {
            Log.d(TAG, "gps not enabled");
        }
        return currentLocation;
    }

    public void tearDown() {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
            return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        Intent intent = new Intent();
        intent.setAction("com.iiitd.hammad13060.trackme.BroadCastReceivers.CurrentLocationReceiver");
        intent.putExtra(EXTRA_MY_LAT, currentLocation.getLatitude());
        intent.putExtra(EXTRA_MY_LONG, currentLocation.getLongitude());
        context.sendBroadcast(intent);
        Log.d(TAG, "LATITUDE: " + currentLocation.getLatitude() + " LONGITUDE: " + currentLocation.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "provider status changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "provider enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "provider disabled");
    }
}

package com.iiitd.hammad13060.trackme.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;

import com.iiitd.hammad13060.trackme.BroadCastReceivers.CurrentLocationReceiver;
import com.iiitd.hammad13060.trackme.Fragments.JourneyFragment;
import com.iiitd.hammad13060.trackme.MyLocationInterface;
import com.iiitd.hammad13060.trackme.helpers.MyLocation;

public class JourneyService extends Service implements MyLocationInterface {

    public static double SrctLat;
    public static double SrcLAlt;
    public static double DestLat;
    public static double DestAlt;

    public static boolean journeyRunning = false;

    private double currentLat;
    private double currentLong;

    private BroadcastReceiver currentLocationReceiver = null;

    private MyLocation myLocation = new MyLocation(this);


    MyLocationInterface myLocationInterface = null;
    public JourneyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerMyLocationReceiver();


        journeyRunning = true;

        Location currentLocation = myLocation.getMyCurrentLocation();
        currentLat = currentLocation.getLatitude();
        currentLong = currentLocation.getLongitude();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        journeyRunning = false;
        unregisterMyLocationReceiver();
    }

    @Override
    public void myLocationUpdate(double latitude, double longitude) {
        currentLat = latitude;
        currentLong = longitude;
    }

    private void registerMyLocationReceiver() {
        currentLocationReceiver = new CurrentLocationReceiver(this);
        IntentFilter intentFilter = new IntentFilter("com.iiitd.hammad13060.trackme.BroadCastReceivers.CurrentLocationReceiver");
        registerReceiver(currentLocationReceiver, intentFilter);
    }

    private void unregisterMyLocationReceiver() {
        unregisterReceiver(currentLocationReceiver);
    }
}

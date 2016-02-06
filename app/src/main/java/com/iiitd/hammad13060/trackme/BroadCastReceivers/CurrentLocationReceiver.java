package com.iiitd.hammad13060.trackme.BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iiitd.hammad13060.trackme.MyLocationInterface;
import com.iiitd.hammad13060.trackme.helpers.MyLocation;

public class CurrentLocationReceiver extends BroadcastReceiver {

    private static final String TAG = "CurrentLocationRec";

    MyLocationInterface myLocationInterface = null;

    public CurrentLocationReceiver() {

    }

    public CurrentLocationReceiver(MyLocationInterface context) {
        myLocationInterface = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received current location broadcast");
        if (intent.hasExtra(MyLocation.EXTRA_MY_LONG) && intent.hasExtra(MyLocation.EXTRA_MY_LAT)) {
            Log.d(TAG, "have required latitude and longitude");
            double lat = intent.getDoubleExtra(MyLocation.EXTRA_MY_LAT, 0);
            double longi = intent.getDoubleExtra(MyLocation.EXTRA_MY_LONG, 0);
            myLocationInterface.myLocationUpdate(lat, longi);
        }
    }
}

package com.iiitd.hammad13060.trackme.BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iiitd.hammad13060.trackme.Interfaces.DestinationReachedInterface;

public class DestinationReachedReceiver extends BroadcastReceiver {

    private static final String TAG = DestinationReachedReceiver.class.getName();

    public static final String DESTINATION_REACHED_FILTER_TAG = "com.iiitd.hammad13060.trackme.BroadCastReceivers"
            + DestinationReachedReceiver.class.getName();

    private DestinationReachedInterface destinationReachedInterface;
    public DestinationReachedReceiver(DestinationReachedInterface destinationReachedInterface) {
        this.destinationReachedInterface = destinationReachedInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "destination reached event triggered");
        if (destinationReachedInterface != null) {
            destinationReachedInterface.onDestinationReached(intent);
            Log.d(TAG, "interface is null");
        } else {
            Log.d(TAG, "interface is null");
        }
    }
}

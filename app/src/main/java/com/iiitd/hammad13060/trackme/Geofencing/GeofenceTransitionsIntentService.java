package com.iiitd.hammad13060.trackme.Geofencing;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.iiitd.hammad13060.trackme.BroadCastReceivers.DestinationReachedReceiver;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = GeofenceTransitionsIntentService.class.getName();
    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            /*String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());*/
            String errorMessage = "geofencing event has error";
            Log.e(TAG, errorMessage);
            return;
        }

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d(TAG, "device has entered the geofence");
            sendDestinationReachedBroadcast();
        } else {
            // Log the error.
            Log.e(TAG, "error with transition type");
        }
    }

    private void sendDestinationReachedBroadcast() {
        Intent intent = new Intent(DestinationReachedReceiver.DESTINATION_REACHED_FILTER_TAG);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }


}

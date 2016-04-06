package com.iiitd.hammad13060.trackme.cloudeMessaging;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GcmPubSub;
import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.dbHandler.ArchivedDBHandler;
import com.iiitd.hammad13060.trackme.dbHandler.JourneyDBHandler;
import com.iiitd.hammad13060.trackme.entities.Journey;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.services.journeyServiceHelper.JourneyConstants;

import java.io.IOException;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    public static final String _MSG_TYPE = "_msg_type";
    public static final String _JOURNEY_TOPIC = "_topic";
    public static final String _FROM = "_from";
    public static final String _SRC_LAT = "_src_lat";
    public static final String _SRC_LONG = "_src_long";
    public static final String _DST_LAT = "_dst_lat";
    public static final String _DST_LONG = "_dst_long";
    public static final String _CURRENT_LAT = "_current_lat";
    public static final String _CURRENT_LONG = "_current_long";
    public static final String _UPDATE_TYPE = "_update_type";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "onMessageReceived() called");
        if (from.startsWith("/topics/")) {
            onLocationUpdateReceived(from, data);
        } else {
            onNewJourneyDataReceived(from, data);
        }

    }

    private void onNewJourneyDataReceived(String from, Bundle data) {
        try {
            String msg_type = data.getString(_MSG_TYPE);

            Log.d(TAG, "onJourneyDataReceived() called");
            if (msg_type.equals("new-journey")) {
                String topic = data.getString(_JOURNEY_TOPIC);

                subscribeToTopic(topic);
                RegistrationIntentService.topics.add(topic);

                String _from = data.getString(_FROM);
                String src_lat = data.getString(_SRC_LAT);
                String src_long = data.getString(_SRC_LONG);

                String dst_lat = data.getString(_DST_LAT);
                String dst_long = data.getString(_DST_LONG);

                String current_lat = data.getString(_CURRENT_LAT);
                String current_long = data.getString(_CURRENT_LONG);

                Journey journey = new Journey(
                        topic,
                        _from,
                        Double.parseDouble(src_lat),
                        Double.parseDouble(src_long),
                        Double.parseDouble(dst_lat),
                        Double.parseDouble(dst_long),
                        Double.parseDouble(current_lat),
                        Double.parseDouble(current_long)
                );

                Log.d(TAG, journey.toString());

                JourneyDBHandler dbHandler = new JourneyDBHandler(getApplicationContext(), null, null, 1);
                dbHandler.insertJourney(journey);

                Log.d(TAG, "sending new journey broadcast");
                sendNewJourneyBroadcast(data);

                newJourneyNotification();
            }
        } catch(IOException e) {
            Log.d(TAG, "topic subscription failed");
            e.printStackTrace();
        }
    }


    private void onLocationUpdateReceived(String from, Bundle data) {
        Log.d(TAG, "onLocationUpdateReceived() called");
        String update_type = data.getString(_UPDATE_TYPE);
        if (update_type.equals(JourneyConstants.UPDATE_TYPE_LOCATION)) {
            String topic = data.getString(_JOURNEY_TOPIC);
            String _from = data.getString(_FROM);
            double current_lat = Double.parseDouble(data.getString(_CURRENT_LAT));
            double current_long = Double.parseDouble(data.getString(_CURRENT_LONG));

            Journey journey = new Journey(
                    topic,
                    _from,
                    current_lat,
                    current_long
            );
            Log.d("LocationUpdate: ", journey.toString());

            JourneyDBHandler dbHandler = new JourneyDBHandler(this, null, null, 1);
            dbHandler.updateCurrentLocation(journey);

            sendLocationUpdateBroadcast(data);
        } else if (update_type.equals(JourneyConstants.UPDATE_TYPE_TERMINATION)) {

            String topic = data.getString(_JOURNEY_TOPIC);
            String _from = data.getString(_FROM);
            double current_lat = Double.parseDouble(data.getString(_CURRENT_LAT));
            double current_long = Double.parseDouble(data.getString(_CURRENT_LONG));

            Journey journey = new Journey(
                    topic,
                    _from,
                    current_lat,
                    current_long
            );
            Log.d("LocationUpdate: ", journey.toString());

            JourneyDBHandler dbHandler = new JourneyDBHandler(this, null, null, 1);
            dbHandler.updateCurrentLocation(journey);

            //bug from
            //String _from = data.getString(_FROM);
            String _journey_topic = data.getString(_JOURNEY_TOPIC);
            journey = dbHandler.deleteJourney(_journey_topic);

            if (journey != null) {

                ArchivedDBHandler archivedDBHandler = new ArchivedDBHandler(this, null, null, 1);
                archivedDBHandler.insertJourney(journey);

                sendLocationUpdateBroadcast(data);
                sendJourneyCompleteBroadcast(data);

                journeyCompleteNotification();
            }
        }
    }


    private void sendNewJourneyBroadcast(Bundle data) {
        Intent intent = new Intent(JourneyConstants.NEW_JOURNEY_NOTIFIED);
        intent.putExtra(JourneyConstants.EXTRA_JOURNEY_DATA, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendLocationUpdateBroadcast(Bundle data) {
        Intent intent = new Intent(JourneyConstants.LOCATION_UPDATE_NOTIFIED);
        intent.putExtra(JourneyConstants.EXTRA_JOURNEY_DATA, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendJourneyCompleteBroadcast(Bundle data) {
        Intent intent = new Intent(JourneyConstants.JOURNEY_COMPLETION_NOTIFIED);
        intent.putExtra(JourneyConstants.EXTRA_JOURNEY_DATA, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void subscribeToTopic(String topic) throws IOException {
        Log.d(TAG, "subscribing to topic: " + topic);
        SharedPreferences token_file = getApplicationContext().getSharedPreferences(Constants.PREFERENCE_TOKEN_FILE,
                Context.MODE_PRIVATE);
        String gcm_token = token_file.getString(QuickstartPreferences.GCM_TOKEN, "mo token");

        if (!gcm_token.equals("no token")){
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(gcm_token, "/topics/" + topic, null);
        }
    }


    private void newJourneyNotification() {
        Log.d("NotificationManager", "newJourneyNotification() called");
        //Icon icon = Icon.createWithResource(this, R.mipmap.ic_launcher);
        int id = 1;
        Notification noti = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("You have a new journey to track")
                .setContentText("New journey")
                .setSmallIcon(R.drawable.destination)
                //.setLargeIcon(icon)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(id, noti);
    }

    private void journeyCompleteNotification() {
        Log.d("NotificationManager", "journeyCompleteNotification() called");
        int id = 0;
        //Icon icon = Icon.createWithResource(this, R.mipmap.ic_launcher);
        Notification noti = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("A journey being tracked completed")
                .setContentText("Destination Reached")
                .setSmallIcon(R.drawable.destination)
                //.setLargeIcon(icon)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(id, noti);
    }
}
package com.iiitd.hammad13060.trackme.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.iiitd.hammad13060.trackme.BroadCastReceivers.CurrentLocationReceiver;
import com.iiitd.hammad13060.trackme.BroadCastReceivers.JourneyReadyReceiver;
import com.iiitd.hammad13060.trackme.Fragments.JourneyFragment;
import com.iiitd.hammad13060.trackme.MyLocationInterface;
import com.iiitd.hammad13060.trackme.activities.MainActivity;
import com.iiitd.hammad13060.trackme.helpers.Authentication;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.Contact;
import com.iiitd.hammad13060.trackme.helpers.JSONRequest;
import com.iiitd.hammad13060.trackme.helpers.MyLocation;
import com.iiitd.hammad13060.trackme.services.journeyServiceHelper.JourneyConstants;
import com.iiitd.hammad13060.trackme.services.journeyServiceHelper.JourneyNotificationToContacts;
import com.iiitd.hammad13060.trackme.services.journeyServiceHelper.NotifyContactsTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JourneyService extends Service implements MyLocationInterface {

    private static final String TAG = "JourneyService";
    private static final String WEB_URL = Constants.SERVER_URL + "getDirections/";

    public static final String EXTRA_DIRECTIONS = "com.iiitd.hammad13060.trackme.services.EXTRA_DIRECTIONS";

    private static double SrctLat;
    private static double SrcLong;
    private static double DestLat;
    private static double DestLong;

    public static boolean journeyRunning = false;

    private static double currentLat;
    private static double currentLong;

    private List<Contact> contactList = null;

    private BroadcastReceiver currentLocationReceiver = null;
    private BroadcastReceiver peopleNotifiedReceiver = null;

    private MyLocation myLocation;


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
        registerPeopleNotifiedReceiver();
        myLocation = new MyLocation(getApplicationContext());
        /*Location currentLocation = myLocation.getMyCurrentLocation();
        currentLat = currentLocation.getLatitude();
        currentLong = currentLocation.getLongitude();*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);

        if (intent != null) {

            Log.d(TAG, intent.toString());

        SrctLat = intent.getDoubleExtra(MainActivity.EXTRA_SRC_LAT, 0);
        SrcLong = intent.getDoubleExtra(MainActivity.EXTRA_SRC_LONG, 0);
        DestLat = intent.getDoubleExtra(MainActivity.EXTRA_DST_LAT, 0);
        DestLong = intent.getDoubleExtra(MainActivity.EXTRA_DST_LONG, 0);

        //dummy
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.EXTRA_CONTACT_LIST);
        contactList = new ArrayList<>();
        for (int i = 0; i < parcelables.length; i++)
            contactList.add((Contact)parcelables[i]);

        currentLat = SrctLat;
        currentLong = SrcLong;

        try {
            notifyContacts();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "Not able to notify contacts");
        }

        journeyRunning = true;

        getDirections();
        }


        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        journeyRunning = false;
        unregisterMyLocationReceiver();
        unregisterPeopleNotifiedReceiver();
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

    private void sendJourneyReadyBroadcast() {
        Intent broadCastIntent = new Intent();
        broadCastIntent.putExtra(MainActivity.EXTRA_SRC_LAT, SrctLat);
        broadCastIntent.putExtra(MainActivity.EXTRA_SRC_LONG, SrcLong);
        broadCastIntent.putExtra(MainActivity.EXTRA_DST_LAT, DestLat);
        broadCastIntent.putExtra(MainActivity.EXTRA_DST_LONG,DestLong);
        broadCastIntent.putExtra(MyLocation.EXTRA_MY_LAT, currentLat);
        broadCastIntent.putExtra(MyLocation.EXTRA_MY_LONG, currentLong);
        broadCastIntent.setAction(JourneyReadyReceiver.ACTION_VALUE);
        sendBroadcast(broadCastIntent);
    }

    private void getDirections() {

        try {
            JSONObject requestObject = getDirectionsJSON();

            GetDirectionsTask getDirectionsTask = new GetDirectionsTask();
            getDirectionsTask.execute(requestObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JSONObject getDirectionsJSON() throws JSONException {
        JSONObject directionsObject = new JSONObject();
        JSONObject latlangObject = new JSONObject();

        directionsObject.put(Constants.JSON_CONTACT_NUMBER, Constants.getUserID(getApplicationContext()));
        directionsObject.put(Constants.JSON_TOKEN, Constants.getFileToken(getApplicationContext()));

        latlangObject.put(Constants.JSON_SRC_LAT, SrctLat);
        latlangObject.put(Constants.JSON_SRC_LONG, SrcLong);
        latlangObject.put(Constants.JSON_DST_LAT, DestLat);
        latlangObject.put(Constants.JSON_DST_LONG, DestLong);

        directionsObject.put(Constants.JSON_DIRECTIONS, latlangObject);


        return directionsObject;
    }


    public static double getSrctLat() {
        return SrctLat;
    }

    public static double getSrcLong() {
        return SrcLong;
    }


    public static double getDestLat() {
        return DestLat;
    }


    public static double getDestLong() {
        return DestLong;
    }

    public static double getCurrentLat() {
        return currentLat;
    }

    public static double getCurrentLong() {
        return currentLong;
    }

    private class GetDirectionsTask extends AsyncTask<JSONObject, Void, Void> {

        private Response.Listener<JSONObject> responseListener = null;
        private Response.ErrorListener errorListener = null;
        private RequestQueue requestQueue = null;
        @Override
        protected Void doInBackground(JSONObject... objects) {
            initResponseListener();
            initErrorListener();
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            //volley request object
            if (objects.length > 0) {
                JSONObject requestObject = objects[0];
                JSONRequest request = new JSONRequest(Request.Method.POST, WEB_URL, null,
                        responseListener, errorListener, requestObject);
                requestQueue.add(request);
            }
            return null;
        }

        private void initResponseListener() {
            responseListener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "directions response json: " + response.toString());
                        boolean notBlocked = !response.getBoolean(Constants.JSON_BLOCKED);
                        if (notBlocked) {
                            boolean fetched = response.getBoolean(Constants.JSON_FETCHED);

                            if (fetched) {
                                JSONObject directionsJSON = response.getJSONObject(Constants.JSON_DIRECTIONS);

                                Intent journeyReadyIntent = new Intent();
                                journeyReadyIntent.setAction(JourneyReadyReceiver.ACTION_VALUE);
                                journeyReadyIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                journeyReadyIntent.putExtra(EXTRA_DIRECTIONS, directionsJSON.toString());
                                sendBroadcast(journeyReadyIntent);

                            } else {

                            }
                        } else {
                            Log.d(TAG, "directions api blocked for usage !!! Try again.");
                            //Constants.showLongToast(getApplicationContext(), "directions api blocked for usage !!! Try again.");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestQueue.stop();
                }
            };
        }

        private void initErrorListener() {
            errorListener = new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    requestQueue.stop();
                }
            };
        }
    }


    ///////////////////////////////////////notifying people/////////////////////////////////////////

    private void notifyContacts() throws JSONException {
        JSONArray _numbers = new JSONArray();

        SharedPreferences token_file = getSharedPreferences(Constants.PREFERENCE_TOKEN_FILE, Context.MODE_PRIVATE);
        String id = token_file.getString(Constants.PREFERENCE_TOKEN_FILE_ID, "no number");
        String token = token_file.getString(Constants.PREFERENCE_TOKEN_FILE_TOKEN, "1");

        if (contactList != null) {
            for (int i = 0; i < contactList.size(); i++) {
                Contact contact = contactList.get(i);
                List<String> phoneList = contact.phoneList;

                for (String number: phoneList) {
                    if (numberNotInList(_numbers, number)) _numbers.put(number);
                    Log.d(TAG, "_numbers: " + _numbers);
                }
            }

            JSONObject requestObject = new JSONObject();
            requestObject.put(Constants.JSON_CONTACT_NUMBER, id);
            requestObject.put(Constants.JSON_TOKEN, token);
            requestObject.put(Contact.CONTACT_JSON_NUMBERS, _numbers);

            requestObject.put(Constants.JSON_SRC_LAT, SrctLat);
            requestObject.put(Constants.JSON_SRC_LONG, SrcLong);

            requestObject.put(Constants.JSON_DST_LAT, DestLat);
            requestObject.put(Constants.JSON_DST_LONG, DestLong);

            requestObject.put(Constants.JSON_CURRENT_LAT, currentLat);
            requestObject.put(Constants.JSON_CURRENT_LONG, currentLong);

            //NotifyContactsTask notifyContactsTask = new NotifyContactsTask(getApplicationContext());
            //notifyContactsTask.execute(requestObject);

            notifyPeopleOnServer(requestObject);
        }
    }

    boolean numberNotInList(JSONArray list, String number) {
        boolean result = true;

        for (int i = 0; i < list.length(); i++) {
            try {
                if (list.getString(i).equals(number)) {
                    result = false;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    private RequestQueue requestQueue = null;

    public void notifyPeopleOnServer(JSONObject request) {
        Response.Listener<JSONObject> responseListener = null;
        Response.ErrorListener errorListener = null;



        responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "server response: " + response.toString());
                try {
                    boolean blocked = response.getBoolean(Constants.JSON_BLOCKED);
                    if (!blocked) {
                        Log.d(TAG, "user is not blocked");
                        String journey_topic = response.getString(JourneyConstants.JSON_JOURNEY_TOPIC);

                        SharedPreferences journey_file = getApplicationContext().getSharedPreferences(
                                JourneyConstants.PREFERENCE_JOURNEY_FILE,
                                Context.MODE_PRIVATE
                        );

                        SharedPreferences.Editor editor = journey_file.edit();
                        editor.putString(JourneyConstants.PREFERENCE_JOURNEY_FILE_TOPIC, journey_topic);
                        editor.commit();
                    } else {
                        //this method is stub
                        //implement it
                        Log.d(TAG, "user is blocked");
                        Authentication.enterRegistrationActivity(getApplicationContext());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "json exception: relevant fields not present maybe");
                }
                sendPeopleNotifiedBroadcast();

                requestQueue.stop();
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "not able to reach server");
                sendPeopleNotifiedBroadcast();
                requestQueue.stop();
            }
        };

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONRequest jsonRequest = new JSONRequest(Request.Method.POST, Constants.SERVER_URL + "pubsub/newJourney", null,
                responseListener, errorListener, request);
        requestQueue.add(jsonRequest);
    }

    protected void sendPeopleNotifiedBroadcast() {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(JourneyConstants.PEOPLE_NOTIFIED));
    }



//////////////////////////////////////////////////Location and journey updates to server (Long polling)//////////////////////////////////////

    // broadcast receivers for notification regarding if people have been notified or not
    // and to check if we have journey topic to relay updates on

    private void initPeopleNotifiedReceiver() {
        peopleNotifiedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sendLocationUpdateToServer();
            }
        };
    }

    private void registerPeopleNotifiedReceiver() {
        if (peopleNotifiedReceiver == null) {
            initPeopleNotifiedReceiver();
            IntentFilter filter = new IntentFilter(JourneyConstants.PEOPLE_NOTIFIED);
            LocalBroadcastManager.getInstance(this).registerReceiver(peopleNotifiedReceiver, filter);
        }
    }

    private void unregisterPeopleNotifiedReceiver() {
        if (peopleNotifiedReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(peopleNotifiedReceiver);
        }
    }


    private void sendLocationUpdateToServer() {
        SharedPreferences journey_file = getApplicationContext().getSharedPreferences(
                JourneyConstants.PREFERENCE_JOURNEY_FILE,
                Context.MODE_PRIVATE
        );

        String journey_topic = journey_file.getString(JourneyConstants.PREFERENCE_JOURNEY_FILE_TOPIC, "no topic");

        SharedPreferences token_file = getApplicationContext().getSharedPreferences(
                Constants.PREFERENCE_TOKEN_FILE,
                Context.MODE_PRIVATE
        );

        String id = token_file.getString(Constants.PREFERENCE_TOKEN_FILE_ID, "no id");
        String token = token_file.getString(Constants.PREFERENCE_TOKEN_FILE_TOKEN, "1");

        String update_type = JourneyConstants.UPDATE_TYPE_LOCATION;

        JSONObject requestObject = new JSONObject();

        try {
            requestObject.put(Constants.JSON_CONTACT_NUMBER, id);
            requestObject.put(Constants.JSON_TOKEN, token);
            requestObject.put(JourneyConstants.JSON_JOURNEY_TOPIC, journey_topic);
            requestObject.put(JourneyConstants.JSON_UPDATE_TYPE, update_type);
            requestObject.put(Constants.JSON_CURRENT_LAT, currentLat);
            requestObject.put(Constants.JSON_CURRENT_LONG, currentLong);

            sendJourneyNotification(requestObject);

            //JourneyNotificationToContacts journeyNotificationToContacts = new JourneyNotificationToContacts(getApplicationContext());
            //journeyNotificationToContacts.execute(requestObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendJourneyNotification(JSONObject request) {
        Response.Listener<JSONObject> responseListener = null;
        Response.ErrorListener errorListener = null;



        responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean blocked = response.getBoolean(Constants.JSON_BLOCKED);
                    if (!blocked) {
                        Log.d(TAG, "user not blocked");
                        boolean notified = response.getBoolean(JourneyConstants.JSON_NOTIFIED);
                        if (notified) {
                            Log.d(TAG, "all users notified");
                        } else {
                            Log.d(TAG, "server failed to notify every user");
                        }
                    } else {
                        Log.d(TAG, "user not allowed to access api");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendPeopleNotifiedBroadcast();
                requestQueue.stop();
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "not able to reach server");
                sendPeopleNotifiedBroadcast();
                requestQueue.stop();
            }
        };

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONRequest jsonRequest = new JSONRequest(Request.Method.POST, Constants.SERVER_URL + "pubsub/updateTopic", null,
                responseListener, errorListener, request);
        requestQueue.add(jsonRequest);
    }
}
package com.iiitd.hammad13060.trackme.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.iiitd.hammad13060.trackme.BroadCastReceivers.JourneyReadyReceiver;
import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.cloudeMessaging.MyGcmListenerService;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.Contact;
import com.iiitd.hammad13060.trackme.helpers.JSONRequest;
import com.iiitd.hammad13060.trackme.services.JourneyService;
import com.iiitd.hammad13060.trackme.services.journeyServiceHelper.JourneyConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JourneyActivity extends AppCompatActivity {

    private static final String TAG = JourneyActivity.class.getName();

    private static final String WEB_URL = Constants.SERVER_URL + "getDirections/";

    private String journey_topic;
    private String from;

    private double src_lat;
    private double src_long;

    private double dst_lat;
    private double dst_long;

    private double current_lat;
    private double current_long;

    private OnMapReadyCallback onMapReadyCallback = null;

    private boolean mapReady = false;
    private Marker currentLocationMarker;
    private Marker sourceLocationMarker;
    private Marker destinationLocationMarker;
    private GoogleMap map;

    JSONObject directions;



    private String source_text;
    private String destination_text;
    private String duration_text;
    private String distance_text;


    BroadcastReceiver locationUpdateReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initJourneyData();
        setMap();
        registerLocationUpdateReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterLocationUpdateReceiver();
    }

    private void initJourneyData() {
        Intent intent = getIntent();

        journey_topic = intent.getStringExtra(MyGcmListenerService._JOURNEY_TOPIC);
        from = intent.getStringExtra(MyGcmListenerService._FROM);

        src_lat = intent.getDoubleExtra(MyGcmListenerService._SRC_LAT, 0.0);
        src_long = intent.getDoubleExtra(MyGcmListenerService._SRC_LONG, 0.0);

        dst_lat = intent.getDoubleExtra(MyGcmListenerService._DST_LAT, 0.0);
        dst_long = intent.getDoubleExtra(MyGcmListenerService._DST_LONG, 0.0);

        current_lat = intent.getDoubleExtra(MyGcmListenerService._CURRENT_LAT, 0.0);
        current_long = intent.getDoubleExtra(MyGcmListenerService._CURRENT_LONG, 0.0);
    }


    public void setMap() {
        initOnMapReadyCallback();
        mapReady = false;
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(onMapReadyCallback);
    }

    private void initOnMapReadyCallback() {

        onMapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapReady = true;
                map = googleMap;
                //map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                currentLocationMarker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(current_lat, current_long))
                                .title("Your current location.")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                );

                sourceLocationMarker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(src_lat, src_long))
                                .title("Source.")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                );

                destinationLocationMarker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(dst_lat, dst_long))
                                .title("Destination.")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );


                //Log.d(TAG, "directions json in callback :" + directions.toString());
                fetchDirections();

            }
        };
    }

    private void plotDirections(GoogleMap googleMap, JSONObject directions) {
        StreetViewPanorama streetViewPanorama;
        List<LatLng> cordList = new ArrayList<>();
        try {
            JSONArray steps = directions.getJSONArray("routes")
                    .getJSONObject(0).
                            getJSONArray("legs")
                    .getJSONObject(0).
                            getJSONArray("steps");

            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                double start_lat = step.getJSONObject("start_location").getDouble("lat");
                double start_lng = step.getJSONObject("start_location").getDouble("lng");

                double end_lat = step.getJSONObject("end_location").getDouble("lat");
                double end_lng = step.getJSONObject("end_location").getDouble("lng");

                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(new LatLng(start_lat, start_lng), new LatLng(end_lat, end_lng))
                        .width(5)
                        .color(Color.BLUE)
                        .visible(true);
                googleMap.addPolyline(polylineOptions);

            }
            Log.d(TAG, "DIRECTIONS PLOTTED");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //fetching directions json;

    private Response.Listener<JSONObject> responseListener = null;
    private Response.ErrorListener errorListener = null;
    private RequestQueue requestQueue = null;


    private JSONObject getDirectionsJSON() throws JSONException {
        JSONObject directionsObject = new JSONObject();
        JSONObject latlangObject = new JSONObject();

        directionsObject.put(Constants.JSON_CONTACT_NUMBER, Constants.getUserID(getApplicationContext()));
        directionsObject.put(Constants.JSON_TOKEN, Constants.getFileToken(getApplicationContext()));

        latlangObject.put(Constants.JSON_SRC_LAT, src_lat);
        latlangObject.put(Constants.JSON_SRC_LONG, src_long);
        latlangObject.put(Constants.JSON_DST_LAT, dst_lat);
        latlangObject.put(Constants.JSON_DST_LONG, dst_long);

        directionsObject.put(Constants.JSON_DIRECTIONS, latlangObject);


        return directionsObject;
    }


    private void fetchDirections() {
        initResponseListener();
        initResponseListener();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        try {
            JSONObject requestObject = getDirectionsJSON();
            JSONRequest request = new JSONRequest(Request.Method.POST, WEB_URL, null,
                    responseListener, errorListener, requestObject);
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                            Constants.showShortToast(getApplicationContext(), "directions fetched from server");
                            JSONObject directionsJSON = response.getJSONObject(Constants.JSON_DIRECTIONS);
                            directions = directionsJSON;
                            plotDirections(map, directionsJSON);
                            initJourneyDisplayInfo();
                            setJourneyDataOnUI();
                        } else {
                            Constants.showShortToast(getApplicationContext(), "not able to fetch directions");
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
                Constants.showShortToast(getApplicationContext(),
                        "cannot establish connection with server for fetching directions !!!");
                Log.d(TAG, "VOLLEY FETCH DIRECTIONS ERROR: " + error.toString());
                requestQueue.stop();
            }
        };
    }

    private void initJourneyDisplayInfo() {
        try {
            JSONObject leg = directions.getJSONArray("routes")
                    .getJSONObject(0).
                            getJSONArray("legs")
                    .getJSONObject(0);

            source_text = leg.getString("start_address");
            destination_text = leg.getString("end_address");
            distance_text = leg.getJSONObject("distance").getString("text");
            duration_text = leg.getJSONObject("duration").getString("text");

            zoomIntoPath(current_lat, current_long);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setJourneyDataOnUI() {
        //TextView view = (TextView)findViewById(R.id.start_address_text);
        Button bText = (Button)findViewById(R.id.source_button);
        source_text = "Source: " + source_text;
        bText.setText(source_text);

        //view = (TextView)findViewById(R.id.end_address_text);
        bText = (Button)findViewById(R.id.destination_button);
        destination_text = "Destination: "+ destination_text;
        bText.setText(destination_text);


        //TextView view = (TextView)findViewById(R.id.distance_text);
        bText = (Button)findViewById(R.id.durationAndTime);
        //view.setText(distance_text);
        distance_text = "Distance: " + distance_text + "\n";
        //view = (TextView)findViewById(R.id.duration_text);
        duration_text = distance_text + "Duration: " + duration_text;
        bText.setText(duration_text);
    }


    //////////////////////////////registering/un registering location update broadcast

    private void registerLocationUpdateReceiver() {
        if (locationUpdateReceiver == null) {
            initLocationUpdateBroadcastReceiver();
            IntentFilter filter = new IntentFilter(JourneyConstants.LOCATION_UPDATE_NOTIFIED);
            LocalBroadcastManager.getInstance(this).registerReceiver(locationUpdateReceiver, filter);
        }
    }

    private void unregisterLocationUpdateReceiver() {
        if (locationUpdateReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(locationUpdateReceiver);
    }

    private void initLocationUpdateBroadcastReceiver() {
        locationUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle data = intent.getBundleExtra(JourneyConstants.EXTRA_JOURNEY_DATA);
                String topic = data.getString(MyGcmListenerService._JOURNEY_TOPIC);
                if (topic.equals(journey_topic)) {
                    if (mapReady) {
                        double latitude = Double.parseDouble(data.getString(MyGcmListenerService._CURRENT_LAT));
                        double longitude = Double.parseDouble(data.getString(MyGcmListenerService._CURRENT_LONG));
                        currentLocationMarker.setPosition(new LatLng(latitude, longitude));
                        zoomIntoPath(latitude, longitude);
                    }
                }
            }
        };
    }

    private void zoomIntoPath(double lat, double lon) {
        LatLng currentLatLng = new LatLng(lat, lon);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLatLng)      // Sets the center of the map to Mountain View
                .zoom(14)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}

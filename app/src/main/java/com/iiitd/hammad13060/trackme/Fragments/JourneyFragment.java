package com.iiitd.hammad13060.trackme.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.iiitd.hammad13060.trackme.BroadCastReceivers.CurrentLocationReceiver;
import com.iiitd.hammad13060.trackme.Interfaces.MyLocationInterface;
import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.entities.Journey;
import com.iiitd.hammad13060.trackme.services.JourneyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JourneyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JourneyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JourneyFragment extends Fragment implements MyLocationInterface {

    private static final String TAG = "JourneyFragment";

    private double currentLat;
    private double currentLong;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    private OnMapReadyCallback onMapReadyCallback = null;
    private BroadcastReceiver currentLocationReceiver = null;
    private BroadcastReceiver journeyReadyReciever = null;
    private BroadcastReceiver journeyFinishedReceiver = null;
    private boolean mapReady = false;

    private GoogleMap map = null;

    private Marker currentLocationMarker = null;

    private static JSONObject directions;

    private GeoJsonLayer geoJsonLayer;

    private String source_text;
    private String destination_text;
    private String duration_text;
    private String distance_text;

    private static View basic_view = null;
    private static View map_view = null;

    private MapFragment mapFragment;

    public JourneyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JourneyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JourneyFragment newInstance(String param1, String param2) {
        JourneyFragment fragment = new JourneyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            View tmpView;
            Log.d(TAG, "journey status: " + JourneyService.journeyRunning);
            if (JourneyService.journeyRunning){
                Log.d(TAG, "setting map ui");
                if (map_view == null)
                {
                    map_view = inflater.inflate(R.layout.fragment_journey_map, container, false);
                    return map_view;
                }
                else {
                    //map_view.refreshDrawableState();
                    return map_view;
                }
            }
            else {
                Log.d(TAG, "setting no current journey ui");
                if (basic_view == null) {
                    basic_view = inflater.inflate(R.layout.fragment_journey, container, false);
                    return basic_view;
                }
                else{
                    //basic_view.refreshDrawableState();
                    return basic_view;
                }
            }
        } catch(InflateException e) {
            Log.d(TAG, "layout already inflated");
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (JourneyService.journeyRunning) {
            mapReady = false;
            initJourneyDisplayInfo();
            setJourneyDataOnUI();
            setMap();
        }
        registerMyLocationReceiver();
        registerJourneyFinishedReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterMyLocationReceiver();
        unregisterJourneyFinishedReceiver();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void resetJourney(Intent intent) {
        try {
            directions = new JSONObject(intent.getStringExtra(JourneyService.EXTRA_DIRECTIONS));
            reinitFragment();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reinitFragment() {
        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().detach(this).attach(this).commit();
    }

    public void setMap() {

            if (mapFragment == null) {
                initOnMapReadyCallback();

                mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
                if (mapFragment != null) {
                    Log.d(TAG, "mapFragment not null");
                    mapFragment.getMapAsync(onMapReadyCallback);
                }
                else {
                    Log.d(TAG, "mapFragment is null");
                    mapFragment = new MapFragment();
                    getActivity().getFragmentManager()
                            .beginTransaction()
                            .add(R.id.map, mapFragment, "map").commit();
                    mapFragment.getMapAsync(onMapReadyCallback);
                }
            } else {
                mapFragment.getMapAsync(onMapReadyCallback);
            }
    }

    private void initOnMapReadyCallback() {

        if (onMapReadyCallback == null) {

            onMapReadyCallback = new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mapReady = true;
                    map = googleMap;
                    map.clear();
                    //map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    currentLocationMarker = googleMap.addMarker(
                            new MarkerOptions()
                                    .position(new LatLng(JourneyService.getCurrentLat(), JourneyService.getCurrentLong()))
                                    .title("Your current location.")
                    );
                    Log.d(TAG, "directions json in callback :" + directions.toString());

                    plotDirections(googleMap, directions);
                }


            };
        }
    }

    private void registerMyLocationReceiver() {
        if (currentLocationReceiver == null) {
            currentLocationReceiver = new CurrentLocationReceiver(this);
            IntentFilter intentFilter = new IntentFilter("com.iiitd.hammad13060.trackme.BroadCastReceivers.CurrentLocationReceiver");
            getActivity().registerReceiver(currentLocationReceiver, intentFilter);
        }
    }

    private void unregisterMyLocationReceiver() {
        if (currentLocationReceiver != null) {
            getActivity().unregisterReceiver(currentLocationReceiver);
            currentLocationReceiver = null;
        }

    }

    @Override
    public void myLocationUpdate(double latitude, double longitude) {
        Log.d(TAG, "Latitude: " + latitude + " Longitude: " + longitude);
        currentLat = latitude;
        currentLong = longitude;
        updateCurrentLocationMarker();
        zoomIntoPath(currentLat, currentLong);
    }

    private void updateCurrentLocationMarker() {
        if (mapReady) {
            currentLocationMarker.setPosition(new LatLng(currentLat, currentLong));
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLong), 14.0f));
        }
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

                //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLong), 14.0f));

            }
            Log.d(TAG, "DIRECTIONS PLOTTED");
            zoomIntoPath(JourneyService.getCurrentLat(), JourneyService.getCurrentLong());
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setJourneyDataOnUI() {
        //TextView view = (TextView)getView().findViewById(R.id.start_address_text);
        Button bText = (Button)getView().findViewById(R.id.source_button);
        source_text = "Source: " + source_text;
        bText.setText(source_text);
        //view.setText(source_text);

        //view = (TextView)getView().findViewById(R.id.end_address_text);
        bText = (Button)getView().findViewById(R.id.destination_button);
        destination_text = "Destination: "+ destination_text;
        bText.setText(destination_text);
        //view.setText(destination_text);

        //TextView view = (TextView)findViewById(R.id.distance_text);
        bText = (Button)getView().findViewById(R.id.durationAndTime);
        //view.setText(distance_text);
        distance_text = "Distance: " + distance_text + "\n";
        //view = (TextView)findViewById(R.id.duration_text);
        duration_text = distance_text + "Duration: " + duration_text;
        bText.setText(duration_text);


    }

    /////////////////////////////////////registering journey finished receiver /////////////////////////////////

    private void initJourneyFinishedReceiver() {
        journeyFinishedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.arrowroute));
                reinitFragment();
            }
        };
    }

    private void registerJourneyFinishedReceiver() {
        if (journeyFinishedReceiver == null) {
            initJourneyFinishedReceiver();
            IntentFilter filter = new IntentFilter(JourneyService.JOURNEY_COMPLETE_BROADCAST_TAG);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(journeyFinishedReceiver,filter);
        }
    }

    private void unregisterJourneyFinishedReceiver() {
        if (journeyFinishedReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(journeyFinishedReceiver);
            journeyFinishedReceiver = null;
        }
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

package com.iiitd.hammad13060.trackme.SourceDestinationClasses;

import com.couchbase.lite.Context;
import com.iiitd.hammad13060.trackme.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.iiitd.hammad13060.trackme.dbHandler.UsersDBHandler;
import com.iiitd.hammad13060.trackme.helpers.Constants;


public class MyDestination extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private TextView mNameTextView;
    private TextView mAddressTextView;
    private TextView mIdTextView;
    private TextView mPhoneTextView;
    private TextView mWebTextView;
    private TextView mAttTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    double Destlat = 0,DestLon = 0;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

//    UsersDBHandler usersDB = new UsersDBHandler(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_destination);

        Bundle NameData = getIntent().getExtras();
        if (NameData == null){
            return;
        }

        String sourceUser = NameData.getString("sourceText");
        final EditText TShow = (EditText) findViewById(R.id.editText);
        TShow.setText(sourceUser);

        mGoogleApiClient = new GoogleApiClient.Builder(MyDestination.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mIdTextView = (TextView) findViewById(R.id.place_id);
        mNameTextView = (TextView) findViewById(R.id.name);
        mAddressTextView = (TextView) findViewById(R.id.address);
        mIdTextView = (TextView) findViewById(R.id.place_id);
        mPhoneTextView = (TextView) findViewById(R.id.phone);
        mWebTextView = (TextView) findViewById(R.id.web);
        mAttTextView = (TextView) findViewById(R.id.att);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);



        selectContacts();


        Button startJourney = (Button) findViewById(R.id.start_journey);
        startJourney.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    //returnIntent.putExtra("result",result);
                    if(Destlat == 0&& DestLon ==0)
                    {
                        Constants.showLongToast(getApplicationContext(), "Please choose corect Destination");
                    }else {
                        returnIntent.putExtra("resultDestLat1",Destlat);
                        returnIntent.putExtra("resultDestLon2",DestLon);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }

            }
        });
    }



    public void selectContacts()
    {
        final Intent i = new Intent(this,SelectContacts.class);
        Button contBtn = (Button) findViewById(R.id.contact_button);
        contBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(i);


            }
        });
    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            //mNameTextView.setText(Html.fromHtml(place.getName() + ""));
            String ss = (String) (place.getAddress() + "");
            GeocodingLocation locationAddress = new GeocodingLocation();
            locationAddress.getAddressFromLocation(ss,
                    getApplicationContext(), new GeocoderHandler());
            //mAddressTextView.setText(ss);
            //mIdTextView.setText(Html.fromHtml(place.getId() + ""));
            //mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));
            //mWebTextView.setText(place.getWebsiteUri() + "");
            if (attributions != null) {
                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }
        }
    };

    private class GeocoderHandler extends Handler {

        TextView latLongTV = (TextView) findViewById(R.id.place_id);

        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    Destlat = bundle.getDouble("DestLat");
                    DestLon = bundle.getDouble("DestLon");
                    break;
                default:
                    Destlat = 0;
            }
            //String ss = "Lat " + Double.toString(Destlat)+ "\nLon "+ Double.toString(DestLon);
            //latLongTV.setText(ss);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }


}
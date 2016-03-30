package com.iiitd.hammad13060.trackme.SourceDestinationClasses;

import com.iiitd.hammad13060.trackme.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.iiitd.hammad13060.trackme.helpers.Constants;


public class MyDestination extends AppCompatActivity implements PlaceSelectionListener {

    public Double Destlat=0.0,DestLon=0.0;
    public static String TAG = "My Destination Class";
    public String fullDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_destination);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
    }


    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());
        TextView DetailsText = (TextView) findViewById(R.id.place_details);
        TextView ste = (TextView)findViewById(R.id.select);
        ste.setText("Your Selected Destination:");
        CharSequence placNam = place.getName();
        CharSequence cs = place.getAddress();
        fullDestination = placNam.toString() + ", " + cs.toString();
        DetailsText.setText(cs.toString());
        Geocoding locationAddress = new Geocoding();
        locationAddress.getAddressFromLocation(cs.toString(),
                getApplicationContext(), new GeocoderHandler());

    }

    private class GeocoderHandler extends Handler {

        Double Destlat,DestLon;

        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    Destlat = bundle.getDouble("DestLat");
                    DestLon = bundle.getDouble("DestLon");
                    break;

            }
            //String ss = "Lat " + Double.toString(Destlat)+ "\nLon "+ Double.toString(DestLon);
            //latLongTV.setText(ss);
            Intent returnIntent = new Intent();
            if(Destlat == 0.0 && DestLon == 0.0)
            {
                Constants.showLongToast(getApplicationContext(), "Please choose corect Destination");
            }else {
                returnIntent.putExtra("resultDestLat1",Destlat);
                returnIntent.putExtra("resultDestLon2",DestLon);
                returnIntent.putExtra("fullDestName",fullDestination);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
}

    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

}

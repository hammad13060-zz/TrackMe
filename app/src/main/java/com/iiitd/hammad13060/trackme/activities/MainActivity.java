package com.iiitd.hammad13060.trackme.activities;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.*;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

//import com.couchbase.lite.Manager;
import com.iiitd.hammad13060.trackme.BroadCastReceivers.DestinationReachedReceiver;
import com.iiitd.hammad13060.trackme.BroadCastReceivers.JourneyReadyReceiver;
import com.iiitd.hammad13060.trackme.Fragments.ArchivedFragment;
import com.iiitd.hammad13060.trackme.Fragments.JourneyFragment;
import com.iiitd.hammad13060.trackme.Fragments.TrackFragment;
import com.iiitd.hammad13060.trackme.Interfaces.JourneyReadyInterface;
import com.iiitd.hammad13060.trackme.R;

import com.iiitd.hammad13060.trackme.SourceDestinationClasses.LoadingScreen;
import com.iiitd.hammad13060.trackme.helpers.Authentication;
import com.iiitd.hammad13060.trackme.helpers.Contact;
import com.iiitd.hammad13060.trackme.services.ContactListUpdateService;

//import com.iiitd.hammad13060.trackme.SourceDestinationClasses.Source_Dst;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.Contact;

import com.iiitd.hammad13060.trackme.services.JourneyService;

public class MainActivity extends AppCompatActivity implements JourneyReadyInterface {

    public static final String EXTRA_SRC_LAT= "com.iiitd.hammad13060.trackme.activities.SRC_LAT";
    public static final String EXTRA_SRC_LONG= "com.iiitd.hammad13060.trackme.activities.SRC_LONG";
    public static final String EXTRA_DST_LAT= "com.iiitd.hammad13060.trackme.activities.DEST_LAT";
    public static final String EXTRA_DST_LONG= "com.iiitd.hammad13060.trackme.activities.DEST_LONG";
    public static final String EXTRA_CONTACT_LIST = "com.iiitd.hammad13060.trackme.activities.EXTRA_CONTACT_LIST";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static final String TAG = "MainActivity";
    static final int SELECT_SOURCE_DESTINATION_REQUEST_CODE = 1;

    public static List<Parcelable> contactList = new ArrayList<>();


    private JourneyFragment journeyFragment = null;
    private TrackFragment trackFragment = null;
    private ArchivedFragment archivedFragment = null;


    private BroadcastReceiver currentLocationReceiver = null;
    private BroadcastReceiver journeyReadyReciever = null;

    ViewPagerAdapter adapter;

    private DialogFragment stopJourneyDialog = new DialogFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*Intent gcmListnerIntent = new Intent(this, MyGcmListenerService.class);
        Intent gcmIDListnerIntent = new Intent(this, MyInstanceIDListenerService.class);
        startService(gcmListnerIntent);
        startService(gcmIDListnerIntent);*/

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                   //     .setAction("Action", null).show();

                if (!JourneyService.journeyRunning) {
                    Intent i = new Intent(MainActivity.this, LoadingScreen.class);
                    startActivityForResult(i, SELECT_SOURCE_DESTINATION_REQUEST_CODE);
                } else {
                    createStopJourneyDialog();
                    //stopJourneyDialog.show(getSupportFragmentManager(), "dialog_for_force_stopping_current_journey");
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerJourneyReadyReciever();

        if (JourneyService.journeyRunning) {
            FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
            floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.stopjourney));
        } else {
            FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
            floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrowroute));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterJourneyReadyReciever();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        journeyFragment = new JourneyFragment();
        trackFragment = new TrackFragment();
        archivedFragment = new ArchivedFragment();

        adapter.addFragment(journeyFragment, "Journey");
        adapter.addFragment(trackFragment, "Track");
        adapter.addFragment(archivedFragment, "HISTORY");
        viewPager.setAdapter(adapter);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    private void enterRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra(RegistrationActivity.EXTRA_UI_STATE, true);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_SOURCE_DESTINATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Double Destination_lat = data.getDoubleExtra("DestLat",0);
                Double Destination_longi = data.getDoubleExtra("DestLongi", 0);
                Double Source_lat = data.getDoubleExtra("SrcLat",0);
                Double Source_longi = data.getDoubleExtra("SrcLongi", 0);
                //contactList = data.getParcelableArrayListExtra("contList");
                Log.d(TAG," " + contactList.toString());
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                startJourneyService(data);

            } else if (resultCode == RESULT_CANCELED) {
                Constants.showLongToast(this, "Journey setup failed !!! Try again !!!");
            }
        }
    }


    private void registerJourneyReadyReciever() {
        journeyReadyReciever = new JourneyReadyReceiver(this);
        IntentFilter intentFilter = new IntentFilter(JourneyReadyReceiver.ACTION_VALUE);
        registerReceiver(journeyReadyReciever, intentFilter);
    }

    private void unregisterJourneyReadyReciever() {
        unregisterReceiver(journeyReadyReciever);
    }

    private void startJourneyService(Intent data) {
        Intent serviceIntent = new Intent(this, JourneyService.class);

        Double Destination_lat = data.getDoubleExtra("DestLat",0);
        Double Destination_longi = data.getDoubleExtra("DestLongi", 0);
        Double Source_lat = data.getDoubleExtra("SrcLat",0);
        Double Source_longi = data.getDoubleExtra("SrcLongi", 0);
        contactList = data.getParcelableArrayListExtra("contList");
        Log.d("MainActivity", "got result");
        Log.d("MainActivity", String.valueOf(Destination_lat));
        Log.d("MainActivity", String.valueOf(Destination_longi));
        Log.d("MainActivity", String.valueOf(Source_lat));
        Log.d("MainActivity", String.valueOf(Source_longi));
        Log.d("MainActivity", "" + contactList.size());

        serviceIntent.putExtra(EXTRA_DST_LAT, Destination_lat);
        serviceIntent.putExtra(EXTRA_DST_LONG, Destination_longi);
        serviceIntent.putExtra(EXTRA_SRC_LAT, Source_lat);
        serviceIntent.putExtra(EXTRA_SRC_LONG, Source_longi);


        /////////////////////////////////////////////////////DUMMY/////////////////////////////////////
        Parcelable[] p = new Parcelable[contactList.size()];
        for (int i = 0; i < contactList.size(); i++) {
            p[i] = (Parcelable)contactList.get(i);
        }
        serviceIntent.putExtra(EXTRA_CONTACT_LIST, p);
        startService(serviceIntent);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.stopjourney));
    }



    @Override
    public void journeyData(Intent intent) {
        if (journeyFragment != null)  journeyFragment.resetJourney(intent);
    }


    private Parcelable[] dummyContactData() {
        Contact contact = new Contact();
        contact.id = 1;
        contact.name = "Hammad Akhtar";
        contact.phoneList.add("+919718052178");

        Parcelable[] p = {contact};

        return p;
    }

   private void createStopJourneyDialog() {

                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Do you want to stop your current journey")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // user wants to stop his current journey
                                Intent intent = new Intent(DestinationReachedReceiver.DESTINATION_REACHED_FILTER_TAG);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.show();

    }
}

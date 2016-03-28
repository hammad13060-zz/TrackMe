package com.iiitd.hammad13060.trackme.activities;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

//import com.couchbase.lite.Manager;
import com.iiitd.hammad13060.trackme.BroadCastReceivers.CurrentLocationReceiver;
import com.iiitd.hammad13060.trackme.Fragments.JourneyFragment;
import com.iiitd.hammad13060.trackme.Fragments.TrackFragment;
import com.iiitd.hammad13060.trackme.Fragments.TwoFragment;
import com.iiitd.hammad13060.trackme.MyLocationInterface;
import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.SourceDestinationClasses.LoadingScreen;
import com.iiitd.hammad13060.trackme.helpers.Authentication;
import com.iiitd.hammad13060.trackme.helpers.Contact;
import com.iiitd.hammad13060.trackme.services.ContactListUpdateService;
import com.iiitd.hammad13060.trackme.services.JourneyService;

public class MainActivity extends AppCompatActivity implements MyLocationInterface {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static final String TAG = "MainActivity";
    static final int SELECT_SOURCE_DESTINATION_REQUEST_CODE = 1;
    public static List<Contact> contactList = new ArrayList<>();
    private Fragment journeyFragment = null;
    private Fragment trackFragment = null;

    private BroadcastReceiver currentLocationReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent contactListUpdateServiceIntent = new Intent(getApplicationContext(), ContactListUpdateService.class);

        if (!Authentication.hasAccess(getApplicationContext())) {
            enterRegistrationActivity();
            stopService(contactListUpdateServiceIntent); //stopping service contact List Update Service
        } else startService(contactListUpdateServiceIntent); //starting service contact List Update Service

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                Intent i = new Intent(MainActivity.this,LoadingScreen.class);
                startActivityForResult(i, SELECT_SOURCE_DESTINATION_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerMyLocationReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterMyLocationReceiver();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        journeyFragment = new JourneyFragment();
        trackFragment = new TrackFragment();

        adapter.addFragment(journeyFragment, "Journey");
        adapter.addFragment(trackFragment, "Track");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void myLocationUpdate(double latitude, double longitude) {

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                contactList = data.getParcelableArrayListExtra("contList");
                Log.d(TAG," " + contactList.toString());
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    private void registerMyLocationReceiver() {
        currentLocationReceiver = new CurrentLocationReceiver(this);
        IntentFilter intentFilter = new IntentFilter("com.iiitd.hammad13060.trackme.BroadCastReceivers.CurrentLocationReceiver");
        registerReceiver(currentLocationReceiver, intentFilter);
    }

    private void unregisterMyLocationReceiver() {
        unregisterReceiver(currentLocationReceiver);
    }
}

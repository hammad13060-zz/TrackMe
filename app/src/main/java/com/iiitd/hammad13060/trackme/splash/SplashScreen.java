package com.iiitd.hammad13060.trackme.splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.iiitd.hammad13060.trackme.BroadCastReceivers.ContactListUpdatedReceiver;
import com.iiitd.hammad13060.trackme.Interfaces.ContactListUpdatedInterface;
import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.activities.MainActivity;
import com.iiitd.hammad13060.trackme.activities.RegistrationActivity;
import com.iiitd.hammad13060.trackme.cloudeMessaging.QuickstartPreferences;
import com.iiitd.hammad13060.trackme.cloudeMessaging.RegistrationIntentService;
import com.iiitd.hammad13060.trackme.helpers.Authentication;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.services.ContactListUpdateIntentService;
import com.iiitd.hammad13060.trackme.services.ContactListUpdateService;

public class SplashScreen extends AppCompatActivity implements ContactListUpdatedInterface {

    private static final String TAG = "SplashScreen";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver gcmRegistrationReciever = null;
    private BroadcastReceiver contactListUpdatedReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Intent contactListUpdateServiceIntent = new Intent(getApplicationContext(), ContactListUpdateService.class);

        if (checkPlayServices()) {
            if (!Authentication.hasAccess(getApplicationContext())) {
                enterRegistrationActivity();
                //stopService(contactListUpdateServiceIntent); //stopping service contact List Update Service
            } else {
                    //startService(contactListUpdateServiceIntent);
                    Intent intent = new Intent(this, RegistrationIntentService.class);
                    startService(intent);
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerGCMReceiver();
        registerContactListUpdatedReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterGCMReceiver();
        unregisterContactListUpdatedReceiver();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Constants.showLongToast(this, "Your device is not supported");
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void enterRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra(RegistrationActivity.EXTRA_UI_STATE, true);
        startActivity(intent);
        finish();
    }

    private void registerGCMReceiver() {
        if (gcmRegistrationReciever == null) {
            initGCMReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(gcmRegistrationReciever,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        }
    }

    private void unRegisterGCMReceiver() {
        if (gcmRegistrationReciever != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(gcmRegistrationReciever);
        }
    }

    private void initGCMReceiver() {
        gcmRegistrationReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.PREFERENCE_TOKEN_FILE,
                        Context.MODE_PRIVATE);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //enterMainActivity();
                    fetchContacts();
                } else {
                    Constants.showLongToast(getApplicationContext(), "couldn't register for push notification service\n" +
                            "Check your network connection");
                    finish();
                }
            }
        };
    }

    private void enterMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void fetchContacts() {
        Intent intent = new Intent(this, ContactListUpdateIntentService.class);
        startService(intent);
    }

    private void registerContactListUpdatedReceiver() {
        if (contactListUpdatedReceiver == null) {
            contactListUpdatedReceiver = new ContactListUpdatedReceiver(this);
            IntentFilter intent = new IntentFilter(ContactListUpdatedReceiver.CONTACT_LIST_UPDATED_RECEIVER_TAG);
            LocalBroadcastManager.getInstance(this).registerReceiver(contactListUpdatedReceiver, intent);
        }
    }

    private void unregisterContactListUpdatedReceiver() {
        if (contactListUpdatedReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(contactListUpdatedReceiver);
        }
    }

    @Override
    public void onContactListUpdated(Intent intent) {
        enterMainActivity();
    }
}

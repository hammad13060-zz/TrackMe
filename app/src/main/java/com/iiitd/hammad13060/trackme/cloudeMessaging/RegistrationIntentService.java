package com.iiitd.hammad13060.trackme.cloudeMessaging;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.JSONRequest;
import com.iiitd.hammad13060.trackme.helpers.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RegistrationIntentService extends IntentService {

    public static List<String> topics = new ArrayList<>();

    public static final String JSON_GCM_TOKEN_SAVED = "_gcm_token_saved";
    public static final String JSON_GCM_TOKEN = "_gcm_token";

    private static final String TAG = RegistrationIntentService.class.getName();

    private static final String WEB_URL = Constants.SERVER_URL + "registerGCMToken/";

    private Response.Listener<JSONObject> responseListener = null;
    private Response.ErrorListener errorListener = null;
    private RequestQueue requestQueue = null;

    private SharedPreferences token_file;

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("596257233359",
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);
            Constants.showLongToast(getApplicationContext(), "push notification token obtained");

            sendTokenToServer(token);

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        }
    }

    private void sendTokenToServer(final String token) {

        token_file = getApplicationContext().getSharedPreferences(Constants.PREFERENCE_TOKEN_FILE,
                Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = token_file.edit();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        final JSONObject requestJSON = new JSONObject();

        responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean blocked = response.getBoolean(Constants.JSON_BLOCKED);
                    Log.d(TAG, "json received: " + response.toString());
                    if (!blocked) {
                        Log.d(TAG, "user not blocked");
                        boolean gcm_token_saved = response.getBoolean(JSON_GCM_TOKEN_SAVED);
                        if (gcm_token_saved) {
                            Log.d(TAG, "gcm token saved on server");
                            editor.putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true);
                            editor.putString(QuickstartPreferences.GCM_TOKEN, token);
                            subscribeToTopics(token);
                        } else {
                            Log.d(TAG, "not able to save gcm token on server");
                            editor.putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                        }

                    } else {
                        Log.d(TAG, "user blocked");
                        editor.putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "json doesn't contain relevant fields");
                    e.printStackTrace();
                    editor.putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                } catch (IOException e){
                    e.printStackTrace();
                }
                editor.commit();
                sendRegistrationCompleteBroadcast();
                requestQueue.stop();
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "response error !!!");
                editor.putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                editor.commit();
                sendRegistrationCompleteBroadcast();
                requestQueue.stop();
            }
        };

        try {
            requestJSON.put(Constants.JSON_CONTACT_NUMBER, token_file.getString(Constants.PREFERENCE_TOKEN_FILE_ID, "1"));
            requestJSON.put(Constants.JSON_TOKEN, token_file.getString(Constants.PREFERENCE_TOKEN_FILE_TOKEN, "no token"));
            requestJSON.put(JSON_GCM_TOKEN, token);
            JSONRequest request = new JSONRequest(Request.Method.POST, WEB_URL, null,
                    responseListener, errorListener, requestJSON);
            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void subscribeToTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : topics) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    private void sendRegistrationCompleteBroadcast() {
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}
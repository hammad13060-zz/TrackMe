package com.iiitd.hammad13060.trackme.services.journeyServiceHelper;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iiitd.hammad13060.trackme.helpers.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hammad on 7/3/16.
 */
public class JourneyNotificationToContacts extends NotifyContactsTask {

    protected static final String TAG = JourneyNotificationToContacts.class.getName();
    protected static final String WEB_URL = Constants.SERVER_URL + "pubsub/updateTopic";


    public JourneyNotificationToContacts(Context context) {
        super(context);
    }

    @Override
    protected void initResponseListner() {
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
    }

    @Override
    protected void initErrorListner() {
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "not able to reach server");
                sendPeopleNotifiedBroadcast();
                requestQueue.stop();
            }
        };
    }
}

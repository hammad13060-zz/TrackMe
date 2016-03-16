package com.iiitd.hammad13060.trackme.services.journeyServiceHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.iiitd.hammad13060.trackme.helpers.Authentication;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.Contact;
import com.iiitd.hammad13060.trackme.helpers.JSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hammad on 7/3/16.
 */
public class NotifyContactsTask extends AsyncTask<JSONObject,Void, Void> {


    protected static final String TAG = NotifyContactsTask.class.getName();
    protected Context context = null;

    protected static final String WEB_URL = Constants.SERVER_URL + "pubsub/newJourney";

    protected Response.Listener<JSONObject> responseListener = null;
    protected Response.ErrorListener errorListener = null;
    protected RequestQueue requestQueue = null;

    public NotifyContactsTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(JSONObject... params) {

        if (params.length > 0) {

            initResponseListner();
            initErrorListner();
            requestQueue = Volley.newRequestQueue(context);

            JSONObject requestObject = params[0];

            JSONRequest request = new JSONRequest(Request.Method.POST, WEB_URL, null,
                    responseListener, errorListener, requestObject);
            requestQueue.add(request);
        }

        return null;
    }


    protected void initResponseListner() {
        responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "server response: " + response.toString());
                try {
                    boolean blocked = response.getBoolean(Constants.JSON_BLOCKED);
                    if (!blocked) {
                        Log.d(TAG, "user is not blocked");
                        String journey_topic = response.getString(JourneyConstants.JSON_JOURNEY_TOPIC);

                        SharedPreferences journey_file = context.getSharedPreferences(
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
                        Authentication.enterRegistrationActivity(context);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "json exception: relevant fields not present maybe");
                }
                sendPeopleNotifiedBroadcast();

                requestQueue.stop();
            }
        };
    }

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

    protected void sendPeopleNotifiedBroadcast() {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(JourneyConstants.PEOPLE_NOTIFIED));
    }
}

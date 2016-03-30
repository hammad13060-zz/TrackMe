package com.iiitd.hammad13060.trackme.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.iiitd.hammad13060.trackme.BroadCastReceivers.ContactListUpdatedReceiver;
import com.iiitd.hammad13060.trackme.dbHandler.UsersDBHandler;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.Contact;
import com.iiitd.hammad13060.trackme.helpers.ContactsProvider;
import com.iiitd.hammad13060.trackme.helpers.JSONRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ContactListUpdateIntentService extends IntentService {

    private static final String TAG = ContactListUpdateIntentService.class.getSimpleName();

    private static String UPDATE_CONTACT_URL = Constants.SERVER_URL + "getContactList/";

    List<Contact> contactList = null;

    private RequestQueue requestQueue = null;
    private Response.Listener<JSONObject> responseListener = null;
    private Response.ErrorListener errorListener = null;

    public ContactListUpdateIntentService() {
        super(ContactListUpdateIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        updateContacts();
    }

    private void sendContactListUpdatedBroadcast() {
        Intent intent = new Intent(ContactListUpdatedReceiver.CONTACT_LIST_UPDATED_RECEIVER_TAG);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void updateContacts() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        initResponseListener();
        initErrorListener();

        ContactsProvider contactsProvider = new ContactsProvider(getApplicationContext());
        contactList = contactsProvider.getContacts();

        JSONArray requestArray = new JSONArray();

        for (Contact contact : contactList) {
            JSONObject contactObject = contact.toJSON();
            requestArray.put(contactObject);
        }

        makePost(requestArray);

    }


    private void makePost(JSONArray contacts) {

        JSONObject requestJson = new JSONObject();

        try {
            requestJson.put(Constants.JSON_CONTACTS, contacts);

            //Log.d(TAG, "sent: " + requestJson.toString());

            JSONRequest requestObject = new JSONRequest(Request.Method.POST, UPDATE_CONTACT_URL, null,
                    responseListener, errorListener, requestJson);
            requestQueue.add(requestObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initResponseListener() {
        responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                UsersDBHandler usersDBHandler = new UsersDBHandler(getApplicationContext());

                Log.d(TAG, "received: " + response.toString());

                usersDBHandler.addUsers(response);

                List<Contact> phoneList = usersDBHandler.getAllData();
                Log.d(TAG, "data saved: " + phoneList.toString());

                sendContactListUpdatedBroadcast();
                requestQueue.stop();
            }
        };

    }

    private void initErrorListener() {
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sendContactListUpdatedBroadcast();
                requestQueue.stop();
            }
        };
    }

}

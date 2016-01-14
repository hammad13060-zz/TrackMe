package com.iiitd.hammad13060.trackme.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.JSONRequest;
import com.iiitd.hammad13060.trackme.helpers.VolleyRequest;
import com.sinch.verification.Config;
import com.sinch.verification.PhoneNumberUtils;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ProcessingActivity extends AppCompatActivity {
    public static final String TAG = "ProcessingActivity";
    private static final String WEB_URL = Constants.SERVER_URL+ "registration/";


    private String contact_number = null;
    String phoneNumberInE164 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        Intent processingIntent = getIntent();
        contact_number = processingIntent.getStringExtra(Constants.EXTRA_CONTACT_NUMBER);
        verifyUser();
    }

    private void verifyUser() {
        Config config = SinchVerification.config().applicationKey("82224d5f-b267-4c5b-9d73-27b895c29d10")
                .context(getApplicationContext()).build();
        VerificationListener listener = initVerificationListener();
        String defaultRegion = PhoneNumberUtils.getDefaultCountryIso(this);
        phoneNumberInE164 = PhoneNumberUtils.formatNumberToE164(contact_number, defaultRegion);
        Verification verification = SinchVerification.createSmsVerification(config, phoneNumberInE164, listener);
        verification.initiate();
    }

    private VerificationListener initVerificationListener() {
        return new VerificationListener() {
            @Override
            public void onInitiated() {
                Log.d(TAG, "verification: onInitiated() called");
            }

            @Override
            public void onInitiationFailed(Exception e) {
                Log.d(TAG, "onInitiationFailed(): " + e.toString());
                Constants.showLongToast(getApplicationContext(), "verification failed !!! Try again.");
                enterRegistrationActivity();
            }

            @Override
            public void onVerified() {
                Log.d(TAG, "verification: onVerified() called");
                registerUser();
            }

            @Override
            public void onVerificationFailed(Exception e) {
                Log.d(TAG, "verification: onVerificationFailed() called");
                Constants.showLongToast(getApplicationContext(), "verification failed !!! Try again.");
                enterRegistrationActivity();
            }
        };
    }

    private void registerUser() {
        if (phoneNumberInE164 != null) {
            try {
                JSONObject requestObject = createJSON(phoneNumberInE164);
                Log.d(TAG, "json sent: " + requestObject.toString());
                makePost(requestObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    private JSONObject createJSON(String contact_number) throws JSONException {
        JSONObject object = new JSONObject();
        object.put(Constants.JSON_CONTACT_NUMBER, contact_number);
        return object;
    }

    private void makePost(JSONObject requestObject) {
        RegisterUserTask registerUserTask = new RegisterUserTask();
        registerUserTask.execute(requestObject);
    }


    private class RegisterUserTask extends AsyncTask<JSONObject, Void, Void> {

        private Response.Listener<JSONObject> responseListener = null;
        private Response.ErrorListener errorListener = null;
        private RequestQueue requestQueue = null;
        @Override
        protected Void doInBackground(JSONObject... objects) {
            initResponseListener();
            initErrorListener();
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            //volley request object
            if (objects.length > 0) {
                JSONObject requestObject = objects[0];
                JSONRequest request = new JSONRequest(Request.Method.POST, WEB_URL, null,
                        responseListener, errorListener, requestObject);
                requestQueue.add(request);
            }
            return null;
        }

        private void initResponseListener() {
            responseListener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "response json: " + response.toString());
                        boolean registered = response.getBoolean(Constants.JSON_REGISTERED);
                        if (registered) {
                            String token = response.getString(Constants.JSON_TOKEN);
                            Constants.showLongToast(getApplicationContext(), "registered");

                            //saving data to shared preferences
                            Context context = getApplicationContext();
                            SharedPreferences token_file = context.getSharedPreferences(Constants.PREFERENCE_TOKEN_FILE,
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = token_file.edit();
                            editor.putString(Constants.PREFERENCE_TOKEN_FILE_ID, phoneNumberInE164);
                            editor.putString(Constants.PREFERENCE_TOKEN_FILE_TOKEN, token);
                            editor.commit();
                            ///////////////////////////////////

                            enterWelcomeActivity();
                        } else {
                            Constants.showLongToast(getApplicationContext(), "registration failed !!! Try again.");
                            enterRegistrationActivity();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestQueue.stop();
                }
            };
        }

        private void initErrorListener() {
            errorListener = new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    requestQueue.stop();
                    enterRegistrationActivity();
                }
            };
        }
    }

    private void enterRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra(RegistrationActivity.EXTRA_UI_STATE, false);
        intent.putExtra(Constants.EXTRA_CONTACT_NUMBER, contact_number);
        startActivity(intent);
        finish();
    }

    private void enterWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
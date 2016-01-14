package com.iiitd.hammad13060.trackme.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by hammad on 16/12/15.
 */
public class Constants {
    //public static final String SERVER_URL = "http://10.0.0.10:8080/";
    public static final String SERVER_URL = "http://192.168.54.96:8080/";
    public static final String EXTRA_CONTACT_NUMBER = "com.iiitd.hammad13060.trackme.EXTRA_CONTACT_NUMBER";

    public static final String JSON_CONTACT_NUMBER = "_id";
    public static final String JSON_REGISTERED = "_registered";
    public static final String JSON_TOKEN = "_token";

    public static final String PREFERENCE_TOKEN_FILE = "PREFERENCE_TOKEN_FILE";
    public static final String PREFERENCE_TOKEN_FILE_ID = "_id";
    public static final String PREFERENCE_TOKEN_FILE_TOKEN = "_token";

    public static void showLongToast(Context context,String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showShortToast(Context context,String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}

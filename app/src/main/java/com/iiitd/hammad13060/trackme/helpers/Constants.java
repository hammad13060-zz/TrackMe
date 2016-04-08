package com.iiitd.hammad13060.trackme.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by hammad on 16/12/15.
 */
public class Constants {
    //public static final String SERVER_URL = "http://10.0.0.7:8080/";
    //public static final String SERVER_URL = "http://10.0.0.10:8080/";
    //public static final String SERVER_URL = "http://192.168.54.96:8080/";
   // public static final String SERVER_URL = "http://192.168.54.96:8080/";
    //public static final String SERVER_URL = "https://server-hammad13060-1.c9users.io:8080/";
    //public static final String SERVER_URL = "https://server-hammad13060.c9users.io:8080/";
    public static final String SERVER_URL = "https://independentprojectfollowme.appspot.com/";
    //public static final String SERVER_URL = "http://192.168.0.11:8080/";
   //public static final String SERVER_URL = "http://192.168.58.35:8080/";
    //public static final String SERVER_URL = "http://192.168.0.11:8080/";

    //public static final String SERVER_URL = "https://server-hammad13060.c9users.io:8080/";
    //public static final String SERVER_URL = "http://192.168.0.11:8080/";
    //public static final String SERVER_URL = "http://192.168.58.35:8080/";
    public static final String EXTRA_CONTACT_NUMBER = "com.iiitd.hammad13060.trackme.EXTRA_CONTACT_NUMBER";

    //public static final String SERVER_URL = "https://server-hammad13060-1.c9users.io:8080/";
    public static final String JSON_CONTACT_NUMBER = "_id";
    public static final String JSON_REGISTERED = "_registered";
    public static final String JSON_TOKEN = "_token";
    public static final String JSON_CONTACTS = "_contacts";
    public static final String JSON_BLOCKED = "_blocked";
    public static final String JSON_SRC_LAT = "_src_lat";
    public static final String JSON_SRC_LONG = "_src_long";
    public static final String JSON_DST_LAT = "_dst_lat";
    public static final String JSON_DST_LONG = "_dst_long";
    public static final String JSON_CURRENT_LAT = "_current_lat";
    public static final String JSON_CURRENT_LONG = "_current_long";
    public static final String JSON_DIRECTIONS = "_directions";
    public static final String JSON_FETCHED = "_fetched";

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

    public static String getUserID(Context contextt) {
        //saving data to shared preferences
        Context context = contextt.getApplicationContext();
        SharedPreferences token_file = context.getSharedPreferences(Constants.PREFERENCE_TOKEN_FILE,
                Context.MODE_PRIVATE);
        return token_file.getString(Constants.PREFERENCE_TOKEN_FILE_ID, "no id");
    }

    public static String getFileToken(Context contextt) {
        //saving data to shared preferences
        Context context = contextt.getApplicationContext();
        SharedPreferences token_file = context.getSharedPreferences(Constants.PREFERENCE_TOKEN_FILE,
                Context.MODE_PRIVATE);
        return token_file.getString(Constants.PREFERENCE_TOKEN_FILE_TOKEN, "no token");
    }

}

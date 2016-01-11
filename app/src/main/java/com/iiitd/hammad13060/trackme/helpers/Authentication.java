package com.iiitd.hammad13060.trackme.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hammad on 11/1/16.
 */
public class Authentication {

    public static boolean hasAccess(Context context) {
        SharedPreferences token_file = context.getSharedPreferences(Constants.PREFERENCE_TOKEN_FILE,
                Context.MODE_PRIVATE);
        String token = token_file.getString(Constants.PREFERENCE_TOKEN_FILE_TOKEN, "no token");

        if (token.equals("no token")) {
            return false;
        }

        return true;
    }

}

package com.iiitd.hammad13060.trackme.helpers;

import android.app.DownloadManager;
import android.content.Context;

import com.android.volley.RequestQueue;

/**
 * Created by hammad on 16/12/15.
 */

//singleton in nature
public class VolleyRequest {
    private static VolleyRequest instance = null;

    private VolleyRequest() {
    }

    public static VolleyRequest getInstance() {
        if (instance == null) {

        }

        return instance;
    }
}

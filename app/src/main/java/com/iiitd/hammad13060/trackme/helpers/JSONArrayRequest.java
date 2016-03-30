package com.iiitd.hammad13060.trackme.helpers;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hammad on 18/1/16.
 */
public class JSONArrayRequest extends JsonObjectRequest {
    private JSONArray object = null;

    public JSONArrayRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, JSONArray object) {
        super(method, url, jsonRequest, listener, errorListener);
        this.object = object;
    }

    @Override
    public byte[] getBody() {
        return object.toString().getBytes();
    }
}

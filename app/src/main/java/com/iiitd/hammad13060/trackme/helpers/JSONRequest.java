package com.iiitd.hammad13060.trackme.helpers;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class JSONRequest extends JsonObjectRequest{

    private JSONObject object = null;

    public JSONRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, JSONObject object) {
        super(method, url, jsonRequest, listener, errorListener);
        this.object = object;
    }

    @Override
    public byte[] getBody() {
        return object.toString().getBytes();
    }


}
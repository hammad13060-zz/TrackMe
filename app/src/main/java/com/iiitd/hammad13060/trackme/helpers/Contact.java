package com.iiitd.hammad13060.trackme.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract.Data;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by hammad on 17/1/16.
 */
public class Contact implements Parcelable {
    private static final String TAG = "Contact";

    public static final String CONTACT_JSON_ID = "_id";
    public static final String CONTACT_JSON_NAME = "_name";
    public static final String CONTACT_JSON_NUMBERS = "_numbers";

    public int id;
    public String name;
    public List<String> phoneList;
    public String email;
    public String uriString;
    public boolean hasMobile;


    public Contact() {
        hasMobile = false;
        phoneList = new ArrayList<>(0);
    }

    public void addMobile(String mobile) {
        phoneList.add(mobile);
    }

    @Override
    public String toString() {
        String text = "id: " + id + "name: " + name + " mobile number: ";
        for (String number: phoneList) {
            text += number + ", ";
        }

        return text;
    }

    public JSONObject toJSON() {
        JSONObject contactObject = new JSONObject();
        JSONArray numberArray = new JSONArray();

        try {

            for(String number: phoneList)
                numberArray.put(number);

            contactObject.put(CONTACT_JSON_ID, id);
            contactObject.put(CONTACT_JSON_NAME, name);
            contactObject.put(CONTACT_JSON_NUMBERS, numberArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contactObject;
    }

    public static Contact jsonToContact(JSONObject contactObject) throws JSONException {
        Contact contact = new Contact();
        String name = contactObject.getString(CONTACT_JSON_NAME);
        int id = contactObject.getInt(CONTACT_JSON_ID);
        JSONArray numbers = contactObject.getJSONArray(CONTACT_JSON_NUMBERS);
        for (int i = 0; i < numbers.length(); i++) {
            contact.hasMobile = true;
            String number = (String)numbers.get(i);
            contact.addMobile(number);
        }
        contact.name = name;
        contact.id = id;
        return contact;
    }


    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    /*
    * public int id;
    public String name;
    public List<String> phoneList;
    public String email;
    public String uriString;
    public boolean hasMobile;
    * */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeList(phoneList);
        dest.writeString(email);
        dest.writeString(uriString);
    }

    public Contact(Parcel in) {
        id = in.readInt();
        name = in.readString();
        phoneList = new ArrayList<String>();
        in.readList(phoneList, null);
        email = in.readString();
        uriString = in.readString();
    }
}

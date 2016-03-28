package com.iiitd.hammad13060.trackme.dbHandler;

import android.content.Context;
import android.util.Log;

import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.Contact;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hammad on 28/1/16.
 */
public class UsersDBHandler {

    public static final String TAG = "UsersDB";
    private static final String DB_DATA = "_data";
    private DB usersDB = null;



    public UsersDBHandler(Context context) {
        try {
            this.usersDB = DBFactory.open(context);
        } catch (SnappydbException e) {
            this.usersDB = null;
            Log.d(TAG, "cannot initiate userdb: " + e.toString());
            e.printStackTrace();
        }
    }


    public void addUsers(JSONObject object) {
        String jsonString = object.toString();
        if (usersDB != null) {
            try {
                usersDB.put(DB_DATA, jsonString);
            } catch (SnappydbException e) {
                Log.d(TAG, "could not save data");
                e.printStackTrace();
            }
        }
    }

    public List<Contact> getAllData() {

        List<Contact> contactList = new ArrayList<>();
        if (usersDB != null) {
            try {
                String objectString = usersDB.get(DB_DATA);
                JSONObject dataObject = new JSONObject(objectString);

                JSONArray numberArray = dataObject.getJSONArray(Constants.JSON_CONTACTS);

                for (int i = 0;i < numberArray.length(); i++) {
                    Contact contact = Contact.jsonToContact(numberArray.getJSONObject(i));
                    contactList.add(contact);

                }

            } catch (SnappydbException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "//////      HAMMAAD ADD NHI HUA KYA KARE       ////////////// " + contactList.toString());
        return contactList;
    }



    //Fake List
    /*public List<Contact> getUsers()
    {

        List<Contact> users = new ArrayList<>();

        users.add(new Contact(11,"Pushkin"));
        users.add(new Contact(12,"Yas"));
        users.add(new Contact(13,"KAhsj"));
        users.add(new Contact(14,"Lot"));
        users.add(new Contact(15,"Got"));
        return users;
    }*/



    public void closeDB() {
        try {
            usersDB.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }
}

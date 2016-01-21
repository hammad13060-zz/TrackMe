package com.iiitd.hammad13060.trackme.dbHandler;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.QueryOptions;
import com.couchbase.lite.android.AndroidContext;
import com.iiitd.hammad13060.trackme.helpers.Contact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hammad on 21/1/16.
 */
public class UsersDBHandler {

    private static final String TAG = "UsersDB";

    private Manager couchManager = null;
    private Database usersDB = null;

    public UsersDBHandler(Context context){
        try {
            couchManager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            usersDB = couchManager.getDatabase("users");
        } catch (IOException e) {
            couchManager = null;
            usersDB = null;
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            couchManager = null;
            usersDB = null;
            e.printStackTrace();
        }
    }

    public void addUser(Contact contact) {

        if (couchManager != null && usersDB != null) {
            int _id = contact.id;
            String _name = contact.name;
            List<String> _phoneList = contact.phoneList;

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("_id", _id);
            properties.put("_name", _name);
            properties.put("_phoneList", _phoneList);
            Document document = usersDB.getDocument(Integer.toString(_id));
            try {
                document.putProperties(properties);
            } catch (CouchbaseLiteException e) {
                Log.d(TAG, "cannot save document");
                e.printStackTrace();
            }
        }
    }

    public List<Contact> getAllUsers() {

        List<Contact> contacts = new ArrayList<>();

        if (couchManager != null) {
            try {

                QueryOptions options = new QueryOptions();
                options.getAllDocsMode();
                options.setDescending(false);
                Map<String, Object> allDocs = usersDB.getAllDocs(options);

                Contact contact = new Contact();
                contact.name = (String)allDocs.get("_name");
                contact.id = (int)allDocs.get("_id");
                contact.phoneList = (List<String>)allDocs.get("_phoneList");

                contacts.add(contact);

            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }

        return contacts;
    }


}

package com.iiitd.hammad13060.trackme.SourceDestinationClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.Context;
import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.dbHandler.UsersDBHandler;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.Contact;

import java.util.ArrayList;
import java.util.List;

public class SelectContacts extends AppCompatActivity  {

    public static final String TAG = "SelectContacts";
    UsersDBHandler udb;
    //UsersDBHandler udb = new UsersDBHandler();

    private ArrayAdapter<Contact> il;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);
        populateContactList();
    }

    private void populateContactList() {
        ListView listView = (ListView) findViewById(R.id.listview);
        //HEADER
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.header_listview, listView, false);
        listView.addHeaderView(header, null, false);

        udb = new UsersDBHandler(getApplicationContext());
        List<Contact> arrayOfUsers = udb.getAllData();
        //List<Contact> arrayOfUsers = udb.getUsers(); //FOR Dummy Contacts
        final ContactAdapter adapter = new ContactAdapter(this, arrayOfUsers);
        listView.setAdapter(adapter);

    }

    public void onChecked(View v)
    {
        List<Contact> itemlist=new ArrayList<>();
        int i=0;
        for (Contact c : ContactAdapter.getBox()) {
            if (c.box){
                itemlist.add(c);
                Log.d(TAG, "////// Ye add hua h:  " + itemlist.get(i).getName());
                i++;
            }
        }
        Intent returnIntent = new Intent();
        returnIntent.putParcelableArrayListExtra("contList",(ArrayList<? extends Parcelable>)itemlist);
        /*for (Contact c : itemlist) {
            Log.d(TAG, "////// c.getName:  " + c.getName());

        }*/
        for (Contact c : itemlist) {
            Log.d(TAG, "////// c.getName:  " + c.getName());

        }
        if(itemlist.size() != 0) {
            setResult(Activity.RESULT_OK, returnIntent);
        }
        else
        {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        finish();
    }

}

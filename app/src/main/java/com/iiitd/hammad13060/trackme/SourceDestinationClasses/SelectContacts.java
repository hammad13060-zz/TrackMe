package com.iiitd.hammad13060.trackme.SourceDestinationClasses;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.iiitd.hammad13060.trackme.R;

import java.util.ArrayList;

public class SelectContacts extends AppCompatActivity implements
        android.widget.CompoundButton.OnCheckedChangeListener {

    ListView lv;
    ArrayList<ContactNumber> contactList;
    ContactAdapter ctAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);

        lv = (ListView) findViewById(R.id.listview);
        displayContactList();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = lv.getPositionForView(buttonView);
        if (pos != ListView.INVALID_POSITION) {
            ContactNumber p = contactList.get(pos);
            p.setSelected(isChecked);

            Toast.makeText(this," YOU CLICKED ON IT", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayContactList() {

        contactList = new ArrayList<ContactNumber>();
        contactList.add(new ContactNumber("9213530316"));
        contactList.add(new ContactNumber("9871555499"));
        contactList.add(new ContactNumber("9997997979"));
        contactList.add(new ContactNumber("7844579784"));
        contactList.add(new ContactNumber("8884754712"));

        ctAdapter = new ContactAdapter(contactList, this);
        lv.setAdapter(ctAdapter);
    }
}

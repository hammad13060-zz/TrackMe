package com.iiitd.hammad13060.trackme.SourceDestinationClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.Contact;

import java.util.ArrayList;
import java.util.List;

public class SourceDestinationUI extends AppCompatActivity {

    //public static List<Contact> selectContact = new ArrayList<>();
    public static List<Contact> selectContact = new ArrayList<>();
    Double Destination_latitude=0.0,Destination_longitude=0.0;
    public String fullDestination,NewSource;
    public static final String TAG = "SourceDestinationUI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_destination_ui);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        Bundle NameData = getIntent().getExtras();
        if (NameData == null){
            return;
        }

        String sourceUser = NameData.getString("sourceText");
        TextView sourcetext = (TextView)findViewById(R.id.sourceText);

        String fullSource = "From: " + sourceUser;
        sourcetext.setText(fullSource);
        selectContacts();

        Button startJourney = (Button) findViewById(R.id.start_journey);
        Button choosedestination = (Button)findViewById(R.id.destination_button);
        Button sourcedestination = (Button)findViewById(R.id.source_button);
        final Intent sourcei = new Intent(this, CurrentSource.class);
        sourcedestination.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SourceDestinationUI.this);
                alertDialog.setTitle("Do you want to change your current location?");
                alertDialog.setMessage("Click on \"Yes\" to change.");
                // alertDialog.setIcon(R.drawable.delete);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(sourcei, 5);

                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();


            }
        });

        startJourney.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent returnIntent_source = new Intent();
                if(Destination_latitude == 0.0 && Destination_longitude == 0.0)
                {
                    Constants.showLongToast(getApplicationContext(), "Please choose corect Destination");
                }
                else {
                    returnIntent_source.putExtra("resultDestLat1", Destination_latitude);
                    returnIntent_source.putExtra("resultDestLon2", Destination_longitude);
                    returnIntent_source.putParcelableArrayListExtra("contList", (ArrayList<? extends Parcelable>)selectContact);
                    setResult(Activity.RESULT_OK, returnIntent_source);
                    finish();
                }
            }
        });

        final Intent iMydest = new Intent(this, MyDestination.class);
        choosedestination.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivityForResult(iMydest, 4);
            }
        });
    }


    public void selectContacts()
    {

        Button contBtn = (Button) findViewById(R.id.contact_button);
        contBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(SourceDestinationUI.this,SelectContacts.class);
                startActivityForResult(i,74);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SourceDestinationUI.this);
        alertDialog.setTitle("Do you want to exit?");
        alertDialog.setMessage("If you click on \"Yes\", then your journey will be cancelled. ");
        // alertDialog.setIcon(R.drawable.delete);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 74) {
            if (resultCode == Activity.RESULT_OK) {
                selectContact = data.getParcelableArrayListExtra("contList");
                Log.d(TAG, "Selected Contacts are:  " + selectContact.get(0).toString());
                for (Contact c : selectContact) {
                        Log.d(TAG, "Selected Contacts are:  " + c.getName());
                }


                //ListView listView = (ListView) findViewById(R.id.listViewContacts);
                TextView tview = (TextView)findViewById(R.id.cont);
                String textContact = "Selected Contacts are:\n ";
                int n = selectContact.size();
                String[] conts = new String[n];
                for(int i=0;i<n;i++)
                {
                    Contact c = selectContact.get(i);
                    conts[i] = c.getName();
                    textContact = textContact + " "+(i+1) + ". "+conts[i] + "\n ";

                }
                tview.setText(textContact);
                //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                  //      android.R.layout.simple_list_item_1, conts);

                //listView.setAdapter(adapter);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "CANCEELED............");
                finish();
            }

        }


        TextView destinationtext = (TextView) findViewById(R.id.DestinationText);
        TextView sourcetext2 = (TextView) findViewById(R.id.sourceText);
        String fd, sd;
        if (requestCode == 4) {
            if (resultCode == Activity.RESULT_OK) {

                Destination_latitude = data.getDoubleExtra("resultDestLat1", 0);
                Destination_longitude = data.getDoubleExtra("resultDestLon2", 0);
                fd = data.getStringExtra("fullDestName");
                fullDestination = "To: " + fd;
                destinationtext.setText(fullDestination);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }

        if (requestCode == 5) {
            if (resultCode == Activity.RESULT_OK) {

                sd = data.getStringExtra("result");
                NewSource = "To: " + sd;
                sourcetext2.setText(NewSource);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }


    }
}

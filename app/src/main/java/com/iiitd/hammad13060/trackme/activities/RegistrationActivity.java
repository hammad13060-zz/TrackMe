package com.iiitd.hammad13060.trackme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.helpers.Constants;
import com.iiitd.hammad13060.trackme.helpers.Contact;
import com.iiitd.hammad13060.trackme.helpers.ContactsProvider;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";

    public static final String EXTRA_UI_STATE = "com.iiitd.hammad13060.trackme.activities.UI_STATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // setting value of edit text if registration failed
        Intent ui_intet = getIntent();
        if (ui_intet.hasExtra(EXTRA_UI_STATE)) {
            String number = ui_intet.getStringExtra(EXTRA_UI_STATE);
            EditText editText = (EditText)findViewById(R.id.contact_number_field);
            editText.setText(number);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //enter processing activity if number found valid
    public void onRegister(View view) {
        //reading the text
        EditText editText = (EditText)findViewById(R.id.contact_number_field);
        String contact_number = editText.getText().toString();

        if (isValidNumber(contact_number)) enterProcessingActivity(contact_number);
        else Constants.showLongToast(this, "make sure number you entered is valid");
    }

    //checks if number is valid on the basis of lenght and starts with 7.8 or 9
    private boolean isValidNumber(String number) {
        StringTokenizer tokenizer = new StringTokenizer(number, "");
        if (tokenizer.countTokens() <= 0) return false; //contact field is empty
        else {
            String pattern = "^[7-9][0-9]{9}$";
            return number.matches(pattern);
        }
    }

    //function which takes us to processing activity
    private void enterProcessingActivity(String number) {
        Intent intent = new Intent(this, ProcessingActivity.class);
        intent.putExtra(Constants.EXTRA_CONTACT_NUMBER, number);
        startActivity(intent);
        finish();
    }



}

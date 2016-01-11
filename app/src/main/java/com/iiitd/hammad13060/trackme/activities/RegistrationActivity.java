package com.iiitd.hammad13060.trackme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.helpers.Constants;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    public static final String EXTRA_UI_STATE = "com.iiitd.hammad13060.trackme.activities.UI_STATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    //enter processing activity if number found valid
    public void onRegister(View view) {
        //reading the text
        EditText editText = (EditText)findViewById(R.id.contact_number_field);
        String contact_number = editText.getText().toString();

        if (isValidNumber(contact_number)) enterProcessingActivity(contact_number);
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
    }

}

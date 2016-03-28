package com.iiitd.hammad13060.trackme.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.iiitd.hammad13060.trackme.GenericApplication;
import com.sinch.verification.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hammad on 18/1/16.
 */

// reference http://blog.wittchen.biz.pl/how-to-read-contacts-in-android-device-using-contentresolver/
public class ContactsProvider {

    private static final String TAG = "ContactsProvider";

    private Uri QUERY_URI = ContactsContract.Contacts.CONTENT_URI;
    private String CONTACT_ID = ContactsContract.Contacts._ID;
    private String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    private String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
    private String EMAIL_DATA = ContactsContract.CommonDataKinds.Email.DATA;
    private String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private String STARRED_CONTACT = ContactsContract.Contacts.STARRED;
    private int TYPE_MOBILE = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
    private ContentResolver contentResolver;

    public ContactsProvider() {
        contentResolver = GenericApplication.getContext().getContentResolver();
    }

    public ContactsProvider(Context context) {
        contentResolver = context.getContentResolver();
    }

    public List<Contact> getContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        String[] projection = new String[]{CONTACT_ID, DISPLAY_NAME, HAS_PHONE_NUMBER, STARRED_CONTACT};
        String selection = null;
        Cursor cursor = contentResolver.query(QUERY_URI, projection, selection, null, null);

        while (cursor.moveToNext()) {
            Contact contact = getContact(cursor);
            if (contact.hasMobile) contactList.add(contact);
        }

        cursor.close();
        return contactList;
    }

    private Contact getContact(Cursor cursor) {
        String contactId = cursor.getString(cursor.getColumnIndex(CONTACT_ID));
        String name = (cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
        Uri uri = Uri.withAppendedPath(QUERY_URI, String.valueOf(contactId));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        String intentUriString = intent.toUri(0);

        Contact contact = new Contact();
        contact.id = Integer.valueOf(contactId);
        contact.name = name;
        contact.uriString = intentUriString;

        getPhone(cursor, contactId, contact);
        //getEmail(contactId, contact);
        return contact;
    }

    private void getPhone(Cursor cursor, String contactId, Contact contact) {
        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
        if (hasPhoneNumber > 0) {
            Cursor phoneCursor = contentResolver.query(PHONE_CONTENT_URI, null, PHONE_CONTACT_ID + " = ?", new String[]{contactId}, null);
            while (phoneCursor.moveToNext()) {
                if (phoneCursor.getType(TYPE_MOBILE) == 0) {
                    String phoneNumber = prependCountryCode(phoneCursor.getString(phoneCursor.getColumnIndex(PHONE_NUMBER)));
                    if (phoneNumber != null) {
                        contact.hasMobile = true;
                        //Log.d(TAG, "Mobile number: " + phoneNumber);
                        contact.addMobile(phoneNumber);
                    }
                }
            }
            phoneCursor.close();
        }
    }

    private void getEmail(String contactId, Contact contact) {
        Cursor emailCursor = contentResolver.query(EMAIL_CONTENT_URI, null, EMAIL_CONTACT_ID + " = ?", new String[]{contactId}, null);
        while (emailCursor.moveToNext()) {
            String email = emailCursor.getString(emailCursor.getColumnIndex(EMAIL_DATA));
            if (!TextUtils.isEmpty(email)) {
                contact.email = email;
            }
        }
        emailCursor.close();
    }

    private String prependCountryCode(String phoneNumber) {
        String phoneNumberInE164 = PhoneNumberUtils.formatNumberToE164(phoneNumber, "+91");
        return phoneNumberInE164;
    }
}

package com.iiitd.hammad13060.trackme.BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iiitd.hammad13060.trackme.Interfaces.ContactListUpdatedInterface;

public class ContactListUpdatedReceiver extends BroadcastReceiver {

    public static final String CONTACT_LIST_UPDATED_RECEIVER_TAG = "com.iiitd.hammad13060.trackme.BroadCastReceivers"
            + ContactListUpdatedReceiver.class.getSimpleName();

    ContactListUpdatedInterface contactListUpdatedInterface;

    public ContactListUpdatedReceiver() {

    }

    public ContactListUpdatedReceiver(ContactListUpdatedInterface contactListUpdatedInterface) {
        this.contactListUpdatedInterface = contactListUpdatedInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        contactListUpdatedInterface.onContactListUpdated(intent);
    }
}

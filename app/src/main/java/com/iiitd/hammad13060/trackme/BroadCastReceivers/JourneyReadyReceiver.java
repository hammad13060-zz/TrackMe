package com.iiitd.hammad13060.trackme.BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iiitd.hammad13060.trackme.Interfaces.JourneyReadyInterface;

public class JourneyReadyReceiver extends BroadcastReceiver {

    public static final String ACTION_VALUE = "com.iiitd.hammad13060.trackme.BroadCastReceivers.JourneyReadyService";

    private JourneyReadyInterface journeyReadyInterface = null;

    public JourneyReadyReceiver() {
    }

    public JourneyReadyReceiver(JourneyReadyInterface journeyReadyInterface) {
        this.journeyReadyInterface = journeyReadyInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        journeyReadyInterface.journeyData(intent);
    }
}

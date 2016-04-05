package com.iiitd.hammad13060.trackme.Fragments;

import android.support.v4.app.Fragment;

import com.iiitd.hammad13060.trackme.dbHandler.ArchivedDBHandler;
import com.iiitd.hammad13060.trackme.entities.Journey;

import java.util.List;

/**
 * Created by hammad on 9/3/16.
 */
public class ArchivedFragment extends TrackFragment {

    @Override
    public List<Journey> getAllJourney() {
        ArchivedDBHandler dbHandler = new ArchivedDBHandler(getActivity(), null, null, 1);
        return dbHandler.getAllJourneys();
    }

    @Override
    public void registerReceivers() {
        registerJourneyCompletionBroadcast();
    }

    @Override
    public void unregisterReceivers() {
        unregisterJourneyCompletionBroadcast();
    }

    @Override
    public void setHeader() {
        headerView.setText("History of journeys tracked");
    }
}

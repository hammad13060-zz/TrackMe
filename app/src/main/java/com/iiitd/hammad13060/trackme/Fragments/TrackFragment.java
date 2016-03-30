package com.iiitd.hammad13060.trackme.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.iiitd.hammad13060.trackme.JourneyListAdapter;
import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.dbHandler.JourneyDBHandler;
import com.iiitd.hammad13060.trackme.entities.Journey;
import com.iiitd.hammad13060.trackme.helpers.Contact;
import com.iiitd.hammad13060.trackme.services.journeyServiceHelper.JourneyConstants;

import java.util.List;


public class TrackFragment extends Fragment {

    public View myView;
    public ListView listView;
    public List<Journey> journeyList;
    public JourneyListAdapter journeyListAdapter;

    public BroadcastReceiver journeyCompletionBroadcast = null;
    public BroadcastReceiver newJourneyBroadcast = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_track, container, false);
        return myView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registerReceivers();

        listView = (ListView)myView.findViewById(R.id.journey_list_view);
        journeyList = getAllJourney();
        journeyListAdapter = new JourneyListAdapter(getActivity(), journeyList);

        listView.setAdapter(journeyListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceivers();
    }

    public void refreshList() {
        journeyList = getAllJourney();
        journeyListAdapter.setJourneyList(journeyList);
        journeyListAdapter.notifyDataSetChanged();
    }

    //override this method in subclass
    public List<Journey> getAllJourney() {
        JourneyDBHandler dbHandler = new JourneyDBHandler(getActivity(), null, null, 1);
        return dbHandler.getAllJourneys();
    }

    //override receivers registration and un registration
    public void registerReceivers() {
        registerJourneyCompletionBroadcast();
        registerNewJourneyBroadcast();
    }

    public void unregisterReceivers() {
        unregisterJourneyCompletionBroadcast();
        unregisterNewJourneyBroadcast();
    }


    public void registerJourneyCompletionBroadcast() {
        if (journeyCompletionBroadcast == null) {
            initJourneyCompletionBroadcast();
            IntentFilter filter = new IntentFilter(JourneyConstants.JOURNEY_COMPLETION_NOTIFIED);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(journeyCompletionBroadcast, filter);
        }
    }

    public void unregisterJourneyCompletionBroadcast() {
        if (journeyCompletionBroadcast != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(journeyCompletionBroadcast);
            journeyCompletionBroadcast = null;
    }

    public void registerNewJourneyBroadcast() {
        if (newJourneyBroadcast == null) {
            initNewJourneyBroadcast();
            IntentFilter filter = new IntentFilter(JourneyConstants.NEW_JOURNEY_NOTIFIED);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(newJourneyBroadcast, filter);
        }
    }

    public void unregisterNewJourneyBroadcast() {
        if (newJourneyBroadcast != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(newJourneyBroadcast);
            newJourneyBroadcast = null;
    }



    public void initJourneyCompletionBroadcast() {
        journeyCompletionBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //refreshList();
                reloadFragment();
            }
        };
    }

    public void initNewJourneyBroadcast() {
        newJourneyBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //refreshList();
                reloadFragment();
            }
        };
    }


    //resetting fragment
    public void reloadFragment() {
        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().detach(this).attach(this).commit();
    }
}

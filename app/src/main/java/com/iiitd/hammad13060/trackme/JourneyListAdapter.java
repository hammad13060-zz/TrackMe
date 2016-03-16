package com.iiitd.hammad13060.trackme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iiitd.hammad13060.trackme.activities.JourneyActivity;
import com.iiitd.hammad13060.trackme.cloudeMessaging.MyGcmListenerService;
import com.iiitd.hammad13060.trackme.dbHandler.UsersDBHandler;
import com.iiitd.hammad13060.trackme.entities.Journey;
import com.iiitd.hammad13060.trackme.helpers.Contact;

import java.util.List;

/**
 * Created by hammad on 8/3/16.
 */
public class JourneyListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Journey> journeyList;

    public JourneyListAdapter(Activity activity, List<Journey> journeyList) {
        this.activity = activity;
        this.journeyList = journeyList;
    }

    public void setJourneyList(List<Journey> journeyList) {
        this.journeyList = journeyList;
    }

    public List<Journey> getJourneyList() {
        return this.journeyList;
    }

    @Override
    public int getCount() {
        return journeyList.size();
    }

    @Override
    public Object getItem(int position) {
        return journeyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.journey_list_row, null);
        TextView journeyTitleTextView = (TextView) convertView.findViewById(R.id.journey_title_text_view);

        Journey journey = journeyList.get(position);
        String journey_title = journey.get_from(); //getJourneyTitle(journey);


        journey.setTitle(journey_title);
        journeyTitleTextView.setText(journey_title);


        onJourneyClicked(convertView, journey);

        return convertView;
    }


    private String getJourneyTitle(Journey journey) {
        UsersDBHandler usersDBHandler = new UsersDBHandler(activity);
        List<Contact> contactList = usersDBHandler.getAllData();

        for (int i = 0; i < contactList.size(); i++) {
            List<String> phoneList = contactList.get(i).phoneList;
            for (String number:
                 phoneList) {
                if (number.equals(journey.get_from())) {
                    return contactList.get(i).name;
                }
            }
        }

        return journey.get_from();
    }

    private void onJourneyClicked(View view, Journey jour) {
        final Journey journey = jour;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, JourneyActivity.class);

                intent.putExtra(MyGcmListenerService._JOURNEY_TOPIC, journey.get_journey_topic());
                intent.putExtra(MyGcmListenerService._FROM, journey.get_from());

                intent.putExtra(MyGcmListenerService._SRC_LAT, journey.get_src_lat());
                intent.putExtra(MyGcmListenerService._SRC_LONG, journey.get_src_long());

                intent.putExtra(MyGcmListenerService._DST_LAT, journey.get_dst_lat());
                intent.putExtra(MyGcmListenerService._DST_LONG, journey.get_dst_long());

                intent.putExtra(MyGcmListenerService._CURRENT_LAT, journey.get_current_lat());
                intent.putExtra(MyGcmListenerService._CURRENT_LONG, journey.get_current_long());

                activity.startActivity(intent);
            }
        });
    }
}

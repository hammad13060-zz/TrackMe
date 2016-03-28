package com.iiitd.hammad13060.trackme.SourceDestinationClasses;

/**
 * Created by Pushkin on 02-Feb-16.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.iiitd.hammad13060.trackme.R;
import com.iiitd.hammad13060.trackme.helpers.Contact;


public class ContactAdapter extends ArrayAdapter<Contact>{

    SparseBooleanArray checkContacts;
    int layoutResourceId;
    public static List<Contact> contList;

    public ContactAdapter(Context context, List<Contact> users) {
        super(context, 0, users);
        contList = users;
    }

    @Override
    public int getCount() {
        return contList.size();
    }

    @Override
    public Contact getItem(int position) {
        return contList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact user = getContact(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        CheckBox chk = (CheckBox) convertView.findViewById(R.id.chk_box);
        tvName.setText(user.name);
        chk.setOnCheckedChangeListener(myCheckChangList);
        chk.setTag(position);
        chk.setChecked(user.box);

        return convertView;
    }

    Contact getContact(int position)
    {
        return ((Contact) getItem(position));
    }

    static List<Contact> getBox() {
        List<Contact> box = new ArrayList<>();
        for (Contact p : contList) {
            if (p.box)
                box.add(p);
        }
        return box;
    }

    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            getContact((Integer) buttonView.getTag()).box = isChecked;

        }
    };
}

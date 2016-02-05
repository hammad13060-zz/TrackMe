package com.iiitd.hammad13060.trackme.SourceDestinationClasses;

/**
 * Created by Pushkin on 02-Feb-16.
 */
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.iiitd.hammad13060.trackme.R;

class ContactNumber {

    String name;

    boolean selected = false;

    public ContactNumber(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

public class ContactAdapter extends ArrayAdapter<ContactNumber>{

    private List<ContactNumber> contactList;
    private Context context;

    public ContactAdapter(List<ContactNumber> contactList, Context context) {
        super(context, R.layout.list_view, contactList);
        this.contactList = contactList;
        this.context = context;
    }

    private static class ContactHolder {
        public TextView contactName;
        public CheckBox chkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        ContactHolder holder = new ContactHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_view, null);

            holder.contactName = (TextView) v.findViewById(R.id.name);
            holder.chkBox = (CheckBox) v.findViewById(R.id.chk_box);

            holder.chkBox.setOnCheckedChangeListener((SelectContacts) context);

        } else {
            holder = (ContactHolder) v.getTag();
        }

        ContactNumber p = contactList.get(position);
        holder.contactName.setText(p.getName());
        holder.chkBox.setChecked(p.isSelected());
        holder.chkBox.setTag(p);

        return v;
    }
}

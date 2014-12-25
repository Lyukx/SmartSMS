package com.tyf.smartsms;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ContactsAdapter extends ArrayAdapter<MyContacts> {
	
	private int reaourceId;

	public ContactsAdapter(Context context, int resource,
			List<MyContacts> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		reaourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MyContacts myContacts = getItem(position);
		View view;
		ContactsViewHolder viewHolder;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(reaourceId, null);
			viewHolder = new ContactsViewHolder();
			viewHolder.name = (TextView) view.findViewById(R.id.my_name);
			viewHolder.neckName = (TextView) view.findViewById(R.id.my_neck_name);
			viewHolder.number = (TextView) view.findViewById(R.id.my_number);
			viewHolder.checked = (CheckBox) view.findViewById(R.id.my_check);
			view.setTag(viewHolder);
		}
		else{
			view = convertView;
			viewHolder = (ContactsViewHolder) view.getTag();
		}
		viewHolder.name.setText(myContacts.getName());
		viewHolder.neckName.setText(myContacts.getNeckName());
		viewHolder.number.setText(myContacts.getNumber());
		viewHolder.checked.setChecked(myContacts.getCheck());
		return view;
	}
	
	class ContactsViewHolder{
		TextView name;
		TextView neckName;
		TextView number;
		CheckBox checked;
	}

}
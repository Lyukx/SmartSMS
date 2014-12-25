package com.tyf.smartsms;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EditAdapter extends ArrayAdapter<EditContacts> {

	private int reaourceId;
	
	public EditAdapter(Context context, int resource, List<EditContacts> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		reaourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		EditContacts editContacts = getItem(position);
		View view;
		EditViewHolder viewHolder;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(reaourceId, null);
			viewHolder = new EditViewHolder();
			viewHolder.name = (TextView) view.findViewById(R.id.edit_name);
			viewHolder.neckName = (TextView) view.findViewById(R.id.edit_neck_name);
			viewHolder.number = (TextView) view.findViewById(R.id.edit_number);
			view.setTag(viewHolder);
		}
		else{
			view = convertView;
			viewHolder = (EditViewHolder) view.getTag();
		}
		viewHolder.name.setText(editContacts.getName());
		viewHolder.neckName.setText(editContacts.getNeckName());
		viewHolder.number.setText(editContacts.getNumber());
		return view;
	}
	
	class EditViewHolder{
		TextView name;
		TextView neckName;
		TextView number;
	}
	
}

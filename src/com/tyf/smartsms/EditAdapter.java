package com.tyf.smartsms;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class EditAdapter extends ArrayAdapter<MyContacts> { //自定义适配器继承自ArrayAdapter，指定泛型为MyContacts

	private int reaourceId;

	public EditAdapter(Context context, int resource, List<MyContacts> objects) {
		super(context, resource, objects);
		reaourceId = resource; //保存resource
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyContacts myContacts = getItem(position); //获取当前项的MyContacts实例
		View view;
		EditViewHolder viewHolder;
		if(convertView == null){ //如果convertView为空，则当前项为第一次加载
			view = LayoutInflater.from(getContext()).inflate(reaourceId, null); //动态加载布局文件
			viewHolder = new EditViewHolder(); //新建EditViewHolder保存控件实例
			viewHolder.name = (TextView) view.findViewById(R.id.my_name);
			viewHolder.neckName = (TextView) view.findViewById(R.id.my_neck_name);
			viewHolder.number = (TextView) view.findViewById(R.id.my_number);
			viewHolder.checked = (CheckBox) view.findViewById(R.id.my_check);
			view.setTag(viewHolder);
		}
		else{ //如果convertView不为空，说明当前项不是第一次加载，直接使用缓存
			view = convertView;
			viewHolder = (EditViewHolder) view.getTag(); //从缓存中取出EditViewHolder获取保存过的控件实例
		}
		viewHolder.name.setText(myContacts.getName()); //根据MyContacts的实例设置文本框的内容
		viewHolder.neckName.setText(myContacts.getNeckName());
		viewHolder.number.setText(myContacts.getNumber());
		viewHolder.checked.setVisibility(View.GONE); //编辑联系人信息时不需要用到复选框，设置为不显示
		return view;
	}

	class EditViewHolder{ //EditViewHolder类用来保存控件的实例
		TextView name;
		TextView neckName;
		TextView number;
		CheckBox checked;
	}

}

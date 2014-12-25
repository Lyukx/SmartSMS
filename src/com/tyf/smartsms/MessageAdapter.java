package com.tyf.smartsms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

/**
 * Created by Lkx on 2014/12/24.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    private int resourceId;

    public MessageAdapter(Context context, int textViewResourceId, List<Message> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Message msg = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.left);
            viewHolder.rightLayout = (LinearLayout) view.findViewById(R.id.right);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            viewHolder.leftTime = (TextView) view.findViewById(R.id.left_time);
            viewHolder.rightTime = (TextView) view.findViewById(R.id.right_time);

            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if(msg.getIsLeft() == true){
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getContent());
            viewHolder.leftTime.setText(msg.getTime());
            viewHolder.rightTime.setVisibility(View.GONE);
            viewHolder.leftTime.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightMsg.setText(msg.getContent());
            viewHolder.rightTime.setText(msg.getTime());
            viewHolder.rightTime.setVisibility(View.VISIBLE);
            viewHolder.leftTime.setVisibility(View.GONE);
        }

        return view;
    }

    class ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView leftTime;
        TextView rightTime;
    }
}

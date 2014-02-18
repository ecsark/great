package com.ecwork.great.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ecwork.great.R;
import com.ecwork.great.common.Item;

import java.util.ArrayList;

/**
 * User: ecsark
 * Date: 2/11/14
 * Time: 10:58 PM
 */
public class BrowserItemAdapter extends ArrayAdapter<Item> {

    private Context context;

    private ArrayList<Item> itemList;

    private LayoutInflater inflater;


    public BrowserItemAdapter(Context context, int resource,
                              ArrayList<Item> objects) {
        super(context, resource, objects);
        this.context = context;
        itemList = objects;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BlockHolder holder;

        if (convertView == null) {
            convertView =  inflater.inflate(R.layout.block_item, null);
            holder = new BlockHolder();
            holder.block = (LinearLayout)convertView;
            convertView.setTag(holder);
        }

        holder = (BlockHolder) convertView.getTag();
        holder.setUnselected();
        TextView text = (TextView) convertView.findViewById(R.id.block_text);

        TextView subtext = (TextView) convertView.findViewById(R.id.block_subtext);
        subtext.setVisibility(TextView.GONE);


        text.setText(itemList.get(position).getValue());

        return convertView;
    }
}
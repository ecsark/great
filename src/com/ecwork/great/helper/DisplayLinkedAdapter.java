package com.ecwork.great.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.ecwork.great.R;
import com.ecwork.great.common.Item;
import com.ecwork.great.common.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ecsark
 * Date: 2/16/14
 * Time: 9:22 PM
 */
public class DisplayLinkedAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private LayoutInflater inflater;

    private Item mItem;

    public enum Direction {
        CONNECT_TO, CONNECT_FROM
    }

    private Direction dir;
    public DisplayLinkedAdapter(Context context, Direction direction) {
        super();
        mContext = context;
        inflater = LayoutInflater.from(context);
        dir = direction;
    }


    public DisplayLinkedAdapter(Context context, Item item, Direction direction) {
        super();
        mContext = context;
        inflater = LayoutInflater.from(context);
        mItem = item;
        dir = direction;
    }

    public void setItem(Item item) {
        mItem = item;
    }


    private String getFixedWidthSubstring(String s, int length) {
        if (s.getBytes().length < length)
            return s;
        String k = s.substring(0,s.length()/2);
        int idx = s.length()/2;
        while (k.getBytes().length < length) {
            k += s.charAt(idx);
            idx += 1;
        }
        return k;
    }


    private String getSynopsis(List<String> rawStrings) {
        if (rawStrings.size()==0)
            return "";
        final int charNum = 30;
        int charPerString = charNum/rawStrings.size();
        if (charPerString < 8)
            charPerString = 8;

        String result = "";
        for (int i=0; i<rawStrings.size(); ++i) {
            String raw = rawStrings.get(i);
            /* TODO
             * it is just a workaround to prevent a character
             * taking up too much space
             */
            if (raw.getBytes().length < charPerString)
                result += raw;
            else
                result += getFixedWidthSubstring(raw, charPerString) + "~";

            if (i != rawStrings.size()-1)
                result += "；";
        }

        if (result.getBytes().length > charNum)
            result = getFixedWidthSubstring(result, charNum);
        return result;
    }


    public void setConnectTo(View view, Item item) {
        List<String> raw = new ArrayList<String>();
        view.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_orange_dark));
        for (Link k : item.getOutgoingLinks())
            raw.add(k.to.getValue());
        String synopsis = getSynopsis(raw);
        TextView textView = (TextView) view.findViewById(R.id.content_self_header_closed);
        textView.setText(synopsis);
    }


    public void setConnectFrom(View view, Item item) {
        List<String> raw = new ArrayList<String>();
        view.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
        for (Link k : item.getIncomingLinks())
            raw.add(k.from.getValue());
        String synopsis = getSynopsis(raw);
        TextView textView = (TextView) view.findViewById(R.id.content_self_header_closed);
        textView.setText(synopsis);
    }

    public void setExpanded(View view, String title) {
        TextView textView = (TextView) view.findViewById(R.id.content_self_header_closed);
        textView.setText(title);
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (dir==Direction.CONNECT_TO) {
            return mItem.getOutgoingLinks().size();
        } else {
            return mItem.getIncomingLinks().size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.content_display_linked_header, null);
        }

        if (isExpanded) {
            String title = dir==Direction.CONNECT_FROM ? "Link From" : "Link To";
            setExpanded(convertView, title);
            return convertView;
        }

        if (dir == Direction.CONNECT_FROM)
            setConnectFrom(convertView, mItem);
        else
            setConnectTo(convertView, mItem);

        return convertView;

    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

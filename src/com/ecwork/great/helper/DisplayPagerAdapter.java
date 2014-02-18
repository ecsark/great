package com.ecwork.great.helper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.ecwork.great.R;
import com.ecwork.great.common.Item;

import java.util.ArrayList;

/**
 * User: ecsark
 * Date: 2/13/14
 * Time: 2:54 PM
 */
public class DisplayPagerAdapter extends PagerAdapter {

    private Context context;

    private LayoutInflater inflater;

    private ArrayList<Item> itemList;

    private ArrayList<Integer> dynamicPages;

    private ArrayList<DisplayLinkedAdapter> contentAdapterTo, contentAdapterFrom;
    private ArrayList<View> contentView;

    private final int VOUT0 = 0, VIN0 = 1, VOUT1 = 2, VIN1 = 3, VOUT2 = 4, VIN2 = 5;

    public DisplayPagerAdapter(Context context, ArrayList<Item> itemList, ArrayList<Integer> dynamicPages) {
        super();
        this.context = context;
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);
        this.dynamicPages = dynamicPages;
        contentView = new ArrayList<View>();
        for (int i=0; i<3; ++i)
            contentView.add(inflater.inflate(R.layout.content_display, null));
        initContentAdapters();
    }

    private void initContentAdapters() {
        contentAdapterTo = new ArrayList<DisplayLinkedAdapter>();
        contentAdapterFrom = new ArrayList<DisplayLinkedAdapter>();
        for (int i=0; i<3; ++i) {
            contentAdapterTo.add(new DisplayLinkedAdapter(context,
                    DisplayLinkedAdapter.Direction.CONNECT_TO));
            contentAdapterFrom.add(new DisplayLinkedAdapter(context,
                    DisplayLinkedAdapter.Direction.CONNECT_FROM));
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ScrollView view = (ScrollView) contentView.get(position);

        int idx = dynamicPages.get(position) % itemList.size();
        if (idx < 0) { // prevent negative values
            idx += itemList.size();
        }

        Item item = itemList.get(idx);

        TextView content = (TextView) view.findViewById(R.id.content_text);
        content.setText(item.getValue());

        ExpandableListView expTo = (ExpandableListView) view.findViewById(R.id.exp_link_to);
        DisplayLinkedAdapter adapterTo = contentAdapterTo.get(position);
        adapterTo.setItem(item);
        expTo.setAdapter(adapterTo);

        ExpandableListView expFrom = (ExpandableListView) view.findViewById(R.id.exp_link_from);
        DisplayLinkedAdapter adapterFrom = contentAdapterFrom.get(position);
        adapterFrom.setItem(item);
        expFrom.setAdapter(adapterFrom);

        container.addView(view);
        return view;
    }


    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

}

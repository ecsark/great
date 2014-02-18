package com.ecwork.great.helper;

import android.widget.LinearLayout;
import com.ecwork.great.R;

/**
 * User: ecsark
 * Date: 2/16/14
 * Time: 2:20 PM
 */
public class BlockHolder {
    LinearLayout block;

    public void setSelected() {
        block.setBackgroundResource(R.drawable.block_bg_selected);
    }

    public void setLinkToSelected() {
        block.setBackgroundResource(R.drawable.block_bg_selected_link_to);
    }

    public void setLinkFromSelected() {
        block.setBackgroundResource(R.drawable.block_bg_selected_link_from);
    }

    public void setUnselected() {
        block.setBackgroundResource(R.drawable.block_bg);
    }
}
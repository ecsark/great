package com.ecwork.great.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.ecwork.great.R;
import com.ecwork.great.common.ItemManager;
import com.ecwork.great.helper.DisplayPagerAdapter;
import com.ecwork.great.helper.UniformViewPager;

/**
 * User: ecsark
 * Date: 2/13/14
 * Time: 1:34 PM
 */
public class ItemDisplayDialogFragment extends DialogFragment {

    UniformViewPager viewPager;

    DisplayPagerAdapter pagerAdapter;

    int position;

    public ItemDisplayDialogFragment(int position) {
        this.position = position;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        viewPager = (UniformViewPager) inflater.inflate(R.layout.dialog_display, null);
        viewPager.initialize(position);
        //viewPager.setPageTransformer(true, new DepthPageTransformer());

        pagerAdapter = new DisplayPagerAdapter(getActivity(),
                ItemManager.instance(getActivity()).getAllItems(), viewPager.getDynamicPages());
        viewPager.setAdapter(pagerAdapter);

        return builder.setView(viewPager).create();
    }

}



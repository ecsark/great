package com.ecwork.great.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.ecwork.great.R;

/**
 * User: ecsark
 * Date: 2/11/14
 * Time: 5:51 PM
 */
public class ItemEditingDialogFragment extends DialogFragment {

    public interface ItemEditingListener {
        public void onItemEdited(String itemValue);
    }

    private String titleText, promptText;

    private ItemEditingListener mListener;

    ItemEditingDialogFragment(String titleText, String promptText, ItemEditingListener listener) {
        this.titleText = titleText;
        this.promptText = promptText;
        this.mListener = listener;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_edititem, null);

        if (titleText != null)
            builder.setTitle(titleText);

        final EditText input = (EditText)dialogView.findViewById(R.id.dialog_edit_input);
        if (promptText != null)
            input.setText(promptText);

        builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String itemValue = input.getText().toString().trim();
                        mListener.onItemEdited(itemValue);
                    }
                })
                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ItemEditingDialogFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
package com.ecwork.great.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.ecwork.great.R;
import com.ecwork.great.common.Item;
import com.ecwork.great.common.ItemManager;
import com.ecwork.great.common.Link;
import com.ecwork.great.db.StorageException;
import com.ecwork.great.helper.BlockHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ecsark
 * Date: 2/16/14
 * Time: 2:13 PM
 */
public class BrowserAction implements ActionMode.Callback {

    BrowserActivity browser;

    ActionMode mActionMode;

    ArrayList<Integer> selected;

    Menu contextMenu;

    ArrayList<Integer> alreadyConnected;

    enum State {
        FREE, LINKTO, LINKFROM
    }

    private interface ActionConfirmedListener {
        public void onActionConfirmed();
    }

    int[] contextMenuOptionsForSingle = {R.id.action_link_to_browser,
            R.id.action_link_from_browser, R.id.action_edit_browser};
    private State state;

    public BrowserAction(BrowserActivity browser) {
        this.browser = browser;
        selected = new ArrayList<Integer>();
        alreadyConnected = new ArrayList<Integer>();
        state = State.FREE;
    }


    public boolean triggered() {
        return mActionMode != null;
    }

    public void start(int position) {
        mActionMode = browser.startActionMode(this);
        select(position);
    }

    public void onItemClicked(int position) {
        switch (state) {
            case FREE:
                if (selected.contains(position)) {
                    deselect(position);
                    if (selected.size() == 0)
                        mActionMode.finish();
                } else {
                    select(position);
                }
                break;
            case LINKTO:
            case LINKFROM:
                if (selected.contains(position)) {
                    if (selected.indexOf(position) == 0)
                        Toast.makeText(browser, "Cannot connect with itself", Toast.LENGTH_LONG).show();
                    else
                        deselect(position);
                } else {
                    select(position);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        assert(inflater!=null);
        inflater.inflate(R.menu.ctxmenu_browser, menu);
        contextMenu = menu;
        hideItem(R.id.action_exe_browser);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }


    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete_browser:
                executeDelete();
                return true;
            case R.id.action_edit_browser:
                executeEdit();
                return true;
            case R.id.action_link_to_browser:
                state = State.LINKTO;
                executionMode();
                prepareAlreadyLinked();
                return true;
            case R.id.action_link_from_browser:
                state = State.LINKFROM;
                executionMode();
                prepareAlreadyLinked();
                return true;
            case R.id.action_exe_browser:
                execute();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

        while (selected.size() > 0) {
            int position = selected.get(0);
            browser.getBlockHolder(position).setUnselected();
            selected.remove(Integer.valueOf(position));
        }
        mActionMode = null;
        state = State.FREE;
    }

    private void select(int position) {
        BlockHolder block = browser.getBlockHolder(position);
        selected.add(position);

        switch (state) {
            case FREE:
                block.setSelected();
                mActionMode.setTitle(Integer.toString(selected.size()) + " selected");
                if (selected.size() > 1) {
                    for (int id : contextMenuOptionsForSingle) {
                        hideItem(id);
                    }
                }
                break;
            case LINKTO:
                block.setLinkToSelected();
                onUpdateLinkSelection();
                break;
            case LINKFROM:
                block.setLinkFromSelected();
                onUpdateLinkSelection();
                break;
        }
    }

    private void deselect(int position) {
        browser.getBlockHolder(position).setUnselected();
        selected.remove(Integer.valueOf(position));

        switch (state) {
            case FREE:
                if (selected.size() == 1) {
                    for (int id : contextMenuOptionsForSingle) {
                        unhideItem(id);
                    }
                }
                mActionMode.setTitle(Integer.toString(selected.size()) + " selected");
                break;
            case LINKFROM:
            case LINKTO:
                onUpdateLinkSelection();
                break;
        }
    }


    private void executionMode() {
        final MenuItem[] prompt = new MenuItem[]{
                contextMenu.findItem(R.id.action_delete_browser),
                contextMenu.findItem(R.id.action_edit_browser),
                contextMenu.findItem(R.id.action_link_to_browser),
                contextMenu.findItem(R.id.action_link_from_browser)};

        for (MenuItem mi : prompt)
            mi.setVisible(false);

        mActionMode.setTitle("");
        unhideItem(R.id.action_exe_browser);
    }


    private void prepareAlreadyLinked() {
        int oidx = selected.get(0);
        List<Item> allItems = ItemManager.instance(browser).getAllItems();
        Item origin = allItems.get(oidx);
        switch (state) {
            case LINKTO:
                List<Link> outgoingLinks = origin.getOutgoingLinks();
                for (Link k : outgoingLinks) {
                    int position = allItems.indexOf(k.to);
                    if (position >= 0)
                        select(position);
                }
                break;
            case LINKFROM:
                List<Link> incomingLinks = origin.getIncomingLinks();
                for (Link k : incomingLinks) {
                    int position = allItems.indexOf(k.from);
                    if (position >= 0)
                        select(position);
                }
                break;
            default:
                return;
        }

        alreadyConnected.clear();
        alreadyConnected.addAll(selected);
        onUpdateLinkSelection();
    }


    private void execute() {
        switch (state) {
            case LINKFROM: case LINKTO:
                executeLink();
                break;
            default:
                break;
        }
    }


    private void executeLink() {
        ActionConfirmedListener actions = new ActionConfirmedListener() {
            @Override
            public void onActionConfirmed() {
                // doing link
                List<Integer> newConnected = new ArrayList<Integer>();
                newConnected.addAll(selected);

                ItemManager im = ItemManager.instance(browser);
                Item origin = im.getAllItems().get(selected.get(0));
                List<Item> allItems = im.getAllItems();

                // add newly selected connections
                newConnected.removeAll(alreadyConnected);
                for (int fresh : newConnected) {
                    if (state==State.LINKTO) {
                        Item to = allItems.get(fresh);
                        im.connect(origin, to);
                    } else if (state==State.LINKFROM) {
                        Item from = allItems.get(fresh);
                        im.connect(from, origin);
                    }
                }

                // remove deselected connections
                alreadyConnected.removeAll(selected);
                for (int obsolete : alreadyConnected) {
                    if (state==State.LINKTO) {
                        Item to = allItems.get(obsolete);
                        im.disconnect(origin, to);
                    } else if (state==State.LINKFROM) {
                        Item from = allItems.get(obsolete);
                        im.disconnect(from, origin);
                    }
                }

                while (selected.size() > 0) {
                    deselect(selected.get(0));
                }
                browser.notifyAdapter();
                mActionMode.finish();
            }
        };

        String message = "Connect ";
        if (state == State.LINKTO)
            message += "to";
        else if (state == State.LINKFROM)
            message += "from";
        else
            return;
        message += selected.size() <=2 ? " this item?" :
                " these " + Integer.toString(selected.size()-1) + " items?";

        createConfirmationDialog(actions, message).show();
    }


    private void onUpdateLinkSelection() {

        String title = "Link ";
        switch (state) {
            case LINKFROM:
                title += "from ";
                break;
            case LINKTO:
                title += "to ";
                break;
            default:
                break;
        }
        title += Integer.toString(selected.size() - 1);
        title += selected.size() > 2 ? " items" : " item";
        mActionMode.setTitle(title);

    }


    private void executeDelete() {
        ActionConfirmedListener actions = new ActionConfirmedListener() {
            @Override
            public void onActionConfirmed() {
                // doing delete
                for (int position : selected) {
                    Item item = ItemManager.instance(browser).getAllItems().get(position);
                    ItemManager.instance(browser).removeItem(item);
                }
                while (selected.size() > 0) {
                    deselect(selected.get(0));
                }

                mActionMode.finish();
                browser.notifyAdapter();
            }
        };

        String message = "Are you sure to delete";
        message += selected.size() == 1 ? " this item?" :
                " these " + Integer.toString(selected.size()) + " items?";

        createConfirmationDialog(actions, message).show();
    }


    private void executeEdit() {
        final Item item = ItemManager.instance(browser).getAllItems().get(selected.get(0));
        ItemEditingDialogFragment dialog = new ItemEditingDialogFragment(
                "Edit", item.getValue(),
                new ItemEditingDialogFragment.ItemEditingListener() {
                    @Override
                    public void onItemEdited(String newValue) {
                        try {
                            item.updateValue(browser, newValue);
                        } catch (StorageException e) {
                            Toast.makeText(browser, "Application internal error! Please restart the application", Toast.LENGTH_LONG).show();
                            throw new RuntimeException(e);
                        }

                        mActionMode.finish();
                        browser.notifyAdapter();
                    }
                });
        dialog.show(browser.getFragmentManager(), "ItemEditingDialogFragment");
    }


    private Dialog createConfirmationDialog(final ActionConfirmedListener listener, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(browser);

        builder.setIcon(R.drawable.ic_action_warning)
                .setTitle("Confirmation")
                .setMessage(message)
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                listener.onActionConfirmed();
            }
        })
                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });

        return builder.create();
    }


    private void hideItem(int id) {
        MenuItem item = contextMenu.findItem(id);
        assert(item!=null);
        item.setVisible(false);
    }

    private void unhideItem(int id) {
        MenuItem item = contextMenu.findItem(id);
        assert(item!=null);
        item.setVisible(true);
    }

}

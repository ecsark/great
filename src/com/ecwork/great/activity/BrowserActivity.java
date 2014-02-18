package com.ecwork.great.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.ecwork.great.R;
import com.ecwork.great.common.ItemManager;
import com.ecwork.great.common.Ref;
import com.ecwork.great.helper.BlockHolder;
import com.ecwork.great.helper.BrowserItemAdapter;
import com.origamilabs.library.views.StaggeredGridView;

/**
 * User: ecsark
 * Date: 2/11/14
 * Time: 6:10 PM
 */
public class BrowserActivity extends Activity
        implements StaggeredGridView.OnItemClickListener,
        StaggeredGridView.OnItemLongClickListener {

    private StaggeredGridView gridView;

    private BrowserItemAdapter gridAdapter;

    private BrowserAction action;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        gridView = (StaggeredGridView) findViewById(R.id.browser_grid);
        gridAdapter = new BrowserItemAdapter(this, R.id.block_browser, ItemManager.instance(this).getAllItems());
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();

        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);

        action = new BrowserAction(this);

        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                && getActionBar()!=null) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setTitle("Browser");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_browser, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add_browser:
                newItem();
                return true;
            case R.id.action_search_browser:
                //TODO
                return true;
            case R.id.action_settings:
                //TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
        if (action.triggered()) {
            action.onItemClicked(position);
            return;
        }
        ItemDisplayDialogFragment dialog = new ItemDisplayDialogFragment(position);
        dialog.show(getFragmentManager(), "ItemDisplayDialogFragment");
    }

    @Override
    public boolean onItemLongClick(StaggeredGridView parent, View view, int position, long id) {
        if (action.triggered())
            return false;
        action.start(position);
        return true;
    }


    private void newItem() {
        ItemEditingDialogFragment dialog = new ItemEditingDialogFragment(
                getResources().getString(R.string.dialog_hdr_title_new), null,
                new ItemEditingDialogFragment.ItemEditingListener() {
                    @Override
                    public void onItemEdited(String itemValue) {
                        ItemManager.instance(BrowserActivity.this).createItem(Ref.TYPE_UNDEFINED, itemValue);
                        gridAdapter.notifyDataSetChanged();
                    }
                });
        dialog.show(getFragmentManager(), "ItemEditingDialogFragment");
    }


    public void notifyAdapter() {
        gridAdapter.notifyDataSetChanged();
    }

    public BlockHolder getBlockHolder(int position) {
        View view = gridView.getChildAt(position);
        if (view==null)
            throw new RuntimeException("View is null");
        return (BlockHolder)view.getTag();
    }


}
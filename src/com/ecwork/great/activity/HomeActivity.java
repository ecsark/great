package com.ecwork.great.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.ecwork.great.R;

public class HomeActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gotoBrowser();
    }

    private void gotoBrowser() {
        Intent intent = new Intent(this, BrowserActivity.class);
        startActivity(intent);
    }
}

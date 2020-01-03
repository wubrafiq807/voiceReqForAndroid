package com.istvn.speechrecognitionsystem.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.istvn.speechrecognitionsystem.R;
import com.istvn.speechrecognitionsystem.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializations
        init();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();

        setSupportActionBar(toolbar);
        // Set the Title
        title.setText("Settings");

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initialization Methods
     */
    private void init() {

        setContentView(R.layout.activity_settings);
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.appNameTv);
    }
}

package com.hankarun.gevrek;

import android.os.Bundle;

public class SettingsActivty extends BaseAppcompat {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_activty);

        setupToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.content_frame1, new SettingsFragment()).commit();
    }
}

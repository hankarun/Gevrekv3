package com.hankarun.gevrek;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseAppcompat extends AppCompatActivity {
    private int currentTheme;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentTheme = getCurrentTheme();

        setTheme(currentTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        if(currentTheme != getCurrentTheme())
        {
            currentTheme = getCurrentTheme();
            setTheme(currentTheme);
            finish();
            startActivity(getIntent());
        }

        super.onResume();
    }

    void setupToolbar()
    {
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    int getCurrentTheme()
    {
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.pref_dark_theme),"0");

        if (theme.equals("0")) return R.style.AppTheme_Dark;

        return R.style.AppTheme;
    }
}

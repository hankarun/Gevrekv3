package com.hankarun.gevrek;

import android.os.Bundle;

public class NewsGroupEditActivity extends BaseAppcompat {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_group_edit);

        setupToolbar();
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(R.string.select_newsgroups);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new GroupEditFragment()).commit();
    }
}

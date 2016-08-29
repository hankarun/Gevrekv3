package com.hankarun.gevrek;

import android.os.Bundle;
import android.view.MenuItem;

public class AddCourseActivity extends BaseAppcompat {
    private AddCourseFragment addCourseFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        setupToolbar();

        addCourseFragment = new AddCourseFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, addCourseFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

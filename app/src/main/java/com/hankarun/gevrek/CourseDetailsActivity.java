package com.hankarun.gevrek;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;

public class CourseDetailsActivity extends BaseAppcompat {
    FragmentCourseDetails courseDetails;
    private String title;
    private String url;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        setupToolbar();

        if(savedInstanceState != null)
        {
            title = savedInstanceState.getString("title");
            url = savedInstanceState.getString("url");
        }else{
            Intent intent = getIntent();
            title = intent.getStringExtra("title");
            url = intent.getStringExtra("url");
        }
        toolbarTitle.setText(Html.fromHtml(title));

        courseDetails = FragmentCourseDetails.instance(url);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, courseDetails).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("title",title);
        outState.putString("url",url);
        super.onSaveInstanceState(outState);
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

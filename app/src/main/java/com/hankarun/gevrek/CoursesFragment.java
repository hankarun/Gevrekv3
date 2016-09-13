package com.hankarun.gevrek;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hankarun.gevrek.model.CowCourse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoursesFragment extends MainBaseFragment{
    @BindView(R.id.newsGroupRecycle) RecyclerView mRecyclerView;

    CowCourseAdapter mAdapter;

    public CoursesFragment()
    {
        mAdapter = new CowCourseAdapter(null, R.layout.course_list_row, getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses, container, false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.miAddNew:
                Intent intent = new Intent(getContext(), AddCourseActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onloadFinished(Object _data) {
        mAdapter.setData((ArrayList<CowCourse>) _data);
    }

    public class CowCourseAdapter extends RecyclerView.Adapter<CowCourseAdapter.ViewHolder> {
        private List<CowCourse> courseList;
        private int rowLayout;
        private Context mContext;
        View v;

        public CowCourseAdapter(ArrayList<CowCourse> data, int rowLayout, Context context) {
            if(data == null)
                this.courseList = new ArrayList<>();
            else
                this.courseList = data;
            this.rowLayout = rowLayout;
            this.mContext = context;
        }

        public void setData(ArrayList<CowCourse> data)
        {
            this.courseList = data;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            final CowCourse course = courseList.get(i);
            viewHolder.onBind(course);
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.courseName)
            TextView mCourseName;
            @BindView(R.id.courseCode)
            TextView mCourseCode;
            @BindView(R.id.courseLayout)
            LinearLayout mCourseLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }

            public void onBind(final CowCourse course)
            {
                mCourseName.setText(course.mCourseName);
                mCourseCode.setText(course.mCourseCode);
                mCourseLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CourseDetailsActivity.class);
                        intent.putExtra("url",course.mCourseUrl);
                        intent.putExtra("title", course.mCourseCode);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(getActivity(), mCourseCode, "courseCode");
                        startActivity(intent, options.toBundle());
                    }
                });
            }
        }
    }
}

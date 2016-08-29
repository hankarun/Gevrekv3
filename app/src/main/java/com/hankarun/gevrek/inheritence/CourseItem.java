package com.hankarun.gevrek.inheritence;

import android.view.View;

import com.hankarun.gevrek.model.CowCourse;

import butterknife.ButterKnife;

public class CourseItem extends EditBase {
    private CowCourse cowCourse;

    public CourseItem(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }


    @Override
    void onBind(Object object) {

    }
}

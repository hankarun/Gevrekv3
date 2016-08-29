package com.hankarun.gevrek.lib;

import android.content.Context;

import com.hankarun.gevrek.model.CowCourse;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class CowCourseListLoader extends CowAsyncLoader<ArrayList<CowCourse>>{
    public CowCourseListLoader(Context context) {
        super(context);
        url = "https://cow.ceng.metu.edu.tr/Courses/";
    }

    @Override
    public ArrayList<CowCourse> getData(Document doc) {
        ArrayList<CowCourse> courseList = new ArrayList<>();
        Elements courses = doc.select("div#mtm_menu_horizontal");
        Elements as = courses.select("a");
        for(int x = 1; x < as.size(); ++x) {
            CowCourse tempCourse = new CowCourse();
            tempCourse.mCourseCode = as.get(x).html();
            tempCourse.mCourseUrl = as.get(x).attr("href");
            courseList.add(tempCourse);
        }

        Elements courseNames = doc.select("table.cow").select("tr");
        for(int x = 4; x < courseNames.size(); ++x)
        {
            Elements singles = courseNames.get(x).select("td");
            if(singles.size() > 1) {
                String courseCode = singles.get(0).text();
                for (CowCourse course : courseList) {
                    if (course.mCourseCode.equals(courseCode))
                        course.mCourseName = singles.get(1).text();
                }
            }
        }
        return courseList;
    }
}

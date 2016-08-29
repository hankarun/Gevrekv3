package com.hankarun.gevrek.lib;

import android.content.Context;

import com.hankarun.gevrek.model.CowCourse;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class CourseAddLoader extends CowAsyncLoader<ArrayList<CourseEdit>> {
    public CourseAddLoader(Context context) {
        super(context);
        url = "https://cow.ceng.metu.edu.tr/Courses/";
    }

    @Override
    public ArrayList<CourseEdit> getData(Document doc) {
        ArrayList<CowCourse> studentList = new ArrayList<>();
        Elements courses = doc.select("div#mtm_menu_horizontal");
        Elements as = courses.select("a");
        for (int x = 1; x < as.size(); ++x) {
            CowCourse tempCourse = new CowCourse();
            tempCourse.mCourseCode = as.get(x).html();
            tempCourse.mCourseUrl = as.get(x).attr("href");
            studentList.add(tempCourse);
        }

        ArrayList<CourseEdit> courseList = new ArrayList<>();
        Elements courseNames = doc.select("table.cow").select("tr");
        for (int x = 4; x < courseNames.size(); ++x) {
            Elements singles = courseNames.get(x).select("td");
            String courseCode = singles.get(0).text();
            for (CowCourse course : studentList) {
                if (!course.mCourseCode.equals(courseCode)) {
                    CourseEdit tempCourse = new CourseEdit();
                    tempCourse.mCourseCode = singles.get(0).text();
                    tempCourse.mCourseName = singles.get(1).text();
                    courseList.add(tempCourse);
                    break;
                }
            }
        }
        return courseList;
    }
}
package com.hankarun.gevrek.lib;

import android.content.Context;

import com.hankarun.gevrek.model.CourseDetail;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CourseDetailLoader extends CowAsyncLoader<CourseDetail> {
    public CourseDetailLoader(Context context, String _url) {
        super(context);
        url = "https://cow.ceng.metu.edu.tr"+_url;
    }

    @Override
    public CourseDetail getData(Document doc) {
        Elements tds = doc.select("td[class=content]");
        Elements tds1 = tds.select("td");

        CourseDetail temp = new CourseDetail();

        temp.mInfo = tds1.get(1).html();
        temp.mInstructors = tds1.get(2).toString();
        temp.mAnnounsments = tds1.get(3).toString();
        temp.mLectureNotes = tds1.get(4).toString();
        temp.mExams = tds1.get(5).toString();
        temp.mHomeworks = tds1.get(6).toString();

        return temp;
    }
}

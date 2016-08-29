package com.hankarun.gevrek.lib;

import android.content.Context;

import com.hankarun.gevrek.model.NewsGroupEdit;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class NewsGroupListLoader extends CowAsyncLoader<ArrayList<NewsGroupEdit>> {
    public NewsGroupListLoader(Context context) {
        super(context);
        url = "https://cow.ceng.metu.edu.tr/News/setOptions.php";
    }

    @Override
    public ArrayList<NewsGroupEdit> getData(Document doc) {
        Elements test = doc.select("input");
        ArrayList<NewsGroupEdit> groups = new ArrayList<NewsGroupEdit>();

        for(Element e: test){
            if(e.attr("name").equals("mygroups[]")){
                NewsGroupEdit tmp = new NewsGroupEdit();

                tmp.mGroupName = e.attr("value");
                tmp.mChecked = e.hasAttr("checked");
                tmp.mDisabled = e.hasAttr("disabled");

                groups.add(tmp);
            }
        }
        return groups;
    }
}

package com.hankarun.gevrek.lib;

import android.content.Context;

import com.hankarun.gevrek.model.GroupMessage;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Vector;

public class GroupMessagesLoader extends CowAsyncLoader<GroupMessagesLoader.DataStore>{
    public class DataStore
    {
        public Vector<String> pages;
        public ArrayList<GroupMessage> groupMessages;
    }

    private boolean loadPages;
    public GroupMessagesLoader(Context context, String _url, boolean loadPages) {
        super(context);
        url = _url;
        this.loadPages = loadPages;
    }

    @Override
    public DataStore getData(Document doc) {
        DataStore dataStore = new DataStore();
        dataStore.pages = new Vector<>();
        dataStore.groupMessages = new ArrayList<>();
        if(loadPages)
        {
            Elements pages = doc.select("span.np_pages").select("a.np_pages_unselected");
            for(Element e: pages)
            {
                dataStore.pages.add(e.attr("href"));
            }
        }


        Elements mainTable = doc.select("tr");
        for(int x = 1; x < mainTable.size(); ++x)
        {
            GroupMessage tempHeader = new GroupMessage();
            Elements trEl = mainTable.get(x).select("td");
            if(trEl.size() > 3)
            {
                tempHeader.mMessageDate = trEl.get(0).select("span.np_thread_line_text").html();
                tempHeader.mMessage[0] = trEl.get(1).select("span.np_thread_line_text").select("a[href]").html();
                tempHeader.mMessage[1] = trEl.get(1).select("span.np_thread_line_text").select("a[href]").attr("href");
                tempHeader.mAuthor = trEl.get(3).select("span.np_thread_line_text").text();
                tempHeader.mLevel = trEl.get(1).select("img.thread_image").size();

                dataStore.groupMessages.add(tempHeader);
            }
        }
        return dataStore;
    }
}

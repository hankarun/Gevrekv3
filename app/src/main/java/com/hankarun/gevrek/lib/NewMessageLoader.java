package com.hankarun.gevrek.lib;

import android.content.Context;

import com.hankarun.gevrek.model.CowMessage;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;

public class NewMessageLoader extends CowAsyncLoader<CowMessage> {
    public NewMessageLoader(Context context, String _url) {
        super(context);
        url = _url;
    }

    @Override
    public CowMessage getData(Document doc) {
        CowMessage tempMessage = new CowMessage();

        Elements trs = doc.select("input");
        tempMessage.mSubject = trs.get(0).attr("value");
        tempMessage.mFrom = trs.get(1).attr("value");

        Elements aa = doc.select("textarea");
        if(aa.size() > 8) {
            tempMessage.mBody = aa.get(0).text();

            tempMessage.replyParameters = new HashMap<>();
            tempMessage.replyParameters.put(trs.get(4).attr("name"), trs.get(4).attr("value"));
            tempMessage.replyParameters.put(trs.get(5).attr("name"), trs.get(5).attr("value"));
            tempMessage.replyParameters.put(trs.get(6).attr("name"), trs.get(6).attr("value"));
            tempMessage.replyParameters.put(trs.get(7).attr("name"), trs.get(7).attr("value"));
        }
        return tempMessage;

    }
}


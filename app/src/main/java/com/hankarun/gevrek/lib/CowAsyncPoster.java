package com.hankarun.gevrek.lib;

import android.content.Context;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

public class CowAsyncPoster extends CowAsyncLoader<Boolean> {
    public Map<String,String> paramList;

    public CowAsyncPoster(Context context,Map<String,String> _paramList, String _url) {
        super(context);
        paramList = _paramList;
        url = _url;
    }

    @Override
    public Boolean loadInBackground() {
        checkTime();
        if(cookieMap.size() > 0) {
            try {
                Jsoup.connect(url)
                        .data(paramList)
                        .cookies(cookieMap)
                        .post();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        try {
            Connection.Response res= Jsoup.connect(url)
                    .data(paramList)
                    .data("username", "e175903", "password", "For9876um")
                    .cookies(cookieMap)
                    .execute();

            cookieMap = res.cookies();
            cookieTime =  Calendar.getInstance();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

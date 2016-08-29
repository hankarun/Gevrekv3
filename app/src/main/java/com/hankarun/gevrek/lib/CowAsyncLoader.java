package com.hankarun.gevrek.lib;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class CowAsyncLoader<N> extends AsyncTaskLoader<N> {
    static public Map<String,String> cookieMap = new HashMap<>();
    public String url;
    static Calendar cookieTime;
    private String username;
    private String password;

    public CowAsyncLoader(Context context) {
        super(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        username = prefs.getString("username", "");
        password = prefs.getString("password", "");
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    static public boolean checkTime()
    {
        if(cookieTime == null)
            return false;
        Calendar previous = Calendar.getInstance();
        previous.setTime(cookieTime.getTime());
        Calendar now = Calendar.getInstance();
        long diff = now.getTimeInMillis() - previous.getTimeInMillis();
        if(diff >= 10 * 60 * 1000)
        {
            cookieMap.clear();
        }
        return false;
    }

    public void clearCache()
    {
        cookieMap.clear();
    }

    Document get()
    {
        checkTime();
        if(cookieMap.size() > 0) {
            try {
                return Jsoup.connect(url)
                        .cookies(cookieMap)
                        .timeout(4000)
                        .get();
            } catch (IOException e) {
                return null;
            }
        }
        try {
            Connection.Response res = Jsoup.connect(url)
                    .data("username", username, "password", password)
                    .method(Connection.Method.POST)
                    .timeout(4000)
                    .execute();

            cookieMap = res.cookies();
            cookieTime =  Calendar.getInstance();
            return res.parse();
        } catch (IOException e) {
            return null;
        }
    }


    public N getData(Document doc)
    {
        return null;
    }

    @Override
    public N loadInBackground() {
        Document doc = get();
        if(doc != null)
            return getData(doc);
        else
        {
            return null;
        }
    }
}

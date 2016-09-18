package com.hankarun.gevrek;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

public class MainBaseFragment extends Fragment{
    public onPageRefreshed mRefreshCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        onAttach(activity);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mRefreshCallback = (onPageRefreshed) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void onLoadFragment()
    {

    }

    public void onRefreshFragment()
    {

    }

    public void onloadFinished(Object _data){

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mRefreshCallback = null;
    }
}

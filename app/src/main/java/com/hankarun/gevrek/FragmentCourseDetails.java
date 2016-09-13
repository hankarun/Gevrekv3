package com.hankarun.gevrek;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.hankarun.gevrek.lib.CourseDetailLoader;
import com.hankarun.gevrek.lib.CowAsyncLoader;
import com.hankarun.gevrek.model.CourseDetail;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentCourseDetails extends Fragment implements LoaderManager.LoaderCallbacks {
    private CourseDetailsAdapter mAdapter;
    @BindView(R.id.courseDetailList)
    RecyclerView mGroupRecycler;
    static public FragmentCourseDetails instance(String course)
    {
        FragmentCourseDetails newFragment = new FragmentCourseDetails();

        Bundle args = new Bundle();
        args.putString("url", course);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_details, container, false);
        ButterKnife.bind(this,view);

        mAdapter = new CourseDetailsAdapter(getContext(), null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mGroupRecycler.setLayoutManager(linearLayoutManager);
        mGroupRecycler.setItemAnimator(new DefaultItemAnimator());
        mGroupRecycler.setAdapter(mAdapter);

        Bundle bundle = new Bundle();
        bundle.putString("url",getArguments().getString("url"));
        getActivity().getSupportLoaderManager().initLoader(1,bundle,this);
        return view;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CourseDetailLoader(getContext(), args.getString("url"));
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mAdapter.setData((CourseDetail) data);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public class CourseDetailsAdapter extends RecyclerView.Adapter<CourseDetailsAdapter.ViewHolder> {
        private Context mContext;
        private View v;
        private CourseDetail courseDetail;

        public CourseDetailsAdapter(Context context, CourseDetail _detail) {
            courseDetail = _detail;
            this.mContext = context;
        }

        public void setData(CourseDetail _detail)
        {
            courseDetail = _detail;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_detail_card, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.onBind(courseDetail, i);
        }

        @Override
        public int getItemCount() {
            return courseDetail == null ? 0 : 6;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.sectionName)
            TextView sectionName;
            @BindView(R.id.webView)
            WebView sectionDetail;

            private class HelloWebViewClient extends WebViewClient {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        // AppRTC requires third party cookies to work
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.setAcceptThirdPartyCookies(view, true);
                        cookieManager.setAcceptCookie(true);
                    }
                    view.loadUrl(url, CowAsyncLoader.cookieMap);
                    /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                    String username = prefs.getString("username", "");
                    String password = prefs.getString("password", "");
                    Uri uri = Uri.parse(url + "&cow_username="+username+"&cow_password="+password+"&cow_login=Login");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);*/
                    return true;
                }
            }

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                sectionDetail.setWebViewClient(new HelloWebViewClient());
            }

            public void onBind(final CourseDetail course, int position)
            {
                switch (position)
                {
                    case 3:
                        sectionName.setText("Info");
                        sectionDetail.loadDataWithBaseURL("https://cow.ceng.metu.edu.tr", course.mInfo,"text/html; charset=UTF-8", null, null);
                        break;
                    case 4:
                        sectionName.setText("Staff");
                        sectionDetail.loadDataWithBaseURL("https://cow.ceng.metu.edu.tr", course.mInstructors,"text/html; charset=UTF-8", null, null);
                        break;
                    case 5:
                        sectionName.setText("Annou");
                        sectionDetail.loadDataWithBaseURL("https://cow.ceng.metu.edu.tr", course.mAnnounsments,"text/html; charset=UTF-8", null, null);
                        break;
                    case 2:
                        sectionName.setText("Notes");
                        sectionDetail.loadDataWithBaseURL("https://cow.ceng.metu.edu.tr/Courses", course.mLectureNotes,"text/html; charset=UTF-8", null, null);
                        break;
                    case 1:
                        sectionName.setText("Exams");
                        sectionDetail.loadDataWithBaseURL("https://cow.ceng.metu.edu.tr", course.mExams,"text/html; charset=UTF-8", null, null);
                        break;
                    case 0:
                        sectionName.setText("Homeworks");
                        sectionDetail.loadDataWithBaseURL("https://cow.ceng.metu.edu.tr", course.mHomeworks,"text/html; charset=UTF-8", null, null);
                        break;
                }

                /*sectionDetail.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        startActivity(
                                new Intent(Intent.ACTION_VIEW, request.getUrl()));
                        return true;
                    }
                });*/
            }
        }
    }
}

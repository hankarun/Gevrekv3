package com.hankarun.gevrek;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hankarun.gevrek.model.NewsGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsGroupsFragment extends MainBaseFragment{
    @BindView(R.id.newsGroupRecycle) RecyclerView mRecyclerView;
    @BindView(R.id.newsgroupSwipe) SwipeRefreshLayout mSwipeRefreshLayout;

    private NewsGroupsAdapter mAdapter;

    public NewsGroupsFragment()
    {
        mAdapter = new NewsGroupsAdapter(null, R.layout.newsgroup_row, getContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsgroups, container, false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);

        mAdapter = new NewsGroupsAdapter(null, R.layout.newsgroup_row, getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshCallback.onPageRefreshed();
            }
        });

        mSwipeRefreshLayout.setRefreshing(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.miAddNew:
                if(MainActivity.usernameAndPasswordSet(getActivity()))
                {
                    Intent intent = new Intent(getContext(), NewsGroupEditActivity.class);
                    startActivity(intent);
                } else
                    Snackbar.make(getActivity().findViewById(R.id.coordinator), "You need to login in.", Snackbar.LENGTH_SHORT)
                            .setAction("Login in.", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view) {
                                    new LoginDialog(getActivity());
                                }
                            })
                            .show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onloadFinished(Object _data) {
        LinkedHashMap<String, Vector<NewsGroup>> data = (LinkedHashMap<String, Vector<NewsGroup>>) _data;
        //mSwipeRefreshLayout.setRefreshing(false);
        if(data != null)
            mAdapter.setData(data);
    }

    public class NewsGroupsAdapter extends RecyclerView.Adapter<NewsGroupsAdapter.ViewHolder> {
        class head
        {
            int type;
            String name;
            String url;
        }
        private List<head> newsGroupList;
        private int rowLayout;
        private Context mContext;
        View v;

        public NewsGroupsAdapter(LinkedHashMap<String, Vector<NewsGroup>> data, int rowLayout, Context context) {
            if(data == null)
                this.newsGroupList = new ArrayList<>();
            else
                this.newsGroupList = getList(data);
            this.rowLayout = rowLayout;
            this.mContext = context;
        }

        public void setData(LinkedHashMap<String, Vector<NewsGroup>> data)
        {
            this.newsGroupList = getList(data);
            notifyDataSetChanged();
        }

        List<head> getList(LinkedHashMap<String, Vector<NewsGroup>> data)
        {
            List<head> temp = new ArrayList<>();
            for (Map.Entry<String, Vector<NewsGroup>> entry : data.entrySet()) {
                head thead = new head();
                thead.name = entry.getKey();
                thead.type = 0;
                temp.add(thead);
                for (NewsGroup group : entry.getValue()) {
                    head shead = new head();
                    shead.name = group.mName;
                    shead.name += " " + group.mCount;
                    shead.url = group.mUrl;
                    shead.type = 1;
                    temp.add(shead);
                }
            }

            return temp;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            final head newsGroup = newsGroupList.get(i);
            viewHolder.onBind(newsGroup);
        }

        @Override
        public int getItemCount() {
            return newsGroupList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return newsGroupList.get(position).type;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.recyclerItemText) TextView newsGroupName;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }

            public void onBind(final head newsGroup)
            {
                if(newsGroup.type == 0) {
                    //newsGroupName.setBackgroundResource(R.attr.colorPrimary);
                    TypedValue outValue = new TypedValue();
                    getContext().getTheme().resolveAttribute(R.attr.colorPrimary, outValue, true);
                    newsGroupName.setBackgroundColor(outValue.data);
                    newsGroupName.setTextColor(Color.WHITE);
                    newsGroupName.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                newsGroupName.setText(Html.fromHtml(newsGroup.name));

                newsGroupName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    if(newsGroup.type == 1) {
                        Intent intent = new Intent(getContext(), GroupMessagesActivity.class);
                        intent.putExtra("url",newsGroup.url);
                        intent.putExtra("group", newsGroup.name);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(getActivity(), newsGroupName, "groupName");
                        startActivity(intent, options.toBundle());
                    }
                    }
                });
            }
        }
    }
}

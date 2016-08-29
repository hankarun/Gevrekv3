package com.hankarun.gevrek;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.hankarun.gevrek.lib.CowAsyncPoster;
import com.hankarun.gevrek.lib.EndlessRecyclerViewScrollListener;
import com.hankarun.gevrek.lib.GroupMessagesLoader;
import com.hankarun.gevrek.lib.MessageDetailLoader;
import com.hankarun.gevrek.lib.NewMessageLoader;
import com.hankarun.gevrek.model.CowMessage;
import com.hankarun.gevrek.model.GroupMessage;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupMessagesFragment extends Fragment implements LoaderManager.LoaderCallbacks{
    OnFabStateChanged mCallback;
    private String mainGroupUrl;
    private String groupUrl;
    private GroupMessagesAdapter mAdapter;

    @BindView(R.id.groupMesagesRecycle)
    RecyclerView mGroupMessagesRecycler;

    @BindView(R.id.groupMessagesSwipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.slidingReadMessage)
    SlidingLayer mSlidingRead;

    @BindView(R.id.slidingWriteMessage)
    SlidingLayer mSlidingWrite;

    @BindView(R.id.author)
    TextView author;
    @BindView(R.id.body)
    TextView body;
    @BindView(R.id.dateText)
    TextView date;
    @BindView(R.id.imageView2)
    NetworkImageView avatar;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.messageTopicTextView)
    TextView messageTopicText;
    @BindView(R.id.writeBodyEdit)
    EditText mWriteBody;
    @BindView(R.id.writeFromEdit)
    EditText mWriteFrom;
    @BindView(R.id.writeSubjectEdit)
    EditText mWriteSubject;

    private CowMessage tempMessage;
    private ImageLoader mImageLoader;
    private Vector<String> pagesList;
    private Snackbar mGlobalSnackBar;

    private boolean firstTime = true;

    static public GroupMessagesFragment instance(String group)
    {
        GroupMessagesFragment newFragment = new GroupMessagesFragment();

        Bundle args = new Bundle();
        args.putString("url", group);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            mainGroupUrl = savedInstanceState.getString("url");
            groupUrl = savedInstanceState.getString("url");
        }
            else {
            mainGroupUrl = getArguments().getString("url");
            groupUrl = getArguments().getString("url");
        }

        try {
            mCallback = (OnFabStateChanged) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }

        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });



        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url",mainGroupUrl);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_messages, container, false);
        ButterKnife.bind(this,view);

        mAdapter = new GroupMessagesAdapter(new ArrayList<GroupMessage>(), R.layout.group_messages_row);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mGroupMessagesRecycler.setLayoutManager(linearLayoutManager);
        mGroupMessagesRecycler.setItemAnimator(new DefaultItemAnimator());
        mGroupMessagesRecycler.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getSupportLoaderManager().restartLoader(1,null,GroupMessagesFragment.this);
            }
        });

        getActivity().getSupportLoaderManager().initLoader(1,null,this);

        mSlidingRead.setOnInteractListener(new SlidingLayer.OnInteractListener() {
            @Override
            public void onOpen() {
            }

            @Override
            public void onShowPreview() {

            }

            @Override
            public void onClose() {
                mCallback.onFabStateChange(FABSTATE.NEWMESSAGE);
            }

            @Override
            public void onOpened() {
            }

            @Override
            public void onPreviewShowed() {

            }

            @Override
            public void onClosed() {
            }
        });

        mSlidingWrite.setOnInteractListener(new SlidingLayer.OnInteractListener() {
            @Override
            public void onOpen() {
            }

            @Override
            public void onShowPreview() {

            }

            @Override
            public void onClose() {
                if(mSlidingRead.isOpened())
                    mCallback.onFabStateChange(FABSTATE.REPLY);
                else
                    mCallback.onFabStateChange(FABSTATE.NEWMESSAGE);
            }

            @Override
            public void onOpened() {
            }

            @Override
            public void onPreviewShowed() {

            }

            @Override
            public void onClosed() {
            }
        });

        mGroupMessagesRecycler.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(pagesList.size() > 0)
                {
                    groupUrl = "https://cow.ceng.metu.edu.tr/News/"+pagesList.firstElement();
                    pagesList.remove(0);
                    getActivity().getSupportLoaderManager().restartLoader(1,null,GroupMessagesFragment.this);
                }
            }
        });

        mGlobalSnackBar = Snackbar.make(getActivity().findViewById(R.id.coordinator), "Loading.", Snackbar.LENGTH_INDEFINITE);

        return view;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id)
        {
            case 1:
                return new GroupMessagesLoader(getContext(),groupUrl, true);
            case 2:
                return new MessageDetailLoader(getContext(),args.getString("url"));
            case 3:
                return new NewMessageLoader(getContext(), args.getString("url"));
            case 4:
                if(args != null)
                    return new CowAsyncPoster(getContext(),
                            (HashMap<String, String>) args.getSerializable("params"),
                            "https://cow.ceng.metu.edu.tr/News/post.php");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case 1:
                GroupMessagesLoader.DataStore loaderData = (GroupMessagesLoader.DataStore) data;
                int pageSize;
                if(firstTime)
                {
                    firstTime = false;
                    pageSize = loaderData.pages.size();
                    pagesList = loaderData.pages;
                }
                else
                    pageSize = pagesList.size();
                mAdapter.setData(loaderData.groupMessages,pageSize);
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case 2:
                if(data != null) {
                    setMessageRead((CowMessage) data);
                    mSlidingRead.openLayer(true);
                    mGlobalSnackBar.dismiss();
                }else
                {
                    Snackbar.make(getActivity().findViewById(R.id.coordinator), "Network problem.", Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;
            case 3:
                setNewMessage((CowMessage) data);
                mSlidingWrite.openLayer(true);
                break;
            case 4:
                getActivity().getSupportLoaderManager().restartLoader(1, null, GroupMessagesFragment.this);
                mSwipeRefreshLayout.setRefreshing(true);
                Snackbar.make(getActivity().findViewById(R.id.coordinator), "Message send.", Snackbar.LENGTH_SHORT)
                        .show();
                break;
        }
    }

    public void fabClicked(FABSTATE fabstate)
    {
        switch (fabstate) {
            case REPLY:
            {
                Bundle args = new Bundle();
                args.putString("url", "https://cow.ceng.metu.edu.tr/News/" + tempMessage.mReplyTo);
                getActivity().getSupportLoaderManager().restartLoader(3, args, GroupMessagesFragment.this);
            } break;
            case NEWMESSAGE:
            {
                Bundle args = new Bundle();
                String _url = groupUrl;
                _url = _url.replace("thread","post");
                _url = _url.replace("group=","newsgroups=");
                _url += "&type=new";
                args.putString("url", _url);
                getActivity().getSupportLoaderManager().restartLoader(3, args, GroupMessagesFragment.this);
            } break;
            case POSTMESSAGE:
                mSlidingWrite.closeLayer(true);
                mSlidingRead.closeLayer(true);
                tempMessage.mSubject = mWriteSubject.getText().toString();
                tempMessage.mBody = mWriteBody.getText().toString();
                Snackbar.make(getActivity().findViewById(R.id.coordinator), "Message sending.", Snackbar.LENGTH_INDEFINITE)
                        .show();
                postMessage(tempMessage);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public interface OnFabStateChanged {
        void onFabStateChange(FABSTATE state);
    }

    public boolean backPressed()
    {
        if (mSlidingWrite.isOpened()) {
            mSlidingWrite.closeLayer(true);
            if(mSlidingRead.isOpened())
                mCallback.onFabStateChange(FABSTATE.REPLY);
            else
                mCallback.onFabStateChange(FABSTATE.NEWMESSAGE);
            return true;
        } else if (mSlidingRead.isOpened()) {
            mCallback.onFabStateChange(FABSTATE.NEWMESSAGE);
            mSlidingRead.closeLayer(true);
            return true;
        }
        return false;
    }

    private void setNewMessage(CowMessage message) {
        tempMessage = message;
        mWriteSubject.setText(message.mSubject);
        mWriteBody.setText(message.mBody);
        mWriteFrom.setText(message.mFrom);
    }

    public void setMessageRead(CowMessage message) {
        tempMessage = message;
        messageTopicText.setText(message.mSubject);
        author.setText(message.mFrom);
        body.setText(Html.fromHtml(message.mBody));
        DateFormat outputFormat = new SimpleDateFormat("HH:mm:ss / dd MMMM yyyy");
        date.setText(outputFormat.format(message.mDate));
        avatar.setImageUrl(message.mImage, mImageLoader);
        mScrollView.setVisibility(View.VISIBLE);
    }

    private void postMessage(final CowMessage message)
    {
        Map<String,String> params = new HashMap<>();
        params.put("body", message.mBody);
        params.put("subject", message.mSubject);
        params.putAll(message.replyParameters);
        params.put("type","post");
        Bundle args = new Bundle();
        args.putSerializable("params", (Serializable) params);
        getActivity().getSupportLoaderManager().initLoader(4,args,this);
    }

    public class GroupMessagesAdapter extends RecyclerView.Adapter<GroupMessagesAdapter.ViewHolder> {
        ArrayList<GroupMessage> list;
        int pageCount = 0;

        private int rowLayout;

        public GroupMessagesAdapter(ArrayList<GroupMessage> data, int rowLayout) {
            list = data;
            this.rowLayout = rowLayout;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == 0)
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false));
            if(pageCount < 1)
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false));

            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.waiting_row, parent, false));
        }

        void setData(ArrayList<GroupMessage> data, int _pageCount) {
            list.addAll(data);
            pageCount = _pageCount;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return (position >= list.size()) ? 1: 0 ;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if(getItemViewType(position) == 0)
            {
                final GroupMessage message = list.get(position);
                holder.bind(message);

                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onFabStateChange(FABSTATE.REPLY);
                        Bundle args = new Bundle();
                        args.putString("url",message.mMessage[1]);
                        getActivity().getSupportLoaderManager().restartLoader(2,args,GroupMessagesFragment.this);
                        mCallback.onFabStateChange(FABSTATE.REPLY);
                        mGlobalSnackBar = Snackbar.make(getActivity().findViewById(R.id.coordinator), "Loading.", Snackbar.LENGTH_INDEFINITE);
                        mGlobalSnackBar.show();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            if(pageCount > 0)
               return list.size() + 1;
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.dateTextView)
            public TextView date;
            @BindView(R.id.messageHeaderText)
            public TextView message;
            @BindView(R.id.authorTestView)
            public TextView author;
            @BindView(R.id.messageHeaderLayout)
            public LinearLayout layout;
            @BindView(R.id.lines)
            public LinearLayout lines;



            public void bind(GroupMessage message) {
                LinearLayout.LayoutParams params = new
                        LinearLayout.LayoutParams
                        (5, LinearLayout.LayoutParams.WRAP_CONTENT);
                lines.removeAllViews();
                lines.setPadding(10,0,10,0);
                for(int a = 1;a<message.mLevel;a++)
                {
                    if(a > 20)
                        break;
                    View first = new View(getActivity());
                    first.setLayoutParams(params);
                    first.setPadding(10,0,10,0);
                    first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    lines.addView(first);
                }

                this.message.setText(Html.fromHtml(message.mMessage[0]));
                author.setText(message.mAuthor);
                date.setText(Html.fromHtml(message.mMessageDate));
            }

            public ViewHolder(View itemView) {
                super(itemView);
                try {
                    ButterKnife.bind(this,itemView);
                }catch (Exception ignored)
                {

                }
            }
        }
    }
}

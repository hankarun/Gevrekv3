package com.hankarun.gevrek;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.hankarun.gevrek.lib.CowAsyncPoster;
import com.hankarun.gevrek.lib.NewsGroupListLoader;
import com.hankarun.gevrek.model.NewsGroupEdit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupEditFragment extends Fragment implements LoaderManager.LoaderCallbacks{
    @BindView(R.id.allGroupsList) ListView listview;
    @BindView(R.id.groupSearchEdit) EditText filterEditText;
    @BindView(R.id.groupsave) Button mGroupSave;
    GroupListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_group, container, false);
        ButterKnife.bind(this,view);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsGroupEdit tmp = (NewsGroupEdit) mAdapter.getItem(i);

                mAdapter.names.get(mAdapter.names.indexOf(tmp)).mChecked = !mAdapter.names.get(mAdapter.names.indexOf(tmp)).mChecked;
                mAdapter.notifyDataSetChanged();
            }
        });

        filterEditText.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(mAdapter != null)
                    mAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        filterEditText.setOnTouchListener(new View.OnTouchListener() {
            final Drawable imgX = getResources().getDrawable(R.mipmap.ic_launcher );
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Is there an X showing?
                if (filterEditText.getCompoundDrawables()[2] == null) return false;
                // Only do this for up touches
                if (event.getAction() != MotionEvent.ACTION_UP) return false;
                // Is touch on our clear button?
                assert imgX != null;
                if (event.getX() > filterEditText.getWidth() - filterEditText.getPaddingRight() - imgX.getIntrinsicWidth()) {
                    filterEditText.setText("");

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                return false;
            }
        });

        mGroupSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendGroupsviaVolley();
            }
        });

        getActivity().getSupportLoaderManager().initLoader(1,null,this);
        return view;
    }

    private void sendGroupsviaVolley(){
        Map<String,String> paramList = new HashMap<>();
        paramList.put("submitOptions","save Options");
        for(NewsGroupEdit group: mAdapter.names){
            if(group.mChecked)
                paramList.put("mygroups["+mAdapter.names.indexOf(group)+"]",group.mGroupName);
        }
        paramList.put("type","post");
        Bundle args = new Bundle();
        args.putSerializable("params", (Serializable) paramList);
        getActivity().getSupportLoaderManager().initLoader(2,args,this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 1:
                return new NewsGroupListLoader(getContext());
            case 2:
                if(args != null)
                    return new CowAsyncPoster(getContext(),
                            (HashMap<String, String>) args.getSerializable("params"),
                             "https://cow.ceng.metu.edu.tr/News/setOptions.php");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId())
        {
            case 1:
                mAdapter = new GroupListAdapter(getContext(),(ArrayList<NewsGroupEdit>) data);
                listview.setAdapter(mAdapter);
                break;
            case 2:
                getActivity().finish();
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


    public class GroupListAdapter extends BaseAdapter implements Filterable {
        final Context context;
        public final ArrayList<NewsGroupEdit> names;
        private final ArrayList<NewsGroupEdit> filteredModelItemsArray;
        CustomFilter filter;

        public GroupListAdapter(Context _context, ArrayList<NewsGroupEdit> _names){
            context = _context;
            names = _names;
            filteredModelItemsArray = new ArrayList<>();
            filteredModelItemsArray.addAll(names);
        }

        @Override
        public int getCount() {
            return filteredModelItemsArray.size();
        }

        @Override
        public Object getItem(int i) {
            return filteredModelItemsArray.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                LayoutInflater infalInflater = LayoutInflater.from(context);
                view = infalInflater.inflate(R.layout.group_edit_item, null);
            }

            TextView newsGroupEdit = (TextView) view.findViewById(R.id.groupNameText);
            CheckBox groupCheck = (CheckBox) view.findViewById(R.id.groupCheckBox);

            NewsGroupEdit tmp = filteredModelItemsArray.get(i);

            newsGroupEdit.setText(tmp.mGroupName);
            groupCheck.setChecked(tmp.mChecked);
            groupCheck.setEnabled(!tmp.mDisabled);

            return view;
        }

        @Override
        public Filter getFilter() {
            if (filter == null){
                filter = new CustomFilter();
            }
            return filter;
        }


        private class CustomFilter extends Filter {

            @SuppressLint("DefaultLocale")
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if(constraint.toString().length() > 0)
                {
                    ArrayList<NewsGroupEdit> filteredItems = new ArrayList<>();

                    for(int i = 0, l = names.size(); i < l; i++)
                    {
                        String m = names.get(i).mGroupName;
                        if(m.contains(constraint))
                            filteredItems.add(names.get(i));
                    }
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                }
                else
                {
                    synchronized(this)
                    {
                        result.values = names;
                        result.count = names.size();
                    }
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                filteredModelItemsArray.clear();
                filteredModelItemsArray.addAll((ArrayList<NewsGroupEdit>) results.values);
                notifyDataSetChanged();
            }

        }
    }
}

package com.hankarun.gevrek;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hankarun.gevrek.lib.CourseAddLoader;
import com.hankarun.gevrek.lib.CourseEdit;
import com.hankarun.gevrek.model.CowCourse;
import com.hankarun.gevrek.model.CowMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddCourseFragment extends Fragment implements LoaderManager.LoaderCallbacks{
    @BindView(R.id.allGroupsList)
    ListView listview;
    @BindView(R.id.groupSearchEdit)
    EditText filterEditText;

    CourseListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_course, container, false);
        ButterKnife.bind(this,view);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CourseEdit tmp = (CourseEdit) mAdapter.getItem(i);
                mAdapter.names.get(mAdapter.names.indexOf(tmp)).mChecked = !mAdapter.names.get(mAdapter.names.indexOf(tmp)).mChecked;
                mAdapter.notifyDataSetChanged();
            }
        });

        mAdapter = new CourseListAdapter(getContext(),null);
        listview.setAdapter(mAdapter);
        getActivity().getSupportLoaderManager().initLoader(1,null,this);
        return view;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CourseAddLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mAdapter.setData((ArrayList<CourseEdit>) data);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public class CourseListAdapter extends BaseAdapter implements Filterable {


        final Context context;
        public ArrayList<CourseEdit> names;
        private ArrayList<CourseEdit> filteredModelItemsArray;
        CustomFilter filter;

        public CourseListAdapter(Context _context, ArrayList<CourseEdit> _names){
            context = _context;
            setData(_names);
        }

        public void setData(ArrayList<CourseEdit> _names)
        {
            if(_names != null)
            {
                names = _names;
                filteredModelItemsArray = new ArrayList<>();
                filteredModelItemsArray.addAll(names);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return filteredModelItemsArray == null ? 0 : filteredModelItemsArray.size();
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
                view = infalInflater.inflate(R.layout.course_edit_item, null);
            }

            TextView courseCode = (TextView) view.findViewById(R.id.courseCodeTextV);
            TextView courseName = (TextView) view.findViewById(R.id.courseNameTextV);

            final CourseEdit tmp = filteredModelItemsArray.get(i);
            courseCode.setText(tmp.mCourseCode);
            courseName.setText(tmp.mCourseName);

            LinearLayout courseLayout = (LinearLayout) view.findViewById(R.id.courseEditLayout);
            courseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Bu dersi eklemek istediginizden emin misiniz?");
                    builder.setTitle(tmp.mCourseCode);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Post add course
                            addCourse(tmp);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                    });
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            return view;
        }

        private void addCourse(CowCourse course)
        {
            /*Map<String,String> params = new HashMap<>();
            params.put("username",username);
            params.put("course", course.mCourseCode);
            params.put("semester", semester);
            params.put("task_courses_student","add");
            params.put("author", username);
            params.put("locked", locked);
            Bundle args = new Bundle();
            args.putSerializable("params", (Serializable) params);
            getActivity().getSupportLoaderManager().initLoader(4,args,this);*/
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
                    ArrayList<CourseEdit> filteredItems = new ArrayList<>();

                    for(int i = 0, l = names.size(); i < l; i++)
                    {
                        String m = names.get(i).mCourseName;
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
                filteredModelItemsArray.addAll((ArrayList<CourseEdit>) results.values);
                notifyDataSetChanged();
            }

        }
    }
}

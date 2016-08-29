package com.hankarun.gevrek;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.hankarun.gevrek.model.BaseItem;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter implements Filterable {
    final Context context;
    public ArrayList<EditItem> names;
    private ArrayList<EditItem> filteredModelItemsArray;
    CustomFilter filter;
    int viewId;

    public class EditItem
    {
        public BaseItem     mItem;
        public boolean      mChecked;
        public boolean      mDisabled;
    }

    public void setData(Object data)
    {
        ArrayList<BaseItem> list = (ArrayList<BaseItem>) data;
        ArrayList<EditItem> newList = new ArrayList<>();
        for (BaseItem item : list) {
            EditItem it = new EditItem();
            it.mItem = item;
            newList.add(it);
        }
        names = newList;
        filteredModelItemsArray = new ArrayList<>();
        filteredModelItemsArray.addAll(names);
        notifyDataSetChanged();
    }

    public CustomListAdapter(Context _context, ArrayList<BaseItem> _names, int _viewId){
        context = _context;

        if(_names != null)
        {
            ArrayList<EditItem> newList = new ArrayList<>();
            for (BaseItem item : _names) {
                EditItem it = new EditItem();
                it.mItem = item;
                newList.add(it);
            }
            names = newList;
        }
        viewId = _viewId;

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
            view = infalInflater.inflate(viewId, null);
        }

        TextView newsGroupEdit = (TextView) view.findViewById(R.id.groupNameText);
        CheckBox groupCheck = (CheckBox) view.findViewById(R.id.groupCheckBox);

        EditItem tmp = filteredModelItemsArray.get(i);

        newsGroupEdit.setText(tmp.mItem.mName);
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
                ArrayList<EditItem> filteredItems = new ArrayList<>();

                for(int i = 0, l = names.size(); i < l; i++)
                {
                    String m = names.get(i).mItem.mName;
                    if(m.equals(constraint))
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
            filteredModelItemsArray.addAll((ArrayList<EditItem>) results.values);
            notifyDataSetChanged();
        }

    }
}

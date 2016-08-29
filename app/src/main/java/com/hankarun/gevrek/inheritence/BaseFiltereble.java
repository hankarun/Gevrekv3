package com.hankarun.gevrek.inheritence;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;

import com.hankarun.gevrek.R;
import com.hankarun.gevrek.model.NewsGroupEdit;

import java.util.ArrayList;

public class BaseFiltereble  extends RecyclerView.Adapter<EditBase> implements Filterable {
    Context context;
    public ArrayList<EditBase> listItems;
    private ArrayList<EditBase> filteredItems;
    CustomFilter filter;
    private EditText    filterText;
    private int layout;
    View v;


    public BaseFiltereble(Context _context, ArrayList<EditBase> items, EditText _filter, int _layout)
    {
        listItems = items;
        filteredItems = items;
        filterText = _filter;

        filterText.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                filter.filter(s.toString());
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
    }

    @Override
    public EditBase onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new EditBase(v);
    }

    @Override
    public void onBindViewHolder(EditBase holder, int position) {
        holder.onBind(filteredItems.get(position));
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
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
                ArrayList<EditBase> filteredItems = new ArrayList<>();

                for(int i = 0, l = listItems.size(); i < l; i++)
                {
                    String m = listItems.get(i).toString();
                    if(m.contains(constraint))
                        filteredItems.add(listItems.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = listItems;
                    result.count = listItems.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            filteredItems.clear();
            filteredItems.addAll((ArrayList<EditBase>) results.values);
            notifyDataSetChanged();
        }

    }
}

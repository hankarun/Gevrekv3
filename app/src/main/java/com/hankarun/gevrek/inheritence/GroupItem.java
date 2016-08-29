package com.hankarun.gevrek.inheritence;

import android.view.View;
import android.widget.TextView;

import com.hankarun.gevrek.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupItem extends EditBase{
    @BindView(R.id.recyclerItemText)
    TextView newsGroupName;

    public GroupItem(View itemview)
    {
        super(itemview);
        ButterKnife.bind(this,itemView);
    }

    @Override
    void onBind(final Object object) {
        newsGroupName.setText("This is a test");
    }
}

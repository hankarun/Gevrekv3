package com.hankarun.gevrek;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;

public class GroupMessagesActivity extends BaseAppcompat implements View.OnClickListener,GroupMessagesFragment.OnFabStateChanged, LoginDialog.onDialogFinished {
    @Override
    public void onFabStateChange(FABSTATE state) {
        changeFabState(state);
    }

    private FABSTATE fabstate = FABSTATE.NEWMESSAGE;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.groupMessagesFAB)
    FloatingActionButton mFab;

    private String title;
    private String url;

    private GroupMessagesFragment groupMessageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messages);

        setupToolbar();

        if(savedInstanceState != null)
        {
            title = savedInstanceState.getString("group");
            url = savedInstanceState.getString("url");
        }else{
            Intent intent = getIntent();
            title = intent.getStringExtra("group");
            url = intent.getStringExtra("url");
        }

        toolbarTitle.setText(Html.fromHtml(title));
        groupMessageFragment = GroupMessagesFragment.instance(url);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame1, groupMessageFragment).commit();

        if(!MainActivity.usernameAndPasswordSet(this))
            mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(this);

        if(!MainActivity.usernameAndPasswordSet(this)) {
            Snackbar.make(findViewById(R.id.coordinator),"You need to login.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Login in.", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new LoginDialog(GroupMessagesActivity.this, getCurrentTheme()).show();
                        }
                    }).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("group",title);
        outState.putString("url",url);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if(!groupMessageFragment.backPressed())
                    supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        groupMessageFragment.fabClicked(fabstate);
        switch (view.getId())
        {
            case R.id.groupMessagesFAB:
                switch (fabstate) {
                    case REPLY:
                    case NEWMESSAGE:
                        changeFabState(FABSTATE.POSTMESSAGE);
                        break;
                    case POSTMESSAGE:
                        changeFabState(FABSTATE.NEWMESSAGE);
                        Snackbar.make(findViewById(R.id.coordinator), R.string.message_posting, Snackbar.LENGTH_INDEFINITE)
                                .show();
                        break;
                }
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(!groupMessageFragment.backPressed())
                    return super.onKeyDown(keyCode, event);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void changeFabState(FABSTATE state)
    {
        fabstate = state;
        switch (fabstate)
        {
            case REPLY:
            case POSTMESSAGE:
                mFab.setImageResource(R.drawable.ic_reply_white_24dp);
                break;
            case NEWMESSAGE:
                mFab.setImageResource(android.R.drawable.ic_dialog_email);
                break;
        }
    }

    @Override
    public void onDialogReturn() {


    }
}

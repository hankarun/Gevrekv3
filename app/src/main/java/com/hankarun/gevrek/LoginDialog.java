package com.hankarun.gevrek;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.hankarun.gevrek.lib.CowAsyncLoader;

public class LoginDialog extends Dialog implements Button.OnClickListener {
    private Context context;
    private EditText usernameText;
    private EditText passwordText;

    onDialogFinished mCallback;

    public interface onDialogFinished {
        void onDialogReturn();
    }

    public LoginDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_NoActionBar);
        this.context = context;
        setContentView(R.layout.login_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        (findViewById(R.id.btn_login)).setOnClickListener(this);
        usernameText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mCallback = (onDialogFinished) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }

    @Override
    public void onClick(View view) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", usernameText.getText().toString());
        editor.putString("password", passwordText.getText().toString());
        editor.apply();
        CowAsyncLoader.cookieMap.clear();
        mCallback.onDialogReturn();
        dismiss();
    }
}

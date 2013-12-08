package com.jess.ticket.ui.activity;

import com.jess.ticket.R;

import android.os.Bundle;

public class ProtocolActivity extends BaseActivity {

    @Override
    protected boolean needWeibo() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
    }

}

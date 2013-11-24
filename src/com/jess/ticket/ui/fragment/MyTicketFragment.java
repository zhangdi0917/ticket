package com.jess.ticket.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jess.ticket.R;

public class MyTicketFragment extends BaseFragment {

    @Override
    public int getTitleResourceId() {
        return R.string.drawer_menu_my;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ticket, null);
        return view;
    }
    
}

package com.jess.ticket.app;

import com.jess.ticket.volley.VolleyManager;

import android.app.Application;

public class TicketApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        VolleyManager.init(this);
    }

}

package com.jess.ticket.sns;

public interface Constants {

    public static class Weibo {
        public static final String APP_KEY = "3428780541";

        public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

        public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
                + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                + "follow_app_official_microblog," + "invitation_write";
    }
    
    public static class Wechat {
        public static final String APP_ID = "wx41cf60ba41beac2a";

        public static final String APP_KEY = "0781758a34bc9a188a33efdda3ab4edf";
    }
}

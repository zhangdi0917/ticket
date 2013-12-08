package com.jess.ticket.sns;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.UsersAPI;

public class WeiboAccount {

    private Context mContext;

    private UsersAPI mUsersAPI;

    private RequestUserListener mRequestUserListener;

    private static final int WHAT_COMPLETE = 1;
    private static final int WHAT_ERROR = 2;

    public WeiboAccount(Context context, Oauth2AccessToken accessToken) {
        mContext = context.getApplicationContext();
        mUsersAPI = new UsersAPI(accessToken);
    }

    public static interface RequestUserListener {
        public void onComplete();

        public void onError();
    }

    public void getUserInfo(long uid, RequestUserListener listener) {
        mRequestUserListener = listener;

        mUsersAPI.show(uid, new RequestListener() {

            @Override
            public void onIOException(IOException e) {
                mHandler.sendEmptyMessage(WHAT_ERROR);
            }

            @Override
            public void onError(WeiboException e) {
                mHandler.sendEmptyMessage(WHAT_ERROR);
            }

            @Override
            public void onComplete4binary(ByteArrayOutputStream responseOS) {

            }

            @Override
            public void onComplete(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String screenName = jsonObject.getString("screen_name");
                    String avatar = jsonObject.getString("avatar_large");
                    if (!TextUtils.isEmpty(screenName)) {
                        WeiboKeeper.writeScreenName(mContext, screenName);
                    }
                    if (!TextUtils.isEmpty(avatar)) {
                        WeiboKeeper.writeAvatar(mContext, avatar);
                    }
                    mHandler.sendEmptyMessage(WHAT_COMPLETE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(WHAT_ERROR);
                }
            }
        });
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case WHAT_COMPLETE:
                if (mRequestUserListener != null)
                    mRequestUserListener.onComplete();
                break;
            case WHAT_ERROR:
                if (mRequestUserListener != null)
                    mRequestUserListener.onError();
                break;
            }
        }
    };

}

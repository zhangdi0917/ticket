package com.jess.ticket.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jess.ticket.R;
import com.jess.ticket.app.TicketApplication;
import com.jess.ticket.sns.WechatManager;
import com.jess.ticket.sns.WechatNotSupportException;
import com.jess.ticket.sns.WeiboKeeper;
import com.jess.ticket.ui.activity.MainActivity;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

public class ShareFragment extends BaseFragment {

    private static final String TAG = ShareFragment.class.getSimpleName();

    private View mShareSMSView;
    private View mShareWeiboView;
    private View mShareWechatView;
    private View mSharePyqView;

    private Oauth2AccessToken mAccessToken;

    @Override
    public int getTitleResourceId() {
        return R.string.drawer_menu_share;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, null);
        mShareSMSView = view.findViewById(R.id.share_sms_tv);
        mShareWeiboView = view.findViewById(R.id.share_weibo_tv);
        mShareWechatView = view.findViewById(R.id.share_wechat_tv);
        mSharePyqView = view.findViewById(R.id.share_pyq_tv);

        mShareSMSView.setOnClickListener(mOnClickListener);
        mShareWeiboView.setOnClickListener(mOnClickListener);
        mShareWechatView.setOnClickListener(mOnClickListener);
        mSharePyqView.setOnClickListener(mOnClickListener);

        return view;
    }

    OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
            case R.id.share_sms_tv:
                sendSMS("妈妈再也不用担心我的停车了");
                break;
            case R.id.share_weibo_tv:
                mAccessToken = WeiboKeeper.readAccessToken(getActivity());
                if (getActivity() != null) {
                    if (mAccessToken != null && mAccessToken.isSessionValid()) {
                        ((MainActivity) getActivity()).shareWeibo("妈妈再也不用担心我的停车了");
                    } else {
                        ((MainActivity) getActivity()).authWeibo(new AuthListener());
                    }
                }
                break;
            case R.id.share_wechat_tv:
                if (getActivity() != null) {
                    new WechatManager(getActivity()).sendToWechat("妈妈再也不用担心我的停车了");
                }
                break;
            case R.id.share_pyq_tv:
                if (getActivity() != null) {
                    try {
                        new WechatManager(getActivity()).sendToTimeline("妈妈再也不用担心我的停车了");
                    } catch (WechatNotSupportException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), R.string.wechat_not_support_timeline, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    };

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                WeiboKeeper.writeAccessToken(TicketApplication.getInstance(), mAccessToken);
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).shareWeibo("妈妈再也不用担心我的停车了");
                    ((MainActivity) getActivity()).getWeiboAccount(null);
                }
                Toast.makeText(TicketApplication.getInstance(), R.string.authrize_weibo_success, Toast.LENGTH_LONG)
                        .show();
            } else {
                String code = values.getString("code");
                Log.d(TAG, "authrize weibo failed, the code is " + code);
                Toast.makeText(TicketApplication.getInstance(), R.string.authrize_weibo_failed, Toast.LENGTH_LONG)
                        .show();
            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.d(TAG, "authrize weibo exception " + e.getMessage());
            Toast.makeText(TicketApplication.getInstance(), R.string.authrize_weibo_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void sendSMS(String text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}

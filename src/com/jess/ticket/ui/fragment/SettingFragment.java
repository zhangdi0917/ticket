package com.jess.ticket.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jess.ticket.R;
import com.jess.ticket.app.TicketApplication;
import com.jess.ticket.sns.WeiboAccount.RequestUserListener;
import com.jess.ticket.sns.WeiboKeeper;
import com.jess.ticket.ui.activity.MainActivity;
import com.jess.ticket.ui.activity.ProtocolActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.openapi.LogoutAPI;

public class SettingFragment extends BaseFragment {

    private static final String TAG = SettingFragment.class.getSimpleName();

    private View mWeiboLayout;
    private ImageView mWeiboAvatarIv;
    private TextView mWeiboTopTv;
    private TextView mWeiboBottomTv;

    private View mProtocolView;

    private AlertDialog mAlertDialog;

    private Oauth2AccessToken mAccessToken;

    private DisplayImageOptions mOptions;

    @Override
    public int getTitleResourceId() {
        return R.string.drawer_menu_setting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
                .showImageForEmptyUri(R.drawable.weibo_avatar_empty).considerExifParams(true).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);
        mWeiboLayout = view.findViewById(R.id.weibo_layout);
        mWeiboAvatarIv = (ImageView) view.findViewById(R.id.weibo_avatar);
        mWeiboTopTv = (TextView) view.findViewById(R.id.weibo_top_tv);
        mWeiboBottomTv = (TextView) view.findViewById(R.id.weibo_bottom_tv);
        mWeiboLayout.setOnClickListener(mOnClickListener);

        mProtocolView = (TextView) view.findViewById(R.id.protocol_tv);
        mProtocolView.setOnClickListener(mOnClickListener);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    private void updateView() {
        mAccessToken = WeiboKeeper.readAccessToken(getActivity());
        if (mAccessToken.isSessionValid()) {
            String screenName = WeiboKeeper.readScreenName(getActivity());
            String avatar = WeiboKeeper.readAvatar(getActivity());
            mWeiboTopTv.setText(R.string.setting_weibo_bind_top_label);
            mWeiboBottomTv.setText(screenName);
            ImageLoader.getInstance().displayImage(avatar, mWeiboAvatarIv, mOptions, null);
        } else {
            mWeiboTopTv.setText(R.string.setting_weibo_unbind_top_label);
            mWeiboBottomTv.setText(R.string.setting_weibo_unbind_bottom_label);
            ImageLoader.getInstance().displayImage(null, mWeiboAvatarIv, mOptions, null);
        }
    }

    private void dismissDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.weibo_layout:
                mAccessToken = WeiboKeeper.readAccessToken(getActivity());
                if (mAccessToken.isSessionValid()) {
                    dismissDialog();
                    mAlertDialog = new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.logout_weibo_dialog_msg)
                            .setPositiveButton(R.string.logout_weibo_dialog_positive,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new LogoutAPI(mAccessToken).logout(null);
                                            WeiboKeeper.clear(getActivity());
                                            updateView();
                                        }
                                    }).setNegativeButton(R.string.logout_weibo_dialog_negative, null).create();
                    mAlertDialog.show();
                } else {
                    ((MainActivity) getActivity()).authWeibo(mAuthListener);
                }
                break;
            case R.id.protocol_tv:
                Intent intent = new Intent(getActivity(), ProtocolActivity.class);
                startActivity(intent);
                break;
            }
        }
    };

    private WeiboAuthListener mAuthListener = new WeiboAuthListener() {

        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                WeiboKeeper.writeAccessToken(TicketApplication.getInstance(), mAccessToken);
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).getWeiboAccount(mRequestUserListener);
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
    };

    private RequestUserListener mRequestUserListener = new RequestUserListener() {

        @Override
        public void onError() {
            Log.d(TAG, "get weibo userinfo error");
        }

        @Override
        public void onComplete() {
            updateView();
        }
    };

}

package com.jess.ticket.ui.activity;

import com.jess.ticket.R;
import com.jess.ticket.sns.Constants;
import com.jess.ticket.sns.WeiboAccount;
import com.jess.ticket.sns.WeiboAccount.RequestUserListener;
import com.jess.ticket.sns.WeiboKeeper;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboShareException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {

    private WeiboAuth mWeiboAuth;
    private SsoHandler mSsoHandler;
    private static final int AUTH_WEIBO_REQUEST_CODE = 1234;
    private IWeiboShareAPI mWeiboShareAPI;

    protected abstract boolean needWeibo();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        if (needWeibo()) {
            mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.Weibo.APP_KEY);
            boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
            if (!isInstalledWeibo) {
                mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), R.string.cancel_download_weibo, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
            if (savedInstanceState != null) {
                mWeiboShareAPI.handleWeiboResponse(getIntent(), mWeiboResponse);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (needWeibo()) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), mWeiboResponse);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void authWeibo(WeiboAuthListener listener) {
        if (needWeibo()) {
            if (mWeiboAuth == null)
                mWeiboAuth = new WeiboAuth(this, Constants.Weibo.APP_KEY, Constants.Weibo.REDIRECT_URL,
                        Constants.Weibo.SCOPE);
            mSsoHandler = new SsoHandler(this, mWeiboAuth);
            mSsoHandler.authorize(AUTH_WEIBO_REQUEST_CODE, listener, getPackageName());
        }
    }

    public void getWeiboAccount(RequestUserListener listener) {
        if (needWeibo()) {
            Oauth2AccessToken accessToken = WeiboKeeper.readAccessToken(this);
            if (accessToken.isSessionValid()) {
                long uid = Long.parseLong(accessToken.getUid());
                new WeiboAccount(this, accessToken).getUserInfo(uid, listener);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (needWeibo()) {
            if (requestCode == AUTH_WEIBO_REQUEST_CODE && mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

    public void shareWeibo(String text) {
        if (!needWeibo()) {
            return;
        }
        try {
            if (mWeiboShareAPI.checkEnvironment(true)) {
                mWeiboShareAPI.registerApp();
                if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                    WeiboMessage weiboMessage = new WeiboMessage();
                    TextObject textObject = new TextObject();
                    textObject.text = text;
                    weiboMessage.mediaObject = textObject;
                    SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
                    request.transaction = String.valueOf(System.currentTimeMillis());
                    request.message = weiboMessage;
                    mWeiboShareAPI.sendRequest(request);
                } else {
                    Toast.makeText(this, R.string.weibo_not_support_api_hint, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (WeiboShareException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    IWeiboHandler.Response mWeiboResponse = new IWeiboHandler.Response() {

        @Override
        public void onResponse(BaseResponse arg0) {
            switch (arg0.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(getApplicationContext(), R.string.weibo_share_success, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(getApplicationContext(), R.string.weibo_share_canceled, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(getApplicationContext(),
                        getString(R.string.weibo_share_failed) + "Error Message: " + arg0.errMsg, Toast.LENGTH_LONG)
                        .show();
                break;
            }
        }
    };

}

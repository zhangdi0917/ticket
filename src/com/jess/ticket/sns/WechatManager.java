package com.jess.ticket.sns;

import android.content.Context;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class WechatManager {

    private IWXAPI mWxAPI;

    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    public WechatManager(Context context) {
        mWxAPI = WXAPIFactory.createWXAPI(context, Constants.Wechat.APP_ID, false);
    }

    public void init() {
        mWxAPI.registerApp(Constants.Wechat.APP_ID);
    }

    public void sendToWechat(String text) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        mWxAPI.sendReq(req);
    }

    public void sendToTimeline(String text) throws WechatNotSupportException {
        int wxSdkVersion = mWxAPI.getWXAppSupportAPI();
        if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
            WXTextObject textObj = new WXTextObject();
            textObj.text = text;
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = textObj;
            msg.description = text;
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
            mWxAPI.sendReq(req);
        } else {
            throw new WechatNotSupportException();
        }
    }

}

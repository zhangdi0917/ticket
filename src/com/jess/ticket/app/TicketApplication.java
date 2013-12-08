package com.jess.ticket.app;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class TicketApplication extends Application {

    private static TicketApplication mInstance;

    public BMapManager mBMapManager = null;

    public LocationClient mLocationClient = null;

    public static final String BMAP_KEY = "2O6yNGhDDC643Q5YnLOXZ9qB";

    public static TicketApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initBMapManager(this);
        initLocation(this);
        initImageLoader(this);
    }

    public void initBMapManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(BMAP_KEY, new MyGeneralListener())) {
            Toast.makeText(TicketApplication.getInstance().getApplicationContext(), "BMapManager初始化错误!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(TicketApplication.getInstance().getApplicationContext(), "您的网络出错啦！", Toast.LENGTH_LONG)
                        .show();
            } else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(TicketApplication.getInstance().getApplicationContext(), "输入正确的检索条件！", Toast.LENGTH_LONG)
                        .show();
            }
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError != 0) {
                Toast.makeText(TicketApplication.getInstance().getApplicationContext(), "key认证失败", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void initLocation(Context context) {
        mLocationClient = new LocationClient(context);
        mLocationClient.setAK(BMAP_KEY);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setAddrType("all");
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        option.disableCache(true);
        mLocationClient.setLocOption(option);
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }
}

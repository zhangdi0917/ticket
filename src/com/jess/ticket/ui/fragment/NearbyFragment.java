package com.jess.ticket.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.jess.ticket.R;
import com.jess.ticket.app.TicketApplication;

public class NearbyFragment extends BaseFragment {

    private MapView mMapView = null;
    private MapController mMapController = null;

    private LocationClient mLocClient = null;
    private BDLocationListener mLocListener = new MyLocationListener();

    private LocationData mLocData = null;
    private MyLocationOverlay mLocationOverlay = null;

    boolean mIsRequest = false;
    boolean mIsFirstLoc = true;

    @Override
    public int getTitleResourceId() {
        return R.string.drawer_menu_nearby;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mLocClient = TicketApplication.getInstance().mLocationClient;
        mLocClient.registerLocationListener(mLocListener);
        mLocClient.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, null);
        mMapView = (MapView) view.findViewById(R.id.map_view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBMap();
        initLocation();
        initLocationOverlay();
    }

    private void initBMap() {
        mMapController = mMapView.getController();
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6), (int) (116.404 * 1E6));
        mMapController.setCenter(point);
        mMapController.setZoom(12);
        mMapController.enableClick(true);
    }

    private void initLocation() {
        mIsFirstLoc = true;
        mLocData = new LocationData();
    }

    private void initLocationOverlay() {
        mLocationOverlay = new MyLocationOverlay(mMapView);
        mLocationOverlay.setData(mLocData);
        mMapView.getOverlays().add(mLocationOverlay);
        mMapView.refresh();
    }

    private void requestLocation() {
        if (mLocClient != null && mLocClient.isStarted()) {
            mIsRequest = true;
            mLocClient.requestLocation();
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;

            mLocData.latitude = location.getLatitude();
            mLocData.longitude = location.getLongitude();

            mLocationOverlay.setData(mLocData);
            mMapView.refresh();

            if (mIsFirstLoc || mIsRequest) {
                // 移动地图到定位点
                mMapController
                        .animateTo(new GeoPoint((int) (mLocData.latitude * 1e6), (int) (mLocData.longitude * 1e6)));
                mIsRequest = false;
                mIsFirstLoc = false;
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_nearby, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        mMapView.destroy();
        if (mLocClient != null) {
            mLocClient.stop();
            mLocClient = null;
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

}

package com.yjy.gdmap;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.*;
import com.amap.api.navi.*;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.TravelStrategy;
import com.amap.api.navi.model.*;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.yjy.gdmap.UI.PoiAroundSearchActivity;
import com.yjy.gdmap.app.BaseActivity;
import com.yjy.gdmap.overlay.MyPoiOverlay;
import com.yjy.gdmap.route.RideRouteActivity;
import com.yjy.gdmap.route.RouteActivity;
import com.yjy.gdmap.util.TimeUtil;
import com.yjy.gdmap.util.ToastUtil;

import javax.crypto.Mac;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.amap.api.maps.AMap.MAP_TYPE_BUS;
import static com.amap.api.maps.AMap.MAP_TYPE_NIGHT;
import static com.yjy.gdmap.overlay.MyPoiOverlay.markers;

public class MainActivity extends BaseActivity implements AMapLocationListener, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, PoiSearch.OnPoiSearchListener {
    //标点双击事件计时器
    long mLastTime = 0;
    long mCurTime = 0;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    private double mylatm, mylng;
    private UiSettings mUiSettings;
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private Marker detailMarker;
    private Marker mlastMarker;
    private boolean isPoi = false;
    private PoiSearch poiSearch;
    private MyPoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据
    private RelativeLayout mPoiDetail;
    private TextView search_edit;
    private Button search_btn;
    //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
//启动导航组件

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private MapView mMapView;
    private Marker marker;
    private String keyword;
    AMap aMap;
    private boolean isGo = false;
    private LatLonPoint lp;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = findViewById(R.id.map);
        search_edit = findViewById(R.id.edit_search);
        search_btn = findViewById(R.id.btn_search);
        mPoiDetail = findViewById(R.id.poi_detail);
           mainActivity = this;
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
            mUiSettings = aMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(false);
            mUiSettings.setScaleControlsEnabled(true);
            mUiSettings.setMyLocationButtonEnabled(true);
        }
//        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mlocationClient = new AMapLocationClient(this);
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        mLocationOption.setInterval(15000);
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(15000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
        aMap.setOnMarkerClickListener(this);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        search_btn.setOnClickListener(v -> {
            mylng=  mlocationClient.getLastKnownLocation().getLongitude();
            mylatm=  mlocationClient.getLastKnownLocation().getLatitude();
            sUtils.setString("mylat", mylatm + "");
            sUtils.setString("mylng", mylng + "");
            sUtils.setString("city",mlocationClient.getLastKnownLocation().getCity());
            startActivityForResult(new Intent(MainActivity.this, PoiAroundSearchActivity.class), 0);
        });
        search_edit.setOnClickListener(v -> {
            mylng=  mlocationClient.getLastKnownLocation().getLongitude();
            mylatm=  mlocationClient.getLastKnownLocation().getLatitude();
            sUtils.setString("mylat", mylatm + "");
            sUtils.setString("mylng", mylng + "");
            sUtils.setString("city",mlocationClient.getLastKnownLocation().getCity());
            startActivityForResult(new Intent(MainActivity.this, PoiAroundSearchActivity.class), 0);
        });

        aMap.setOnMapClickListener(latLng1 -> {
            if (!isPoi) {
                mylng=  mlocationClient.getLastKnownLocation().getLongitude();
                mylatm=  mlocationClient.getLastKnownLocation().getLatitude();
                sUtils.setString("mylat", mylatm + "");
                sUtils.setString("mylng", mylng + "");
                sUtils.setString("endlat", latLng1.latitude + "");
                sUtils.setString("endlng", latLng1.longitude + "");
                isGo = true;
                LatLng latLng = new LatLng(latLng1.latitude, latLng1.longitude);
                if (marker != null) {
                    marker.remove();
                }
                marker = aMap.addMarker(new MarkerOptions().position(latLng).title("目标点").snippet("位置：" + latLng1.latitude + "," + latLng1.longitude));
            }
        });
        if(Integer.parseInt(TimeUtil.getTime())>18&&Integer.parseInt(TimeUtil.getTime())<6){
            aMap.setMapType(MAP_TYPE_NIGHT);
        }

    }

    public static MainActivity getIn() {
        return mainActivity;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
        AmapNaviPage.getInstance().exitRouteActivity();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                Log.e("TAG", "onLocationChanged: " + amapLocation.getCity());
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                mylatm = amapLocation.getLatitude();//获取纬度
                mylng = amapLocation.getLongitude();//获取经度
                lp = new LatLonPoint(mylatm, mylng);

                sUtils.setString("city", amapLocation.getCity());
                sUtils.setString("mylat", mylatm + "");
                sUtils.setString("mylng", mylng + "");
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    public void car(View v) {

        LatLng mylatLng = new LatLng(mylatm, mylng);
        Poi poi = new Poi("当前位置", mylatLng, "");

        if (marker == null) {
           UIToast("请点击一个您想去的地方");
        } else {
            LatLng latLng = marker.getPosition();
            Poi endpoi = new Poi("目标位置", latLng, "");
            AmapNaviParams params = new AmapNaviParams(poi, null, endpoi, AmapNaviType.DRIVER);
            params.setUseInnerVoice(true);
            AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, naviInfoCallback);
        }


    }

    public void bike(View v) {

        LatLng mylatLng = new LatLng(mylatm, mylng);
        Poi poi = new Poi("当前位置", mylatLng, "");

        if (marker == null) {
            UIToast("请点击一个您想去的地方");
        } else {
            LatLng latLng = marker.getPosition();
            Poi endpoi = new Poi("目标位置", latLng, "");
            AmapNaviParams params = new AmapNaviParams(poi, null, endpoi, AmapNaviType.RIDE);
            params.setUseInnerVoice(true);
            AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, naviInfoCallback);
        }
    }

    public void foot(View v) {

        LatLng mylatLng = new LatLng(mylatm, mylng);
        Poi poi = new Poi("当前位置", mylatLng, "");

        if (marker == null) {
            UIToast("请点击一个您想去的地方");
        } else {
            LatLng latLng = marker.getPosition();
            Poi endpoi = new Poi("目标位置", latLng, "");
            AmapNaviParams params = new AmapNaviParams(poi, null, endpoi, AmapNaviType.WALK);
            params.setUseInnerVoice(true);
            AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, naviInfoCallback);
        }
    }

    public void UIToast(String s) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show());
    }

    INaviInfoCallback naviInfoCallback = new INaviInfoCallback() {
        @Override
        public void onInitNaviFailure() {
            UIToast("导航初始化失败");
        }

        @Override
        public void onGetNavigationText(String s) {

        }

        @Override
        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        }

        @Override
        public void onArriveDestination(boolean b) {

        }

        @Override
        public void onStartNavi(int i) {

        }

        @Override
        public void onCalculateRouteSuccess(int[] ints) {

        }

        @Override
        public void onCalculateRouteFailure(int i) {

        }

        @Override
        public void onStopSpeaking() {

        }

        @Override
        public void onReCalculateRoute(int i) {

        }

        @Override
        public void onExitPage(int i) {

        }

        @Override
        public void onStrategyChanged(int i) {

        }

        @Override
        public View getCustomNaviBottomView() {
            return null;
        }

        @Override
        public View getCustomNaviView() {
            return null;
        }

        @Override
        public void onArrivedWayPoint(int i) {

        }

        @Override
        public void onMapTypeChanged(int i) {

        }

        @Override
        public View getCustomMiddleView() {
            return null;
        }

        @Override
        public void onNaviDirectionChanged(int i) {

        }

        @Override
        public void onDayAndNightModeChanged(int i) {

        }

        @Override
        public void onBroadcastModeChanged(int i) {

        }

        @Override
        public void onScaleAutoChanged(boolean b) {

        }
    };

    //返回的图标点击事件
    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    public void tarNavi(LatLng latLng) {
        LatLng mylatLng = new LatLng(mylatm, mylng);
        Poi poi = new Poi("当前位置", mylatLng, "");
        Poi endpoi = new Poi("目标位置", latLng, "");
        AmapNaviParams params = new AmapNaviParams(poi, null, endpoi, AmapNaviType.DRIVER);
        params.setUseInnerVoice(true);
        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, naviInfoCallback);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            Log.e("TAG", "onPoiSearched: " + result.getQuery().getCity());
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //清除POI信息显示
                        whetherToShowDetailInfo(false);
                        //并还原点击marker样式
                        if (mlastMarker != null) {
                            resetlastmarker();
                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay != null) {
                            poiOverlay.removeFromMap();
                        }
                        aMap.clear();
                        poiOverlay = new MyPoiOverlay(aMap, poiItems, getApplicationContext());
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.decodeResource(
                                                getResources(), R.drawable.point4)))
                                .position(new LatLng(lp.getLatitude(), lp.getLongitude())));

                        aMap.addCircle(new CircleOptions()
                                .center(new LatLng(lp.getLatitude(),
                                        lp.getLongitude())).radius(5000)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb(50, 1, 1, 1))
                                .strokeWidth(2));
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(this.getApplicationContext(),
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil
                        .show(this.getApplicationContext(), R.string.no_result);
            }
        } else {
            ToastUtil
                    .showerror(this.getApplicationContext(), rcode);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    protected void doSearchQuery() {
        currentPage = 0;
        //TODO
        query = new PoiSearch.Query(keyword, "", "东营");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            Log.e("TAG", "doSearchQuery: ");
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    // 将之前被点击的marker置为原来的状态
    private void resetlastmarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        } else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
        }
        mlastMarker = null;

    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(this, infomation);

    }

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);

        } else {
            mPoiDetail.setVisibility(View.GONE);

        }
    }

    public void route(View view) {
        if (isGo) {
            startActivity(new Intent(MainActivity.this, RouteActivity.class));
        } else {
            Toast.makeText(getApplicationContext(), "请点击一个您想去的地方", Toast.LENGTH_SHORT).show();
        }
    }
    int  refrush =1;
    public void refrush(View view) {
        if(refrush==1){
            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 矢量地图模式
            refrush =2;
        }else if (refrush==2){
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
            refrush =3;
        }else if(refrush==3){
            aMap.setMapType(MAP_TYPE_BUS);
            refrush =4;
        }else if(refrush==4){
            aMap.setMapType(MAP_TYPE_NIGHT);
            refrush =1;
        }
    }
}

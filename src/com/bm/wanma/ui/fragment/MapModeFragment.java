package com.bm.wanma.ui.fragment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
 
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.bm.wanma.R;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.entity.AnchorAll;
import com.bm.wanma.entity.AnchorSummary;
import com.bm.wanma.entity.BespokeDetailBean;
import com.bm.wanma.entity.CityUpdateTimeBean;
import com.bm.wanma.entity.MapModeStationBean;
import com.bm.wanma.entity.MapModePileBean;
import com.bm.wanma.entity.MapModeBean;
import com.bm.wanma.entity.MyCollectBean;
import com.bm.wanma.entity.SelectValueBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.popup.AnchorPopupWindow;
import com.bm.wanma.popup.SelectValuePopupWindow;
import com.bm.wanma.socket.SocketConstant;
import com.bm.wanma.socket.StreamUtil;
import com.bm.wanma.socket.TCPSocketManager;
import com.bm.wanma.ui.activity.BespokeDetailActivity;
import com.bm.wanma.ui.activity.ITcpCallBack;
import com.bm.wanma.ui.activity.RealTimeChargeActivity;
import com.bm.wanma.utils.DatabaseHelper;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.TimeUtil;
import com.bm.wanma.utils.Tools;
import com.bm.wanma.widget.RequstDataClipLoading;

public class MapModeFragment extends BaseFragment implements LocationSource,AMapLocationListener
	,AMap.OnMarkerClickListener,OnMapClickListener ,OnClickListener,ITcpCallBack{
	 
	private AMap aMap;
	private MapView mapView;
	private LocationManagerProxy mAMapLocationManager;
	private OnLocationChangedListener mListener;
	private MarkerOptions markerOption;
	private String currentlat,currentlng;
	private Bundle locBundle;
	// 登录实体，记录是否登录
	private String pkUserinfo;
	// 获取Map模式下的实体
	private static ArrayList<MapModeBean> mSearchMapBean;
	private RelativeLayout loading;
	private RequstDataClipLoading ccl;
	//private NetWorkReceiver receiver = null;
	private BitmapDescriptor map_isppoint, map_not_isppoint,lastBitmapDescriptor;
	private BitmapDescriptor map_isppoint_big, map_not_isppoint_big;
	private FinalDb finalDb;
	private List<MapModePileBean> allMapStiltBean;
	private List<MapModeStationBean> allMapStationBean;
	private List<MapModeBean> allMapBean;
	private AnchorSummary anchorBean;
	private AnchorAll anchorAllBean;
	private String cityCode,pkBespoke;
	private boolean isFirstUpdate;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private boolean isSelected;
	private TextView home_select_btn;
	private FrameLayout bespoke_circle_btn,charge_circle_btn;
	private ImageButton home_location_btn;
//	private NetWorkReceiver receiver = null;
	private View mapModeFragment;
	private SelectValuePopupWindow selectWindow;//底部筛选框
	private IClickConfirm mcallback;
	private String markerLng,markerLat,markerName,markerAddr;
	private String markerElectricId,markerElectricType;
	private String pilenum,headnum;
	private TCPSocketManager mTcpSocketManager;
	private Marker currentMarker;
	private MyLocationStyle myLocationStyle;
	private BespokeDetailBean bespokeDetailBean;
	private long duringTime;
	
	//声明一个动画帧集合。
	private ArrayList<BitmapDescriptor> giflist;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finalDb = FinalDb.create(getActivity(),Protocol.DATABASE_NAME,false,Protocol.dbNumer,null);
		//finalDb = FinalDb.create(getActivity(),Protocol.DATABASE_NAME);
		/*dbHelper = DatabaseHelper.getInstance(getActivity(), Protocol.dbNumer);
		db = dbHelper.getWritableDatabase();*/
		duringTime = System.currentTimeMillis();
		registerBoradcastReceiver();
		
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/*// 延迟30s注册广播，防止访问两次接口
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// 动态方式注册广播接收者 
						receiver = new NetWorkReceiver();
						LocalBroadcastManager  broadcastManager = LocalBroadcastManager.getInstance(getActivity());
						IntentFilter filter = new IntentFilter();
						filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
						broadcastManager.registerReceiver(receiver, filter);
					}
				}, 30000);*/
		

		// 高德地图view
		mapModeFragment = inflater.inflate(R.layout.fragment_home_mapmode,
				container, false);
		mapView = (MapView) mapModeFragment.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 必须要写
		init();
		initDBValue();
		home_select_btn = (TextView) mapModeFragment
				.findViewById(R.id.home_select);
		home_select_btn.setOnClickListener(this);
		bespoke_circle_btn = (FrameLayout)mapModeFragment.findViewById(R.id.bespoke_circle);
		bespoke_circle_btn.setOnClickListener(this);
		charge_circle_btn = (FrameLayout)mapModeFragment.findViewById(R.id.charge_circle);
		charge_circle_btn.setOnClickListener(this);
		
		//home_shaixuan.getBackground().setAlpha(170);//设置透明度
		home_location_btn = (ImageButton) mapModeFragment
				.findViewById(R.id.home_location);
		home_location_btn.setOnClickListener(this);
		
		loading = (RelativeLayout) mapModeFragment
				.findViewById(R.id.mapmode_fragment_loading);
		ccl = (RequstDataClipLoading) loading
				.findViewById(R.id.customClipLoading);
		// 数据库地图点为空时，加载数据
		if (allMapBean.size() == 0 && isNetConnection()) {
			loading.setVisibility(View.VISIBLE);
			ccl.start();
			GetDataPost.getInstance(getActivity()).getElectricPileMapList(handler, null,null,
					null, null, null, null, null);
			isFirstUpdate = false;
		}
		if (allMapBean.size() > 0 && !isNetConnection()) {
			addMarkersToMap(allMapBean);

		} 
		if (allMapBean.size() > 0 ) {
			isFirstUpdate = true;

		} 

		return mapModeFragment;
	}
/**
 * 初始化值
 * 
 */
	private void initDBValue(){
		allMapBean = new ArrayList<MapModeBean>();
		
		synchronized (finalDb) {
			allMapStiltBean = finalDb.findAll(MapModePileBean.class);
		}
		synchronized (finalDb) {
			allMapStationBean = finalDb.findAll(MapModeStationBean.class);
		}
		allMapBean.clear();
		if (allMapStationBean.size() > 0) {
			allMapBean.addAll(allMapStationBean);
		}
		if (allMapStiltBean.size() > 0) {
			
			allMapBean.addAll(allMapStiltBean);
		}
		
		mcallback = new IClickConfirm() {
			@Override
			public void OnclickConfirm(SelectValueBean bean) {
				isSelected = true;
				String chargeMode = null;
				String powerInterface = null;
				String freeStatus = null;
				String matchMyCar = null;
				//筛选回调
				//chargeMode,powerInterface,freeStatus,matchMyCar
				//充电模式（5直流，14交流）
				if(bean.isFast() && !bean.isSlow()){
					chargeMode = "5";
				}else if(!bean.isFast() && bean.isSlow()){
					chargeMode = "14";
				}
				//接口方式（7国标，19美标，20欧标
				if(bean.isGuo() && !bean.isOu()){
					powerInterface = "7";
				}else if(!bean.isGuo() && bean.isOu()){
					powerInterface = "20";
				}
				//空闲充电点（智能、联网、有空闲枪头的桩）1选中
				if(bean.isIdle()){
					freeStatus = "1";
				}
				if(bean.isMatch()){
					matchMyCar = "1";
					loading.setVisibility(View.VISIBLE);
					ccl.start();
					pkUserinfo = PreferencesUtil.getStringPreferences(getActivity(),"pkUserinfo");
					GetDataPost.getInstance(getActivity()).getElectricPileMapList(handler, powerInterface,
							chargeMode, freeStatus, matchMyCar,pkUserinfo, null, null);
				}else {
					loading.setVisibility(View.VISIBLE);
					ccl.start();
					GetDataPost.getInstance(getActivity()).getElectricPileMapList(handler, powerInterface,
							chargeMode, freeStatus,null, null, null, null);
				}
			}

		};
	}
	/**
	 * 初始化
	 */
	private void init() {
		
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		//声明一个动画帧集合。
				giflist = new ArrayList<BitmapDescriptor>();
				//添加每一帧图片。
				//giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point1));
				giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point2)); 
				giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point3));
				giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point4)); 
				giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point5));
				giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point6)); 
				giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point5));
				giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point4)); 
				giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point3));
				
			    myLocationStyle = new MyLocationStyle();
				myLocationStyle.myLocationIcon(BitmapDescriptorFactory
						.fromResource(R.drawable.point6));// 设置小蓝点的图标
				myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
				myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
				aMap.setMyLocationStyle(myLocationStyle);
				
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.setOnMapClickListener(this); 
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		//aMap.getUiSettings().setZoomControlsEnabled(false);//设置默认缩放按钮是否显示
		aMap.getUiSettings().setRotateGesturesEnabled(false);//禁用手势旋转地图
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
		//aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
	}


	@SuppressLint("NewApi")
	@SuppressWarnings("static-access")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_select: //筛选按钮点击事件
			if(selectWindow == null){
				selectWindow = new SelectValuePopupWindow(getActivity());
				selectWindow.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						home_select_btn.setBackground(getResources().getDrawable(R.drawable.select_btn_circle));
					}
				});
			}
			home_select_btn.setBackground(getResources().getDrawable(R.drawable.select_btn_circle_orange));
			selectWindow.setCallBack(mcallback);
			selectWindow.showAtLocation(mapModeFragment.findViewById(R.id.mapmode_fragment), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			
			break;

		case R.id.home_location: //定位按钮点击事件
			String geoLat = PreferencesUtil.getStringPreferences(getActivity()
					.getApplicationContext(), "currentlat");
			String geoLng = PreferencesUtil.getStringPreferences(getActivity()
					.getApplicationContext(), "currentlng");
			if (!geoLat.equals("") && !geoLng.equals("")) {
				aMap.animateCamera( new CameraUpdateFactory().newLatLngZoom (new
						  LatLng(Double.parseDouble(geoLat), Double.parseDouble(geoLng)),
						  14));
				
			} else {
				showToast("正在定位，请稍等...");
				startLocation();
			}
			
			
			break;
		case R.id.bespoke_circle: //预约中点击事件
			//请求预约详情，跳转到详情界面
			if(isNetConnection()){
				showPD("正在请求数据...");
				GetDataPost.getInstance(getActivity()).getBespokeDetail(handler, pkBespoke);
			}
			
			break;
		case R.id.charge_circle://充电中点击事件
			showPD("正在获取充电信息...");
			mTcpSocketManager = TCPSocketManager.getInstance(getActivity());
			mTcpSocketManager.setTcpCallback(this);
			mTcpSocketManager.conn(pilenum, 
					Byte.parseByte(headnum));
			
			break;
			
		default:
			break;
		}
		
	}  

	//处理tcp报文
	@Override
	public void handleTcpPacket(final ByteArrayInputStream result) {
		//收到实时数据，进入实时数据界面
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				cancelPD();
			    try {
					StreamUtil.readByte(result);//int reason = 
					short cmdtype = StreamUtil.readShort(result);
					switch (cmdtype) {
					case SocketConstant.CMD_TYPE_REAL_DATA:
						int state = StreamUtil.readByte(result);
						short chargeTime = StreamUtil.readShort(result);
						StreamUtil.readShort(result);//short dianya = 
						StreamUtil.readShort(result);//short dianliu = 
						int diandu = StreamUtil.readInt(result);
						short feilv = StreamUtil.readShort(result);
						int yuchong = StreamUtil.readInt(result);
						int yichong = StreamUtil.readInt(result);
						int soc = StreamUtil.readByte(result);
						StreamUtil.readInt(result);//int fushu = 
						StreamUtil.readInt(result);//int gaojing = 
						Intent realIn = new Intent(getActivity(),
								RealTimeChargeActivity.class);
						realIn.putExtra("state", state);
						realIn.putExtra("chargeTime", chargeTime);
						realIn.putExtra("diandu", diandu);
						realIn.putExtra("feilv", feilv);
						realIn.putExtra("yuchong", yuchong);
						realIn.putExtra("yichong", yichong);
						realIn.putExtra("soc", soc);
						//mTcpSocketManager.close();
						startActivity(realIn);
						break;
					case SocketConstant.CMD_TYPE_CONNECT:
						int successflag = StreamUtil.readByte(result);
						short errorcode = StreamUtil.readShort(result);
						switch (successflag) {
						case 1:
							//连接成功
							int headState = StreamUtil.readByte(result);
							if(3 == headState || 0 == headState){
								 showToast("充电已结束");
								 PreferencesUtil.setPreferences(getActivity(),"chargepilenum","");
					        	 PreferencesUtil.setPreferences(getActivity(),"chargeheadnum","");
					        	 charge_circle_btn.setVisibility(View.GONE);
					        	 mTcpSocketManager.close();
							}
							
							break;
						case 0:
							showErrorCode(errorcode);
							mTcpSocketManager.close();
							break;
						default:
							break;
						}
						
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
		
				
			}
		});
				
	}

	@Override
	public void onStart() {
		super.onStart();
		
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
		startLocation();//重新启动定位
		//pkUserinfo = PreferencesUtil.getStringPreferences(getActivity().getApplicationContext(),"pkUserinfo");
		pkBespoke = PreferencesUtil.getStringPreferences(getActivity(),"bespokePK");
		pilenum = PreferencesUtil.getStringPreferences(getActivity(),"chargepilenum");
		headnum = PreferencesUtil.getStringPreferences(getActivity(),"chargeheadnum");
		if(!Tools.isEmptyString(pkBespoke)){
			 bespoke_circle_btn.setVisibility(View.VISIBLE);
		}else {
			bespoke_circle_btn.setVisibility(View.GONE);
		}
		if(!Tools.isEmptyString(pilenum) && !Tools.isEmptyString(headnum)){
			charge_circle_btn.setVisibility(View.VISIBLE);
		}else {
			charge_circle_btn.setVisibility(View.GONE);
		}
		//R.id.charge_circle
	}
	
	/**
	 * 在地图上添加marker
	 */
	private void addMarkersToMap(List<MapModeBean> mMapBean) {
		aMap.clear();
		/*String geoLat = PreferencesUtil.getStringPreferences(getActivity().getApplicationContext(), "currentlat");
		String geoLng = PreferencesUtil.getStringPreferences(getActivity().getApplicationContext(), "currentlng");
		if(!Tools.isEmptyString(geoLat) && !Tools.isEmptyString(geoLng)){
			aMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(geoLat),
					Double.parseDouble(geoLng))).icons(giflist).period(50));
		}*/
		aMap.setMyLocationStyle(myLocationStyle);
		if (!mMapBean.isEmpty() && mMapBean != null) { 
				/*map_stilt = BitmapDescriptorFactory
					.fromAsset("btn_map_stilt.png");*/
			map_isppoint = BitmapDescriptorFactory.fromResource(R.drawable.btn_map_charging_appointment);
			map_not_isppoint = BitmapDescriptorFactory.fromResource(R.drawable.btn_map_charging);
			map_isppoint_big = BitmapDescriptorFactory.fromResource(R.drawable.btn_map_charging_appointment_p);
			map_not_isppoint_big = BitmapDescriptorFactory.fromResource(R.drawable.btn_map_charging_p);
				for (MapModeBean bean : mMapBean) {
					String lat = null;
					String longi = null;
					String isppoint = null;
					String state = null;
					String isdel = null;
					if(bean != null){
						lat = bean.getLatitude();
						longi = bean.getLongitude();
						isppoint = bean.getIsAppoint();
						state = bean.getElectricState();//为15时，是上线
						isdel = bean.getDel();
					}  
					if(!Tools.isEmptyString(lat) && !Tools.isEmptyString(longi) && "15".equals(state) && "0".equals(isdel)){
						markerOption = new MarkerOptions();
						markerOption.position(new LatLng(Double.parseDouble(lat),
								Double.parseDouble(longi)));
						//动态替换marker图标  是否支持预约 0不支持，1支持
						if ("1".equals(isppoint)) {
							/*markerOption.icon(BitmapDescriptorFactory
									.fromAsset("btn_map_stilt.png"));*/
							markerOption.icon(map_isppoint);
						} else if ("0".equals(isppoint)) {
							//Bitmap btm = BitmapFactory.decodeResource(getResources(), R.drawable.btn_map_station);
							markerOption.icon(map_not_isppoint);
						} 
						Marker marker = aMap.addMarker(markerOption);
						marker.setObject(bean);// 这里可以存储数据信息
					}
				}
				/*//放在这，加载完地图锚点，再隐藏加载动画
				ccl.stop();
				loading.setVisibility(View.GONE);*/
		}
	}
	

	/**
	 * 保存全国电桩数据
	 */
	private void addAllToSave(ArrayList<MapModeBean> mMapBeanList){
		MapModePileBean stiltBean = new MapModePileBean();
		MapModeStationBean stationBean = new MapModeStationBean();
		for (MapModeBean bean : mMapBeanList) {
			//区分电桩 电站 分两个表存到数据库
			if("1".equals(bean.getElectricType())){//电桩
				if("1".equals(bean.getDel())){
					//0正常1删除，对于删除的数据应该从本地缓存中移除
						finalDb.deleteByWhere(MapModePileBean.class, "electricId ="+bean.getElectricId());
					//return;
				}else {
					//MapModePileBean stiltBean = new MapModePileBean();
					stiltBean.setDel(bean.getDel());
					stiltBean.setCityCode(bean.getCityCode());
					stiltBean.setElectricId(bean.getElectricId());
					stiltBean.setElectricState(bean.getElectricState());
					stiltBean.setElectricType(bean.getElectricType());
					stiltBean.setLatitude(bean.getLatitude());
					stiltBean.setLongitude(bean.getLongitude());
					stiltBean.setElectricAddress(bean.getElectricAddress());
					stiltBean.setElectricName(bean.getElectricName());
					stiltBean.setIsAppoint(bean.getIsAppoint());
					finalDb.save(stiltBean);
				}
				
			}else if("2".equals(bean.getElectricType())){//电站
				if("1".equals(bean.getDel())){
					//0正常1删除，对于删除的数据应该从本地缓存中移除
					finalDb.deleteByWhere(MapModeStationBean.class, "electricId ="+bean.getElectricId());
				}else {
					//MapModeStationBean stationBean = new MapModeStationBean();
					stationBean.setDel(bean.getDel());
					stationBean.setCityCode(bean.getCityCode());
					stationBean.setElectricId(bean.getElectricId());
					stationBean.setElectricState(bean.getElectricState());
					stationBean.setElectricType(bean.getElectricType());
					stationBean.setLatitude(bean.getLatitude());
					stationBean.setLongitude(bean.getLongitude());
					stationBean.setElectricAddress(bean.getElectricAddress());
					stationBean.setElectricName(bean.getElectricName());
					stationBean.setIsAppoint(bean.getIsAppoint());
					finalDb.save(stationBean);
				}
			}
		}
		allMapBean = mMapBeanList;
		
		//获取当前时间，转换时间戳为时间格式
		long cuttentTime = System.currentTimeMillis()/1000;
		String update = TimeUtil.getTime(String.valueOf(cuttentTime), "");
		//LogUtil.i("cm_time",time );
		//dbHelper = DatabaseHelper.getInstance(getActivity(),Protocol.dbNumer);
		//SQLiteDatabase db = dbHelper.getWritableDatabase();
		dbHelper = DatabaseHelper.getInstance(getActivity(), Protocol.dbNumer);
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from tb_m_city", null);
		CityUpdateTimeBean cityuptateBean = new CityUpdateTimeBean();
		while (cursor.moveToNext()) {
		   String code = cursor.getString(1); //获取第一列的值,第一列的索引从0开始---城市code
		  // String provincecode = cursor.getString(1);//获取第二列的值
		   //String provinceName = cursor.getString(2);
		  // CityUpdateTimeBean cityuptateBean = new CityUpdateTimeBean();
		   cityuptateBean.setCityCode(code);
		   cityuptateBean.setUpdateTime(update);
		   finalDb.save(cityuptateBean);
		}
		cursor.close();
		db.close();
	}
	/**
	 * 更新当前城市数据
	 */
	private void updateCurrentCity(ArrayList<MapModeBean> mMapBeanList){
	
		long cuttentTime = System.currentTimeMillis()/1000;
		String update = TimeUtil.getTime(String.valueOf(cuttentTime), "");
		MapModePileBean stiltBean = new MapModePileBean();
		MapModeStationBean stationBean = new MapModeStationBean();
		for (MapModeBean bean : mMapBeanList) {
			if("1".equals(bean.getElectricType())){//电桩
				if("1".equals(bean.getDel())){
					//0正常1删除，对于删除的数据应该从本地缓存中移除
					finalDb.deleteByWhere(MapModePileBean.class, "electricId ="+bean.getElectricId());
					return;
				}
				stiltBean.setDel(bean.getDel());
				stiltBean.setCityCode(bean.getCityCode());
				stiltBean.setElectricId(bean.getElectricId());
				stiltBean.setElectricState(bean.getElectricState());
				stiltBean.setElectricType(bean.getElectricType());
				stiltBean.setLatitude(bean.getLatitude());
				stiltBean.setLongitude(bean.getLongitude());
				finalDb.update(stiltBean);
			}else if("2".equals(bean.getElectricType())){//电站
				if("1".equals(bean.getDel())){
					//0正常1删除，对于删除的数据应该从本地缓存中移除
					finalDb.deleteByWhere(MapModeStationBean.class, "electricId ="+bean.getElectricId());
					return;
				}
				
				stationBean.setDel(bean.getDel());
				stationBean.setCityCode(bean.getCityCode());
				stationBean.setElectricId(bean.getElectricId());
				stationBean.setElectricState(bean.getElectricState());
				stationBean.setElectricType(bean.getElectricType());
				stationBean.setLatitude(bean.getLatitude());
				stationBean.setLongitude(bean.getLongitude());
				finalDb.update(stationBean);
			}
			
		}
		   CityUpdateTimeBean cityupdateBean = new CityUpdateTimeBean();
		   cityupdateBean.setCityCode(cityCode);
		   cityupdateBean.setUpdateTime(update);
		   finalDb.update(cityupdateBean);//更新数据库
		    allMapStiltBean = finalDb.findAll(MapModePileBean.class);
			allMapStationBean = finalDb.findAll(MapModeStationBean.class);
			allMapBean.clear();
			if(allMapStationBean.size()>0){
				allMapBean.addAll(allMapStationBean);
			}
			if(allMapStiltBean.size()>0){
				allMapBean.addAll(allMapStiltBean);
			}
		   //addMarkersToMap(allMapBean);
	}
	
	//点击非marker区域，将显示的InfoWindow隐藏  
	@Override
	public void onMapClick(LatLng arg0) {
		Log.i("cm_socket", "onMapClick");
		
	}
	
	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		//public boolean onMarkerClick(final Marker marker) {
		if (aMap != null) {
			//if(!marker.equals(currentMarker)){
				MapModeBean markerbean = (MapModeBean) marker.getObject();
				if(markerbean != null){
						if(isNetConnection()){
							//获取地图锚点 简介信息
							String geoLat = PreferencesUtil.getStringPreferences(getActivity().getApplicationContext(), "currentlat");
							String geoLng = PreferencesUtil.getStringPreferences(getActivity().getApplicationContext(), "currentlng");
							markerLat =  markerbean.getLatitude();
							markerLng = markerbean.getLongitude();
							markerName = markerbean.getElectricName();
							markerAddr = markerbean.getElectricAddress();
							markerElectricId = markerbean.getElectricId();
							markerElectricType = markerbean.getElectricType();
							showPD("正在加载数据，请稍等...");
							GetDataPost.getInstance(getActivity())
							.getAnchorSummary(handler,geoLng, geoLat, 
									markerElectricId,markerElectricType);
						}else {
							showToast("亲，网络不稳，请检查网络连接!");
						}
						currentMarker = marker;
						lastBitmapDescriptor = marker.getIcons().get(0);
							if ("1".equals(markerbean.getIsAppoint())) {
								marker.setIcon(map_isppoint_big);
							}else if ("0".equals(markerbean.getIsAppoint())) {
								marker.setIcon(map_not_isppoint_big);
							} 
						
				}
			//}
			
		}

		return false;//返回:true 表示点击marker后marker 不会移动到地图中心；返回false 表示点击marker后marker 会自动移动到地图中心
	}

	
	/**
	 * 方法必须重写
	 */
	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
		stopLocation();//停止定位

	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		/*if(receiver!=null){
	          getActivity().unregisterReceiver(receiver);
			}*/
		getActivity().unregisterReceiver(mBroadcastReceiver);
	}
	
	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 定位成功后回调函数
	 */
	@SuppressWarnings("static-access")
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				// 保存当前经纬度
				currentlat = String.valueOf(amapLocation.getLatitude());
				currentlng = String.valueOf(amapLocation.getLongitude());
				PreferencesUtil.setPreferences(getActivity()
						.getApplicationContext(), "currentlat", currentlat);
				PreferencesUtil.setPreferences(getActivity()
						.getApplicationContext(), "currentlng", currentlng);
				locBundle = amapLocation.getExtras();
				if (locBundle != null) {
					String desc = locBundle.getString("desc");
					// adcode=330106, citycode=0571, desc=浙江
					String adcode = locBundle.getString("adcode");
					if (adcode.isEmpty()) {
						showToast("请开启定位权限或检查网络连接");
						return;
					}
					adcode = adcode.substring(0, 4);
					StringBuilder sb = new StringBuilder();
					sb.append(adcode).append("00");
					cityCode = sb.toString();
					PreferencesUtil.setPreferences(getActivity(), "cityCode", cityCode);
					if (isFirstUpdate) {
						isFirstUpdate = false;
						aMap.animateCamera(CameraUpdateFactory
								.newCameraPosition(new CameraPosition(new LatLng(
										amapLocation.getLatitude(), amapLocation
							 					.getLongitude()), 18, 0, 30)),
								1000, null);
							
						List<CityUpdateTimeBean> listcityupdate = finalDb
								.findAllByWhere(CityUpdateTimeBean.class,
										" cityCode=\"" + cityCode + "\"");
						String updateT = listcityupdate.get(0).getUpdateTime();
						//更新当前城市点
						if(isNetConnection()){
							loading.setVisibility(View.VISIBLE);
							ccl.start();
							GetDataPost.getInstance(getActivity()).getElectricPileMapList(handler, null, null,
									null, null, null, cityCode,updateT);
						}
					}
					PreferencesUtil.setPreferences(getActivity()
							.getApplicationContext(), "currentcitycode", sb
							.toString());
					PreferencesUtil.setPreferences(getActivity()
							.getApplicationContext(), "currentaddres", desc);

				}
				

			} else {
				String geoLat = PreferencesUtil.getStringPreferences(getActivity()
						.getApplicationContext(), "currentlat");
				String geoLng = PreferencesUtil.getStringPreferences(getActivity()
						.getApplicationContext(), "currentlng");
				if (!Tools.isEmptyString(geoLat) && !Tools.isEmptyString(geoLng)) {
					aMap.animateCamera( new CameraUpdateFactory().newLatLngZoom (new
							  LatLng(Double.parseDouble(geoLat), Double.parseDouble(geoLng)),
							  16));
					if (allMapBean.size() > 0 ) {
						addMarkersToMap(allMapBean);
					}
				} else {
					showToast("正在定位，请稍等...");
					startLocation();
					Double initLat = 30.270695;
					Double initLng = 120.129251;
					PreferencesUtil.setPreferences(getActivity()
							.getApplicationContext(), "currentlat", String.valueOf(initLat));
					PreferencesUtil.setPreferences(getActivity()
							.getApplicationContext(), "currentlng", String.valueOf(initLng));
					aMap.animateCamera( new CameraUpdateFactory().newLatLngZoom (new
							  LatLng(initLat,initLng),
							  16));
					
				}
				//showToast("请开启定位权限");
			}
		}
		
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 60 * 1000 * 5, 10, this);
			mAMapLocationManager.setGpsEnable(true);
		}
		
	}
	
	/**
	 * 重新启动定位
	 * 
	 */
	private void startLocation(){
		if (mAMapLocationManager == null) {
			Log.i("cm_location","重新定位" );
			mAMapLocationManager = LocationManagerProxy
					.getInstance(getActivity().getApplicationContext());
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 60 * 1000 * 5 , 10, this);
			mAMapLocationManager.setGpsEnable(true);
		}
		
	}
	/**
	 * 暂停定位
	 */
	@SuppressWarnings("deprecation")
	private void stopLocation() {
	    if (mAMapLocationManager != null) {
	    	Log.i("cm_location","停止 定位" );
	    	mAMapLocationManager.removeUpdates(this);
	    	mAMapLocationManager.destory();
	    }
	    mAMapLocationManager = null;
	}
	
	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		//获取锚点信息
		if (sign.equals(Protocol.GET_ANCHOR_SUMMARY)) {
				anchorBean = (AnchorSummary) bundle.getSerializable(Protocol.DATA);
				anchorAllBean = new AnchorAll();
				if(anchorBean != null){
					if(!Tools.isEmptyString(markerName)){
						anchorAllBean.setName(markerName);
					}
					if(!Tools.isEmptyString(markerAddr)){
						anchorAllBean.setAddress(markerAddr);
					}
					if(!Tools.isEmptyString(markerLng)){
						anchorAllBean.setLng(markerLng);
					}
					if(!Tools.isEmptyString(markerLat)){
						anchorAllBean.setLat(markerLat);
					}
					if(!Tools.isEmptyString(markerElectricId)){
						anchorAllBean.setElectricId(markerElectricId);
					}
					if(!Tools.isEmptyString(markerElectricType)){
						anchorAllBean.setElectricType(markerElectricType);
					}
					if(!Tools.isEmptyString(anchorBean.getZlHeadNum())){
						anchorAllBean.setZlHeadNum(anchorBean.getZlHeadNum());
					}
					if(!Tools.isEmptyString(anchorBean.getZlFreeHeadNum())){
						anchorAllBean.setZlFreeHeadNum(anchorBean.getZlFreeHeadNum());
					}
					if(!Tools.isEmptyString(anchorBean.getJlHeadNum())){
						anchorAllBean.setJlHeadNum(anchorBean.getJlHeadNum());
					}
					if(!Tools.isEmptyString(anchorBean.getJlFreeHeadNum())){
						anchorAllBean.setJlFreeHeadNum(anchorBean.getJlFreeHeadNum());
					}
					if(!Tools.isEmptyString(anchorBean.getIsAppoint())){
						anchorAllBean.setIsAppoint(anchorBean.getIsAppoint());
					}
					if(!Tools.isEmptyString(anchorBean.getDistance())){
						anchorAllBean.setDistance(anchorBean.getDistance());
					}
					AnchorPopupWindow anchorPop = new AnchorPopupWindow(getActivity(),anchorAllBean);
					anchorPop.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss() {
							// TODO 处理marker变换事件
							//showToast("消失");
							currentMarker.setIcon(lastBitmapDescriptor);
						}
					});
					anchorPop.showAtLocation(mapModeFragment.findViewById(R.id.mapmode_fragment), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				}
			
		}else if(sign.equals(Protocol.GET_STATION_ELECTRIC_MAP)) {
			/*ccl.stop();
			loading.setVisibility(View.GONE);*/
			
			if(mSearchMapBean != null && !mSearchMapBean.isEmpty()){
				mSearchMapBean.clear();
			}
			mSearchMapBean = (ArrayList<MapModeBean>) bundle
					.getSerializable(Protocol.DATA);
			if(isSelected){
			//筛选后的数据，直接显示，不保存数据库
				addMarkersToMap(mSearchMapBean);
				ccl.stop();
				loading.setVisibility(View.GONE);
				return;
			}
			if(mSearchMapBean != null && mSearchMapBean.size()>0){
				if(allMapBean.size()==0 && mapView != null){
					//第一次加载全国数据--
						task.execute("allCity");
				}else if(allMapBean.size()>0 && mapView != null){
					//更新单个城市数据--
					task.execute("singleCity");
					
				}
			}else {
				ccl.stop();
				loading.setVisibility(View.GONE);
				addMarkersToMap(allMapBean);
				
			}
		}else if(sign.equals(Protocol.MYBESPOKE_DETAIL)){
			//预约详情实体类
			bespokeDetailBean = (BespokeDetailBean) bundle.getSerializable(Protocol.DATA);
			if(bespokeDetailBean != null){
				if(!"3".equals(bespokeDetailBean.getePHeElectricpileHeadState())){
					 bespoke_circle_btn.setVisibility(View.GONE);
		        	 PreferencesUtil.setPreferences(getActivity(),"bespokePK","");
				} 
					Intent bespokeDetailIn = new Intent();
					bespokeDetailIn.setClass(getActivity(), BespokeDetailActivity.class);
					bespokeDetailIn.putExtra("bespokeDetail", bespokeDetailBean);
					bespokeDetailIn.putExtra("mode", "bespoke");
					bespokeDetailIn.putExtra("downtime", "1");
					getActivity().startActivity(bespokeDetailIn);
				
			}else {
				 bespoke_circle_btn.setVisibility(View.GONE);
	        	 PreferencesUtil.setPreferences(getActivity(),"bespokePK","");
			}
			
		}else if(sign.equals(Protocol.GET_API_TOKEN)){//获取token
			String apiToken = (String) bundle.getSerializable(Protocol.DATA);
			PreferencesUtil.setPreferences(getActivity(), "apiToken",
					apiToken);
		}
	}
	
	@Override
	public void onFaile(String sign, Bundle bundle) {

		showToast(bundle.getString(Protocol.MSG));
		if (sign.equals(Protocol.GET_ANCHOR_SUMMARY)) {
			// 获取锚点信息
			currentMarker.setIcon(lastBitmapDescriptor);
		} else if (sign.equals(Protocol.GET_STATION_ELECTRIC_MAP)) {
			// 电站、电桩查找-地图
			ccl.stop();
			loading.setVisibility(View.GONE);
			addMarkersToMap(allMapBean);
		}
		
		if ("5000".equals(bundle.getString(Protocol.MSG))) {
			GetDataPost.getInstance(getActivity()).getApiToken(handler);
		}
		
		
	}
	
	
	AsyncTask<String, Integer, ArrayList<MapModeBean>> task = new AsyncTask<String, Integer, ArrayList<MapModeBean>>(){
		@Override
		protected ArrayList<MapModeBean> doInBackground(
				String... params) {
			LogUtil.i("cm_param", ""+params[0]);
			if(params[0].equals("allCity")){
				addAllToSave(mSearchMapBean);
			}else if(params[0].equals("singleCity")){
				updateCurrentCity(mSearchMapBean);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(
				ArrayList<MapModeBean> result) {
			super.onPostExecute(result);
			ccl.stop();
			loading.setVisibility(View.GONE);
			addMarkersToMap(allMapBean);
		}
		
	};
	
	//筛选界面 确认按钮回调接口
	public interface IClickConfirm{
		public void OnclickConfirm(SelectValueBean msg);
		
	}

	public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_UPDATAPOINT); 
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_SEARCH_POINT); 
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_Bespoke_Finish);
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_Bespoke_OK); 
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_Bespoke_OK_VISIBLE);
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_Bespoke_CANCLE);
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_Charge_CANCLE);
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_Charge_Ing);
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_Force_Offline);
        myIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //注册广播        
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }  
	//更新地图点
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){  
		@SuppressWarnings("static-access")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			 if(action.equals(BroadcastUtil.BROADCAST_UPDATAPOINT)){  
				    allMapStiltBean = finalDb.findAll(MapModePileBean.class);
					allMapStationBean = finalDb.findAll(MapModeStationBean.class);
					allMapBean.clear();
					if (allMapStationBean.size() > 0) {
						allMapBean.addAll(allMapStationBean);
					}
					if (allMapStiltBean.size() > 0) {
						allMapBean.addAll(allMapStiltBean);
					}
				   addMarkersToMap(allMapBean);
	         } else if(action.equals(BroadcastUtil.BROADCAST_SEARCH_POINT)){ 
	        	 //搜索点击
	        	/*String lat =  intent.getStringExtra("lat");
	        	String lng =  intent.getStringExtra("lng");*/
	        	 String searchMode = intent.getStringExtra("searchMode");
	        	 if("auto".equals(searchMode)){
	        		 MapModeBean  myAutoBean =  (MapModeBean) intent.getSerializableExtra("allKeywordMapBean");
	        		 aMap.animateCamera(new CameraUpdateFactory().newLatLngZoom(
	    						new LatLng(Double.parseDouble(myAutoBean.getLatitude()),
	    								Double.parseDouble(myAutoBean.getLongitude())), 14));
	        	
	        		 
	        	 }else if("collect".equals(searchMode)){
	        			MyCollectBean myCollectBean = (MyCollectBean) intent.getSerializableExtra("searchbean");
	    				aMap.animateCamera(new CameraUpdateFactory().newLatLngZoom(
	    						new LatLng(Double.parseDouble(myCollectBean.getLat()),
	    								Double.parseDouble(myCollectBean.getLng())), 14));
	        	 }
	        	 
	        	 
	         }else if(action.equals(BroadcastUtil.BROADCAST_Bespoke_Finish)){
	        	 //预约结束
	        	 bespoke_circle_btn.setVisibility(View.GONE);
	        	 PreferencesUtil.setPreferences(getActivity(),"bespokePK","");
	         }else if(action.equals(BroadcastUtil.BROADCAST_Bespoke_CANCLE)){
	        	 //预约结束
	        	 bespoke_circle_btn.setVisibility(View.GONE);
	        	 PreferencesUtil.setPreferences(getActivity(),"bespokePK","");
	         } else if(action.equals(BroadcastUtil.BROADCAST_Bespoke_OK_VISIBLE)){
	        	 //预约成功
	        	 pkBespoke = intent.getStringExtra("bespokePK");
	        	 PreferencesUtil.setPreferences(getActivity(),"bespokePK",pkBespoke);
	        	 bespoke_circle_btn.setVisibility(View.VISIBLE);
	         }  else if(action.equals(BroadcastUtil.BROADCAST_Charge_CANCLE)){
	        	 //充电结束
	        	 PreferencesUtil.setPreferences(getActivity(),"chargepilenum","");
	        	 PreferencesUtil.setPreferences(getActivity(),"chargeheadnum","");
	        	 charge_circle_btn.setVisibility(View.GONE);
	        	 
	         } else if(action.equals(BroadcastUtil.BROADCAST_Charge_Ing)){
	        	 //充电进行中
	        	 pilenum = intent.getStringExtra("chargepilenum");
	        	 headnum = intent.getStringExtra("chargeheadnum");
	        	 PreferencesUtil.setPreferences(getActivity(),"chargepilenum",pilenum);
	        	 PreferencesUtil.setPreferences(getActivity(),"chargeheadnum",headnum);
	        	 charge_circle_btn.setVisibility(View.VISIBLE);
	        	 
	         } else if(action.equals(BroadcastUtil.BROADCAST_Force_Offline)){
	        	 //强制下线
	        	 bespoke_circle_btn.setVisibility(View.GONE);
	        	 charge_circle_btn.setVisibility(View.GONE);
	        	 PreferencesUtil.setPreferences(getActivity(),"bespokePK","");
	        	 PreferencesUtil.setPreferences(getActivity(),"chargepilenum","");
	        	 PreferencesUtil.setPreferences(getActivity(),"chargeheadnum","");
	         } else if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
	        	 //网络变换
	        	 LogUtil.i("cm_network","网络变化");
	        	 boolean dur = System.currentTimeMillis()-duringTime > 1000 * 60 ;
	        	 if(allMapBean.size() == 0 && isNetConnection() && dur){
 					GetDataPost.getInstance(context).getElectricPileMapList(handler, null, 
 							null, null,null, null, null, null);
     		}
	        	 
	         }
		}  
          
    };

    public void showErrorCode(int error){
		switch (error) {
		case 6000:
			showToast("电桩通讯未连接");
			break;
		case 6001:
			showToast("电桩未响应,超时");
			break;
		case 6100:
			showToast("电桩编码无效");
			break;
		case 6101:
			showToast("电桩枪口编码无效");
			break;
		case 6104:
			showToast("电桩正在升级，不能使用");
			break;
		case 6105:
			showToast("报文错误");
			break;
		case 6200:
			showToast("充电枪被停用,不能使用");
			break;
		case 6300:
			showToast("桩已经被别人使用");
			break;
		case 6301:
			showToast("桩在操作中(设置)，不能预约或充电");
			break;
		case 6401:
			showToast("用户长度无效");
			break;
		case 6402:
			showToast("用户密码错误");
			break;
		case 6403:
			showToast("校验失败，请重新登录");
			break;
		case 6404:
			showToast("用户不存在或者存在多个");
			break;
		case 6405:
			showToast("用户状态无效");
			break;
		case 6406:
			showToast("在使用其他桩");
			break;
		case 6601:
			showToast("已经有其他人预约");
			break;	
		case 6700:
			showToast("充电方式错误");
			break;
		case 6701:
			showToast("有未支付订单,不能充电");
			break;
		case 1002:
			showToast("用户金额不足,不能充电");
			break;
		case 6702:
			showToast("充电枪没插好,不能充电");
			break;
		case 6703:
			showToast("充电枪盖没盖好,不能充电");
			break;
		case 6704:
			showToast("车与桩未建立通讯");
			break;
		case 6705:
			showToast("故障，不能充电");
			break; 
		case 6706:
			showToast("枪盖已经打开,不能重复打开");
			break; 
		case 6800:
			showToast("已经在充电,不能重复充电");
			break;
		case 6801:
			showToast("其他人已经在充电,不能重复充电");
			break;
		case 6802:
			showToast("充电枪被预约,不能充电");
			break;
		case 6803:
			showToast("没有充电,不能停止充电");
			break;
		case 6804:
			showToast("充电桩故障,不能预约或充电");
			break;
		default:
			showToast("未知原因");
			break;
		}
		
	}









	}

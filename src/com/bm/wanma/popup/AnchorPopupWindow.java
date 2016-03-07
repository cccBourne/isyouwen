package com.bm.wanma.popup;

import java.util.ArrayList;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.bm.wanma.R;
import com.bm.wanma.entity.AnchorAll;
import com.bm.wanma.entity.ElectricPileBean;
import com.bm.wanma.entity.PowerStationBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.ui.activity.LoginAndRegisterActivity;
import com.bm.wanma.ui.activity.StationStiltDetailActivity;
import com.bm.wanma.ui.navigation.NaviCustomActivity;
import com.bm.wanma.ui.navigation.TTSController;
import com.bm.wanma.ui.navigation.Utils;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author cm
 *点击图标，底层弹出详情框
 */
public class AnchorPopupWindow extends BasePopupWindow implements OnClickListener,AMapNaviListener{
	private Context mContext;
	private View mMenuView;
	private AnchorAll anchorBean;
	private TextView tv_name,tv_distance,tv_address;
	private TextView tv_fast_num,tv_fast_idle,tv_slow_num,tv_slow_idle;
	private LinearLayout ll_navgation,ll_bespoke;
	private TextView tv_bespoke;
	private ImageView iv_bespoke;
	private RelativeLayout rl_address;
	private String name,distance,address,fastNum,fastIdleNum,slowNum,slowIdleNum;
	private String isAppoint;
	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
	private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态
	private Double startGeoLat,startGeoLng;
	private String markerLat,markerLng;
	private Double endEdoLat,endEdoLng;
	private String pkUserId,electricType,electricId;
	private ArrayList<PowerStationBean> stationBeanList;
	private PowerStationBean stationBean;
	private ArrayList<ElectricPileBean> pileBeanList;
	private ElectricPileBean pileBean;
	public AnchorPopupWindow(Context context,AnchorAll bean) {
		super(context);
		this.mContext = context;
		this.anchorBean = bean;
		LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popup_anchor_summary, null);
        initView(mMenuView);
        
        this.setContentView(mMenuView);
        this.setWidth(LayoutParams.FILL_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimBottom);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
      //设置popwindow如果点击外面区域，便关闭。
       // this.setOutsideTouchable(true);
       // this.setBackgroundDrawable(new BitmapDrawable());
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
        	@Override
            public boolean onTouch(View v, MotionEvent event) {
                 
                int height = mMenuView.findViewById(R.id.pop_detail_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss(); 
                    }
                }               
                return true;
            }
        });
        
	}
	private void initView (View view){
		tv_name = (TextView)view.findViewById(R.id.pop_detail_name);
		tv_distance = (TextView)view.findViewById(R.id.pop_detail_distance);
		tv_address = (TextView)view.findViewById(R.id.pop_detail_addr);
		tv_fast_num = (TextView)view.findViewById(R.id.pop_fast_num);
		tv_fast_idle = (TextView)view.findViewById(R.id.pop_fast_idle);
		tv_slow_num = (TextView)view.findViewById(R.id.pop_slow_num);
		tv_slow_idle = (TextView)view.findViewById(R.id.pop_slow_idle);
		rl_address = (RelativeLayout)view.findViewById(R.id.pop_detail_rl_addr);
		rl_address.setOnClickListener(this);
		ll_navgation = (LinearLayout)view.findViewById(R.id.pop_detail_navgation);
		ll_navgation.setOnClickListener(this);
		ll_bespoke = (LinearLayout)view.findViewById(R.id.pop_detail_bespoke);
		tv_bespoke = (TextView)view.findViewById(R.id.pop_detail_bespoke_tv);
		iv_bespoke = (ImageView)view.findViewById(R.id.pop_detail_bespoke_iv);
		
		name = anchorBean.getName();
		electricId = anchorBean.getElectricId();
		electricType = anchorBean.getElectricType();
		distance = anchorBean.getDistance();
		fastNum = anchorBean.getZlHeadNum();
		fastIdleNum = anchorBean.getZlFreeHeadNum();
		slowNum = anchorBean.getJlHeadNum();
		slowIdleNum = anchorBean.getJlFreeHeadNum();
		address = anchorBean.getAddress();
		isAppoint = anchorBean.getIsAppoint();
		if(!Tools.isEmptyString(name)){
			tv_name.setText(name);
		}
		if(!Tools.isEmptyString(distance)){
			tv_distance.setText(Tools.getMeterOrKM(distance));
		}
		if(!Tools.isEmptyString(address)){
			tv_address.setText(address);
		}
		if(!Tools.isEmptyString(fastNum)){
			tv_fast_num.setText(fastNum);
		}
		if(!Tools.isEmptyString(fastIdleNum)){
			tv_fast_idle.setText(fastIdleNum);
		}
		if(!Tools.isEmptyString(slowNum)){
			tv_slow_num.setText(slowNum);
		}
		if(!Tools.isEmptyString(slowIdleNum)){
			tv_slow_idle.setText(slowIdleNum);
		}
		//1支持预约 0不支持
		if ("1".equals(isAppoint)) {
			ll_bespoke.setOnClickListener(this);
			tv_bespoke.setTextColor(mContext.getResources().getColor(
					R.color.common_orange));
			iv_bespoke.setImageResource(R.drawable.pop_anchor_bespoke);
		}
		
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pop_detail_rl_addr:
			goToDetail();
			
			break;
		case R.id.pop_detail_navgation:
			if(isNetConnection()){
				
				initNavigation();
				// 启动FPS导航
				if (AMapNavi.getInstance(mContext) != null) {
					AMapNavi.getInstance(mContext)
							.calculateDriveRoute(mStartPoints,
									mEndPoints, null,
									AMapNavi.DrivingDefault);
					mRouteCalculatorProgressDialog.show();
				}
			}else {
				showToast("亲，网络不稳，请检查网络连接!");
			}
			break;
		case R.id.pop_detail_bespoke:
			goToDetail();
			break;
		default:
			break;
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		//电站详情
		if (sign.equals(Protocol.POWER_STATION_DETAIL)) {
			stationBeanList = (ArrayList<PowerStationBean>) bundle.getSerializable(Protocol.DATA);
			if(stationBeanList != null){
				stationBean = stationBeanList.get(0);
				Intent detailIn = new Intent();
				detailIn.setClass(mContext, StationStiltDetailActivity.class);
				detailIn.putExtra("stationBean", stationBean);
				detailIn.putExtra("type", "2");
				detailIn.putExtra("electricId", electricId);
				mContext.startActivity(detailIn);
				dismiss();
			}
		}else if (sign.equals(Protocol.POWER_Pile_DETAIL)) {
			//电桩详情
			pileBeanList = (ArrayList<ElectricPileBean>) bundle.getSerializable(Protocol.DATA);
			if(pileBeanList != null){
				pileBean = pileBeanList.get(0);
				Intent detailIn = new Intent();
				detailIn.setClass(mContext, StationStiltDetailActivity.class);
				detailIn.putExtra("pileBean", pileBean);
				detailIn.putExtra("type", "1");
				detailIn.putExtra("electricId", electricId);
				mContext.startActivity(detailIn);
				dismiss();
			}
		}
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		showToast(bundle.getString(Protocol.MSG));

	}
	/**
	 * 进入详情界面
	 */
	private void goToDetail(){
		pkUserId = PreferencesUtil.getStringPreferences(mContext,"pkUserinfo");
		String geoLat = PreferencesUtil.getStringPreferences(mContext,
				"currentlat");
		String geoLng = PreferencesUtil.getStringPreferences(mContext,
				"currentlng");
		if(!Tools.isEmptyString(pkUserId)){
			//先去请求电桩 电站信息，如果有数据，在onsuccess里跳转到详情界面 
			if(isNetConnection()){
				//1是桩，2是站
				if("1".equals(electricType)){
					showPD("正在加载数据...");
					GetDataPost.getInstance(mContext).getPileDetail(handler,
							electricId, pkUserId, geoLng, geoLat);
				}else if("2".equals(electricType)){
					showPD("正在加载数据...");
					GetDataPost.getInstance(mContext).getStationDetail(handler,
							electricId, pkUserId, geoLng, geoLat);
				}
			
			}else {
				showToast("网络不稳，请稍后再试");
			}
			
		}else {
			Intent loginIn = new Intent();
			loginIn.setClass(mContext, LoginAndRegisterActivity.class);
			mContext.startActivity(loginIn);
			dismiss();
		}
		
		
		
	}
	// 初始化导航
	private void initNavigation() {
		TTSController.getInstance(mContext).startSpeaking();
		// 获取当前经纬度
		String geoLat = PreferencesUtil.getStringPreferences(mContext,
				"currentlat");
		String geoLng = PreferencesUtil.getStringPreferences(mContext,
				"currentlng");
		startGeoLat = Double.parseDouble(geoLat);
		startGeoLng = Double.parseDouble(geoLng);
		// 获取目的地经纬度
		markerLat = anchorBean.getLat();
		markerLng = anchorBean.getLng();
		if (!Tools.isEmptyString(markerLng) && !Tools.isEmptyString(markerLat)) {
			endEdoLat = Double.parseDouble(markerLat.trim());
			endEdoLng = Double.parseDouble(markerLng.trim());
			NaviLatLng mNaviStart = new NaviLatLng(startGeoLat, startGeoLng);
			NaviLatLng mNaviEnd = new NaviLatLng(endEdoLat, endEdoLng);
			mStartPoints.clear();
			mEndPoints.clear();
			mStartPoints.add(mNaviStart);
			mEndPoints.add(mNaviEnd);
		}

		mRouteCalculatorProgressDialog = new ProgressDialog(mContext);
		mRouteCalculatorProgressDialog.setCancelable(true);
		AMapNavi aMapNavi = AMapNavi.getInstance(mContext);
		if (this instanceof AMapNaviListener && aMapNavi != null) {
			aMapNavi.setAMapNaviListener(this);
		}

	}
	


	@Override
	public void onArriveDestination() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onCalculateRouteFailure(int arg0) {
		mRouteCalculatorProgressDialog.dismiss();
		
	}
	@Override
	public void onCalculateRouteSuccess() {
		mRouteCalculatorProgressDialog.dismiss();
		Intent intent = new Intent(mContext, NaviCustomActivity.class);
		//Intent intent = new Intent(StationDetailActivity.this, SimpleNaviActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		Bundle bundle = new Bundle();
		bundle.putInt(Utils.ACTIVITYINDEX, Utils.ANCHORSUMMARY);
		intent.putExtras(bundle);
		mContext.startActivity(intent);
		
	}
	@Override
	public void onEndEmulatorNavi() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onInitNaviFailure() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onInitNaviSuccess() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@Deprecated
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onReCalculateRouteForYaw() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub
		
	}


}

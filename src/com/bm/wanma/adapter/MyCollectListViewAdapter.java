package com.bm.wanma.adapter;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.bm.wanma.R;
import com.bm.wanma.entity.ElectricPileBean;
import com.bm.wanma.entity.MyCollectBean;
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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyCollectListViewAdapter extends GetDataPostAdapter implements
		AMapNaviListener, AMapNaviViewListener {

	// 实体类
	private List<MyCollectBean> mdata;
	private Context mContext;
	private LayoutInflater inflater;
	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
	private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态
	private String geoLat,geoLng,edoLat,edoLng;
	private Double startGeoLat,startGeoLng,endEdoLat,endEdoLng;
	private String pkUserinfo;
	private String electricType,electricId;
	private ArrayList<PowerStationBean> stationBeanList;
	private PowerStationBean stationBean;
	private ArrayList<ElectricPileBean> pileBeanList;
	private ElectricPileBean pileBean;
	
	
	public MyCollectListViewAdapter(Context context,
			List<MyCollectBean> data) {
		super(context);
		this.mdata = data;
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		pkUserinfo = PreferencesUtil.getStringPreferences(mContext,"pkUserinfo");
		// init();
	}

	private void init(MyCollectBean navbean) {
		TTSController.getInstance(mContext).startSpeaking();
		//获取当前经纬度
		geoLat = PreferencesUtil.getStringPreferences(mContext, "currentlat");
		geoLng = PreferencesUtil.getStringPreferences(mContext, "currentlng");
		startGeoLat = Double.parseDouble(geoLat);
		startGeoLng = Double.parseDouble(geoLng);
		// 获取目的地经纬度
		edoLat = navbean.getLat();
		edoLng = navbean.getLng();
		if (!Tools.isEmptyString(edoLat) && !Tools.isEmptyString(edoLng)) {
			endEdoLat = Double.parseDouble(edoLat.trim());
			endEdoLng = Double.parseDouble(edoLng.trim());
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
	public int getCount() {
		return mdata.size();
	}

	@Override
	public Object getItem(int position) {
		return mdata.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View conventview, ViewGroup arg2) {
		TextView tv_name = null;
		TextView tv_distance = null;
		TextView tv_address = null;
		TextView tv_fastNum = null;
		TextView tv_slowNum = null;
		TextView tv_bespoke = null;
		LinearLayout ll_navgation= null;
		LinearLayout ll_bespoke = null;
	    ImageView iv_bespoke = null;
	    RelativeLayout rl_listview_addr = null;

		if (conventview == null) {
			conventview = inflater.inflate(R.layout.listview_item_mycollect,
					null);
			tv_name = (TextView) conventview
					.findViewById(R.id.listview_name);
			tv_distance = (TextView) conventview
					.findViewById(R.id.listview_distance);
			tv_address = (TextView) conventview
					.findViewById(R.id.listview_addr);
			tv_fastNum = (TextView) conventview
					.findViewById(R.id.listview_fast_num);
			tv_slowNum = (TextView) conventview
					.findViewById(R.id.listview_slow_num);
			tv_bespoke = (TextView) conventview
					.findViewById(R.id.listview_tv_bespoke);
			iv_bespoke = (ImageView)conventview.findViewById(R.id.listview_iv_bespoke);
			ll_navgation = (LinearLayout)conventview.findViewById(R.id.listview_navgation);
			ll_bespoke = (LinearLayout)conventview.findViewById(R.id.listview_bespoke);
			rl_listview_addr = (RelativeLayout)conventview.findViewById(R.id.listview_rl_addr);
			
			// 保存conventview对象到ObjectClass类中
			conventview.setTag(new ObjectClass(tv_name,
					tv_distance, tv_address,
					tv_fastNum, 
					tv_slowNum,tv_bespoke,
					ll_navgation,ll_bespoke,iv_bespoke,rl_listview_addr
					));

		} else {
			// 得到保存的对象
			ObjectClass objectclass = (ObjectClass) conventview.getTag();
			tv_name = objectclass.obj_tv_name;
			tv_distance = objectclass.obj_tv_distance;
			tv_address = objectclass.obj_tv_address;
			tv_fastNum = objectclass.obj_tv_fastNum;
			tv_slowNum = objectclass.obj_tv_slowNum;
			tv_bespoke = objectclass.obj_tv_bespoke;
			ll_navgation = objectclass.obj_ll_navgation;
			ll_bespoke = objectclass.obj_ll_bespoke;
			iv_bespoke = objectclass.obj_iv_bespoke;
			rl_listview_addr = objectclass.obj_rl_addr;
		}
		
		MyCollectBean listBean = (MyCollectBean) mdata
				.get(position);
		if(listBean != null){
				tv_name.setText(listBean.getNAME()+"");
				tv_address.setText(listBean.getAddr()+"");
			String distance = listBean.getDistance();
			if(!Tools.isEmptyString(distance)){
				distance = Tools.getMeterOrKM(distance);
				tv_distance.setText(distance);
			}else {
				tv_distance.setText("未知");
			}
				tv_fastNum.setText(listBean.getZlHeadNum()+"");
				tv_slowNum.setText(listBean.getJlHeadNum()+"");
			
			//1支持预约 0不支持
			ll_bespoke.setTag(position);
			if("1".equals(listBean.getPoSt_IsAppoint())){
					tv_bespoke.setTextColor(mContext.getResources().getColor(
							R.color.common_orange));
					iv_bespoke.setImageResource(R.drawable.pop_anchor_bespoke);
					//点击预约进入详情界面
					ll_bespoke.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							int pos = Integer.parseInt(v.getTag().toString());
							MyCollectBean bespokelistBean = mdata.get(pos);	
							if(bespokelistBean != null){
								goToDetail(bespokelistBean);
							}
						}
					});
			}
	
			// 添加导航图标点击事件
			ll_navgation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyCollectBean listBean = (MyCollectBean) mdata.get(position);
					// 先初始化导航数据
					init(listBean);
					// 启动FPS导航
					if (AMapNavi.getInstance(mContext) != null) {
						AMapNavi.getInstance(mContext).calculateDriveRoute(
								mStartPoints, mEndPoints, null,
								AMapNavi.DrivingDefault);
						mRouteCalculatorProgressDialog.show();
					}
				}
			});
			//点击进入详情界面
			rl_listview_addr.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyCollectBean listBean = (MyCollectBean) mdata.get(position);
						if(listBean != null){
							goToDetail(listBean);
						}
						
				}
			});
			
		}
		
		return conventview;

	}

	private final class ObjectClass {
		TextView obj_tv_name = null;
		TextView obj_tv_distance = null;
		TextView obj_tv_address = null;
		TextView obj_tv_fastNum = null;
		TextView obj_tv_slowNum = null;
		TextView obj_tv_bespoke = null;
		LinearLayout obj_ll_navgation= null;
		LinearLayout obj_ll_bespoke = null;
	    ImageView obj_iv_bespoke = null;
	    RelativeLayout obj_rl_addr = null;

		public ObjectClass(TextView name, TextView distance, TextView address,
				TextView fastNum, TextView slowNum,
				TextView bespoke, LinearLayout ll_navgation,
				LinearLayout ll_bespoke, ImageView iv_bespoke,RelativeLayout rl_addr) {
			this.obj_tv_name = name;
			this.obj_tv_distance = distance;
			this.obj_tv_address = address;
			this.obj_tv_fastNum = fastNum;
			this.obj_tv_slowNum = slowNum;
			this.obj_tv_bespoke = bespoke;
			this.obj_ll_navgation = ll_navgation;
			this.obj_ll_bespoke = ll_bespoke ;
			this.obj_iv_bespoke = iv_bespoke;
			this.obj_rl_addr = rl_addr;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
				//电站详情
					cancelPD();
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
					}
				}
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 进入详情界面
	 */
	private void goToDetail(MyCollectBean bean){
		pkUserinfo = PreferencesUtil.getStringPreferences(mContext,"pkUserinfo");
		String geoLat = PreferencesUtil.getStringPreferences(mContext,
				"currentlat");
		String geoLng = PreferencesUtil.getStringPreferences(mContext,
				"currentlng");
		
		if(!Tools.isEmptyString(pkUserinfo)){
			//先去请求电桩 电站信息，如果有数据，在onsuccess里跳转到详情界面 
			if(isNetConnection()){
				//1是桩，2是站
				electricType = bean.getUsCo_Type();
				electricId = bean.getUsCo_Objectid();
				if("1".equals(electricType)){
					showPD("正在加载数据...");
					GetDataPost.getInstance(mContext).getPileDetail(handler,
							electricId, pkUserinfo, geoLng, geoLat);
				}else if("2".equals(electricType)){
					showPD("正在加载数据...");
					GetDataPost.getInstance(mContext).getStationDetail(handler,
							electricId, pkUserinfo, geoLng, geoLat);
				}
			
			}else {
				showToast("网络不稳，请稍后再试");
			}
			
		}else {
			Intent loginIn = new Intent();
			loginIn.setClass(mContext, LoginAndRegisterActivity.class);
			mContext.startActivity(loginIn);
		}
		
		
		
	}

	@Override
	public void onNaviCancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviMapMode(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviSetting() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviTurnClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNextRoadClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScanViewButtonClick() {
		// TODO Auto-generated method stub

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
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		Bundle bundle = new Bundle();
		bundle.putInt(Utils.ACTIVITYINDEX, Utils.MYCOLLECT);
		bundle.putBoolean(Utils.ISEMULATOR, false);
		intent.putExtras(bundle);
		mContext.startActivity(intent);
		// mcontext.finish();

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

	@Override
	public void onLockMap(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {
		// TODO Auto-generated method stub
		
	}

	


}

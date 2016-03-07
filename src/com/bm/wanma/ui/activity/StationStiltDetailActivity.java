package com.bm.wanma.ui.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.bm.wanma.R;
import com.bm.wanma.adapter.MyStationGridViewAdapter;
import com.bm.wanma.adapter.MyStationListViewAdapter;
import com.bm.wanma.adapter.MyStationPileGridViewAdapter;
import com.bm.wanma.entity.ElectricPileBean;
import com.bm.wanma.entity.PileHead;
import com.bm.wanma.entity.PowerElectricpileListBean;
import com.bm.wanma.entity.PowerStationBean;
import com.bm.wanma.entity.ShareToThirdBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.popup.CustomShareBoard;
import com.bm.wanma.ui.navigation.NaviCustomActivity;
import com.bm.wanma.ui.navigation.TTSController;
import com.bm.wanma.ui.navigation.Utils;
import com.bm.wanma.utils.ComparatorGunHead;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;
import com.bm.wanma.view.MyDetailGridView;
import com.bm.wanma.view.MyDetailListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StationStiltDetailActivity extends BaseActivity implements OnClickListener,OnItemClickListener,AMapNaviListener, AMapNaviViewListener{

	private MyStationGridViewAdapter stationGridViewAdapter;
	private MyStationPileGridViewAdapter pileGridViewAdapter;
	private MyDetailGridView mGridView;
	private MyStationListViewAdapter listAdapter;
	private MyDetailListView mListView; 
	private ImageView iv_photo,iv_owner_icon;
	private ImageButton ib_back,ib_collect,ib_share,ib_navgation,ib_price_help;
	private TextView tv_photo_num,tv_commentstar,tv_comment_num;
	private TextView tv_name,tv_distance,tv_address,tv_fast_num,tv_fast_idle;
	private TextView tv_slow_num,tv_slow_idle,tv_price;
	private TextView tv_open_time,tv_service_tel,tv_commit_comment,tv_commit_error;
	private TextView tv_owner_name,tv_owner_powersize,tv_owner_interface;
	private LinearLayout ll_attr_power_inter,ll_comment;
	private String pkUserinfo;
	private String currentLat,currentLng,type,price,electricId,isCollect;
	private boolean collect;
	private PowerStationBean stationBean;
	private ArrayList<PowerElectricpileListBean> powerElectricpileListBeans;
	private PowerElectricpileListBean powerElectricpileListBean; 
	private ElectricPileBean electricPileBean;
	private ArrayList<PileHead> pileHeadLists;
	private String commentStar,commentNum,name,address,distance;
	private String zlnum,zlfree,jlnum,jlfree,opentime,servicetel;
	private String edolat,edolng;
	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
	private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态
	private Double startGeoLat,startGeoLng,endEdoLat,endEdoLng;
	//预约单价  -- 预约电桩id
	private String bespokePrice,bespokeElectricId,comm_status,rateId;
	private ArrayList<String> urls;//图片url集合
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_pile_detail);
		initView();
		initValue();
		registerBoradcastReceiver();//注册广播，预约成功，关闭activity
		
		
	}
	private void initView(){
		ib_back = (ImageButton)findViewById(R.id.detail_back);
		ib_back.setOnClickListener(this);
		ib_collect = (ImageButton)findViewById(R.id.detail_collect);
		ib_collect.setOnClickListener(this);
		
		ib_share = (ImageButton)findViewById(R.id.detail_share);
		ib_share.setOnClickListener(this);
		iv_photo = (ImageView)findViewById(R.id.detail_image);
		iv_photo.setOnClickListener(this);
		iv_owner_icon = (ImageView)findViewById(R.id.detail_owner_icon);
		tv_photo_num = (TextView)findViewById(R.id.detail_image_num);
		
		tv_commentstar = (TextView)findViewById(R.id.detail_commentstar);
		tv_comment_num = (TextView)findViewById(R.id.detail_commentnum);
		tv_name = (TextView)findViewById(R.id.detail_name);
		tv_distance = (TextView)findViewById(R.id.detail_distance);
		tv_address = (TextView)findViewById(R.id.detail_address);
		tv_fast_num = (TextView)findViewById(R.id.detail_fast_num);
		tv_fast_idle = (TextView)findViewById(R.id.detail_fast_idle);
		tv_slow_num = (TextView)findViewById(R.id.detail_slow_num);
		tv_slow_idle = (TextView)findViewById(R.id.detail_slow_idle);
		tv_price = (TextView)findViewById(R.id.detail_price);
		ib_price_help = (ImageButton)findViewById(R.id.detail_price_help_icon);
		ib_price_help.setOnClickListener(this);
		ib_navgation = (ImageButton)findViewById(R.id.detail_navgation);
		ib_navgation.setOnClickListener(this);
		tv_open_time = (TextView)findViewById(R.id.detail_open_time);
		tv_service_tel = (TextView)findViewById(R.id.detail_service_tel);
		tv_commit_comment = (TextView)findViewById(R.id.detail_commit_comment);
		tv_commit_comment.setOnClickListener(this);
		tv_commit_error = (TextView)findViewById(R.id.detail_commit_error);
		tv_commit_error.setOnClickListener(this);
		ll_attr_power_inter = (LinearLayout)findViewById(R.id.detail_ll_power_interface);
		ll_comment = (LinearLayout)findViewById(R.id.detail_ll_comment);
		tv_owner_name = (TextView)findViewById(R.id.detail_owner_name);
		tv_owner_interface = (TextView)findViewById(R.id.detail_owner_interface);
		tv_owner_powersize = (TextView)findViewById(R.id.detail_owner_power);
		
		mGridView = (MyDetailGridView)findViewById(R.id.detail_grid);
		
		//mGridView.setAdapter(gridAdapter);
		mListView = (MyDetailListView)findViewById(R.id.detail_listview);
		//mListView.setAdapter(listAdapter);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initValue(){
		if("1".equals(type)){//桩
				commentStar = electricPileBean.getElectricPileCommentStar();
				commentNum = electricPileBean.getElectricPileCommentSum();
				name = electricPileBean.getElectricPileName();
				address = electricPileBean.getElectricPileAdress();
				distance = electricPileBean.getDistance();
				zlnum = electricPileBean.getZlHeadNum();
				zlfree = electricPileBean.getZlFreeHeadNum();
				jlnum = electricPileBean.getJlHeadNum();
				jlfree = electricPileBean.getJlFreeHeadNum();
				price = Tools.addStr(electricPileBean.getRaIn_ServiceCharge(),electricPileBean.getCurrentRate());
				bespokePrice = electricPileBean.getRaIn_ReservationRate();
				opentime = electricPileBean.getOnlineTime();
				servicetel = electricPileBean.getElectricPileTell();
				edolat = electricPileBean.getElpiLatitude();
				edolng = electricPileBean.getElpiLongitude();
				rateId = electricPileBean.getRateId();
				urls = new ArrayList<String>();
				String url = electricPileBean.getElectricPileImage();
				if(!Tools.isEmptyString(url)){
					String[] urllist = url.split(",");
					if(urllist.length >1){
						tv_photo_num.setVisibility(View.VISIBLE);
						tv_photo_num.setText("1/"+urllist.length);
					}
					
					//urls = Arrays.asList(urllist);
					//urls = new ArrayList(Arrays.asList(urllist));//Strng[] 转换为Arraylist
					for(int i=0;i<urllist.length;i++){
						urls.add(urllist[i]);
					}
					DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.imgno)
					.showImageOnFail(R.drawable.imgno) 
					.cacheInMemory(true)
					.cacheOnDisk(false)
					.bitmapConfig(Config.RGB_565)
					.build();
					ImageLoader.getInstance().displayImage(urls.get(0), iv_photo, options);
					
				}
				
				setValueToView();
				initPileGridView();
			
		}else if("2".equals(type)){//站
			commentStar = stationBean.getPowerCommentStar();
			commentNum = stationBean.getPowerCommentSum();
			name = stationBean.getPowerStationName();
			address = stationBean.getPowerStationAddress();
			distance = stationBean.getDistance();
			zlnum = stationBean.getZlHeadNum();
			zlfree = stationBean.getZlFreeHeadNum();
			jlnum = stationBean.getJlHeadNum();
			jlfree = stationBean.getJlFreeHeadNum();
			edolat = stationBean.getPostLatitude();
			edolng = stationBean.getPostLongitude();
			powerElectricpileListBeans = stationBean.getPowerElectricpileList();
		
			urls = new ArrayList<String>();
			String url = stationBean.getPowerStationImage();
			if(!Tools.isEmptyString(url)){
				String[] urllist = url.split(",");
				if(urllist.length >1){
					tv_photo_num.setVisibility(View.VISIBLE);
					tv_photo_num.setText("1/"+urllist.length);
				}
				//urls = Arrays.asList(urllist);
				//urls = new ArrayList(Arrays.asList(urllist));//Strng[] 转换为Arraylist
				for(int i=0;i<urllist.length;i++){
					urls.add(urllist[i]);
				}
				DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.imgno)
				.showImageOnFail(R.drawable.imgno) 
				.cacheInMemory(true)
				.cacheOnDisk(false)
				.bitmapConfig(Config.RGB_565)
				.build();
				ImageLoader.getInstance().displayImage(urls.get(0), iv_photo, options);
			}
			
			opentime = stationBean.getOnlineTime();
			servicetel = stationBean.getPowerStationTell();
			setValueToView();
			initStationGridView();
		}
		
	}
	//为控件赋值
	private void setValueToView(){
		if(!Tools.isEmptyString(commentStar)){
			commentStar = Tools.getSub1Value(commentStar);
			tv_commentstar.setText(commentStar+"分");
		}
		if(!Tools.isEmptyString(commentNum)){
			tv_comment_num.setText("("+commentNum+"评论)");
			if(Integer.valueOf(commentNum) > 0){
				ll_comment.setOnClickListener(this);//评分数大于0时，点击进入详情界面
			}
		}
		if(!Tools.isEmptyString(name)){
			tv_name.setText(name);
		}
		if(!Tools.isEmptyString(distance)){
			String tempDistance = Tools.getMeterOrKM(distance);
			tv_distance.setText(tempDistance);
		}
		if(!Tools.isEmptyString(address)){
			tv_address.setText(address);
		}
		if(!Tools.isEmptyString(zlnum)){
			tv_fast_num.setText(zlnum);
		}
		if(!Tools.isEmptyString(zlfree)){
			tv_fast_idle.setText(zlfree);
		}
		if(!Tools.isEmptyString(jlnum)){
			tv_slow_num.setText(jlnum);
		}
		if(!Tools.isEmptyString(jlfree)){
			tv_slow_idle.setText(jlfree);
		}
		
		if(!Tools.isEmptyString(opentime)){
			tv_open_time.setText(opentime);
		}
		if(!Tools.isEmptyString(servicetel)){
			tv_service_tel.setText(servicetel);
		}
		if(!Tools.isEmptyString(isCollect) && 
				((new BigDecimal(isCollect).compareTo(new BigDecimal(0))> 0))){
			collect = true;
			ib_collect.setImageResource(R.drawable.nav_btn_collect_p);
		}else {
			collect = false;
			ib_collect.setImageResource(R.drawable.nav_btn_collect);
		}
		
	}
	//初始化站中桩 Gridview
	@SuppressWarnings("unchecked")
	private void initStationGridView(){
		//powerElectricpileListBeans = stationBean.getPowerElectricpileList();
		if(powerElectricpileListBeans != null){
			stationGridViewAdapter = new MyStationGridViewAdapter(this, powerElectricpileListBeans);
			stationGridViewAdapter.setSelection(0);
			mGridView.setAdapter(stationGridViewAdapter);
			powerElectricpileListBean = powerElectricpileListBeans.get(0);
			pileHeadLists = powerElectricpileListBean.getPileHeadList();
			bespokeElectricId = powerElectricpileListBean.getElictricPicId();
			bespokePrice = powerElectricpileListBean.getRaIn_ReservationRate();
			comm_status = powerElectricpileListBean.getComm_status();
			//价格赋值
			price = Tools.addStr(powerElectricpileListBean.getCurrentRate(), powerElectricpileListBean.getRaIn_ServiceCharge());
			rateId = powerElectricpileListBean.getRateId();
			tv_price.setText(price+"元/度");
			//先对pileHeadLists排序
			ComparatorGunHead comparator = new ComparatorGunHead();
			Collections.sort(pileHeadLists, comparator);
			listAdapter = new MyStationListViewAdapter(this, pileHeadLists,bespokeElectricId,bespokePrice,comm_status);
			//listview 赋值
			mListView.setAdapter(listAdapter);
			String ownerName = null;
			String ownerInterface = null;
			String ownerPowersize = null;
			ll_attr_power_inter.setVisibility(View.VISIBLE);//所属，功率，接口 --显示
			if(powerElectricpileListBean != null){
				ownerName = powerElectricpileListBean.getOwnerCompany();//0其他，1爱充网，2国网，3特斯拉
				ownerInterface = powerElectricpileListBean.getElpiPowerinterface();
				ownerPowersize = powerElectricpileListBean.getElpiPowersize();
			}
			if("1".equals(ownerName)){
				iv_owner_icon.setImageResource(R.drawable.bg_map_eichong);
				tv_owner_name.setText("爱充网");
			}else if("2".equals(ownerName)){
				iv_owner_icon.setImageResource(R.drawable.bg_map_state);
				tv_owner_name.setText("国网");
			}if("3".equals(ownerName)){
				iv_owner_icon.setImageResource(R.drawable.bg_map_tesla);
				tv_owner_name.setText("特斯拉");
			}if("0".equals(ownerName)){
				iv_owner_icon.setImageResource(R.drawable.bg_map_other);
				tv_owner_name.setText("其他");
			}
			//7国标 19美标 20欧标
			if("7".equals(ownerInterface)){
				tv_owner_interface.setText("国标");
			}else if("20".equals(ownerInterface)){
				tv_owner_interface.setText("欧标");
			}
			else if("19".equals(ownerInterface)){
				tv_owner_interface.setText("美标");
			}
			if(ownerPowersize != null){
				tv_owner_powersize.setText(ownerPowersize);
			}
			//gridview 设置item点击事件
			mGridView.setOnItemClickListener(this);
			
		}
		
	}
	//初始化单个桩 Gridview
			@SuppressWarnings("unchecked")
			private void initPileGridView(){
				pileGridViewAdapter = new MyStationPileGridViewAdapter(this, electricPileBean);
				mGridView.setAdapter(pileGridViewAdapter);
				pileHeadLists = electricPileBean.getPileHeadList();
				bespokePrice = electricPileBean.getRaIn_ReservationRate();
				comm_status = electricPileBean.getComm_status();
				//价格赋值
				price = Tools.addStr(electricPileBean.getCurrentRate(), electricPileBean.getRaIn_ServiceCharge());
				tv_price.setText(price+"元/度");
				//先对pileHeadLists排序
				ComparatorGunHead comparator = new ComparatorGunHead();
				Collections.sort(pileHeadLists, comparator);
				 
				listAdapter = new MyStationListViewAdapter(this, pileHeadLists,electricId,bespokePrice,comm_status);
				mListView.setAdapter(listAdapter);
				String ownerName = null;
				String ownerInterface = null;
				String ownerPowersize = null;
				ll_attr_power_inter.setVisibility(View.VISIBLE);//所属，功率，接口 --显示
				if(electricPileBean != null){
					ownerName = electricPileBean.getOwnerCompany();//0其他，1爱充网，2国网，3特斯拉
					ownerInterface = electricPileBean.getElectricPowerInterface();
					ownerPowersize = electricPileBean.getElectricPowerSize();
				}
				if("1".equals(ownerName)){
					iv_owner_icon.setImageResource(R.drawable.bg_map_eichong);
					tv_owner_name.setText("爱充网");
				}else if("2".equals(ownerName)){
					iv_owner_icon.setImageResource(R.drawable.bg_map_state);
					tv_owner_name.setText("国网");
				}if("3".equals(ownerName)){
					iv_owner_icon.setImageResource(R.drawable.bg_map_tesla);
					tv_owner_name.setText("特斯拉");
				}if("0".equals(ownerName)){
					iv_owner_icon.setImageResource(R.drawable.bg_map_other);
					tv_owner_name.setText("其他");
				}
				//7国标 19美标 20欧标
				if("7".equals(ownerInterface)){
					tv_owner_interface.setText("国标");
				}else if("20".equals(ownerInterface)){
					tv_owner_interface.setText("欧标");
				}
				else if("19".equals(ownerInterface)){
					tv_owner_interface.setText("美标");
				}
				if(ownerPowersize != null){
					tv_owner_powersize.setText(ownerPowersize);
				}
				
			}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//返回按钮
		case R.id.detail_back:
			/*Intent listIn = new Intent();
			listIn.setClass(this, HomeActivity.class);
			listIn.putExtra("tag", "1");
			startActivity(listIn);*/
			finish();
			break;
		// 收藏 
		case R.id.detail_collect:
			if(collect){
			 ib_collect.setImageResource(R.drawable.nav_btn_collect);
			 collect = false;
			 showToast("取消收藏成功");
			}else {
				ib_collect.setImageResource(R.drawable.nav_btn_collect_p);
				 collect = true;
				 showToast("收藏成功");
			}
			/*if(isNetConnection()){
				GetDataPost.getInstance(StationStiltDetailActivity.this).
				collectStationPile(handler, pkUserinfo, type, electricId);
			}else {
				showToast("网络不稳，请稍后再试");
			}*/

			break;
		
		//分享
		case R.id.detail_share:
			ShareToThirdBean mShareBean = new ShareToThirdBean();
			mShareBean.setType(type);
			mShareBean.setAddr(tv_address.getText().toString());
			mShareBean.setLat(edolat);
			mShareBean.setLng(edolng);
			mShareBean.setName(tv_name.getText().toString());
			mShareBean.setService(price);
			CustomShareBoard shareBoard = new CustomShareBoard(getActivity(),mShareBean);
			shareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
			
			break;
		//点击查看多张大图
		case R.id.detail_image:
			if(urls.size()>0){
				imageBrower(0, urls);
			}
			break;
		//点击进入评分详情界面
		case R.id.detail_ll_comment:
			//showToast("评分详情");
			Intent commentIn = new Intent();
			commentIn.putExtra("stationType", type);
			commentIn.putExtra("stationId", electricId);
			commentIn.setClass(StationStiltDetailActivity.this,CommentDetailActivity.class);
			startActivity(commentIn);
			
			break;
		//价格 疑问
		case R.id.detail_price_help_icon:
			Intent priceIn = new Intent();
			priceIn.putExtra("priceId", rateId);
			priceIn.setClass(StationStiltDetailActivity.this, AboutPriceActivity.class);
			startActivity(priceIn);
			
			break;
		//导航
		case R.id.detail_navgation:
			if(isNetConnection()){
				// 启动FPS导航
				if (AMapNavi.getInstance(StationStiltDetailActivity.this) != null) {
					AMapNavi.getInstance(StationStiltDetailActivity.this)
							.calculateDriveRoute(mStartPoints,
									mEndPoints, null,
									AMapNavi.DrivingDefault);
					mRouteCalculatorProgressDialog.show();
				}
			}else {
				showToast("亲，网络不稳，请检查网络连接!");
			}
			
			break;
		//留言
		case R.id.detail_commit_comment:
			//showToast("留言");
			break;
		//纠错
		case R.id.detail_commit_error:
			Intent errorIn = new Intent();
			errorIn.setClass(StationStiltDetailActivity.this,CommitDeviceErrorActivity.class);
			errorIn.putExtra("deviceType", type);
			errorIn.putExtra("epId", electricId);
			startActivity(errorIn);
			break;
			
		default:
			break;
		}
	}
	
	
 
	@Override
	protected void onStart() {
		super.onStart();
		initNavigation();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//退出当前界面时，提交收藏或删除收藏
		if(!Tools.isEmptyString(isCollect) && !collect && isNetConnection()){
			GetDataPost.getInstance(StationStiltDetailActivity.this).removeMyCollect(handler, isCollect, type);
		}else if(collect && Tools.isEmptyString(isCollect) && isNetConnection()){
				GetDataPost.getInstance(StationStiltDetailActivity.this).
				collectStationPile(handler, pkUserinfo, type, electricId);
		}
		
		 unregisterReceiver(mBroadcastReceiver);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//具体点击哪个item
		stationGridViewAdapter.setSelection(position);
		stationGridViewAdapter.notifyDataSetChanged();
		String ownerName = null;
		String ownerInterface = null;
		String ownerPowersize = null;
		ll_attr_power_inter.setVisibility(View.VISIBLE);//所属，功率，接口 --显示
		powerElectricpileListBean = powerElectricpileListBeans.get(position);
		pileHeadLists = powerElectricpileListBean.getPileHeadList();
		comm_status = powerElectricpileListBean.getComm_status();
		
		//价格赋值
		price = Tools.addStr(powerElectricpileListBean.getCurrentRate(), powerElectricpileListBean.getRaIn_ServiceCharge());
		rateId = powerElectricpileListBean.getRateId();
		tv_price.setText(price+"元/度");
		
		//先对pileHeadLists排序
		ComparatorGunHead comparator = new ComparatorGunHead();
		Collections.sort(pileHeadLists, comparator);
		listAdapter = new MyStationListViewAdapter(this, pileHeadLists,powerElectricpileListBean.getElictricPicId(),
				powerElectricpileListBean.getRaIn_ReservationRate(),comm_status);
		mListView.setAdapter(listAdapter);
		//更新适配器 数据
		//listAdapter.notifyDataSetChanged();
		if(powerElectricpileListBean != null){
			ownerName = powerElectricpileListBean.getOwnerCompany();//0其他，1爱充网，2国网，3特斯拉
			ownerInterface = powerElectricpileListBean.getElpiPowerinterface();
			ownerPowersize = powerElectricpileListBean.getElpiPowersize();
		}
		if("1".equals(ownerName)){
			iv_owner_icon.setImageResource(R.drawable.bg_map_eichong);
			tv_owner_name.setText("爱充网");
		}else if("2".equals(ownerName)){
			iv_owner_icon.setImageResource(R.drawable.bg_map_state);
			tv_owner_name.setText("国网");
		}if("3".equals(ownerName)){
			iv_owner_icon.setImageResource(R.drawable.bg_map_tesla);
			tv_owner_name.setText("特斯拉");
		}if("0".equals(ownerName)){
			iv_owner_icon.setImageResource(R.drawable.bg_map_other);
			tv_owner_name.setText("其他");
		}
		//7国标 19美标 20欧标
		if("7".equals(ownerInterface)){
			tv_owner_interface.setText("国标");
		}else if("20".equals(ownerInterface)){
			tv_owner_interface.setText("欧标");
		}
		else if("19".equals(ownerInterface)){
			tv_owner_interface.setText("美标");
		}
		if(ownerPowersize != null){
			tv_owner_powersize.setText(ownerPowersize);
		}
		
	}
	
	/**
	 * 打开图片查看器
	 * @param position
	 * @param urls2
	 */
	protected void imageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		//intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
		//overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		
	}
	/**
	 * 初始化导航
	 */
	private void initNavigation(){
		TTSController.getInstance(StationStiltDetailActivity.this).startSpeaking();
		// 获取当前经纬度
		currentLat = PreferencesUtil.getStringPreferences(this, "currentlat");
		currentLng = PreferencesUtil.getStringPreferences(this, "currentlng");
		// 获取目的地经纬度
		if (!Tools.isEmptyString(edolat) && !Tools.isEmptyString(edolng) && !Tools.isEmptyString(currentLat) && !Tools.isEmptyString(currentLng)) {
			startGeoLat = Double.parseDouble(currentLat);
			startGeoLng = Double.parseDouble(currentLng);
			endEdoLat = Double.parseDouble(edolat);
			endEdoLng = Double.parseDouble(edolng);
			NaviLatLng mNaviStart = new NaviLatLng(startGeoLat, startGeoLng);
			NaviLatLng mNaviEnd = new NaviLatLng(endEdoLat, endEdoLng);
			mStartPoints.clear();
			mEndPoints.clear();
			mStartPoints.add(mNaviStart);
			mEndPoints.add(mNaviEnd);
		}

		mRouteCalculatorProgressDialog = new ProgressDialog(StationStiltDetailActivity.this);
		mRouteCalculatorProgressDialog.setCancelable(true);
		AMapNavi aMapNavi = AMapNavi.getInstance(StationStiltDetailActivity.this);
		if (this instanceof AMapNaviListener && aMapNavi != null) {
			aMapNavi.setAMapNaviListener(this);
		}
		
		
	}
	@Override
	protected void getData() {
		pkUserinfo = PreferencesUtil.getStringPreferences(getActivity(),"pkUserinfo");
		currentLat = PreferencesUtil.getStringPreferences(getActivity(),"currentlat");
		currentLng = PreferencesUtil.getStringPreferences(getActivity(),"currentlng");
		//GetDataPost.getInstance(this).getStationDetail(handler, powerStationId, pkUserinfo, currentLng, currentLat);
		Intent getDetaIn = getIntent(); 
		type = getDetaIn.getStringExtra("type");
		electricId = getDetaIn.getStringExtra("electricId"); 
		
		
		if("1".equals(type)){//桩
			electricPileBean = (ElectricPileBean) getDetaIn.getSerializableExtra("pileBean");
			isCollect = electricPileBean.getIsCollect();
			
		}else if("2".equals(type)){//站
			stationBean = (PowerStationBean) getDetaIn.getSerializableExtra("stationBean");
			isCollect = stationBean.getIsCollect();
		}
		
	}
	

	@Override
	public void onSuccess(String sign, Bundle bundle) {
		// 收藏
		/*if (sign.equals(Protocol.COLLECT_STATION_PILE)) {
			showToast(bundle.getString(Protocol.MSG));
			ib_collected.setVisibility(View.VISIBLE);//已收藏显示
			ib_collect.setVisibility(View.GONE);//收藏按钮隐藏
		}*/
		

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {

		
	}
	
	public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction("com.bm.wanma.bespoke.ok");  
        //注册广播        
        registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }  
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){  
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			 if(action.equals("com.bm.wanma.bespoke.ok")){  
				// showToast("com.bm.wanma.bespoke.ok");
				 finish();
	         }  
		}  
          
    };  
	
	
	@Override
	public void onLockMap(boolean arg0) {
		// TODO Auto-generated method stub
		
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
		Intent intent = new Intent(StationStiltDetailActivity.this, NaviCustomActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		Bundle bundle = new Bundle();
		bundle.putInt(Utils.ACTIVITYINDEX, Utils.STATIONDETAIL);
		bundle.putBoolean(Utils.ISEMULATOR, false);
		intent.putExtras(bundle);
		startActivity(intent);
		
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

package com.bm.wanma.ui.activity;



import java.util.ArrayList;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.bm.wanma.R;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.dialog.CancleBespokeDialog;
import com.bm.wanma.dialog.CancleBespokeSuccessDialog;
import com.bm.wanma.entity.BespokeDetailBean;
import com.bm.wanma.entity.CancleBespokeBean;
import com.bm.wanma.entity.ElectricPileBean;
import com.bm.wanma.entity.PowerStationBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.ui.navigation.NaviCustomActivity;
import com.bm.wanma.ui.navigation.TTSController;
import com.bm.wanma.ui.navigation.Utils;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.TimeUtil;
import com.bm.wanma.utils.Tools;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author cm
 * 预约详情界面
 */
public class BespokeDetailActivity extends BaseActivity implements OnClickListener,AMapNaviListener{

	private ImageButton ib_back;
	private TextView tv_cancle,tv_downtime;
	private TextView tv_code,tv_frozen_tag,tv_frozen;
	private TextView tv_order_time,tv_order_time_tag;
	private TextView tv_address,tv_name,tv_pknum,tv_num,tv_mode,tv_interface,tv_head_name;
	private TextView tv_again_bespoke,tv_led,tv_lock,tv_nav,tv_again;
	private LinearLayout ll_downtime,ll_status,ll_tools,ll_nav;
	private String deviceId,pkuserId,whichMode;
	private String pkBespoke,bespElectricpilehead;
	private BespokeDetailBean bespokeDetailBean;
	private CancleBespokeBean cancleBespokeBean;
	private Handler mHandler;
	private boolean flags,isFirstLed;
	private long countdownss;
	private String isShowDowntime;//list进来区别是否显示倒计时
	private ArrayList<PowerStationBean> stationBeanList;
	private PowerStationBean stationBean;
	private ArrayList<ElectricPileBean> pileBeanList;
	private ElectricPileBean pileBean;
	private CancleBespokeDialog cancleBespokeDialog;
	private CancleBespokeSuccessDialog cancleBespokeSuccessDialog;
	private long lastLEDTime,lastLockTime;
	// 起点终点列表
		private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
		private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
		private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态
		private Double startGeoLat,startGeoLng;
		private String markerLat,markerLng;
		private Double endEdoLat,endEdoLng;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bespoke_detail);
		deviceId = getDeviceId();
		pkuserId = PreferencesUtil.getStringPreferences(this, "pkUserinfo");
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 18:
					if (countdownss > 0) {
						countdownss--;
						String downtimes = TimeUtil.getCutDown3(countdownss);
						tv_downtime.setText(downtimes);
					}else {
						tv_cancle.setVisibility(View.GONE);
						ll_downtime.setVisibility(View.GONE);
						ll_status.setVisibility(View.VISIBLE);
						tv_frozen_tag.setText("本次消费:");
						tv_order_time_tag.setText("预约时间:");
						tv_order_time.setText(TimeUtil.getTimeForBespokeL(Long.valueOf(bespokeDetailBean.getBespBeginTime())));
						ll_tools.setVisibility(View.GONE);
						ll_nav.setVisibility(View.GONE);
						tv_again_bespoke.setVisibility(View.VISIBLE);
					}
					break;
				}
			}
		};
		 
		init();
		if(bespokeDetailBean != null){
			initValue(whichMode);
		}
		flags = true;
		isFirstLed = true;
		
	}
	private void init(){
		ib_back = (ImageButton) findViewById(R.id.bespoke_detail_back);
		ib_back.setOnClickListener(this);
		tv_cancle = (TextView) findViewById(R.id.bespoke_detail_cancle);
		tv_cancle.setOnClickListener(this);
		tv_downtime = (TextView) findViewById(R.id.bespoke_detail_downtime);
		ll_downtime = (LinearLayout) findViewById(R.id.bespoke_detail_downtime_ll);
		ll_status = (LinearLayout) findViewById(R.id.bespoke_detail_status_ll);
		tv_code =(TextView)findViewById(R.id.bespoke_detail_ordernum);
		tv_frozen_tag = (TextView) findViewById(R.id.bespoke_detail_frozen_price_tag);
		tv_frozen = (TextView) findViewById(R.id.bespoke_detail_frozen_price);
		tv_order_time_tag = (TextView) findViewById(R.id.bespoke_detail_order_time_tag);
		tv_order_time = (TextView) findViewById(R.id.bespoke_detail_order_time);
		tv_address = (TextView) findViewById(R.id.bespoke_detail_address);
		tv_name = (TextView) findViewById(R.id.bespoke_detail_name); 
		tv_pknum = (TextView) findViewById(R.id.bespoke_detail_park_num);
		tv_num = (TextView) findViewById(R.id.bespoke_detail_electric_num);
		tv_mode = (TextView) findViewById(R.id.bespoke_detail_electric_mode);
		tv_interface = (TextView) findViewById(R.id.bespoke_detail_electric_interface);
		tv_head_name = (TextView) findViewById(R.id.bespoke_detail_head_name);
		ll_tools = (LinearLayout) findViewById(R.id.bespoke_detail_tool_ll);
		tv_led =(TextView) findViewById(R.id.bespoke_detail_led);
		tv_lock =(TextView) findViewById(R.id.bespoke_detail_lock);
		ll_nav = (LinearLayout) findViewById(R.id.bespoke_detail_nav_ll);
		tv_nav =(TextView) findViewById(R.id.bespoke_detail_nav);
		tv_nav.setOnClickListener(this);
		tv_again =(TextView) findViewById(R.id.bespoke_detail_again);
		tv_again.setOnClickListener(this);
		tv_again_bespoke = (TextView) findViewById(R.id.bespoke_detail_again_bespoke);
		tv_again_bespoke.setOnClickListener(this);
		
	}
	@SuppressLint("NewApi")
	private void initValue(String mode){
		pkBespoke = bespokeDetailBean.getPkBespoke();
		bespElectricpilehead = bespokeDetailBean.getBespElectricpilehead();
		tv_code.setText(""+bespokeDetailBean.getBespResepaymentcode());//预约订单编号
		tv_frozen.setText(bespokeDetailBean.getBespFrozenamt()+" 元");
		tv_address.setText(""+bespokeDetailBean.getEpAddress());
		tv_name.setText(""+bespokeDetailBean.getEpName());
		tv_pknum.setText("车位号:"+bespokeDetailBean.getPark_num());
		tv_num.setText(""+bespokeDetailBean.getEp_num());
		if("5".equals(bespokeDetailBean.getChargingMode())){//（5-直流充电，14-交流充电）
			tv_mode.setText("快充");
		}else if("14".equals(bespokeDetailBean.getChargingMode())){
			tv_mode.setText("慢充");
		}
		if("7".equals(bespokeDetailBean.getPowerInterface())){//7国标 19美标 20欧标
			tv_interface.setText("国标");
		}else if("19".equals(bespokeDetailBean.getPowerInterface())){
			tv_interface.setText("美标");
		}else if("20".equals(bespokeDetailBean.getPowerInterface())){
			tv_interface.setText("欧标");
		}
		
		String headnum = bespokeDetailBean.getEleHeadNum();
		if(!Tools.isEmptyString(headnum)){
			int i = Integer.valueOf(headnum);
			 char c1=(char) (i+64);
			 tv_head_name.setText(c1 + "号枪头");
		}
		//tv_head_name.setText(""+bespokeDetailBean.getePHeElectricpileHeadName());
		if("1".equals(bespokeDetailBean.getLed_flash())){
			tv_led.setOnClickListener(this);
			tv_led.setTextColor(getResources().getColor(R.color.common_orange));
			Drawable drawable= getResources().getDrawable(R.drawable.bg_lamp_off);  
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
			tv_led.setCompoundDrawables(drawable,null,null,null);  
			tv_led.setBackground(getResources().getDrawable(R.drawable.bespoke_detail_led_white));
		}
		if("1".equals(bespokeDetailBean.getPark_lock())){
			tv_lock.setOnClickListener(this);
			tv_lock.setTextColor(getResources().getColor(R.color.common_orange));
			Drawable drawable= getResources().getDrawable(R.drawable.bg_order_lock02);  
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
			tv_lock.setCompoundDrawables(drawable,null,null,null);  
			tv_lock.setBackground(getResources().getDrawable(R.drawable.bespoke_detail_led_white));
		}
		
		
		if("list".equals(mode) && "2".equals(isShowDowntime)){//列表进来,预约已结束
			tv_cancle.setVisibility(View.GONE);
			ll_downtime.setVisibility(View.GONE);
			ll_status.setVisibility(View.VISIBLE);
			tv_frozen_tag.setText("本次消费:");
			tv_order_time_tag.setText("预约时间:");
			String tempBegintime = TimeUtil.getTimeForBespokeL(Long.valueOf(bespokeDetailBean.getBespBeginTime()));
			String tempEndtime = bespokeDetailBean.getBespEndTime();
			//String tempBespoketime = bespokeDetailBean.getBespBespoketime();
			//long temptime = Long.parseLong(tempBegintime)+Long.parseLong(tempBespoketime)*60*1000;
			tv_order_time.setText(tempBegintime+"\n"+tempEndtime);
			ll_tools.setVisibility(View.GONE);
			ll_nav.setVisibility(View.GONE);
			tv_again_bespoke.setVisibility(View.VISIBLE);
		}else if("bespoke".equals(mode)){//预约成功直接进来
			String bespokebegintime = bespokeDetailBean.getBespBeginTime();//预约开始时间
			String bespoketime = bespokeDetailBean.getBespBespoketime();//预约时间
			long bespEndTime = Long.parseLong(bespokebegintime)+Long.parseLong(bespoketime)*60*1000;//预约结束时间
			long currentTime = System.currentTimeMillis();
			long countdowntime = bespEndTime - currentTime;
			countdownss = countdowntime / 1000;
			new TimeThread().start(); // 启动新的线程更新倒计时时间
			tv_frozen_tag.setText("冻结金额:");
			tv_order_time_tag.setText("下单时间:");
			ll_status.setVisibility(View.GONE);
			ll_downtime.setVisibility(View.VISIBLE);
			tv_cancle.setVisibility(View.VISIBLE);
			tv_order_time.setText(TimeUtil.getTimeForBespokeL(Long.valueOf(bespokeDetailBean.getBespBeginTime())));
			ll_tools.setVisibility(View.VISIBLE);
			ll_nav.setVisibility(View.VISIBLE);
			tv_again_bespoke.setVisibility(View.GONE);
			
		}else if("list".equals(mode) && "1".equals(isShowDowntime)){//列表预约中进来
			String bespokebegintime = bespokeDetailBean.getBespBeginTime();//预约开始时间
			String bespoketime = bespokeDetailBean.getBespBespoketime();//预约时间
			long bespEndTime = Long.parseLong(bespokebegintime)+Long.parseLong(bespoketime)*60*1000;//预约结束时间
			long currentTime = System.currentTimeMillis();
			long countdowntime = bespEndTime - currentTime;
			countdownss = countdowntime / 1000;
			new TimeThread().start(); // 启动新的线程更新倒计时时间
			tv_frozen_tag.setText("冻结金额:");
			tv_order_time_tag.setText("下单时间:");
			tv_order_time.setText(TimeUtil.getTimeForBespokeL(Long.valueOf(bespokeDetailBean.getBespBeginTime())));
			tv_cancle.setVisibility(View.VISIBLE);
			ll_tools.setVisibility(View.VISIBLE);
			ll_downtime.setVisibility(View.VISIBLE);
			ll_nav.setVisibility(View.VISIBLE);
			ll_status.setVisibility(View.GONE);
			tv_again_bespoke.setVisibility(View.GONE);
			 
		}
		
	}
	
	
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bespoke_detail_back:
			if("bespoke".equals(whichMode)){//or  list
				//跳转到预约列表
				Intent listIn = new Intent();
				listIn.setClass(this, HomeActivity.class);
				listIn.putExtra("tag", "2");
				startActivity(listIn);
				finish();
			}else {
				finish();
			}
			
			break;
		case R.id.bespoke_detail_cancle:
			//提交取消预约请求
			cancleBespokeDialog = new CancleBespokeDialog(this,"是否取消订单?");
			cancleBespokeDialog.setCancelable(false);
			Window dialogWindow = cancleBespokeDialog.getWindow();
			dialogWindow.setGravity(Gravity.TOP);
			WindowManager m = getWindowManager();
			Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
	        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
	        p.y = 300; // 新位置Y坐标,必须设置 setGravity(Gravity.TOP)
	        //p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
	        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.95
	        dialogWindow.setAttributes(p);
	        cancleBespokeDialog.setOnPositiveListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	//取消预约请求
		        	if(isNetConnection()){
						showPD("正在取消预约，请稍等...");
						GetDataPost.getInstance(BespokeDetailActivity.this).cancelBespoke(handler, pkuserId, pkBespoke, bespElectricpilehead, deviceId);
						
					}else {
						showToast("网络不稳，请稍后再试...");
					}
		        	cancleBespokeDialog.dismiss();
		        }
		    });
	        cancleBespokeDialog.setOnNegativeListener(new OnClickListener() {
				 
				@Override
				public void onClick(View v) {
					cancleBespokeDialog.dismiss();
				}
			});
	        cancleBespokeDialog.show();
			 
			break;
		case R.id.bespoke_detail_led:
			
			//开LED灯
			//if ((System.currentTimeMillis() - lastLEDTime) > 2000) {
				String lat = PreferencesUtil.getStringPreferences(BespokeDetailActivity.this, "currentlat");
				String lng = PreferencesUtil.getStringPreferences(BespokeDetailActivity.this, "currentlng");
				GetDataPost.getInstance(this).ledSwitch(handler, bespokeDetailBean.getElPi_ElectricPileCode(), "1", null,
						pkuserId,lat,lng,bespokeDetailBean.getElPi_Latitude(),bespokeDetailBean.getElPi_Longitude());
				
				//更改点击后的背景色
				tv_led.setOnClickListener(null);
				tv_led.setTextColor(getResources().getColor(R.color.common_white));
				Drawable drawable= getResources().getDrawable(R.drawable.bg_lamp_on);  
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
				tv_led.setCompoundDrawables(drawable,null,null,null);  
				tv_led.setBackground(getResources().getDrawable(R.drawable.bespoke_detail_led_gray));
				
				/*if(isFirstLed){
					GetDataPost.getInstance(this).ledSwitch(handler, bespokeDetailBean.getElPi_ElectricPileCode(), "1", null,
							pkuserId,lat,lng,bespokeDetailBean.getElPi_Latitude(),bespokeDetailBean.getElPi_Longitude());
					isFirstLed = false;
				}else {
					GetDataPost.getInstance(this).ledSwitch(handler, bespokeDetailBean.getElPi_ElectricPileCode(), "2", null,
							pkuserId,lat,lng,bespokeDetailBean.getElPi_Latitude(),bespokeDetailBean.getElPi_Longitude());
					isFirstLed = true;
				}*/
			
			//lastLEDTime = System.currentTimeMillis();
			//}
			break;
		case R.id.bespoke_detail_lock:
			//开地锁
			String lock_lat = PreferencesUtil.getStringPreferences(
					BespokeDetailActivity.this, "currentlat");
			String lock_lng = PreferencesUtil.getStringPreferences(
					BespokeDetailActivity.this, "currentlng");
			GetDataPost.getInstance(this).downParkLock(handler,
					bespokeDetailBean.getElPi_ElectricPileCode(),
					bespokeDetailBean.getEleHeadNum(),
					bespokeDetailBean.getPark_num(), pkuserId, lock_lat, lock_lng,
					bespokeDetailBean.getElPi_Latitude(),
					bespokeDetailBean.getElPi_Longitude());
			
			//更改点击后的背景色
			tv_lock.setOnClickListener(null);
			tv_lock.setTextColor(getResources().getColor(R.color.common_white));
			Drawable lock_drawable= getResources().getDrawable(R.drawable.bg_order_lock);  
			lock_drawable.setBounds(0, 0, lock_drawable.getMinimumWidth(), lock_drawable.getMinimumHeight());  
			tv_lock.setCompoundDrawables(lock_drawable,null,null,null);  
			tv_lock.setBackground(getResources().getDrawable(R.drawable.bespoke_detail_led_gray));
			
			
			break;
		case R.id.bespoke_detail_nav:
			//导航
			if(isNetConnection()){
				initNavigation();
				// 启动FPS导航
				if (AMapNavi.getInstance(this) != null) {
					AMapNavi.getInstance(this)
							.calculateDriveRoute(mStartPoints,
									mEndPoints, null,
									AMapNavi.DrivingDefault);
					mRouteCalculatorProgressDialog.show();
				}
			}else {
				showToast("亲，网络不稳，请检查网络连接!");
			}
			break;
		case R.id.bespoke_detail_again:
			//续约
			Intent bespokeIn = new Intent();
			bespokeIn.setClass(this, BespokeActivity.class);
			bespokeIn.putExtra("bespElectricpileid",bespokeDetailBean.getPk_ElectricPile());//电桩id 
			bespokeIn.putExtra("bespElectricpilehead", bespokeDetailBean.getBespElectricpilehead());//预约枪口id
			bespokeIn.putExtra("bespBespokeprice", bespokeDetailBean.getUnitPrice());//预约单价（保留小数点后2位）
			bespokeIn.putExtra("bespElectricpileheadName",bespokeDetailBean.getePHeElectricpileHeadName());//枪口名称
			String hasbespoketime = bespokeDetailBean.getBespBespoketime();
			int hastime = Integer.parseInt(hasbespoketime)/30;
			bespokeIn.putExtra("bespoke", hastime);//已经预约的时间
			bespokeIn.putExtra("pkBespoke", bespokeDetailBean.getPkBespoke());//预约id
			startActivity(bespokeIn);
			
			break;
		case R.id.bespoke_detail_again_bespoke:
			//已结束的订单，再次预约
			if("0".equals(bespokeDetailBean.getePHeElectricpileHeadState())){//枪口空闲 0空闲中，3预约中，6充电中，9停用中
				Intent idleIn = new Intent();
				idleIn.setClass(this, BespokeActivity.class);
				idleIn.putExtra("bespElectricpileid",bespokeDetailBean.getPk_ElectricPile());//电桩id 
				idleIn.putExtra("bespElectricpilehead", bespokeDetailBean.getBespElectricpilehead());//预约枪口id
				idleIn.putExtra("bespBespokeprice", bespokeDetailBean.getUnitPrice());//预约单价（保留小数点后2位）
				idleIn.putExtra("bespElectricpileheadName",bespokeDetailBean.getePHeElectricpileHeadName());//枪口名称
				startActivity(idleIn);
			}else {//枪口不空闲，判断是桩，还是站
				String currentlat = PreferencesUtil.getStringPreferences(this,"currentlat");
				String currentlng = PreferencesUtil.getStringPreferences(this,"currentlng");
				if(!Tools.isEmptyString(bespokeDetailBean.getElPi_RelevancePowerStation())){//有站id
					//进站详情,获取站的详情
					if(isNetConnection()){
						showPD("正在获取信息，请稍后...");
						GetDataPost.getInstance(this).getStationDetail(handler, bespokeDetailBean.getElPi_RelevancePowerStation(), 
								pkuserId, currentlng, currentlat);
					}else {
						showToast("网络不稳，请稍后再试...");
					}
					
				}else {
					//进电桩，获取桩的详情 
					if(isNetConnection()){
						showPD("正在获取信息，请稍后...");
						GetDataPost.getInstance(this).getPileDetail(handler, bespokeDetailBean.getPk_ElectricPile(), 
								pkuserId, currentlng, currentlat);
					}else {
						showToast("网络不稳，请稍后再试...");
					}
				}
			}
			
			break;
			
		default:
			break;
		}
		
	} 

	@Override
	protected void getData() {
		//bespokePrice = getIntent().getStringExtra("bespBespokeprice");
		bespokeDetailBean = (BespokeDetailBean)getIntent().getSerializableExtra("bespokeDetail");
		whichMode = getIntent().getStringExtra("mode");
		isShowDowntime = getIntent().getStringExtra("downtime");//1--正在预约中  ，2 已经结束，不显示倒计时
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i("socket_cm", "intent"+intent);
		try {
			BespokeDetailBean tempBean = (BespokeDetailBean)getIntent().getSerializableExtra("bespokeDetail");
			if(tempBean == null){
				return;
			}
			bespokeDetailBean = tempBean;
			whichMode = getIntent().getStringExtra("mode");
			isShowDowntime = getIntent().getStringExtra("downtime");//1--正在预约中  ，2 已经结束，不显示倒计时
			initValue(whichMode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		if(sign.equals(Protocol.CANCLE_BESPOKE)){
			cancelPD();
			//取消预约成功,弹框提示
			cancleBespokeBean = (CancleBespokeBean) bundle.getSerializable(Protocol.DATA);
			
			if(cancleBespokeBean != null){
				cancleBespokeSuccessDialog = new CancleBespokeSuccessDialog(this, cancleBespokeBean.getBalance(), cancleBespokeBean.getConsumamt());
				cancleBespokeSuccessDialog.setCancelable(false);
				cancleBespokeSuccessDialog.setOnPositiveListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						cancleBespokeSuccessDialog.dismiss();
						Intent broadIn = new Intent(BroadcastUtil.BROADCAST_Bespoke_CANCLE);
						sendBroadcast(broadIn);
						if("bespoke".equals(whichMode)){//or  list
							//跳转到预约列表
							Intent listIn = new Intent();
							listIn.setClass(BespokeDetailActivity.this, HomeActivity.class);
							listIn.putExtra("tag", "2");
							startActivity(listIn);
							finish();
						}else {
							finish();
						}
					}
				});
				cancleBespokeSuccessDialog.show();
			}else {
				Intent broadIn = new Intent(BroadcastUtil.BROADCAST_Bespoke_CANCLE);
				sendBroadcast(broadIn);
				if("bespoke".equals(whichMode)){//or  list
					//跳转到预约列表
					Intent listIn = new Intent();
					listIn.setClass(this, HomeActivity.class);
					listIn.putExtra("tag", "2");
					startActivity(listIn);
					finish();
				}else {
					finish();
				}
			}
			
			
		}else if(sign.equals(Protocol.POWER_STATION_DETAIL)){
			//站的详情
			stationBeanList = (ArrayList<PowerStationBean>) bundle.getSerializable(Protocol.DATA);
			if(stationBeanList != null){
				stationBean = stationBeanList.get(0);
				Intent detailIn = new Intent();
				detailIn.setClass(this, StationStiltDetailActivity.class);
				detailIn.putExtra("stationBean", stationBean);
				detailIn.putExtra("type", "2");
				detailIn.putExtra("electricId", bespokeDetailBean.getElPi_RelevancePowerStation());
				startActivity(detailIn);
				
			}
			
		}else if(sign.equals(Protocol.POWER_Pile_DETAIL)){
			//电桩详情
			pileBeanList = (ArrayList<ElectricPileBean>) bundle.getSerializable(Protocol.DATA);
			if(pileBeanList != null){
				pileBean = pileBeanList.get(0);
				Intent detailIn = new Intent();
				detailIn.setClass(this, StationStiltDetailActivity.class);
				detailIn.putExtra("pileBean", pileBean);
				detailIn.putExtra("type", "1");
				detailIn.putExtra("electricId", bespokeDetailBean.getPk_ElectricPile());
				startActivity(detailIn);
			}
		}/*else if(sign.equals(Protocol.LED)){
			tv_led.setOnClickListener(this);
		}*/

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		showToast(bundle.getString(Protocol.MSG));
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		flags = true;
	}
	@Override
	protected void onStop() {
		super.onPause();
		flags = false;
	}
	/**
	 * @author cm 新线程，每一秒发送一次消息,更新倒计时
	 */
	class TimeThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Message msg = new Message();
					msg.what = 18; // 消息(一个整型值)
					mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (flags);
		}
	}
	
	// 初始化导航
		private void initNavigation() {
			TTSController.getInstance(this).startSpeaking();
			// 获取当前经纬度
			String geoLat = PreferencesUtil.getStringPreferences(this,
					"currentlat");
			String geoLng = PreferencesUtil.getStringPreferences(this,
					"currentlng");
			startGeoLat = Double.parseDouble(geoLat);
			startGeoLng = Double.parseDouble(geoLng);
			// 获取目的地经纬度
			markerLat = bespokeDetailBean.getElPi_Latitude();
			markerLng = bespokeDetailBean.getElPi_Longitude();
			if (!Tools.isEmptyString(markerLat) && !Tools.isEmptyString(markerLng)) {
				endEdoLat = Double.parseDouble(markerLat.trim());
				endEdoLng = Double.parseDouble(markerLng.trim());
				NaviLatLng mNaviStart = new NaviLatLng(startGeoLat, startGeoLng);
				NaviLatLng mNaviEnd = new NaviLatLng(endEdoLat, endEdoLng);
				mStartPoints.clear();
				mEndPoints.clear();
				mStartPoints.add(mNaviStart);
				mEndPoints.add(mNaviEnd);
			}else {
				showToast("当前定位失败");
				return;
			}

			mRouteCalculatorProgressDialog = new ProgressDialog(this);
			mRouteCalculatorProgressDialog.setCancelable(true);
			AMapNavi aMapNavi = AMapNavi.getInstance(this);
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
		Intent intent = new Intent(this, NaviCustomActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		Bundle bundle = new Bundle();
		bundle.putInt(Utils.ACTIVITYINDEX, Utils.BESPOKEDETAIL);
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

package com.bm.wanma.ui.activity;

import java.util.ArrayList;

import net.tsz.afinal.FinalDb;

import com.bm.wanma.R;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.dialog.CancleBespokeDialog;
import com.bm.wanma.dialog.RefreshPointDialog;
import com.bm.wanma.entity.CityUpdateTimeBean;
import com.bm.wanma.entity.MapModeBean;
import com.bm.wanma.entity.MapModePileBean;
import com.bm.wanma.entity.MapModeStationBean;
import com.bm.wanma.entity.VersionInfoBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.DatabaseHelper;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.TimeUtil;
import com.bm.wanma.utils.Tools;
import com.bm.wanma.utils.UpdateAppManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author cm
 *  设置
 */
public class SettingsActivity extends BaseActivity implements OnClickListener{
	
	private ImageButton ib_back;
	private TextView tv_update_version,tv_update_version_name;
	private RelativeLayout rl_updata_point,rl_feedback,rl_about;
	private RelativeLayout rl_updata_version,rl_security,rl_tel;
	private RelativeLayout rl_logout;
	private String pkUserinfo;
	private CancleBespokeDialog cancleBespokeDialog;
	private RefreshPointDialog refreshPointDialog;
	private static final String TEL = "400-657567775";
	private int verNumber;
	private int verNumServer;
	private String verName;
	// 获取Map模式下的实体
	private static ArrayList<MapModeBean> mSearchMapBean;
	private FinalDb finalDb;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private boolean isCancleUpdata;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		try {
			verNumber = Integer.parseInt(PreferencesUtil.getStringPreferences(this, "versNumber"));
			verNumServer = Integer.parseInt(PreferencesUtil.getStringPreferences(this, "versNumberServer"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verName = PreferencesUtil.getStringPreferences(this, "versName");
		finalDb = FinalDb.create(getActivity(),Protocol.DATABASE_NAME,false,Protocol.dbNumer,null);
		initView();
	}
	private void initView(){
		ib_back = (ImageButton) findViewById(R.id.settings_back);
		ib_back.setOnClickListener(this);
		rl_updata_point = (RelativeLayout) findViewById(R.id.settings_updata_point);
		rl_updata_point.setOnClickListener(this);
		rl_feedback = (RelativeLayout) findViewById(R.id.settings_feedback);
		rl_feedback.setOnClickListener(this);
		rl_about = (RelativeLayout) findViewById(R.id.settings_about_we);
		rl_about.setOnClickListener(this);
		rl_updata_version = (RelativeLayout) findViewById(R.id.settings_updata_version);
		tv_update_version = (TextView) findViewById(R.id.settings_version_tv);
		tv_update_version_name = (TextView) findViewById(R.id.settings_version_tv_name);
		if(verNumServer > verNumber){
			tv_update_version.setVisibility(View.VISIBLE);
		}else {
			tv_update_version_name.setVisibility(View.VISIBLE);
			tv_update_version_name.setText("v"+verName);
		}
		
		
		rl_updata_version.setOnClickListener(this);
		rl_security = (RelativeLayout) findViewById(R.id.settings_security);
		rl_security.setOnClickListener(this);
		rl_tel = (RelativeLayout) findViewById(R.id.settings_tel);
		rl_tel.setOnClickListener(this);
		rl_logout = (RelativeLayout) findViewById(R.id.settings_logout);
		rl_logout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.settings_back:
			finish();
			break;
		case R.id.settings_updata_point:
			//更新充电点
			cancleBespokeDialog = new CancleBespokeDialog(this,"请确认是否要更新全国充电点？");
			cancleBespokeDialog.setCancelable(false);
	        cancleBespokeDialog.setOnPositiveListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	//正在更新充电点
		        	if(isNetConnection()){
						refreshPointDialog = new RefreshPointDialog(SettingsActivity.this);
						refreshPointDialog.setCancelable(false);
						refreshPointDialog.setOnPositiveListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								//取消更新充电点
								isCancleUpdata = true;
								refreshPointDialog.dismiss();
							}
						});
						refreshPointDialog.show();
						GetDataPost.getInstance(SettingsActivity.this).getElectricPileMapList(handler, null,null,
								null, null, null, null, null);
						
					}else {
						showToast("网络异常，请稍后再试...");
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
		case R.id.settings_feedback:
			//意见反馈
			Intent feedbackIn = new Intent();
			feedbackIn.setClass(SettingsActivity.this, CommitFeedbackActivity.class);
			startActivity(feedbackIn);
			
			break;
		case R.id.settings_about_we:
			//关于我们
			startActivity(new Intent(this,AboutWeActivity.class));
			break;
		case R.id.settings_updata_version:
			//版本更新
			if(verNumServer > verNumber){
				//弹出更新框
				if(isNetConnection()){
					GetDataPost.getInstance(this).getAppVersion(handler, String.valueOf(verNumber));
				}else {
					showToast("亲，网络不稳，请检查网络连接");
				}
			}else {
				showToast("当前已是最新版本，无需更新！");
			}
			
			break;
		case R.id.settings_security:
			//安全设置
			startActivity(new Intent(this,SecuritySettingsActivity.class));
			break;
		case R.id.settings_tel:
			//联系客服
			cancleBespokeDialog = new CancleBespokeDialog(this,TEL);
			cancleBespokeDialog.setCancelable(false);
			cancleBespokeDialog.setButtonTitle("呼叫", "取消");
	        cancleBespokeDialog.setOnPositiveListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	//拨打电话
		        	Intent telintent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+TEL));//直接拨打
		        	//Intent telintent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+TEL));
					telintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					startActivity(telintent);
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
		case R.id.settings_logout:
			//退出登录 
			cancleBespokeDialog = new CancleBespokeDialog(this,"是否退出登录?");
			cancleBespokeDialog.setCancelable(false);
	        cancleBespokeDialog.setOnPositiveListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	//正在退出登录
		        	if(isNetConnection()){
		        		showPD("正在退出登录...");
						pkUserinfo = PreferencesUtil.getStringPreferences(SettingsActivity.this,"pkUserinfo");
						GetDataPost.getInstance(SettingsActivity.this).logout(handler, pkUserinfo);
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
		default:
			break;
		}
		
	}
	@Override
	protected void getData() {
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		cancelPD();
	  if(sign.equals(Protocol.TO_LOGOUT)){
		  //退出登录
			clearInfo();
			
			Intent bespokefinishIn = new Intent();
			bespokefinishIn.setAction(BroadcastUtil.BROADCAST_Bespoke_Finish);
			bespokefinishIn.setAction(BroadcastUtil.BROADCAST_Charge_CANCLE);
			sendBroadcast(bespokefinishIn);
			finish();
		}else if(sign.equals(Protocol.GET_APP_VERSION_INFO)){
			//获取版本信息
			VersionInfoBean versionBean = (VersionInfoBean) bundle.getSerializable(Protocol.DATA);
			if(versionBean != null){
				new UpdateAppManager(this, versionBean,verNumber).checkUpdate();
			}
			 
		}else if(sign.equals(Protocol.GET_STATION_ELECTRIC_MAP)) {
			//更新充电点
			mSearchMapBean = (ArrayList<MapModeBean>) bundle
					.getSerializable(Protocol.DATA);
			if(mSearchMapBean!= null && mSearchMapBean.size()>0){
				new Thread(new Runnable() {
					@Override
					public void run() {
						/*finalDb.dropTable(MapModePileBean.class);
						finalDb.dropTable(MapModeStationBean.class);*/
						finalDb.deleteAll(MapModePileBean.class);
						finalDb.deleteAll(MapModeStationBean.class);
						addAllToSave(mSearchMapBean);
						runOnUiThread(new Runnable() {
							public void run() {
								if(!isCancleUpdata){
									refreshPointDialog.dismiss();
									showToast("更新充电点完成");	
									Intent in = new Intent();
									in.setAction(BroadcastUtil.BROADCAST_UPDATAPOINT);
									sendBroadcast(in);
								}
								
							}
						});
						
						
					}
				}).start();
			}
		}
		
	}
	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		showToast(bundle.getString(Protocol.MSG));
		if(sign.equals(Protocol.GET_STATION_ELECTRIC_MAP)) {
		if(refreshPointDialog!= null){
			refreshPointDialog.dismiss();
		}
		}
		
	}
	private void clearInfo(){
		PreferencesUtil.setPreferences(this, "pkUserinfo", "");
		PreferencesUtil.setPreferences(this, "usinPhone", "");
		PreferencesUtil.setPreferences(this, "usinFacticityname", "");
		PreferencesUtil.setPreferences(this, "usinSex", "");
		PreferencesUtil.setPreferences(this, "usinAccountbalance", "");
		PreferencesUtil.setPreferences(this, "usinBirthdate", "");
		PreferencesUtil.setPreferences(this, "usinUserstatus", "");
		PreferencesUtil.setPreferences(this, "usinHeadimage", "");
		PreferencesUtil.setPreferences(this, "nickName", "");
		PreferencesUtil.setPreferences(this, "carType", "");
		PreferencesUtil.setPreferences(this, "carName", "");
		PreferencesUtil.setPreferences(this, "isPpw", "");
		
	}
	/**
	 * 更新全国电桩数据
	 */
	private void addAllToSave(ArrayList<MapModeBean> mMapBeanList){
		MapModePileBean stiltBean = new MapModePileBean();
		MapModeStationBean stationBean = new MapModeStationBean();
		for (MapModeBean bean : mMapBeanList) {
			//区分电桩 电站 分两个表存到数据库
			if("15".equals(bean.getElectricState()) && "0".equals(bean.getDel())){
				if("1".equals(bean.getElectricType())){//电桩
					stiltBean.setCityCode(bean.getCityCode());
					stiltBean.setDel(bean.getDel());
					stiltBean.setElectricId(bean.getElectricId());
					stiltBean.setElectricState(bean.getElectricState());
					stiltBean.setElectricType(bean.getElectricType());
					stiltBean.setLatitude(bean.getLatitude());
					stiltBean.setLongitude(bean.getLongitude());
					stiltBean.setElectricAddress(bean.getElectricAddress());
					stiltBean.setElectricName(bean.getElectricName());
					stiltBean.setIsAppoint(bean.getIsAppoint());
					finalDb.save(stiltBean);
				}else if("2".equals(bean.getElectricType())){//电站
					stationBean.setCityCode(bean.getCityCode());
					stationBean.setDel(bean.getDel());
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
		//获取当前时间，转换时间戳为时间格式
		long cuttentTime = System.currentTimeMillis()/1000;
		String update = TimeUtil.getTime(String.valueOf(cuttentTime), "");
		dbHelper = DatabaseHelper.getInstance(getActivity(), Protocol.dbNumer);
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from tb_m_city", null);
		CityUpdateTimeBean cityuptateBean = new CityUpdateTimeBean();
		finalDb.deleteAll(CityUpdateTimeBean.class);
		while (cursor.moveToNext()) {
		   String code = cursor.getString(1); //获取第一列的值,第一列的索引从0开始---城市code
		   cityuptateBean.setCityCode(code);
		   cityuptateBean.setUpdateTime(update);
		   finalDb.save(cityuptateBean);
		}
		cursor.close();
		db.close();
	}
	
}
package com.bm.wanma.ui.activity;

import java.util.ArrayList;

import com.bm.wanma.R;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.bm.wanma.entity.ElectricPileBean;
import com.bm.wanma.entity.PowerStationBean;
import com.bm.wanma.entity.VersionInfoBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.ui.fragment.BaseFragment;
import com.bm.wanma.ui.fragment.MapAndListFragment;
import com.bm.wanma.ui.fragment.MyHelperFragment;
import com.bm.wanma.ui.fragment.MyOrderFragment;
import com.bm.wanma.ui.fragment.MyPersonFragment;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;
import com.bm.wanma.utils.UpdateAppManager;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主界面
 * @author cm
 */

public class HomeActivity extends BaseActivity implements OnClickListener{
		
		// 四三个tab布局
		private RelativeLayout PileLayout, MyorderLayout, MyPersonLayout,MyHelperLayout;
		// 底部标签切换的Fragment
		private BaseFragment currentFragment;
		private MapAndListFragment mapAndListFragment;
		private MyOrderFragment myOrderFragment;
		private MyPersonFragment myPersonFragment;
		private MyHelperFragment myHelperFragment;
		// 底部标签图片
		private ImageView PileImg, MyOrderImg, MyPersonImg,MyHelperImg;
		// 底部标签的文本
		private TextView PileTv, MyOrderTv, MyPersonTv,MyHelperTv;
		private long exitTime;
		private String pkUserId;
		private String type,electricId;
		private ArrayList<PowerStationBean> stationBeanList;
		private PowerStationBean stationBean;
		private ArrayList<ElectricPileBean> pileBeanList;
		private ElectricPileBean pileBean;
		private VersionInfoBean versionBean;
		private UpdateAppManager mAppManager;
		
		private int versNumber;
		//private String versName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initUI();
		initTab();
	}
	
	 
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//注销掉下边代码，让其不再保存Fragment的状态，达到其随着MainActivity一起被回收的效果！
		//super.onSaveInstanceState(outState);
	}



	/**
	 * 初始化UI
	 */
	private void initUI() {
		PileLayout = (RelativeLayout) findViewById(R.id.rl_pile);
		MyorderLayout = (RelativeLayout) findViewById(R.id.rl_myorder);
		MyPersonLayout = (RelativeLayout) findViewById(R.id.rl_myperson);
		PileLayout.setOnClickListener(this);
		MyorderLayout.setOnClickListener(this);
		MyPersonLayout.setOnClickListener(this);
		MyHelperLayout = (RelativeLayout)findViewById(R.id.rl_myhelper);
		MyHelperLayout.setOnClickListener(this);

		PileImg = (ImageView) findViewById(R.id.iv_pile);
		MyOrderImg = (ImageView) findViewById(R.id.iv_myorder);
		MyPersonImg = (ImageView) findViewById(R.id.iv_myperson);
		PileTv = (TextView) findViewById(R.id.tv_pile);
		MyOrderTv = (TextView) findViewById(R.id.tv_myorder);
		MyPersonTv = (TextView) findViewById(R.id.tv_myperson);
		MyHelperImg = (ImageView)findViewById(R.id.iv_myhelper);
		MyHelperTv = (TextView) findViewById(R.id.tv_myhelper);

	}

	/**
	 * 初始化底部标签
	 */
	private void initTab() {
		if (mapAndListFragment == null) {
			mapAndListFragment = new MapAndListFragment();
		}
		if (!mapAndListFragment.isAdded()) {
			// 提交事务
			getFragmentManager().beginTransaction()
					.add(R.id.content_layout, mapAndListFragment).commit();
			// 记录当前Fragment
			currentFragment = mapAndListFragment;
			// 设置图片文本的变化
			PileImg.setImageResource(R.drawable.tab_home_p);
			PileTv.setTextColor(getResources()
					.getColor(R.color.common_orange));
			MyOrderImg.setImageResource(R.drawable.tab_order_d);
			MyOrderTv.setTextColor(getResources().getColor(
					R.color.common_gray));
			MyPersonImg.setImageResource(R.drawable.tab_my_d);
			MyPersonTv.setTextColor(getResources().getColor(R.color.common_gray));
			MyHelperImg.setImageResource(R.drawable.tab_help_d);
			MyHelperTv.setTextColor(getResources().getColor(R.color.common_gray));

		}

	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_pile: // 首页
			clickTabOfPile();
			break;
		case R.id.rl_myorder: // 订单
			pkUserId = PreferencesUtil.getStringPreferences(HomeActivity.this, "pkUserinfo");
			if(!Tools.isEmptyString(pkUserId)){
				clickTabOfMyorder();
			}else {
				Intent loginIn = new Intent();
				loginIn.setClass(HomeActivity.this, LoginAndRegisterActivity.class);
				startActivity(loginIn);
			}
			break;
		case R.id.rl_myperson: // 我的
			clickTabOfMyperson();
			break;
		case R.id.rl_myhelper://小助手
			clickTabOfMyhelper();
			break;
			
		default:
			break;
		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
		String tag = intent.getStringExtra("tag");
		if("1".equals(tag)){
			clickTabOfPile();
		}else if("2".equals(tag)){
			clickTabOfMyorder();
		}else if("3".equals(tag)){
			clickTabOfMyperson();
		}else if("4".equals(tag)){
			clickTabOfMyhelper();
		}
	
	}
	
	
	/**
	 * 点击第一个tab--Pile首页
	 */
	private void clickTabOfPile() {
		if (mapAndListFragment == null) {
			mapAndListFragment = new MapAndListFragment();
		}
		addOrShowFragment(getFragmentManager().beginTransaction(), mapAndListFragment);
		// 设置底部tab变化
		PileImg.setImageResource(R.drawable.tab_home_p);
		PileTv.setTextColor(getResources().getColor(R.color.common_orange));
		MyOrderImg.setImageResource(R.drawable.tab_order_d);
		MyOrderTv.setTextColor(getResources().getColor(
				R.color.common_gray));
		MyPersonImg.setImageResource(R.drawable.tab_my_d);
		MyPersonTv.setTextColor(getResources().getColor(R.color.common_gray));
		MyHelperImg.setImageResource(R.drawable.tab_help_d);
		MyHelperTv.setTextColor(getResources().getColor(R.color.common_gray));
	}

	/**
	 * 点击第二个tab --订单
	 */
	private void clickTabOfMyorder() {
		if (myOrderFragment == null) {
			myOrderFragment = new MyOrderFragment();
		}
		addOrShowFragment(getFragmentManager().beginTransaction(), myOrderFragment);
		
		PileImg.setImageResource(R.drawable.tab_home_d);
		PileTv.setTextColor(getResources().getColor(R.color.common_gray));
		MyOrderImg.setImageResource(R.drawable.tab_order_p);
		MyOrderTv.setTextColor(getResources().getColor(
				R.color.common_orange));
		MyPersonImg.setImageResource(R.drawable.tab_my_d);
		MyPersonTv.setTextColor(getResources().getColor(R.color.common_gray));
		MyHelperImg.setImageResource(R.drawable.tab_help_d);
		MyHelperTv.setTextColor(getResources().getColor(R.color.common_gray));
	}

	/**
	 * 点击第三个tab -- Myperson
	 */
	private void clickTabOfMyperson() {
		if (myPersonFragment == null) {
			myPersonFragment = new MyPersonFragment();
		}
		
		addOrShowFragment(getFragmentManager().beginTransaction(), myPersonFragment);
		PileImg.setImageResource(R.drawable.tab_home_d);
		PileTv.setTextColor(getResources().getColor(R.color.common_gray));
		MyOrderImg.setImageResource(R.drawable.tab_order_d);
		MyOrderTv.setTextColor(getResources().getColor(
				R.color.common_gray));
		MyPersonImg.setImageResource(R.drawable.tab_my_p);
		MyPersonTv.setTextColor(getResources().getColor(R.color.common_orange));
		MyHelperImg.setImageResource(R.drawable.tab_help_d);
		MyHelperTv.setTextColor(getResources().getColor(R.color.common_gray));
		
	}
/**
 * 点击第四个tab--Myhelper
 */
	private void clickTabOfMyhelper(){
		if (myHelperFragment == null) {
			myHelperFragment = new MyHelperFragment();
		}
		
		addOrShowFragment(getFragmentManager().beginTransaction(), myHelperFragment);
		PileImg.setImageResource(R.drawable.tab_home_d);
		PileTv.setTextColor(getResources().getColor(R.color.common_gray));
		MyOrderImg.setImageResource(R.drawable.tab_order_d);
		MyOrderTv.setTextColor(getResources().getColor(
				R.color.common_gray));
		MyPersonImg.setImageResource(R.drawable.tab_my_d);
		MyPersonTv.setTextColor(getResources().getColor(R.color.common_gray));
		MyHelperImg.setImageResource(R.drawable.tab_help_p);
		MyHelperTv.setTextColor(getResources().getColor(R.color.common_orange));
		
	}
	
 
	private void addOrShowFragment(FragmentTransaction transaction,
			BaseFragment fragment) {
		if (currentFragment == fragment){
			return;
		}else if(currentFragment == myOrderFragment){
			if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
				transaction.remove(myOrderFragment)
						.add(R.id.content_layout, fragment).commit();
			} else {
				transaction.remove(myOrderFragment).show(fragment).commit();
			}
			
		}/*else if(currentFragment == myPersonFragment){
			if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
				transaction.hide(currentFragment)
						.add(R.id.content_layout, fragment).commit();
			} else {
				transaction.hide(currentFragment).show(fragment).commit();
			}
		}*/else {
			if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
				transaction.hide(currentFragment)
						.add(R.id.content_layout, fragment).commit();
			} else {
				transaction.hide(currentFragment).show(fragment).commit();
			}
		}
		
		currentFragment = fragment;
		if(fragment == myPersonFragment){
			 Intent intnet = new Intent("com.bm.wanma.getuserinfo");
			 sendBroadcast(intnet);
		}
	}
	
	// 两次返回键退出程序
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) // System.currentTimeMillis()无论何时调用，肯定大于2000
			{
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void getData() {
		/*
		//程序启动时，获取版本信息
	 	PackageManager packageManager = getPackageManager();     
	    //getPackageName()是你当前类的包名，0代表是获取版本信息      
	    PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			versNumber = packInfo.versionCode;
			versName = packInfo.versionName;
			PreferencesUtil.setPreferences(getApplicationContext(), "versNumber", String.valueOf(versNumber));
			PreferencesUtil.setPreferences(getApplicationContext(), "versName", versName);
			if(isNetConnection()){
				GetDataPost.getInstance(this).getAppVersion(handler, String.valueOf(versNumber));
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			versNumber = 0;
		}  */
		try {
			String tempversNumber = PreferencesUtil.getStringPreferences(this, "versNumber");
			versNumber = Integer.valueOf(tempversNumber);
			//versName = PreferencesUtil.getStringPreferences(this, "versName");
			if(isNetConnection()){
				GetDataPost.getInstance(this).getAppVersion(handler, String.valueOf(versNumber));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		 
		//通过扫描web端二维码进来
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			//String pt = bundle.getString("pt");
			type = bundle.getString("et");//type
			electricId = bundle.getString("d");//id
			if(!Tools.isEmptyString(electricId)){
				pkUserId = PreferencesUtil.getStringPreferences(getApplicationContext(), "pkUserinfo");
				Intent detailin = new Intent();
				if (!Tools.isEmptyString(pkUserId)) {
					String currentLat = PreferencesUtil.getStringPreferences(getActivity(),"currentlat");
					String currentLng = PreferencesUtil.getStringPreferences(getActivity(),"currentlng");
					if("1".equals(type)){
						showPD("正在加载数据...");
						GetDataPost.getInstance(this).getPileDetail(handler,
								electricId, pkUserId, currentLng, currentLat);
					}else if("2".equals(type)){
						showPD("正在加载数据...");
						GetDataPost.getInstance(this).getStationDetail(handler,
								electricId, pkUserId, currentLng, currentLat);
					}

				} else {
					detailin.setClass(HomeActivity.this,
							LoginAndRegisterActivity.class);
				}
				startActivity(detailin);
				
			}
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
			detailIn.setClass(this, StationStiltDetailActivity.class);
			detailIn.putExtra("stationBean", stationBean);
			detailIn.putExtra("type", "2");
			detailIn.putExtra("electricId", electricId);
			this.startActivity(detailIn);
		}
	}else if (sign.equals(Protocol.POWER_Pile_DETAIL)) {
		//电桩详情
		pileBeanList = (ArrayList<ElectricPileBean>) bundle.getSerializable(Protocol.DATA);
		if(pileBeanList != null){
			pileBean = pileBeanList.get(0);
			Intent detailIn = new Intent();
			detailIn.setClass(this, StationStiltDetailActivity.class);
			detailIn.putExtra("pileBean", pileBean);
			detailIn.putExtra("type", "1");
			detailIn.putExtra("electricId", electricId);
			this.startActivity(detailIn);
		}
	}else if(sign.equals(Protocol.GET_APP_VERSION_INFO)){
		//获取版本信息
		 versionBean = (VersionInfoBean) bundle.getSerializable(Protocol.DATA);
		if(versionBean != null){
			PreferencesUtil.setPreferences(getApplicationContext(), "versNumberServer", versionBean.getVersNumber());
			if(mAppManager == null){
				mAppManager = new UpdateAppManager(this, versionBean,versNumber);
			}
			mAppManager.checkUpdate();
		}
	}

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		if(sign.equals(Protocol.GET_APP_VERSION_INFO)){
			PreferencesUtil.setPreferences(getApplicationContext(), "versNumberServer", String.valueOf(versNumber));
		}else {
			showToast(bundle.getString(Protocol.MSG));
		}

	}



}

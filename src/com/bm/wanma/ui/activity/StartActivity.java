package com.bm.wanma.ui.activity;

import cn.jpush.android.api.JPushInterface;

import com.bm.wanma.R;
import com.bm.wanma.entity.VersionInfoBean;
import com.bm.wanma.net.GetDataGet;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.DatabaseHelper;
import com.bm.wanma.utils.IntentUtil;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.ProjectApplication;
import com.bm.wanma.utils.ToastUtil;
import com.bm.wanma.utils.Tools;
import com.bm.wanma.utils.UpdateAppManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

/**
 * @author cm
 * 启动界面
 *
 */
public class StartActivity extends BaseActivity {
	private Uri uri;
	private int verN;
	private int versNumber;
	private String versName;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		Intent i_getvalue = getIntent();
		uri = i_getvalue.getData();
		//程序启动时，获取版本信息
	 	PackageManager packageManager = getPackageManager();     
	    //getPackageName()是你当前类的包名，0代表是获取版本信息      
	    PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			versNumber = packInfo.versionCode;
			versName = packInfo.versionName;
			String temp = PreferencesUtil.getStringPreferences(this, "versNumber");
			PreferencesUtil.setPreferences(getApplicationContext(), "versNumber", String.valueOf(versNumber));
			PreferencesUtil.setPreferences(getApplicationContext(), "versName", versName);
			if(!Tools.isEmptyString(temp)){
				verN = Integer.valueOf(temp);
				if(versNumber>verN){
					ProjectApplication.setGuideType(false);//版本更新后再次进入引导页
				}
			}
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}  
		
		
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (ProjectApplication.getGuideType()) {

					if (uri != null) {
						String pt = uri.getQueryParameter("pt");// 参数类型 为2进入详情
						String d = uri.getQueryParameter("d"); // 电桩或电站id
						String et = uri.getQueryParameter("et"); // 1为电桩，2为电站
						Intent detailin = new Intent();
						Bundle detailBudle = new Bundle();
						detailBudle.putString("pt", pt);
						detailBudle.putString("d", d);
						detailBudle.putString("et", et);
						detailin.putExtras(detailBudle);
						detailin.setClass(StartActivity.this, HomeActivity.class);
						startActivity(detailin);
						finish();
					} else {
						Intent in = new Intent();
						in.setClass(StartActivity.this, HomeActivity.class);
						startActivity(in);
						finish();
					}
				} else {// 进入引导页
					IntentUtil.startIntent(StartActivity.this,
							GuideActivity.class);
					ProjectApplication.setGuideType(true);// 让其以后不进入登录页
					finish();
				}
			} 
		}, 3000);
		 
		

	
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void getData() {
		GetDataPost.getInstance(this).getApiToken(handler);

	}

	@Override
	public void onSuccess(String sign, Bundle bundle) {
		if(sign.equals(Protocol.GET_API_TOKEN)){//获取token
			String apiToken = (String) bundle.getSerializable(Protocol.DATA);
			PreferencesUtil.setPreferences(getApplicationContext(), "apiToken",
					apiToken);
		}
		

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		// TODO Auto-generated method stub

	}

}

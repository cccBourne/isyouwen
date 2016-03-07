package com.bm.wanma.ui.activity;

import cn.jpush.android.api.JPushInterface;

import com.bm.wanma.R;
import com.bm.wanma.alipay.Base64;
import com.bm.wanma.entity.LoginBean;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.ProjectApplication;
import com.bm.wanma.utils.Tools;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {

	private ProgressDialog pd;
	public static final int SHOW_PD = 225;
	public static final int THREAD = 226;
	public static final int HIDE_PD = 224;
	/* 经度，纬度 */
	public static String lon, lat;

	/* 反编码 */
	Geocoder geocoder;
	private String apiToken;
	private long timeStamp;
	public boolean islockScreen;
	public boolean isBackgroud;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ProjectApplication.getInstance().addActivity(this);
		ProjectApplication.getInstance().addExitActivities(this);
		// 注册事件  
        registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));  
        registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));  
        registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT)); 
       // islockScreen = true;
		
     
		
		if (isNetConnection()) {
			getData();// 这个要放在前面
		} else {
			showToast("亲，网络不稳，请检查网络连接!");
		}
	}
	
	public boolean isRunningForeground (Context context)  
    {  
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
        String currentPackageName = cn.getPackageName();  
        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName()))  
        {  
            return true ;  
        }  
       
        return false ;  
    }  
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	public String getDeviceId(){
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId(); 
		deviceId = Tools.encoderByMd5(deviceId);
   		char[] chars = deviceId.toCharArray();
   		String encodeID = "";
   		for (int i = 0; i < chars.length; i++) {
   			encodeID += Tools.replace((byte) chars[i]);
   		}
   		encodeID = Base64.encode(encodeID.getBytes()); 
   		return encodeID;
   	}
   	
	
	 @Override  
	    protected void onUserLeaveHint() { //当用户按Home键等操作使程序进入后台时调用  
	        super.onUserLeaveHint();  
	        LogUtil.i("cm_leave", "后台");
	        isBackgroud = false;
	    }  
	 @Override  
	    protected void onResume() { //当用户使程序恢复为前台显示时执行onResume()方法 
	        super.onResume();  
	        // 友盟统计
			//MobclickAgent.onResume(this);
			JPushInterface.onResume(this);
			if(islockScreen){
				LogUtil.i("cm_lock", "前台");
			}else {
				LogUtil.i("cm_leave", "前台");
				isBackgroud = true;
			}
			
	    }  
	/* 判断是否有网络 */
	public boolean isNetConnection() {
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	} 
	/* 判断是否有wifi网络 */
	public boolean isNotWifiConnection(){
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if(info !=null && info.isAvailable() && info.getType() ==  ConnectivityManager.TYPE_MOBILE){
			return true;
		}else {
			return false;
		}
	}
	
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
		case 6633:
			showToast("预约中，不能在进行除续约之外的其它操作");
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
	
	/* 获取token */
	public String getAccessToken(){
		apiToken = PreferencesUtil.getStringPreferences(getActivity(), "apiToken");
		timeStamp = System.currentTimeMillis();
		String toToken = apiToken + timeStamp ;
		String replaceToken = "";
		char[] chars = toToken.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			replaceToken += Tools.replace((byte) chars[i]);
		}
		replaceToken = Base64.encode(replaceToken.getBytes());
		return replaceToken;
	}
	
	/**
	 * 获取从上个页面传递过来的数据，或者需要从本地读取的数据，如用户数据。
	 */
	protected abstract void getData();
	
	
	
	public abstract void onSuccess(String sign, Bundle bundle);

	public abstract void onFaile(String sign, Bundle bundle);

	
	public LoginBean getLoginBean() {
		return ProjectApplication.getInstance().getLoginBean();
	}
	public void setLoginBean(LoginBean loginBean) {
		ProjectApplication.getInstance().setLoginBean(loginBean);
	}
	
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				onFaile(msg.obj.toString(), msg.getData());
				break;

			case 1:
				onSuccess(msg.obj.toString(), msg.getData());
				break;
			case SHOW_PD:
				showPD();
				break;
			case HIDE_PD:
				cancelPD();
				break;
			case THREAD:
				thread();
				break;
			}

		}

	};
	
	/**
	 * 线程
	 */
	protected void thread() {

	}
	/**
	 *  Toast提示
	 */
	protected void showToast(String content) {
		if (content != null && content.length() > 0)
			Toast.makeText(this, content, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 显示等待窗
	 * 
	 * @param content
	 */
	protected void showPD(String content) {
		if (null == pd) {
			pd = new ProgressDialog(this);
		}
		if (null != content) {
			pd.setMessage(content);
			// 设置点击屏幕Dialog不消失
			pd.setCanceledOnTouchOutside(false);
			// 不能取消
			// pd.setCancelable(false);
		}
 
		if (!pd.isShowing()) {
			pd.show();
		}
	}

	protected void showPD() {
		showPD(getString(R.string.request_data));
	}

	/**
	 * 关闭等待窗
	 */
	protected void cancelPD() {
		if (null != pd && pd.isShowing()) {
			pd.dismiss();
		}
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/**
	 * 隐藏输入法面板
	 */

	public void hideKeyboard(View view) {
		InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
		if (imm != null && getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}


	protected void onPause() {
		super.onPause();
		// 友盟统计
		//MobclickAgent.onPause(this);
		JPushInterface.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myReceiver);
	}


	public BaseActivity getActivity() {
		return this;
	}
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {  
        
        @Override  
        public void onReceive(Context context, Intent intent) {  
            // TODO Auto-generated method stub  
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction()) ) {//当按下电源键，屏幕亮起的时候  
            	//LogUtil.i("cm", "屏幕亮起");
            	islockScreen = true;  
            }  
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction()) ) {//当按下电源键，屏幕变黑的时候  
               islockScreen = true;  
              // LogUtil.i("cm", "屏幕变黑");
            }  
            if (Intent.ACTION_USER_PRESENT.equals(intent.getAction()) ) {//当解除锁屏的时候  
                islockScreen = false;  
                LogUtil.i("cm", "解除锁屏");
            }  
        }  
    };  

}

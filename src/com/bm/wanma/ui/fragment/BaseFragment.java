package com.bm.wanma.ui.fragment;


import com.bm.wanma.R;
import com.bm.wanma.alipay.Base64;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {

	private ProgressDialog pd;
	public static final int SHOW_PD = 225;
	public static final int THREAD = 226;
	public static final int HIDE_PD = 224;
	public boolean islockScreen;
	private String apiToken;
	private long timeStamp;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isNetConnection()) {
		} else {
			showToast("亲，网络不稳，请检查网络连接!");
		}
		// 注册事件  
        getActivity().registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));  
        getActivity().registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));  
        getActivity().registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT)); 
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(myReceiver);
	}
	
	
	
	
/*	@Override
	public void onDetach() {
		super.onDetach();
		
		try {
			Field childFM = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFM.setAccessible(true);
			childFM.set(this, null);
			
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e); 
		}catch (IllegalAccessException  e) {
			throw new RuntimeException(e); 
		}
		
		
	}*/

	
	/**
	 *  Toast提示
	 */
	protected void showToast(String content) {
		if (content != null && content.length() > 0)
			Toast.makeText(getActivity(), content, Toast.LENGTH_LONG).show();
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
			}

		}

	};
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
	
	
	/* 判断是否有网络 */
	protected boolean isNetConnection() {
		ConnectivityManager cwjManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	} 
	
	/**
	 * 显示等待窗
	 * 
	 * @param content
	 */
	protected void showPD(String content) {
		if (null == pd) {
			pd = new ProgressDialog(getActivity());
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
	

	public abstract void onSuccess(String sign, Bundle bundle);

	public abstract void onFaile(String sign, Bundle bundle);
	
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

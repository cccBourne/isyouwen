package com.bm.wanma.broadcast;

import com.bm.wanma.utils.ToastUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkChangeBroadcastReceiver extends BroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager= 
		        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if(info !=null && info.getType() ==  ConnectivityManager.TYPE_MOBILE){
			//ToastUtil.showToast("mobile");
		}else if(info !=null && info.getType() ==  ConnectivityManager.TYPE_WIFI){
			//ToastUtil.showToast("wifi");
		}
		
	}
	
	

}
